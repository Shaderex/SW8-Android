package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

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
    private final long endTime;
    private long lastUpdateTime;
    private final int samplingPeriod;

    public RetrieveCompassDataCallable(final Context context, final long startTime, final long duration, final int samplingPeriod) {
      contextWeakReference = new WeakReference<>(context);
      endTime = startTime + duration;
      this.samplingPeriod = samplingPeriod;
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

      final Context context = contextWeakReference.get();
      final CountDownLatch latch = new CountDownLatch(1);

      if (context == null) {
        return null;
      }

      final List<Float> sensorValues = new ArrayList<>();

      final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
      final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      final Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

      initialAccelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {

          synchronized (CompassSensorProvider.this) {

            acceleromterOutput = event.values;

            if (magneticFieldOutput != null) {

              SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, acceleromterOutput, magneticFieldOutput);

              sensorManager.unregisterListener(initialAccelerometerListener);
              sensorManager.unregisterListener(initialMagneticFieldListener);

              sensorManager.registerListener(accelerometerListener, accelerometerSensor, samplingPeriod, samplingPeriod);
              sensorManager.registerListener(magneticFieldListener, magneticFieldSensor, samplingPeriod, samplingPeriod);
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

              SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, acceleromterOutput, magneticFieldOutput);

              sensorManager.unregisterListener(initialAccelerometerListener);
              sensorManager.unregisterListener(initialMagneticFieldListener);

              sensorManager.registerListener(accelerometerListener, accelerometerSensor, samplingPeriod, samplingPeriod);
              sensorManager.registerListener(magneticFieldListener, magneticFieldSensor, samplingPeriod, samplingPeriod);
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

            acceleromterOutput = event.values;
            final long currentTime = System.currentTimeMillis();

            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, acceleromterOutput, magneticFieldOutput)) {
              SensorManager.getOrientation(rotationMatrix, values);

              if (lastUpdateTime + samplingPeriod / 1000 >= currentTime) {
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

            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, acceleromterOutput, magneticFieldOutput)) {
              SensorManager.getOrientation(rotationMatrix, values);

              if (lastUpdateTime + samplingPeriod / 1000 >= currentTime) {
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

      if (!(sensorManager.registerListener(initialAccelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST) && sensorManager.registerListener(initialMagneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_FASTEST))) {

        sensorManager.unregisterListener(initialAccelerometerListener);
        sensorManager.unregisterListener(initialMagneticFieldListener);
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(magneticFieldListener);

        return null;
      }

      try {
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      sensorManager.unregisterListener(initialAccelerometerListener);
      sensorManager.unregisterListener(initialMagneticFieldListener);
      sensorManager.unregisterListener(accelerometerListener);
      sensorManager.unregisterListener(magneticFieldListener);

      return sensorValues;
    }
  }

  public Future<List<Float>> retrieveCompassDataForPeriod(final Context context, final long startTime, final long duration, final int samplingPeriod) {
    return sensorThreadPool.submit(new RetrieveCompassDataCallable(context, startTime, duration, samplingPeriod));
  }

  private static float sensorDataToOrientation(final float sensorData[]) {
    return (float) (Math.toDegrees(sensorData[0]) + 360) % 360;
  }
}
