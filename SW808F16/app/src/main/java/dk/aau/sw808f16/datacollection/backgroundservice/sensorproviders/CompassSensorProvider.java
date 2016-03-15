package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
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

public class CompassSensorProvider extends SensorProvider {

  private static final int maxArcDegrees = 360;

  public CompassSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency) throws InterruptedException {

    final long endTime = System.currentTimeMillis() + sampleDuration;

    final CountDownLatch latch = new CountDownLatch(1);
    final List<Float> measurements = new ArrayList<>();

    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    final Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    final float[] rotationMatrix = new float[16];
    final float[] inclinationMatrix = new float[16];
    final float[] values = new float[3];

    final Runnable fetchData = new Runnable() {

      // Initial listeners for getting the first measurements, we need at least one measurement from each
      // sensor in order to create a rotationMatrix
      SensorEventListener initialMagneticFieldListener;
      SensorEventListener initialAccelerometerListener;

      // Listeners used when we have one measurement from each sensor
      SensorEventListener accelerometerListener;
      SensorEventListener magneticFieldListener;

      float[] accelerometerOutput;
      float[] magneticFieldOutput;

      private long lastUpdateTime;

      @Override
      public void run() {
        // Convert measurement frequency to micro seconds for the Android API
        final int microPerMilli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
        final int measurementFrequencyInMicroSeconds = (int) (measurementFrequency * microPerMilli);

        initialAccelerometerListener = new SensorEventListener() {
          @Override
          public void onSensorChanged(final SensorEvent event) {

            synchronized (CompassSensorProvider.this) {

              accelerometerOutput = event.values;

              if (magneticFieldOutput != null) {

                SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput);

                SensorManager.getOrientation(rotationMatrix, values);
                measurements.add(sensorDataToOrientation(values));
                final long currentTime = System.currentTimeMillis();
                lastUpdateTime = currentTime;

                sensorManager.unregisterListener(initialAccelerometerListener);
                sensorManager.unregisterListener(initialMagneticFieldListener);

                sensorManager.registerListener(accelerometerListener,
                    accelerometerSensor,
                    measurementFrequencyInMicroSeconds,
                    measurementFrequencyInMicroSeconds);
                sensorManager.registerListener(magneticFieldListener,
                    magneticFieldSensor,
                    measurementFrequencyInMicroSeconds,
                    measurementFrequencyInMicroSeconds);
              }
            }
          }

          @Override
          public void onAccuracyChanged(final Sensor sensor, int accuracy) {
          }
        };

        initialMagneticFieldListener = new SensorEventListener() {
          @Override
          public void onSensorChanged(final SensorEvent event) {

            synchronized (CompassSensorProvider.this) {

              magneticFieldOutput = event.values;

              if (accelerometerOutput != null) {

                SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput);

                SensorManager.getOrientation(rotationMatrix, values);
                measurements.add(sensorDataToOrientation(values));
                final long currentTime = System.currentTimeMillis();
                lastUpdateTime = currentTime;

                sensorManager.unregisterListener(initialAccelerometerListener);
                sensorManager.unregisterListener(initialMagneticFieldListener);

                sensorManager.registerListener(accelerometerListener,
                    accelerometerSensor,
                    measurementFrequencyInMicroSeconds,
                    measurementFrequencyInMicroSeconds);
                sensorManager.registerListener(magneticFieldListener,
                    magneticFieldSensor,
                    measurementFrequencyInMicroSeconds,
                    measurementFrequencyInMicroSeconds);
              }
            }
          }

          @Override
          public void onAccuracyChanged(final Sensor sensor, int accuracy) {
          }
        };

        accelerometerListener = new SensorEventListener() {
          @Override
          public void onSensorChanged(final SensorEvent event) {

            synchronized (CompassSensorProvider.this) {

              accelerometerOutput = event.values;
              final long currentTime = System.currentTimeMillis();

              if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput)) {
                SensorManager.getOrientation(rotationMatrix, values);

                if (lastUpdateTime + measurementFrequency >= currentTime) {
                  return;
                }

                measurements.add(sensorDataToOrientation(values));

                lastUpdateTime = currentTime;
              }

              if (endTime <= currentTime) {
                latch.countDown();
              }
            }
          }

          @Override
          public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
          }
        };

        magneticFieldListener = new SensorEventListener() {
          @Override
          public void onSensorChanged(final SensorEvent event) {

            synchronized (CompassSensorProvider.this) {

              magneticFieldOutput = event.values;
              final long currentTime = System.currentTimeMillis();

              if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput)) {
                SensorManager.getOrientation(rotationMatrix, values);

                if (lastUpdateTime + measurementFrequency >= currentTime) {
                  return;
                }

                measurements.add(sensorDataToOrientation(values));

                lastUpdateTime = currentTime;
              }

              if (endTime <= currentTime) {
                latch.countDown();
              }
            }
          }

          @Override
          public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
          }
        };

        if (!(sensorManager.registerListener(initialAccelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)
            && sensorManager.registerListener(initialMagneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_FASTEST))) {

          sensorManager.unregisterListener(initialAccelerometerListener);
          sensorManager.unregisterListener(initialMagneticFieldListener);
          sensorManager.unregisterListener(accelerometerListener);
          sensorManager.unregisterListener(magneticFieldListener);


        }

        try {
          latch.await();
        } catch (InterruptedException exception) {
          exception.printStackTrace();
        }

        sensorManager.unregisterListener(initialAccelerometerListener);
        sensorManager.unregisterListener(initialMagneticFieldListener);
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(magneticFieldListener);
      }
    };

    fetchData.run();

    return new Sample(measurements);
  }

  @Override
  public boolean isSensorAvailable() {
    return context.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
  }

  private static float sensorDataToOrientation(final float[] sensorData) {
    return (float) (Math.toDegrees(sensorData[0]) + maxArcDegrees) % maxArcDegrees;
  }
}
