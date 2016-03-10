package dk.aau.sw808f16.datacollection;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import dk.aau.sw808f16.datacollection.backgroundservice.BackgroundSensorService;

public class DataCollectionApplication extends Application {

  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  private static final String TAG = "DataCollectionApp";

  @Override
  public void onCreate() {
    super.onCreate();
    startService(new Intent(this, BackgroundSensorService.class));
  }

  /**
   * Check the device to make sure it has the Google Play Services APK. If
   * it doesn't, display a dialog that allows users to download the APK from
   * the Google Play Store or enable it in the device's system settings.
   */
  private boolean checkPlayServices() {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      Toast.makeText(this,"You do not have Google Play Services properly installed", Toast.LENGTH_LONG).show();
      Log.i(TAG, "This device is not supported.");
      android.os.Process.killProcess(android.os.Process.myPid());

      return false;
    }
    return true;
  }
}
