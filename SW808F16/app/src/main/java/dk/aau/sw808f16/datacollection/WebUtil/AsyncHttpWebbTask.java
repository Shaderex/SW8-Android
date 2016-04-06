package dk.aau.sw808f16.datacollection.WebUtil;

import android.os.AsyncTask;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.RetryManager;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class AsyncHttpWebbTask<Result> extends AsyncTask<Void, Void, Response<Result>> {

  private final Method method;
  private final String url;
  private Webb webb;
  private int expectedResponseCode;

  public AsyncHttpWebbTask(Method method, String url, int expectedResponseCode) {
    this.url = url;
    this.webb = Webb.create();
    this.expectedResponseCode = expectedResponseCode;
    this.method = method;
  }

  @Override
  protected Response<Result> doInBackground(Void... params) {

    final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
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
    }
    };

    try {
      final SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      webb.setSSLSocketFactory(sc.getSocketFactory());
    } catch (KeyManagementException | NoSuchAlgorithmException exception) {
      exception.printStackTrace();
    }

    webb.setRetryManager(new RetryManager());

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
  protected final void onPostExecute(Response<Result> response) {

    if (response == null) {
      onConnectionFailure();
    } else if (response.getStatusCode() == this.expectedResponseCode) {
      onResponseCodeMatching(response);
    } else {
      onResponseCodeNotMatching(response);
    }

    super.onPostExecute(response);
  }

  protected abstract Response<Result> sendRequest(Request webb);

  public abstract void onResponseCodeMatching(Response<Result> response);

  public abstract void onResponseCodeNotMatching(Response<Result> response);

  public abstract void onConnectionFailure();

  public enum Method {
    POST,
    GET,
    PUT,
    DELETE
  }
}
