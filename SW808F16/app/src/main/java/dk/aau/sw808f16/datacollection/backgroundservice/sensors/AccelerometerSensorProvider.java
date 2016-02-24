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

public class AccelerometerSensorProvider extends SensorProvider<List<float[]>> {

  public AccelerometerSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  private class RetrieveAccelerometerDataCallable extends RetrieveSensorDataCallable {

    public RetrieveAccelerometerDataCallable(final long sampleDuration, final int measurementFrequency) {
      super(sampleDuration, measurementFrequency);
    }

    // Listeners used when we have one measurement from each sensor
    private SensorEventListener accelerometerListener;

    @Override
    public List<float[]> call() throws InterruptedException {

      final CountDownLatch latch = new CountDownLatch(1);

      final List<float[]> sensorValues = new ArrayList<>();

      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

      accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (AccelerometerSensorProvider.this) {

            final long currentTime = System.currentTimeMillis();

            final int micro_per_milli =  context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
            if (lastUpdateTime + measurementFrequency / micro_per_milli >= currentTime) {
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

      latch.await();

      sensorManager.unregisterListener(accelerometerListener);

      return sensorValues;
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(final long sampleDuration, final int measurementFrequency) {
    return new RetrieveAccelerometerDataCallable(sampleDuration, measurementFrequency);
  }
}
