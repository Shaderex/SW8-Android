package dk.aau.sw808f16.datacollection.backgroundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.gcm.TaskParams;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.Campaign;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class UploadManagementService extends GcmTaskService {

  public static final String CAMPAIGN_SYNCHRONIZATION_TAG = "CAMPAIGN_SYNCHRONIZATION_TAG";

  private boolean isBoundToBackgroundService = false;

  @Override
  public void onCreate() {
    super.onCreate();

    bindToBackgroundService();
  }

  @Override
  public void onDestroy() {
    unbindToBackgroundService();
    super.onDestroy();
  }

  private ServiceConnection serviceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(final ComponentName className, final IBinder binder) {

      // We've bound to LocalService, cast the IBinder and get LocalService instance
      isBoundToBackgroundService = true;
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
      isBoundToBackgroundService = false;
    }
  };

  private void bindToBackgroundService() {
    final Intent serviceIntent = new Intent(this, BackgroundSensorService.class);
    bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  private void unbindToBackgroundService() {
    if (isBoundToBackgroundService && serviceConnection != null) {
      unbindService(serviceConnection);
      isBoundToBackgroundService = false;
    }
  }


  @Override
  public int onRunTask(final TaskParams taskParams) {
    // Do some upload work
    switch (taskParams.getTag()) {
      case CAMPAIGN_SYNCHRONIZATION_TAG:

        if (!isBoundToBackgroundService) {
          Log.d("SynchronizationTimer", "Upload is waiting for background service");
          return GcmNetworkManager.RESULT_SUCCESS;
        }

        Realm realm = null;
        final List<Integer> campaingIdentifiers = new ArrayList<>();
        final List<String> serializedCampaigns = new ArrayList<>();

        try {

          realm = Realm.getDefaultInstance();
          final RealmResults<Campaign> results = realm.where(Campaign.class).findAll();

          if (results.size() == 0) {
            Log.d("SynchronizationTimer", "There are no campaigns to be uploaded");
            return GcmNetworkManager.RESULT_SUCCESS;
          }

          for (int i = 0; i < results.size(); i++) {

            Campaign campaign = results.get(i);

            try {

              final String serializedCampaign = campaign.toJsonObject().toString();
              final int campaignIdentifer = campaign.getIdentifier();

              serializedCampaigns.add(serializedCampaign);
              campaingIdentifiers.add(campaignIdentifer);

            } catch (JSONException exception) {
              exception.printStackTrace();
            }
          }

        } finally {
          if (realm != null) {
            realm.close();
          }
        }

        for (int i = 0; i < campaingIdentifiers.size(); i++) {

          final String serializedCampaign = serializedCampaigns.get(i);
          final int campaignIdentifer = campaingIdentifiers.get(i);

          final String requestUrl = RequestHostResolver.resolveHostForRequest(this,
              "/campaigns/" + campaignIdentifer + "/snapshots");

          final long requestTimestamp = System.currentTimeMillis();

          // Send the campaign to the server
          final AsyncHttpWebbTask<String> task = new AsyncHttpWebbTask<String>(AsyncHttpWebbTask.Method.POST, requestUrl, HttpURLConnection.HTTP_OK, this) {

            @Override
            protected Response<String> sendRequest(final Request webb) {

              try {
                final InstanceID instanceId = InstanceID.getInstance(UploadManagementService.this);
                final String token = instanceId.getToken(
                    UploadManagementService.this.getString(R.string.defaultSenderID),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
                );
                final Response<String> jsonString =
                    webb.param("snapshots", serializedCampaign)
                        .param("device_id", token)
                        .asString();

                Log.d("Service-status", "Snapshot string length: r " + serializedCampaign.length());
                Log.d("Service-status", serializedCampaign);
                return jsonString;
              } catch (IOException exception) {
                exception.printStackTrace();
                return null;
              }
            }

            @Override
            public void onResponseCodeMatching(final Response<String> response) {


              Realm realm = null;
              try {
                realm = Realm.getDefaultInstance();

                try {
                  realm.beginTransaction();
                  final RealmResults<Campaign> campaigns = realm.where(Campaign.class).equalTo("identifier", campaignIdentifer).findAll();

                  if (campaigns.isEmpty()) {
                    return;
                  }

                  final Campaign campaign = campaigns.get(0);

                  final List<Snapshot> snapshots = campaign.getSnapshots();

                  if (snapshots.size() > 0) {
                    for (Snapshot snapshot : campaign.getSnapshots()) {

                      if (!campaign.isSnapshotReady(requestTimestamp, snapshot)) {
                        continue;
                      }

                      for (RealmObject obj : snapshot.children()) {

                        // Ensure that the object have not been modified elsewhere before trying to delete it
                        if (obj.isValid()) {
                          obj.removeFromRealm();
                        }
                      }
                    }
                  }

                  realm.commitTransaction();
                } catch (Exception exception) {
                  realm.cancelTransaction();
                  Log.e("UploadService", "Exception while performing Realm Transaction");
                  exception.printStackTrace();
                  throw exception;
                }
              } finally {
                if (realm != null) {
                  realm.close();
                }
              }

              Log.d("SynchronizationTimer", "All campaigns were uploaded");
            }

            @Override
            public void onResponseCodeNotMatching(final Response<String> response) {
              Log.d("SynchronizationTimer", "onResponseCodeNotMatching. Got " + response.getStatusCode());
            }

            @Override
            public void onConnectionFailure() {
              Log.d("SynchronizationTimer", "onConnectionFailure");
            }
          };

          task.execute();
        }


        return GcmNetworkManager.RESULT_SUCCESS;
      default:
        return GcmNetworkManager.RESULT_FAILURE;
    }
  }
}