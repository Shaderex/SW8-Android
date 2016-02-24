package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

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
public class AmbientLightSensorTest extends ApplicationTestCase<Application> {
  // Increase this to increase the amount of time logging
  private static final int logTime = 0;

  public AmbientLightSensorTest() {
    super(Application.class);
  }

  public void testAmbientLight() {

    final long now = System.currentTimeMillis();
    final long whenToStop = now + logTime;
    final CountDownLatch latch = new CountDownLatch(1);

    final Timer timer = new Timer();

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(final SensorEvent event) {

        final float lux = event.values[0];

        Log.i("LIGHT illuminance", lux + " lx / " + lightSensor.getMaximumRange() + " lx");
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };

    sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

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

