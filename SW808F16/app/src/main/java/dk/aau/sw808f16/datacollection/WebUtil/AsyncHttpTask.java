package dk.aau.sw808f16.datacollection.WebUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class AsyncHttpTask extends AsyncTask<Void, Void, BufferedInputStream> {

  private final URL url;
  private HttpURLConnection urlConnection = null;
  private BufferedInputStream inputStream;
  private final int expectedResponseCode;
  private int responseCode = -1;
  private final ConnectivityManager connectivityManager;
  private Bundle requestProperties = null;
  private int retries = 3;

  public AsyncHttpTask(final Context context, final URL url, final int expectedResponseCode) {

    this.url = url;
    this.expectedResponseCode = expectedResponseCode;
    connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  public AsyncHttpTask(final Context context, final URL url, final int expectedResponseCode, final Bundle requestProperties) {
    this(context, url, expectedResponseCode);
    this.requestProperties = requestProperties;
  }

  public AsyncHttpTask(final Context context, final URL url, final int expectedResponseCode, final Bundle requestProperties, final int retries) {
    this(context, url, expectedResponseCode, requestProperties);
    this.retries = retries;
  }

  @SafeVarargs
  @Override
  protected final BufferedInputStream doInBackground(Void... params) {

    for (; retries > 0; retries--) {

      // Delay the request until we have an active connection if we do not have one
      if (!isNetworkAvailable()) {

        final CountDownLatch requestExpirationLatch = new CountDownLatch(1);

        final ConnectivityManager.OnNetworkActiveListener networkActiveListener = new ConnectivityManager.OnNetworkActiveListener() {

          @Override
          public void onNetworkActive() {

            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

            inputStream = makeRequest();
            requestExpirationLatch.countDown();
          }
        };

        connectivityManager.addDefaultNetworkActiveListener(networkActiveListener);

        try {

          final boolean result = requestExpirationLatch.await(15, TimeUnit.SECONDS);

          if (result) {
            return inputStream;
          }
          else
          {
            continue;
          }

        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          connectivityManager.removeDefaultNetworkActiveListener(networkActiveListener);
        }


      }
      else {
        inputStream = makeRequest();
        return inputStream;
      }
    }

    return null;
  }

  private BufferedInputStream makeRequest() {


    try {
      // TODO: Remove this "Accept all certificate code" once the server gets a https certificate
      TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
          // Not implemented
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
          // Not implemented
        }
      }};

      try {
        final SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      } catch (KeyManagementException e) {
        e.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }

      urlConnection = (HttpURLConnection) url.openConnection();

      if (requestProperties != null) {
        for (String key : requestProperties.keySet()) {
          urlConnection.setRequestProperty(key, requestProperties.getString(key));
        }
      }

      responseCode = urlConnection.getResponseCode();
      return new BufferedInputStream(urlConnection.getInputStream());

    } catch (IOException e) {
      e.printStackTrace();


    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }

    return null;
  }

  @Override
  protected final void onPostExecute(final BufferedInputStream inputStream) {
    super.onPostExecute(inputStream);

    if (responseCode == expectedResponseCode) {
      onResponseCodeMatching(inputStream);
    }
    else {
      onResponseCodeNotMatching(responseCode);
    }
  }

  private boolean isNetworkAvailable() {
    final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  public void execute() {
    this.execute(null, null);
  }

  protected abstract void onResponseCodeMatching(final InputStream in);

  protected abstract void onResponseCodeNotMatching(final Integer responseCode);
}
