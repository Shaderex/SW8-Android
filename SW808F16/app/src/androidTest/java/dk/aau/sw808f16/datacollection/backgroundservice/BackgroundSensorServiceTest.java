package dk.aau.sw808f16.datacollection.backgroundservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.test.ApplicationTestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;

public class BackgroundSensorServiceTest extends ApplicationTestCase<DataCollectionApplication> {
  public BackgroundSensorServiceTest() {
    super(DataCollectionApplication.class);
  }

  public void testServiceRunning() {

    final Intent backgroundServiceIntent = new Intent(getContext(), BackgroundSensorService.class);

    // Start the BackgroundSensorService
    getContext().startService(backgroundServiceIntent);

    boolean isRunning = false;

    // Get the activity manager
    final ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);

    // Run through all running services and check if one of them is the BackgroundSensorService
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (BackgroundSensorService.class.getName().equals(service.service.getClassName())) {
        isRunning = true;
        break;
      }
    }

    getContext().stopService(backgroundServiceIntent);
    assertTrue("The service should be running", isRunning);
  }

  // Before running this test, ensure that you have had launched the application previously
  public void testSnapshotInSharedPreference() throws InterruptedException {

    Thread.sleep(13000);

    final SharedPreferences preferences =
        getContext().getSharedPreferences(BackgroundSensorService.SNAPSHOT_SHARED_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);

    final String serializedSnapshot = preferences.getString(BackgroundSensorService.SNAPSHOT_SHARED_PREFERENCE_KEY, null);

    assertNotNull("Serialized snapshot string was null", serializedSnapshot);

    final Gson gson = new GsonBuilder().create();
    final Snapshot snapshot = gson.fromJson(serializedSnapshot, Snapshot.class);

    assertFalse(snapshot.getSamples(SensorType.ACCELEROMETER).isEmpty());
    assertFalse(snapshot.getSamples(SensorType.AMBIENT_LIGHT).isEmpty());
    assertFalse(snapshot.getSamples(SensorType.PROXIMITY).isEmpty());
  }
}
