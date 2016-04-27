package dk.aau.sw808f16.datacollection.backgroundservice;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.Campaign;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class SynchronizationTimer {
  private Context context;
  private static final long INITIAL_SYNC_DELAY = 1000;
  private long synchronizationInterval;

  public SynchronizationTimer(final Context context, long synchronizationInterval) {
    this.context = context;
    this.synchronizationInterval = synchronizationInterval;
  }

  public void start() {
    // Send the campaign to the server every x minutes
    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        // Check if we have access to wifi. If not, don't try to synchronize
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
          Log.d("CampaignSyncLog", "Unable to upload without network");
          return; // Take no further actions
        }
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<Campaign> results = realm.where(Campaign.class).findAll();


        if (results.size() == 0) {
          Log.d("CampaignSyncLog", "There are no campaigns to be uploaded");
        }

        for (int i = 0; i < results.size(); i++) {
          final Campaign campaign = results.get(i);
          final String requestUrl = RequestHostResolver.resolveHostForRequest(context,
              "/campaigns/" + campaign.getIdentifier() + "/snapshots");

          try {
            final String campaignString = campaign.toJsonObject().toString();
            final int campaignIdentifer = campaign.getIdentifier();

            // Send the campaign to the server
            final AsyncHttpWebbTask<String> task = new AsyncHttpWebbTask<String>(AsyncHttpWebbTask.Method.POST, requestUrl, 200) {
              @Override
              protected Response<String> sendRequest(Request webb) {
                try {
                  final InstanceID instanceId = InstanceID.getInstance(context);
                  final String token = instanceId.getToken(
                      context.getString(R.string.defaultSenderID),
                      GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                      null
                  );
                  final Response<String> jsonString =
                      webb.param("snapshots", campaignString)
                          .param("device_id", token)
                          .asString();

                  Log.d("Service-status", campaignString);
                  return jsonString;
                } catch (IOException e) {
                  e.printStackTrace();
                  return null;
                }
              }

              @Override
              public void onResponseCodeMatching(Response<String> response) {
                final Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                RealmResults<Campaign> campaigns = realm.where(Campaign.class).equalTo("identifier", campaignIdentifer).findAll();

                Campaign campaign = campaigns.get(0);

                for (Snapshot snapshot : campaign.getSnapshots()) {
                  for (RealmObject obj : snapshot.children()) {
                    obj.removeFromRealm();
                  }
                }

                realm.commitTransaction();
                realm.close();

                Log.d("CampaignSyncLog", "All campaigns were uploaded");
              }

              @Override
              public void onResponseCodeNotMatching(Response<String> response) {
              }

              @Override
              public void onConnectionFailure() {
              }
            };
            task.execute();

          } catch (JSONException exception) {
            exception.printStackTrace();
          }
        }
        realm.close();
      }
    }, INITIAL_SYNC_DELAY, synchronizationInterval);
  }
}
