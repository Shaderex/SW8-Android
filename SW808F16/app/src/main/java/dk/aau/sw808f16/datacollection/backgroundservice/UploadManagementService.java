package dk.aau.sw808f16.datacollection.backgroundservice;

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

  @Override
  public int onRunTask(final TaskParams taskParams) {
    // Do some upload work
    switch (taskParams.getTag()) {
      case CAMPAIGN_SYNCHRONIZATION_TAG:
        Realm realm = null;

        try {

          realm = Realm.getDefaultInstance();
          final RealmResults<Campaign> results = realm.where(Campaign.class).findAll();

          if (results.size() == 0) {
            Log.d("SynchronizationTimer", "There are no campaigns to be uploaded");
          }

          for (int i = 0; i < results.size(); i++) {

            final Campaign campaign = results.get(i);
            final String requestUrl = RequestHostResolver.resolveHostForRequest(this,
                "/campaigns/" + campaign.getIdentifier() + "/snapshots");

            try {
              final long requestTimestamp = System.currentTimeMillis();
              final String campaignString = campaign.toJsonObject().toString();

              final int campaignIdentifer = campaign.getIdentifier();

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
                        webb.param("snapshots", campaignString)
                            .param("device_id", token)
                            .asString();

                    Log.d("Service-status", "Snapshot string length: r " + campaignString.length());
                    Log.d("Service-status", campaignString);
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

            } catch (JSONException exception) {
              exception.printStackTrace();
            }
          }
        } finally {
          if (realm != null) {
            realm.close();
          }
        }

        return GcmNetworkManager.RESULT_SUCCESS;
      default:
        return GcmNetworkManager.RESULT_FAILURE;
    }
  }
}