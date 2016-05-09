package dk.aau.sw808f16.datacollection.webutil;

import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.RetryManager;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class AsyncHttpWebbTask<ResultT> extends AsyncTask<Void, Void, Response<ResultT>> {

  private final Method method;
  private final String url;
  private final Webb webb;
  private int expectedResponseCode;

  public AsyncHttpWebbTask(final Method method, final String url, final int expectedResponseCode) {
    this.url = url;
    this.webb = Webb.create();
    this.expectedResponseCode = expectedResponseCode;
    this.method = method;
  }

  @Override
  protected Response<ResultT> doInBackground(Void... params) {

    final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {

      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
        // Not implemented
      }

      @Override
      public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
        // Not implemented
      }
    }
    };

    try {
      final SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      webb.setSSLSocketFactory(sc.getSocketFactory());
    } catch (KeyManagementException | NoSuchAlgorithmException exception) {
      exception.printStackTrace();
    }

    webb.setHostnameVerifier(new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        Log.e("HOSTNAME", hostname);


        String[] domains = {
            "dev.local.element67.dk",
            "dev.global.element67.dk",
            "prod.local.element67.dk",
            "prod.global.element67.dk"
        };

        for (String domain : domains) {
          if (domain.equals(hostname)) {
            return true;
          }
        }

        return false;
      }
    });

    webb.setRetryManager(new RetryManager());

    webb.setDefaultHeader("X-Requested-With", "XMLHttpRequest");

    if (isCancelled()) {
      return null;
    }

    try {
      switch (method) {
        case POST:
          return sendRequest(webb.post(url));
        case GET:
          return sendRequest(webb.get(url));
        case PUT:
          return sendRequest(webb.put(url));
        case DELETE:
          return sendRequest(webb.delete(url));
        default:
          return null;
      }
    } catch (WebbException exception) {
      exception.printStackTrace();
    }
    return null;
  }

  @Override
  protected final void onPostExecute(final Response<ResultT> response) {

    if (response == null) {
      onConnectionFailure();
    } else if (response.getStatusCode() == this.expectedResponseCode) {
      onResponseCodeMatching(response);
    } else {
      onResponseCodeNotMatching(response);
    }

    super.onPostExecute(response);
  }

  protected abstract Response<ResultT> sendRequest(Request webb);

  public abstract void onResponseCodeMatching(Response<ResultT> response);

  public abstract void onResponseCodeNotMatching(Response<ResultT> response);

  public abstract void onConnectionFailure();

  public enum Method {
    POST,
    GET,
    PUT,
    DELETE
  }
}
