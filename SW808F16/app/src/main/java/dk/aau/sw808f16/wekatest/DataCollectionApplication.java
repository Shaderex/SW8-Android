package dk.aau.sw808f16.wekatest;

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

public class DataCollectionApplication extends Application {

  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  public static final String TAG = "DataCollectionApp";
  private boolean isReceiverRegistered = false;

  private BroadcastReceiver registrationBroadcastReceiver;

  @Override
  public void onCreate() {
    super.onCreate();




  }




}
