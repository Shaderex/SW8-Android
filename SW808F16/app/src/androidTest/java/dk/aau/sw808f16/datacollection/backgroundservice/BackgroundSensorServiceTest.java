package dk.aau.sw808f16.datacollection.backgroundservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
}