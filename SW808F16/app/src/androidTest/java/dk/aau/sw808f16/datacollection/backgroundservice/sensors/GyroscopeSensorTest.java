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
public class GyroscopeSensorTest extends ApplicationTestCase<Application> {

  // Increase this to increase the amount of time logging
  private static final int logTime = 0;

  public GyroscopeSensorTest() {
    super(Application.class);
  }

  final List<float[]> data = new LinkedList<>();

  public void testGyroscope() {

    final long now = System.currentTimeMillis();
    final long whenToStop = now + logTime;
    final CountDownLatch latch = new CountDownLatch(1);

    final Timer timer = new Timer();

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(final SensorEvent event) {

        final float xRotation = event.values[0];
        final float yRotation = event.values[1];
        final float zRotation = event.values[2];

        Log.i("Gyroscope x", xRotation + " rad/s / " + gyroscopeSensor.getMaximumRange() + " rad/s");
        Log.i("Gyroscope y", yRotation + " rad/s / " + gyroscopeSensor.getMaximumRange() + " rad/s");
        Log.i("Gyroscope z", zRotation + " rad/s / " + gyroscopeSensor.getMaximumRange() + " rad/s");

        data.add(event.values);
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };

    sensorManager.registerListener(listener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);

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

    sensorManager.unregisterListener(listener);
  }

  @Override
  protected void tearDown() throws Exception {

    Log.i("Gyroscope data: ", data.size() * 4 * 3 + " Bytes");

    super.tearDown();
  }
}

