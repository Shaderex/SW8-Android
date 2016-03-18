package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.measurement.GsonMeasurement;
import io.realm.RealmList;
import io.realm.RealmObject;

public class WifiSensorProvider extends SensorProvider {

  private final Timer wifiMeasurementTimer;

  public WifiSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    wifiMeasurementTimer = new Timer(true);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency)
      throws InterruptedException {


    final long endTime = System.currentTimeMillis() + sampleDuration;
    final CountDownLatch latch = new CountDownLatch(1);
    final WifiManager wifiManager = (WifiManager) context.get().getSystemService(Context.WIFI_SERVICE);
    final RealmList<RealmObject> scanResultListMeasurements = new RealmList<>();

    final TimerTask cellNetworkMeasurementTask = new TimerTask() {
      @Override
      public void run() {
        if (System.currentTimeMillis() > endTime) {
          this.cancel();
          latch.countDown();
          return;
        }

        // Do the measurements
        scanResultListMeasurements.add(new GsonMeasurement<>(wifiManager.getScanResults()));
      }
    };

    wifiMeasurementTimer.scheduleAtFixedRate(cellNetworkMeasurementTask, 0, measurementFrequency);

    latch.await();

    return new Sample(scanResultListMeasurements);
  }

  @Override
  public boolean isSensorAvailable() {
    return ((WifiManager) context.get().getSystemService(Context.WIFI_SERVICE)).getWifiState() == WifiManager.WIFI_STATE_ENABLED;
  }
}
