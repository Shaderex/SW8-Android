package dk.aau.sw808f16.datacollection.webutil;

import android.content.Context;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.gcm.RegistrationIntentService;

public class RequestHostResolver {

  public static String resolveHostForRequest(final Context context, final String resourcePath) {
    final String wifiSsid = RegistrationIntentService.findWifiSsid(context);
    String requestUrl;
    if (wifiSsid != null && wifiSsid.contains(context.getString(R.string.aau_wifi_ssid))) {
      requestUrl = context.getString(R.string.backendURL_AAU);
    } else {
      requestUrl = context.getString(R.string.backendURL);
    }
    requestUrl += resourcePath;

    return requestUrl;
  }
}
