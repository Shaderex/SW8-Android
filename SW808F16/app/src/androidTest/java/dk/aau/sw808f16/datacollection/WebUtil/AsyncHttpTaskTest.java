//package dk.aau.sw808f16.datacollection.WebUtil;
//
//import android.content.Context;
//import android.net.wifi.WifiManager;
//import android.test.ApplicationTestCase;
//import android.util.Log;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutionException;
//
//import dk.aau.sw808f16.datacollection.DataCollectionApplication;
//
//public class AsyncHttpTaskTest extends ApplicationTestCase<DataCollectionApplication> {
//  public AsyncHttpTaskTest() {
//    super(DataCollectionApplication.class);
//  }
//
//  public void testCanRequestGoogle() throws MalformedURLException, InterruptedException, ExecutionException {
//
//    final AsyncHttpTask task = new AsyncHttpTask(getContext(), new URL("http://google.com"), 200) {
//      @Override
//      protected void onResponseCodeMatching(final InputStream in) {
//        assertTrue(in != null);
//      }
//
//      @Override
//      protected void onResponseCodeNotMatching(final Integer responseCode) {
//        fail();
//      }
//    };
//
//    task.execute(null, null).get();
//  }
//
//  public void testCanRequestCampaigns() throws MalformedURLException, InterruptedException, ExecutionException {
//
//    final CountDownLatch latch = new CountDownLatch(1);
//    final int expectedResponseCode = HttpURLConnection.HTTP_OK;
//
//    final AsyncHttpTask task = new AsyncHttpTask(getContext(), new URL("https://dev.local.element67.dk:8000/campaigns"), expectedResponseCode) {
//      @Override
//      protected void onResponseCodeMatching(final InputStream in) {
//        Log.e("DEBUG", "response code matching");
//        assertTrue(in != null);
//        latch.countDown();
//      }
//
//      @Override
//      protected void onResponseCodeNotMatching(final Integer responseCode) {
//        Log.e("DEBUG", "response code not matching, response code was: " + responseCode);
//        assertTrue(expectedResponseCode == responseCode);
//        latch.countDown();
//      }
//    };
//
//    task.execute(null, null);
//
//    latch.await();
//  }
//
//  public void testCanRequestCampaignsWithConnectionDrop() throws MalformedURLException, InterruptedException, ExecutionException {
//
//    final CountDownLatch latch = new CountDownLatch(1);
//
//    final WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
//
//    wifiManager.setWifiEnabled(false);
//
//    Thread.sleep(1000);
//
//    final AsyncHttpTask task = new AsyncHttpTask(getContext(), new URL("https://dev.local.element67.dk:8000/campaigns"), 200) {
//      @Override
//      protected void onResponseCodeMatching(final InputStream in) {
//        Log.e("DEBUG", "response code matching");
//        latch.countDown();
//        assertTrue(in != null);
//      }
//
//      @Override
//      protected void onResponseCodeNotMatching(final Integer responseCode) {
//        Log.e("DEBUG", "response code not matching, response code was: " + responseCode);
//        latch.countDown();
//        fail();
//      }
//    };
//
//    task.execute();
//
//    Thread.sleep(1000);
//
//    wifiManager.setWifiEnabled(true);
//
//    latch.await();
//  }
//
//  @Override
//  protected void tearDown() throws Exception {
//
//    final WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
//    wifiManager.setWifiEnabled(true);
//
//    super.tearDown();
//  }
//}