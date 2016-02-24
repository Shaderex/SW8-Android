package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

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

  public GyroscopeSensorProvider(final Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  private class RetrieveGyroscopeDataCallable extends RetrieveSensorDataCallable {

    public RetrieveGyroscopeDataCallable(final long sampleDuration, final int measurementFrequency) {
      super(sampleDuration, measurementFrequency);
    }

    // Listeners used when we have one measurement from each sensor
    private SensorEventListener gyroscopeListener;

    private final float[] values = new float[3];

    @Override
    public List<float[]> call() throws Exception {

      final CountDownLatch latch = new CountDownLatch(1);

      final List<float[]> sensorValues = new ArrayList<>();

      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

      gyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (GyroscopeSensorProvider.this) {

            final long currentTime = System.currentTimeMillis();
            final int micro_per_milli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
            if (lastUpdateTime + measurementFrequency / micro_per_milli  >= currentTime) {
              return;
            }

            sensorValues.add(event.values);

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

  @Override
  protected RetrieveSensorDataCallable createCallable(long sampleDuration, int measurementFrequency) {
    return new RetrieveGyroscopeDataCallable(sampleDuration, measurementFrequency);
  }
}
