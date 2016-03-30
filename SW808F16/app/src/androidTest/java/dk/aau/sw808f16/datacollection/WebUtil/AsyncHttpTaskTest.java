package dk.aau.sw808f16.datacollection.WebUtil;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;

public class AsyncHttpTaskTest extends ApplicationTestCase<DataCollectionApplication> {
  public AsyncHttpTaskTest() {
    super(DataCollectionApplication.class);
  }

  public void testCanRequestGoogle() throws MalformedURLException, InterruptedException, ExecutionException {

    final AsyncHttpTask<Void, Void> task = new AsyncHttpTask<Void, Void>(getContext(), new URL("http://google.com"), 200) {
      @Override
      protected void onResponseCodeMatching(final InputStream in) {
        assertTrue(in != null);
      }

      @Override
      protected void onResponseCodeNotMatching(final int responseCode) {
        fail();
      }
    };

    task.execute(null, null).get();
  }

  public void testCanRequestCampaigns() throws MalformedURLException, InterruptedException, ExecutionException {

    final AsyncHttpTask<Void, Void> task = new AsyncHttpTask<Void, Void>(getContext(), new URL("https://dev.local.element67.dk:8000/campaigns"), 200) {
      @Override
      protected void onResponseCodeMatching(final InputStream in) {
        Log.e("DEBUG", "response code matching");
        assertTrue(in != null);
      }

      @Override
      protected void onResponseCodeNotMatching(final int responseCode) {
        Log.e("DEBUG", "response code not matching, response code was: " + responseCode);
        fail();
      }
    };

    task.execute(null, null);

    task.get();
  }

  public void testCanRequestCampaignsWithConnectionDrop() throws MalformedURLException, InterruptedException, ExecutionException {

    WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

    wifiManager.setWifiEnabled(false);

    Thread.sleep(1000);

    final AsyncHttpTask<Void, Void> task = new AsyncHttpTask<Void, Void>(getContext(), new URL("http://dev.local.element67.dk:8000/campaigns"), 200) {
      @Override
      protected void onResponseCodeMatching(final InputStream in) {
        Log.e("DEBUG", "response code matching");
        assertTrue(in != null);
      }

      @Override
      protected void onResponseCodeNotMatching(final int responseCode) {
        Log.e("DEBUG", "response code not matching, response code was: " + responseCode);
        fail();
      }
    };

    task.execute(null, null);

    Thread.sleep(1000);

    wifiManager.setWifiEnabled(true);

    task.get();
  }

}