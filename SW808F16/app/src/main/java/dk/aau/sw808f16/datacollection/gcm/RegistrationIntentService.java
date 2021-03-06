package dk.aau.sw808f16.datacollection.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


import java.io.IOException;
import java.net.HttpURLConnection;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;

public class RegistrationIntentService extends IntentService {
  private static final String TAG = "RegIntentService";
  private static final String[] TOPICS = {"global"};

  public RegistrationIntentService() {
    super(TAG);
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  protected void onHandleIntent(final Intent intent) {
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    try {
      // [START register_for_gcm]
      // Initially this call goes out to the network to retrieve the token, subsequent calls
      // are local.
      // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
      // See https://developers.google.com/cloud-messaging/android/start for details on this file.
      // [START get_token]
      final InstanceID instanceIdentifier = InstanceID.getInstance(this);
      final String token = instanceIdentifier.getToken(getString(R.string.defaultSenderID),
          GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      // [END get_token]
      Log.i(TAG, "GCM Registration Token: " + token);

      // TODO: Implement this method to send any registration to your app's servers.
      sendRegistrationToServer(token);

      // Subscribe to topic channels
      subscribeTopics(token);

      // You should store a boolean that indicates whether the generated token has been
      // sent to your server. If the boolean is false, send the token to your server,
      // otherwise your server should have already received the token.
      // [END register_for_gcm]
      sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
    } catch (Exception exception) {
      Log.d(TAG, "Failed to complete token refresh", exception);
      // If an exception happens while fetching the new token or updating our registration data
      // on a third-party server, this ensures that we'll attempt the update at a later time.
      sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
    }
    // Notify UI that registration has completed, so the progress indicator can be hidden.
    final Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
  }

  /**
   * Persist registration to third-party servers.
   * <p/>
   * Modify this method to associate the user's GCM registration token with any server-side account
   * maintained by your application.
   *
   * @param token The new token.
   */
  private void sendRegistrationToServer(final String token) {

    final String request = RequestHostResolver.resolveHostForRequest(this, "/gcm/registerDevice");

    final AsyncHttpWebbTask<String> task = new AsyncHttpWebbTask<String>(AsyncHttpWebbTask.Method.POST,
        request,
        HttpURLConnection.HTTP_OK,
        this) {
      @Override
      protected Response<String> sendRequest(final Request webb) {

        final String deviceIdentifier = token;
        final Response<String> deviceIdentifierString = webb.param("device_id", deviceIdentifier).asString();
        return deviceIdentifierString;

      }

      @Override
      public void onResponseCodeMatching(final Response<String> response) {
        Log.d("Register-Device", "onResponseCodeMatching");
      }

      @Override
      public void onResponseCodeNotMatching(final Response<String> response) {
        Log.d("Register-Device", "onResponseCodeNotMatching: " + response.getResponseMessage());
      }

      @Override
      public void onConnectionFailure() {
        Log.d("Register-Device", "onConnectionFailure");
      }
    };
    task.execute();
  }

  /**
   * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
   *
   * @param token GCM token
   * @throws IOException if unable to reach the GCM PubSub service
   */
  // [START subscribe_topics]
  private void subscribeTopics(final String token) throws IOException {
    GcmPubSub pubSub = GcmPubSub.getInstance(this);
    for (String topic : TOPICS) {
      pubSub.subscribe(token, "/topics/" + topic, null);
    }
  }
  // [END subscribe_topics]




  public static String findWifiSsid(final Context context) {
    final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    final WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

    return wifiInfo.getSSID();
  }

}
