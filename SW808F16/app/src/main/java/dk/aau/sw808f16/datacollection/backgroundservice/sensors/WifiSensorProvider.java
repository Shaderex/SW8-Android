package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.CellInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class WifiSensorProvider extends SensorProvider<List<List<ScanResult>>> {

  private final Context context;

  public WifiSensorProvider(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    this.context = context;
  }

  private class RetrieveWiFiDataCallable extends RetrieveSensorDataCallable {

    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    final List<ScanResult> scanResults = wifiManager.getScanResults();

    public RetrieveWiFiDataCallable(final long sampleDuration, final int measurementFrequency) {
      super(sampleDuration, measurementFrequency);
    }

    @Override
    public List<List<ScanResult>> call() throws Exception {
      return new ArrayList<List<ScanResult>>() {
        {
          add(scanResults);
        }
      };
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(long sampleDuration, int measurementFrequency) {
    return new RetrieveWiFiDataCallable(sampleDuration, measurementFrequency);
  }


}
