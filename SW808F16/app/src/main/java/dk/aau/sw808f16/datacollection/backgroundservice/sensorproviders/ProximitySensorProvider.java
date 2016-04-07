package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
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
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;

public class ProximitySensorProvider extends SensorProvider {

  private final Timer proximitySamplingTimer;

  public ProximitySensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    proximitySamplingTimer = new Timer(true);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency) throws InterruptedException {

    final long endTime = System.currentTimeMillis() + sampleDuration;
    final CountDownLatch latch = new CountDownLatch(1);
    final List<FloatMeasurement> measurements = new ArrayList<>();

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

              measurements.add(new FloatMeasurement(proximitySensorOutput[0]));
            }
          };

          proximitySamplingTimer.scheduleAtFixedRate(proximitySamplingTask, 0, measurementFrequency);
        } else {
          proximitySensorOutput = event.values;
        }
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };
    // Convert measurement frequency to micro seconds for the Android API
    final int microPerMilli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int measurementFrequencyInMicroSeconds = (int) (measurementFrequency * microPerMilli);

    if (!sensorManager.registerListener(proximityListener, proximitySensor, measurementFrequencyInMicroSeconds)) {

      sensorManager.unregisterListener(proximityListener);
      return null;
    }

    latch.await();

    sensorManager.unregisterListener(proximityListener);

    return new Sample(measurements);
  }

  @Override
  public boolean isSensorAvailable() {
    return context.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
  }
}
