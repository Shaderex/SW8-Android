package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("unused")
public class OrientationSensorTest extends ApplicationTestCase<Application> {

  // Increase this to increase the amount of time logging
  private static final int logTime = 0;

  private float[] accelerometerOutput;
  private float[] magneticFieldOutput;

  // Initial listeners for getting the first measurements, we need at least one measurement from each
  // sensor in order to create a rotationMatrix
  private SensorEventListener initialMagneticFieldListener;
  private SensorEventListener initialAccelerometerListener;

  // Listeners used when we have one measurement from each sensor
  private SensorEventListener accelerometerListener;
  private SensorEventListener magneticFieldListener;

  private Sensor accelerometerSensor;
  private Sensor magneticFieldSensor;

  private final float[] rotationMatrix = new float[16];
  private final float[] inclinationMatrix = new float[16];
  private final float[] values = new float[3];

  public OrientationSensorTest() {
    super(Application.class);
  }

  @Override
  public void setUp() throws Exception {

    super.setUp();

    accelerometerSensor = getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    magneticFieldSensor = getSensorManager().getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    accelerometerListener = new SensorEventListener() {
      @Override
      public void onSensorChanged(final SensorEvent event) {

        synchronized (values) {

          accelerometerOutput = event.values;

          if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput)) {
            SensorManager.getOrientation(rotationMatrix, values);

            printOrientationOutput();
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

          if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerOutput, magneticFieldOutput)) {
            SensorManager.getOrientation(rotationMatrix, values);

            printOrientationOutput();
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

        synchronized (OrientationSensorTest.this) {

          accelerometerOutput = event.values;

          if (magneticFieldOutput != null) {


            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerOutput, magneticFieldOutput);

            getSensorManager().unregisterListener(initialAccelerometerListener);
            getSensorManager().unregisterListener(initialMagneticFieldListener);

            getSensorManager().registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            getSensorManager().registerListener(magneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_FASTEST);
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

        synchronized (OrientationSensorTest.this) {

          magneticFieldOutput = event.values;

          if (accelerometerOutput != null) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerOutput, magneticFieldOutput);
            
            getSensorManager().unregisterListener(initialAccelerometerListener);
            getSensorManager().unregisterListener(initialMagneticFieldListener);

            getSensorManager().registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            getSensorManager().registerListener(magneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
    final long whenToStop = now + logTime;
    final CountDownLatch latch = new CountDownLatch(1);

    final Timer timer = new Timer();

    SensorManager.getOrientation(rotationMatrix, values);

    getSensorManager().registerListener(initialAccelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
    getSensorManager().registerListener(initialMagneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_FASTEST);

    final TimerTask stopTask = new TimerTask() {
      @Override
      public void run() {
        latch.countDown();
      }
    };

    timer.schedule(stopTask, whenToStop - now);

    try {
      latch.await();
    } catch (InterruptedException exception) {
      exception.printStackTrace();
    }

    timer.cancel();

    getSensorManager().unregisterListener(accelerometerListener);
    getSensorManager().unregisterListener(magneticFieldListener);

  }

  @Override
  protected void tearDown() throws Exception {

    Log.i("Orientation Data: ", "" + (data.size() * 3 * 4) + " Bytes");

    super.tearDown();
  }

  final List<float[]> data = new LinkedList<>();

  private void printOrientationOutput() {
    //Log.i("Rotation -Z degrees", values[0]*180/Math.PI + " ° / " + 360 + " °");
    //Log.i("Rotation -X degrees", values[1]*180/Math.PI + " ° / " + 360 + " °");
    //Log.i("Rotation Y degrees", Math.toDegrees(values[2]) * 180 / Math.PI + " ° / " + 360 + " °");

    data.add(values);

    Log.i("Rotation", (Math.toDegrees(values[0]) + 360) % 360 + " ° / " + 360 + " °");
  }

  private SensorManager getSensorManager() {
    return (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
  }
}
