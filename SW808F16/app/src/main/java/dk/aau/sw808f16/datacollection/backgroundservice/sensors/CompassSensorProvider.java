package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CompassSensorProvider extends SensorProvider {

  private final ExecutorService sensorThreadPool;

  public CompassSensorProvider(final ExecutorService sensorThreadPool) {
    this.sensorThreadPool = sensorThreadPool;
  }

  public class RetrieveCompassDataCallable implements Callable<List<Float>> {

    private final WeakReference<Context> contextWeakReference;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final long endTime;

    public RetrieveCompassDataCallable(final Context context, final long startTime, final long duration) {
      contextWeakReference = new WeakReference<>(context);
      endTime = startTime + duration;
    }

    float acceleromterOutput[];
    float magneticFieldOutput[];

    // Initial listeners for getting the first measurements, we need at least one measurement from each
    // sensor in order to create a rotationMatrix
    SensorEventListener initialMagneticFieldListener;
    SensorEventListener initialAccelerometerListener;

    // Listeners used when we have one measurement from each sensor
    SensorEventListener accelerometerListener;
    SensorEventListener magneticFieldListener;

    final float rotationMatrix[] = new float[16];
    final float inclinationMatrix[] = new float[16];
    final float values[] = new float[3];

    @Override
    public List<Float> call() throws Exception {

      final List<Float> sensorValues = new ArrayList<>();
      final Context context = contextWeakReference.get();

      if (context == null) {
        return null;
      }

      final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      final Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

      accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (values) {

            acceleromterOutput = event.values;

            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, acceleromterOutput, magneticFieldOutput)) {
              SensorManager.getOrientation(rotationMatrix, values);
              sensorValues.add(sensorDataToOrientation(values));
            }

            if (endTime <= System.currentTimeMillis()) {
              latch.countDown();
            }
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, int accuracy) {

        }
      };

      magneticFieldListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (values) {

            magneticFieldOutput = event.values;

            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, acceleromterOutput, magneticFieldOutput)) {
              SensorManager.getOrientation(rotationMatrix, values);
              sensorValues.add(sensorDataToOrientation(values));
            }

            if (endTime <= System.currentTimeMillis()) {
              latch.countDown();
            }
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, int accuracy) {

        }
      };

      initialAccelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (CompassSensorProvider.this) {

            acceleromterOutput = event.values;

            if (magneticFieldOutput != null) {

              SensorManager.getRotationMatrix(rotationMatrix, null, acceleromterOutput, magneticFieldOutput);

              sensorManager.unregisterListener(initialAccelerometerListener);
              sensorManager.unregisterListener(initialMagneticFieldListener);

              sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
              sensorManager.registerListener(magneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
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

            if (acceleromterOutput != null) {

              SensorManager.getRotationMatrix(rotationMatrix, null, acceleromterOutput, magneticFieldOutput);

              sensorManager.unregisterListener(initialAccelerometerListener);
              sensorManager.unregisterListener(initialMagneticFieldListener);

              sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
              sensorManager.registerListener(magneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
          }
        }

        @Override
        public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        }
      };

      sensorManager.registerListener(initialAccelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
      sensorManager.registerListener(initialMagneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);

      try {
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      sensorManager.unregisterListener(accelerometerListener);
      sensorManager.unregisterListener(magneticFieldListener);

      return sensorValues;
    }
  }

  public Future<List<Float>> retrieveCompassDataForPeriod(final Context context, final long startTime, final long duration) {
    return sensorThreadPool.submit(new RetrieveCompassDataCallable(context, startTime, duration));
  }

  private static float sensorDataToOrientation(float sensorData[]) {
    return (float) (Math.toDegrees(sensorData[0]) + 360) % 360;
  }
}
