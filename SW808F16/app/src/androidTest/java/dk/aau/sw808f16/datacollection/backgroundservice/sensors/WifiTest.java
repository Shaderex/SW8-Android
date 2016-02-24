package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.app.Application;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

@SuppressWarnings("unused")
public class WifiTest extends ApplicationTestCase<Application> {

  public WifiTest() {
    super(Application.class);
  }

  public void testWifi() {

    final WifiManager manager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    final List<ScanResult> results = manager.getScanResults();

    for (ScanResult result : results) {
      Log.i("ScanResult: ", result + " SignalLevel: " + WifiManager.calculateSignalLevel(result.level, 10));
    }
  }
}