package dk.aau.sw808f16.datacollection.backgroundservice;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
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
      Log.d("SynchronizationTimer", "SynchronizationTimer started with " + synchronizationInterval + "s interval between syncs");

      final PeriodicTask myTask = new PeriodicTask.Builder()
          .setService(UploadManagementService.class)
          .setPeriod(synchronizationInterval)
          .setFlex(synchronizationInterval)
          .setUpdateCurrent(true)
          .setRequiredNetwork(Task.NETWORK_STATE_UNMETERED)
          .setTag(UploadManagementService.CAMPAIGN_SYNCHRONIZATION_TAG)
          .build();

      GcmNetworkManager.getInstance(context).schedule(myTask);

      isRunning = true;
    }
  }

  public synchronized void stop() {
    if (isRunning) {

      GcmNetworkManager.getInstance(context).cancelTask(
          UploadManagementService.CAMPAIGN_SYNCHRONIZATION_TAG,
          UploadManagementService.class
      );

      isRunning = false;
    }
  }

}
