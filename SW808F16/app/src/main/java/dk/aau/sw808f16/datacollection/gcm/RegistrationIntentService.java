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

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.R;

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
      final InstanceID instanceID = InstanceID.getInstance(this);
      final String token = instanceID.getToken(getString(R.string.defaultSenderID),
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
      sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
      // [END register_for_gcm]
    } catch (Exception e) {
      Log.d(TAG, "Failed to complete token refresh", e);
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
    final HttpClient httpclient = createHttpClient();

    // TODO: INSERT LOGIC THAT DETERMINES NETWORK STUFF (AAU vs. PUBLIC INTERNET)

    final String wifiSSID = findWifiSSID(this);

    final String request;

    if (wifiSSID != null && wifiSSID.contains(getString(R.string.aau_wifi_ssid))) {
      request = getString(R.string.gcmRegisterBackendURL_AAU);
    } else {
      request = getString(R.string.gcmRegisterBackendURL);
    }

    final HttpPost httpPost = new HttpPost(request);

    final List<NameValuePair> urlParameters = new ArrayList<>();
    try {
      urlParameters.add(new BasicNameValuePair("deviceID", URLEncoder.encode(token, "utf-8")));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    try {
      httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    try {
      // Execute HTTP Post Request
      final HttpResponse response = httpclient.execute(httpPost);
      final StatusLine statusLine = response.getStatusLine();
      final int statusCode = statusLine.getStatusCode();

      switch (statusCode) {
        case HttpURLConnection.HTTP_OK: {
          return;
        }
        default: {
          throw new IOException("Unable post registration to server");
        }
      }

    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
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


  private static HttpClient createHttpClient() {
    final HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
    HttpProtocolParams.setUseExpectContinue(params, true);

    final SchemeRegistry schReg = new SchemeRegistry();
    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

    return new DefaultHttpClient(conMgr, params);
  }

  public static String findWifiSSID(final Context context) {
    final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    final WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
    final String name = wifiInfo.getSSID();

    return name;
  }

}
