package dk.aau.sw808f16.wekatest.backgroundservice.sensors;

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

@SuppressWarnings("unused")
public class BarometerSensorTest extends ApplicationTestCase<Application> {
  // Increase this to increase the amount of time logging
  private static final int logTime = 0;

  public BarometerSensorTest() {
    super(Application.class);
  }

  public void testBarometer() {

    final long now = System.currentTimeMillis();
    final long whenToStop = now + logTime;
    final CountDownLatch latch = new CountDownLatch(1);

    final Timer timer = new Timer();

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {

        final float distance = event.values[0];
        Log.i("PRESSURE", distance + " hPa / " + pressureSensor.getMaximumRange() + " hPa");
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };

    sensorManager.registerListener(listener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);

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
}
