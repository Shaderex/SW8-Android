package dk.aau.sw808f16.datacollection.webutil;

import android.content.Context;
import android.os.AsyncTask;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.RetryManager;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import dk.aau.sw808f16.datacollection.R;

public abstract class AsyncHttpWebbTask<ResultT> extends AsyncTask<Void, Void, Response<ResultT>> {

  private static TrustManagerFactory trustManagerFactory;
  private final Method method;
  private final String url;
  private final Webb webb;
  private final WeakReference<Context> context;
  private int expectedResponseCode;

  public AsyncHttpWebbTask(final Method method, final String url, final int expectedResponseCode, Context context) {
    this.url = url;
    this.webb = Webb.create();
    this.expectedResponseCode = expectedResponseCode;
    this.method = method;
    this.context = new WeakReference<Context>(context);
  }

  @Override
  protected Response<ResultT> doInBackground(Void... params) {

    try {
      final SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, getTrustManagerFactory(context).getTrustManagers(), new java.security.SecureRandom());
      webb.setSSLSocketFactory(sc.getSocketFactory());
    } catch (KeyManagementException | NoSuchAlgorithmException exception) {
      exception.printStackTrace();
    }

    webb.setRetryManager(new RetryManager());

    webb.setDefaultHeader("Accept", "application/json");

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

  public static synchronized TrustManagerFactory getTrustManagerFactory(WeakReference<Context> context) {
    if (trustManagerFactory == null) {
      try {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = new BufferedInputStream(context.get().getResources().openRawResource(R.raw.nginx));
        Certificate ca;
        try {
          ca = cf.generateCertificate(caInput);
          System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
          caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
        trustManagerFactory.init(keyStore);
      } catch (CertificateException e) {
        e.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (KeyStoreException e) {
        e.printStackTrace();
      }
    }
    return trustManagerFactory;
  }

  public enum Method {
    POST,
    GET,
    PUT,
    DELETE
  }
}
