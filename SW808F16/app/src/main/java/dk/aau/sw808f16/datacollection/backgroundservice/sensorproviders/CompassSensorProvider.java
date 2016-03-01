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

public class CompassSensorProvider extends SensorProvider<List<Float>> {

  private static final int maxArcDegrees = 360;

  public CompassSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected List<Float> retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency) throws InterruptedException {

    final long endTime = System.currentTimeMillis() + sampleDuration;

    final CountDownLatch latch = new CountDownLatch(1);
    final List<Float> sensorValues = new ArrayList<>();

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
        initialAccelerometerListener = new SensorEventListener() {
          @Override
          public void onSensorChanged(final SensorEvent event) {

            synchronized (CompassSensorProvider.this) {

              accelerometerOutput = event.values;

              if (magneticFieldOutput != null) {

                SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput);

                sensorManager.unregisterListener(initialAccelerometerListener);
                sensorManager.unregisterListener(initialMagneticFieldListener);

                sensorManager.registerListener(accelerometerListener, accelerometerSensor, measurementFrequency, measurementFrequency);
                sensorManager.registerListener(magneticFieldListener, magneticFieldSensor, measurementFrequency, measurementFrequency);
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

                sensorManager.unregisterListener(initialAccelerometerListener);
                sensorManager.unregisterListener(initialMagneticFieldListener);

                sensorManager.registerListener(accelerometerListener, accelerometerSensor, measurementFrequency, measurementFrequency);
                sensorManager.registerListener(magneticFieldListener, magneticFieldSensor, measurementFrequency, measurementFrequency);
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

                final int micro_per_milli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
                if (lastUpdateTime + measurementFrequency / micro_per_milli >= currentTime) {
                  return;
                }

                sensorValues.add(sensorDataToOrientation(values));

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

                final int micro_per_milli = context.get().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
                if (lastUpdateTime + measurementFrequency / micro_per_milli >= currentTime) {
                  return;
                }

                sensorValues.add(sensorDataToOrientation(values));

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

    return sensorValues;
  }

  private static float sensorDataToOrientation(final float[] sensorData) {
    return (float) (Math.toDegrees(sensorData[0]) + maxArcDegrees) % maxArcDegrees;
  }
}