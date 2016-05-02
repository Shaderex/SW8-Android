package dk.aau.sw808f16.datacollection;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import dk.aau.sw808f16.datacollection.backgroundservice.BackgroundSensorService;
import dk.aau.sw808f16.datacollection.gcm.QuickstartPreferences;
import dk.aau.sw808f16.datacollection.gcm.RegistrationIntentService;

public class DataCollectionApplication extends Application {

  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  public static final String TAG = "DataCollectionApp";
  private boolean isReceiverRegistered = false;

  private BroadcastReceiver registrationBroadcastReceiver;

  @Override
  public void onCreate() {
    super.onCreate();

    startService(new Intent(this, BackgroundSensorService.class));

    registrationBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(final Context context, final Intent intent) {
        final SharedPreferences sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context);
        final boolean sentToken = sharedPreferences
            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

        if (sentToken) {
          Toast.makeText(DataCollectionApplication.this, R.string.gcm_register_success, Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(DataCollectionApplication.this, R.string.gcm_register_failed, Toast.LENGTH_LONG).show();
        }

        LocalBroadcastManager.getInstance(DataCollectionApplication.this).unregisterReceiver(registrationBroadcastReceiver);
        isReceiverRegistered = false;
      }
    };

    // Registering BroadcastReceiver
    registerReceiver();

    if (checkPlayServices()) {
      // Start IntentService to register this application with GCM.
      final Intent intent = new Intent(this, RegistrationIntentService.class);
      startService(intent);
    }
  }

  private void registerReceiver() {
    if (!isReceiverRegistered) {
      LocalBroadcastManager.getInstance(this).registerReceiver(registrationBroadcastReceiver,
          new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
      isReceiverRegistered = true;
    }
  }

  /**
   * Check the device to make sure it has the Google Play Services APK. If
   * it doesn't, display a dialog that allows users to download the APK from
   * the Google Play Store or enable it in the device's system settings.
   */
  private boolean checkPlayServices() {

    final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

    final int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

    if (resultCode != ConnectionResult.SUCCESS) {

      Toast.makeText(this, "You do not have Google Play Services properly installed", Toast.LENGTH_LONG).show();
      Log.i(TAG, "This device is not supported.");
      android.os.Process.killProcess(android.os.Process.myPid());

      return false;
    }

    return true;
  }
}
