package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

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
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class ProximitySensorProvider extends SensorProvider<Sample> {

  private final Timer proximitySamplingTimer;

  public ProximitySensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    proximitySamplingTimer = new Timer(true);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency) throws InterruptedException {

    final long endTime = System.currentTimeMillis() + sampleDuration;
    final CountDownLatch latch = new CountDownLatch(1);
    final List<Float> measurements = new ArrayList<>();

    final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    final SensorEventListener proximityListener = new SensorEventListener() {

      float[] proximitySensorOutput;
      TimerTask proximitySamplingTask;

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

              measurements.add(proximitySensorOutput[0]);
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

    return new Sample(measurements);
  }
}
