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
import java.net.HttpURLConnection;
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
  private static boolean isRunning = false;
  private final Timer timer = new Timer();

  public SynchronizationTimer(final Context context, long synchronizationInterval) {
    this.context = context;
    this.synchronizationInterval = synchronizationInterval;
  }

  public synchronized void start() {
    if (!isRunning) {
      Log.d("SynchronizationTimer", "SynchronizationTimer started with " + synchronizationInterval + "ms interval between syncs");

      // Send the campaign to the server every x minutes
      timer.scheduleAtFixedRate(new SynchronizationTimerTask(), INITIAL_SYNC_DELAY, synchronizationInterval);
      isRunning = true;
    }
  }

  public synchronized void stop() {
    if (isRunning) {
      Log.d("SynchronizationTimer", "SynchronizationTimer stopped");
      timer.cancel();
      isRunning = false;
    }
  }

  private class SynchronizationTimerTask extends TimerTask {

    @Override
    public void run() {
      // Check if we have access to wifi. If not, don't try to synchronize
      final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      if (!wifi.isWifiEnabled()) {
        Log.d("SynchronizationTimer", "Unable to upload without network");
        return; // Take no further actions
      }

      final Realm realm = Realm.getDefaultInstance();
      final RealmResults<Campaign> results = realm.where(Campaign.class).findAll();

      if (results.size() == 0) {
        Log.d("SynchronizationTimer", "There are no campaigns to be uploaded");
      }

      for (int i = 0; i < results.size(); i++) {
        final Campaign campaign = results.get(i);
        final String requestUrl = RequestHostResolver.resolveHostForRequest(context,
            "/campaigns/" + campaign.getIdentifier() + "/snapshots");

        try {
          final long requestTimestamp = System.currentTimeMillis();
          final String campaignString = campaign.toJsonObject().toString();

          final int campaignIdentifer = campaign.getIdentifier();

          // Send the campaign to the server
          final AsyncHttpWebbTask<String> task = new AsyncHttpWebbTask<String>(AsyncHttpWebbTask.Method.POST, requestUrl, HttpURLConnection.HTTP_OK) {

            @Override
            protected Response<String> sendRequest(final Request webb) {

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
      realm.close();
    }
  }
}
