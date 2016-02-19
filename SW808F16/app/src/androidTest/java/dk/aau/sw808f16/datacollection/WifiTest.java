package dk.aau.sw808f16.datacollection;

import android.app.Application;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.junit.Test;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class WifiTest extends ApplicationTestCase<Application> {

  public WifiTest() {
    super(Application.class);
  }

  @Test
  public void testWifi() {

    final WifiManager manager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    final List<ScanResult> results = manager.getScanResults();

    for (ScanResult result : results) {
      Log.i("ScanResult: ", result.toString() + " SignalLevel: " + WifiManager.calculateSignalLevel(result.level, 10));
    }
  }
}