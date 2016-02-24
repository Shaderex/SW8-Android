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

public class ProximitySensorProvider extends SensorProvider {

  private final Timer proximitySamplingTimer;

  public ProximitySensorProvider(final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(sensorThreadPool, sensorManager);
    proximitySamplingTimer = new Timer(true);
  }

  public class RetrieveProximityDataCallable extends RetrieveSensorDataCallable {

    public RetrieveProximityDataCallable(final Context context, final long duration, final int samplingPeriod) {
      super(context, duration, samplingPeriod);
    }

    private float[] proximitySensorOutput;
    private SensorEventListener proximityListener;
    private TimerTask proximitySamplingTask;

    @Override
    public List<Float> call() throws Exception {

      final Context context = contextWeakReference.get();
      final CountDownLatch latch = new CountDownLatch(1);

      if (context == null) {
        return null;
      }

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

            proximitySamplingTimer.scheduleAtFixedRate(proximitySamplingTask, 0, samplingPeriod / 1000);
          } else {
            proximitySensorOutput = event.values;
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        }
      };

      if (!sensorManager.registerListener(proximityListener, proximitySensor, samplingPeriod)) {

        sensorManager.unregisterListener(proximityListener);
        return null;
      }

      try {
        latch.await();
      } catch (InterruptedException exception) {
        exception.printStackTrace();
      }

      sensorManager.unregisterListener(proximityListener);

      return sensorValues;
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(final Context context, final long duration, final int samplingPeriod) {
    return new RetrieveProximityDataCallable(context, duration, samplingPeriod);
  }
}
