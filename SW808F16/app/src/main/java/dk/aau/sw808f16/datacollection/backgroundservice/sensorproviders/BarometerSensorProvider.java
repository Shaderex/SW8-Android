package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;

public class BarometerSensorProvider extends SensorProvider {

  public BarometerSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency) throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final List<FloatMeasurement> sensorValues = new ArrayList<>();
    final long endTime = System.currentTimeMillis() + sampleDuration;

    final Sensor barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

    // Listeners used when we have one measurement from each sensor
    final SensorEventListener barometerEventListener = new SensorEventListener() {

      private long lastUpdateTime;

      @Override
      public void onSensorChanged(final SensorEvent event) {
        final long currentTime = System.currentTimeMillis();

        if (lastUpdateTime + measurementFrequency >= currentTime) {
          return;
        }

        sensorValues.add(new FloatMeasurement(event.values[0]));

        lastUpdateTime = currentTime;

        if (endTime <= currentTime) {
          latch.countDown();
        }
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };

    if (!sensorManager.registerListener(barometerEventListener, barometerSensor, SensorManager.SENSOR_DELAY_FASTEST)) {

      sensorManager.unregisterListener(barometerEventListener);
      return null;
    }

    latch.await();

    sensorManager.unregisterListener(barometerEventListener);

    return new Sample(sensorValues);
  }

  @Override
  public boolean isSensorAvailable() {
    return context.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
  }
}
