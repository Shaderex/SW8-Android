package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.FloatTriple;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class AccelerometerSensorProvider extends SensorProvider<Sample> {

  public AccelerometerSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency) throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final Sample sensorValues = new Sample();
    final long endTime = System.currentTimeMillis() + sampleDuration;

    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    final SensorEventListener accelerometerListener = new SensorEventListener() {

      private long lastUpdateTime;

      @Override
      public void onSensorChanged(final SensorEvent event) {

        synchronized (AccelerometerSensorProvider.this) {

          final long currentTime = System.currentTimeMillis();

          final int micro_per_milli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
          if (lastUpdateTime + measurementFrequency / micro_per_milli >= currentTime) {
            return;
          }

          FloatTriple measurement = new FloatTriple(event.values[0], event.values[1], event.values[2]);
          sensorValues.addMeasurement(measurement);

          lastUpdateTime = currentTime;

          if (endTime <= currentTime) {
            latch.countDown();
          }
        }
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };

    if (!sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)) {

      sensorManager.unregisterListener(accelerometerListener);
      return null;
    }

    latch.await();

    sensorManager.unregisterListener(accelerometerListener);

    return sensorValues;
  }
}
