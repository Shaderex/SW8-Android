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

public class GyroscopeSensorProvider extends SensorProvider<List<float[]>> {

  public GyroscopeSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected List<float[]> retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency) throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final List<float[]> sensorValues = new ArrayList<>();
    final long endTime = System.currentTimeMillis() + sampleDuration;

    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    // Listeners used when we have one measurement from each sensor
    final SensorEventListener gyroscopeListener = new SensorEventListener() {

      private long lastUpdateTime;

      @Override
      public void onSensorChanged(final SensorEvent event) {
          final long currentTime = System.currentTimeMillis();
          final int micro_per_milli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);

          if (lastUpdateTime + measurementFrequency / micro_per_milli >= currentTime) {
            return;
          }

          sensorValues.add(event.values);

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

    return sensorValues;
  }
}
