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

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.FloatTriple;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class GyroscopeSensorProvider extends SensorProvider {

  public GyroscopeSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency) throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final List<FloatTriple> sensorValues = new ArrayList<>();
    final long endTime = System.currentTimeMillis() + sampleDuration;

    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    // Listeners used when we have one measurement from each sensor
    final SensorEventListener gyroscopeListener = new SensorEventListener() {

      private long lastUpdateTime;

      @Override
      public void onSensorChanged(final SensorEvent event) {
        final long currentTime = System.currentTimeMillis();

        if (lastUpdateTime + measurementFrequency >= currentTime) {
          return;
        }

        FloatTriple triple = new FloatTriple(event.values);

        sensorValues.add(triple);

        lastUpdateTime = currentTime;

        if (endTime <= currentTime) {
          latch.countDown();
        }
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };

    if (!sensorManager.registerListener(gyroscopeListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)) {

      sensorManager.unregisterListener(gyroscopeListener);
      return null;
    }

    try {
      latch.await();
    } catch (InterruptedException exception) {
      exception.printStackTrace();
    }

    sensorManager.unregisterListener(gyroscopeListener);

    return new Sample(SensorType.GYROSCOPE, sensorValues);
  }

  @Override
  public boolean isSensorAvailable() {
    return context.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
  }
}
