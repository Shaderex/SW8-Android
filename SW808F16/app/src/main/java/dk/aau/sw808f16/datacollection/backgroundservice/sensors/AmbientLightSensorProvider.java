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

public class AmbientLightSensorProvider extends SensorProvider<List<Float>> {
  public AmbientLightSensorProvider(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  private class RetrieveAmbientLightDataCallable extends RetrieveSensorDataCallable {

    public RetrieveAmbientLightDataCallable(final long duration, final int measurementFrequency) {
      super(duration, measurementFrequency);
    }

    // Listeners used when we have one measurement from each sensor
    private SensorEventListener accelerometerListener;

    @Override
    public List<Float> call() throws InterruptedException {

      final CountDownLatch latch = new CountDownLatch(1);

      final List<Float> sensorValues = new ArrayList<>();

      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

      accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (AmbientLightSensorProvider.this) {

            final long currentTime = System.currentTimeMillis();

            final int micro_per_milli =  context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
            if (lastUpdateTime + measurementFrequency / micro_per_milli >= currentTime) {
              return;
            }

            sensorValues.add(event.values[0]);

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
  protected RetrieveSensorDataCallable createCallable(long sampleDuration, int measurementFrequency) {
    return new RetrieveAmbientLightDataCallable(sampleDuration,measurementFrequency);
  }
}
