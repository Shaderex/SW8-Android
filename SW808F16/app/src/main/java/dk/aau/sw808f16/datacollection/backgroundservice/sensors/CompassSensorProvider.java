package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class CompassSensorProvider extends SensorProvider<List<Float>> {

  public CompassSensorProvider(final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(sensorThreadPool, sensorManager);
  }

  private class RetrieveCompassDataCallable extends RetrieveSensorDataCallable {

    public RetrieveCompassDataCallable(final long duration, final int samplingPeriod) {
      super(duration, samplingPeriod);
    }

    private float[] accelerometerOutput;
    private float[] magneticFieldOutput;

    // Initial listeners for getting the first measurements, we need at least one measurement from each
    // sensor in order to create a rotationMatrix
    private SensorEventListener initialMagneticFieldListener;
    private SensorEventListener initialAccelerometerListener;

    // Listeners used when we have one measurement from each sensor
    private SensorEventListener accelerometerListener;
    private SensorEventListener magneticFieldListener;

    private final float[] rotationMatrix = new float[16];
    private final float[] inclinationMatrix = new float[16];
    private final float[] values = new float[3];

    @Override
    public List<Float> call() throws Exception {

      final CountDownLatch latch = new CountDownLatch(1);

      final List<Float> sensorValues = new ArrayList<>();

      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      final Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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

              if (lastUpdateTime + measurementFrequency / 1000 >= currentTime) {
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

              if (lastUpdateTime + measurementFrequency / 1000 >= currentTime) {
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

        return null;
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

      return sensorValues;
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(final long sampleDuration, final int measurementFrequency) {
    return new RetrieveCompassDataCallable(sampleDuration, measurementFrequency);
  }

  private static float sensorDataToOrientation(final float[] sensorData) {
    return (float) (Math.toDegrees(sensorData[0]) + 360) % 360;
  }
}
