package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class AmbientLightSensorProvider extends SensorProvider {
  public AmbientLightSensorProvider(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency) throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final List<Float> sensorValues = new ArrayList<>();
    final long endTime = System.currentTimeMillis() + sampleDuration;

    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    final SensorEventListener accelerometerListener = new SensorEventListener() {
      private long lastUpdateTime;

      @Override
      public void onSensorChanged(final SensorEvent event) {
        final long currentTime = System.currentTimeMillis();

        final int micro_per_milli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
        if (lastUpdateTime + measurementFrequency / micro_per_milli >= currentTime) {
          return;
        }

        sensorValues.add(event.values[0]);

        lastUpdateTime = currentTime;

        if (endTime <= currentTime) {
          latch.countDown();
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

    return new Sample(sensorValues);
  }
}
