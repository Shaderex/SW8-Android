package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;

public class CompassSensorProvider extends SensorProvider<FloatMeasurement> {

  private static final int maxArcDegrees = 360;

  public CompassSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {


    return new EventListenerRegistrationManager() {

      final HandlerThread thread = new HandlerThread("CompassSensorProvider HandlerThread");

      float[] accelerometerOutput;
      float[] magneticFieldOutput;

      final float[] rotationMatrix = new float[16];
      final float[] inclinationMatrix = new float[16];
      final float[] values = new float[3];

      // Listeners used when we have one measurement from each sensor
      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      final Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

      final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (CompassSensorProvider.this) {

            accelerometerOutput = event.values;

            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput)) {
              SensorManager.getOrientation(rotationMatrix, values);

              onNewMeasurement(new FloatMeasurement(sensorDataToOrientation(values)));
            }
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        }
      };

      final SensorEventListener magneticFieldListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (CompassSensorProvider.this) {

            magneticFieldOutput = event.values;

            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput)) {
              SensorManager.getOrientation(rotationMatrix, values);

              onNewMeasurement(new FloatMeasurement(sensorDataToOrientation(values)));
            }
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        }
      };

      final SensorEventListener initialAccelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (CompassSensorProvider.this) {

            accelerometerOutput = event.values;

            if (magneticFieldOutput != null) {

              SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput);
              SensorManager.getOrientation(rotationMatrix, values);

              onNewMeasurement(new FloatMeasurement(sensorDataToOrientation(values)));

              sensorManager.unregisterListener(initialAccelerometerListener);
              sensorManager.unregisterListener(initialMagneticFieldListener);

              sensorManager.registerListener(accelerometerListener,
                  accelerometerSensor,
                  SensorManager.SENSOR_DELAY_FASTEST, new Handler(thread.getLooper()));
              sensorManager.registerListener(magneticFieldListener,
                  magneticFieldSensor,
                  SensorManager.SENSOR_DELAY_FASTEST, new Handler(thread.getLooper()));
            }
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        }
      };

      final SensorEventListener initialMagneticFieldListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (CompassSensorProvider.this) {

            magneticFieldOutput = event.values;

            if (accelerometerOutput != null) {

              SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput);

              SensorManager.getOrientation(rotationMatrix, values);
              onNewMeasurement(new FloatMeasurement(sensorDataToOrientation(values)));

              sensorManager.unregisterListener(initialAccelerometerListener);
              sensorManager.unregisterListener(initialMagneticFieldListener);

              sensorManager.registerListener(accelerometerListener,
                  accelerometerSensor,
                  SensorManager.SENSOR_DELAY_FASTEST, new Handler(thread.getLooper()));
              sensorManager.registerListener(magneticFieldListener,
                  magneticFieldSensor,
                  SensorManager.SENSOR_DELAY_FASTEST, new Handler(thread.getLooper()));
            }
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        }
      };

      @Override
      public void register(final int frequency) {

        thread.start();
        sensorManager.registerListener(initialAccelerometerListener, accelerometerSensor,
            SensorManager.SENSOR_DELAY_FASTEST, new Handler(thread.getLooper()));
        sensorManager.registerListener(initialMagneticFieldListener, magneticFieldSensor,
            SensorManager.SENSOR_DELAY_FASTEST, new Handler(thread.getLooper()));
      }

      @Override
      public void unregister() {

        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(magneticFieldListener);

        thread.quit();
      }
    };
  }

  @Override
  public boolean isSensorAvailable() {
    return contextWeakReference.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
  }

  @Override
  public SensorType getSensorType() {
    return SensorType.COMPASS;
  }

  private static float sensorDataToOrientation(final float[] sensorData) {
    return (float) (Math.toDegrees(sensorData[0]) + maxArcDegrees) % maxArcDegrees;
  }
}
