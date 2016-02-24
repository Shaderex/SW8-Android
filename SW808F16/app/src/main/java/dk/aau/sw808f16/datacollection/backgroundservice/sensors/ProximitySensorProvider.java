package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.R;

public class ProximitySensorProvider extends SensorProvider<List<Float>> {

  private final Timer proximitySamplingTimer;

  public ProximitySensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    proximitySamplingTimer = new Timer(true);
  }

  private class RetrieveProximityDataCallable extends RetrieveSensorDataCallable {

    public RetrieveProximityDataCallable(final long sampleDuration, final int measurementFrequency) {
      super(sampleDuration, measurementFrequency);
    }

    private float[] proximitySensorOutput;
    private SensorEventListener proximityListener;
    private TimerTask proximitySamplingTask;

    @Override
    public List<Float> call() throws InterruptedException {

      final CountDownLatch latch = new CountDownLatch(1);

      final List<Float> sensorValues = new ArrayList<>();

      final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

      proximityListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          if (proximitySensorOutput == null) {
            proximitySensorOutput = event.values;

            proximitySamplingTask = new TimerTask() {
              @Override
              public void run() {

                if (System.currentTimeMillis() > endTime) {
                  proximitySamplingTask.cancel();
                  latch.countDown();
                  return;
                }

                sensorValues.add(proximitySensorOutput[0]);
              }
            };

            final int micro_per_milli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
            proximitySamplingTimer.scheduleAtFixedRate(proximitySamplingTask, 0, measurementFrequency / micro_per_milli);
          } else {
            proximitySensorOutput = event.values;
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        }
      };

      if (!sensorManager.registerListener(proximityListener, proximitySensor, measurementFrequency)) {

        sensorManager.unregisterListener(proximityListener);
        return null;
      }

      latch.await();

      sensorManager.unregisterListener(proximityListener);

      return sensorValues;
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(final long sampleDuration, final int measurementFrequency) {
    return new RetrieveProximityDataCallable(sampleDuration, measurementFrequency);
  }
}
