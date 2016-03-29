package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class CellularNetworkSensorProvider extends SensorProvider {

  private final Timer cellNetworkMeasurementTimer;

  public CellularNetworkSensorProvider(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    cellNetworkMeasurementTimer = new Timer(true);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency)
      throws InterruptedException {

    final long endTime = System.currentTimeMillis() + sampleDuration;
    final CountDownLatch latch = new CountDownLatch(1);
    final TelephonyManager telephonyManager = (TelephonyManager) context.get().getSystemService(Context.TELEPHONY_SERVICE);
    final List<List<CellInfo>> cellInfoListMeasurements = new ArrayList<>();

    final TimerTask cellNetworkMeasurementTask = new TimerTask() {
      @Override
      public void run() {
        if (System.currentTimeMillis() > endTime) {
          this.cancel();
          latch.countDown();
          return;
        }

        // Do the measurements
        cellInfoListMeasurements.add(telephonyManager.getAllCellInfo());
      }
    };

    cellNetworkMeasurementTimer.scheduleAtFixedRate(cellNetworkMeasurementTask, 0, measurementFrequency);

    latch.await();

    return new Sample(SensorType.CELLULAR, cellInfoListMeasurements);
  }

  @Override
  public boolean isSensorAvailable() {
    return ((TelephonyManager) context.get().getSystemService(Context.TELEPHONY_SERVICE)).getSimState() == TelephonyManager.SIM_STATE_READY;
  }
}
