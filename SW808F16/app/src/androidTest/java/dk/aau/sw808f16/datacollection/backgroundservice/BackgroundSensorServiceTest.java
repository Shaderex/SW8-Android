package dk.aau.sw808f16.datacollection.backgroundservice;

import android.app.ActivityManager;
import android.content.Context;
import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.backgroundservice.BackgroundSensorService;

public class BackgroundSensorServiceTest extends ApplicationTestCase<DataCollectionApplication> {
  public BackgroundSensorServiceTest() {
    super(DataCollectionApplication.class);
  }

  public void testServiceRunning() {
    assertTrue("The service should be running", isMyServiceRunning(BackgroundSensorService.class, getContext()));
  }

  private static boolean isMyServiceRunning(final Class<?> serviceClass, final Context context) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }
}
