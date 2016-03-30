package dk.aau.sw808f16.datacollection.WebUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class AsyncHttpTask<Params, Progress> extends AsyncTask<Params, Progress, BufferedInputStream> {

  private final URL url;
  private HttpURLConnection urlConnection = null;
  private BufferedInputStream inputStream;
  private final int expectedResponseCode;
  private int responseCode = -1;
  private final ConnectivityManager connectivityManager;
  private final CountDownLatch doInBackgroundLatch = new CountDownLatch(1);

  public AsyncHttpTask(final Context context, final URL url, final int expectedResponseCode) {

    this.url = url;
    this.expectedResponseCode = expectedResponseCode;
    connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  @SafeVarargs
  @Override
  protected final BufferedInputStream doInBackground(Params... params) {

    // Delay the request until we have an active connection if we do not have one
    if (!isNetworkAvailable()) {
      connectivityManager.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
        @Override
        public void onNetworkActive() {

          inputStream = makeRequest();
        }
      });
    }
    else {
      inputStream = makeRequest();
    }

    try {
      doInBackgroundLatch.await();
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    return inputStream;
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
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      } catch (KeyManagementException e) {
        e.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }


      urlConnection = (HttpURLConnection) url.openConnection();

      responseCode = urlConnection.getResponseCode();
      return new BufferedInputStream(urlConnection.getInputStream());

    } catch (IOException e) {
      e.printStackTrace();
    } finally {

      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      doInBackgroundLatch.countDown();
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

  protected abstract void onResponseCodeMatching(final InputStream in);

  protected abstract void onResponseCodeNotMatching(final int responseCode);
}
