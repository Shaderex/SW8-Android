package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class WifiSensorProvider extends SensorProvider<List<List<ScanResult>>> {

  private final Context context;

  public WifiSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    this.context = context;
  }

  @Override
  protected List<List<ScanResult>> retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency) throws InterruptedException {

    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    final List<ScanResult> scanResults = wifiManager.getScanResults();

    return new ArrayList<List<ScanResult>>() {
      {
        add(scanResults);
      }
    };
  }
}
