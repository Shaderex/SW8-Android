package dk.aau.sw808f16.datacollection;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class OrientationTest extends ApplicationTestCase<Application> {

  public float acceleromterOutput[];
  public float magneticFieldOutput[];

  // Initial listeners for getting the first measurements, we need at least one measurement from each
  // sensor in order to create a rotationMatrix
  private SensorEventListener initialMagneticFieldListener;
  private SensorEventListener initialAccelerometerListener;

  // Listeners used when we have one measurement from each sensor
  private SensorEventListener accelerometerListener;
  private SensorEventListener magneticFieldListener;

  private SensorManager sensorManager;
  private Sensor accelerometerSensor;
  private Sensor magneticFieldSensor;

  final float rotationMatrix[] = new float[16];
  final float inclinationMatrix[] = new float[16];
  final float values[] = new float[3];

  public OrientationTest() {
    super(Application.class);
  }

  @Override
  public void setUp() throws Exception {

    super.setUp();

    sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    accelerometerListener = new SensorEventListener() {
      @Override
      public void onSensorChanged(final SensorEvent event) {

        synchronized (values) {

          acceleromterOutput = event.values;

          if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, acceleromterOutput, magneticFieldOutput)) {
            SensorManager.getOrientation(rotationMatrix, values);

            theBestPrintMethodCreatedEver();
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

            theBestPrintMethodCreatedEver();
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

        synchronized (OrientationTest.this) {

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

        synchronized (OrientationTest.this) {

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
  }

  public void testCompass() {

    final long now = System.currentTimeMillis();
    final long whenToStop = now + 30000;
    final CountDownLatch latch = new CountDownLatch(1);

    final Timer timer = new Timer();

    SensorManager.getOrientation(rotationMatrix, values);

    sensorManager.registerListener(initialAccelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    sensorManager.registerListener(initialMagneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);

    final TimerTask stopTask = new TimerTask() {
      @Override
      public void run() {
        latch.countDown();
      }
    };

    timer.schedule(stopTask, whenToStop - now);

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    timer.cancel();

    sensorManager.unregisterListener(accelerometerListener);
    sensorManager.unregisterListener(magneticFieldListener);

  }

  public void theBestPrintMethodCreatedEver() {
    //Log.i("Rotation -Z degrees", values[0]*180/Math.PI + " ° / " + 360 + " °");
    //Log.i("Rotation -X degrees", values[1]*180/Math.PI + " ° / " + 360 + " °");
    //Log.i("Rotation Y degrees", Math.toDegrees(values[2]) * 180 / Math.PI + " ° / " + 360 + " °");

    Log.i("Rotation", (Math.toDegrees(values[0]) + 360) % 360 + " ° / " + 360 + " °");
  }
}
