package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class AccelerometerSensorProvider extends SensorProvider<List<float[]>> {

  public AccelerometerSensorProvider(final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(sensorThreadPool, sensorManager);
  }

  private class RetrieveAccelerometerDataCallable extends RetrieveSensorDataCallable {

    public RetrieveAccelerometerDataCallable(final long duration, final int samplingPeriod) {
      super(duration, samplingPeriod);
    }

    // Listeners used when we have one measurement from each sensor
    private SensorEventListener accelerometerListener;

    @Override
    public List<float[]> call() throws Exception {

      final CountDownLatch latch = new CountDownLatch(1);

      final List<float[]> sensorValues = new ArrayList<>();

      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

      accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (AccelerometerSensorProvider.this) {

            final long currentTime = System.currentTimeMillis();

            if (lastUpdateTime + measurementFrequency / 1000 >= currentTime) {
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

      if (!sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)) {

        sensorManager.unregisterListener(accelerometerListener);
        return null;
      }

      try {
        latch.await();
      } catch (InterruptedException exception) {
        exception.printStackTrace();
      }

      sensorManager.unregisterListener(accelerometerListener);

      return sensorValues;
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(final long sampleDuration, final int measurementFrequency) {
    return new RetrieveAccelerometerDataCallable(sampleDuration, measurementFrequency);
  }
}
