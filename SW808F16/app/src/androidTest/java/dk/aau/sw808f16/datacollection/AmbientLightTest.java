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

public class AmbientLightTest extends ApplicationTestCase<Application> {

  public AmbientLightTest() {
    super(Application.class);
  }

  public void testAmbientLight() {

    final long now = System.currentTimeMillis();
    final long whenToStop = now + 30000;
    final CountDownLatch latch = new CountDownLatch(1);

    final Timer timer = new Timer();

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(final SensorEvent event) {

        final float lux = event.values[0];

        Log.i("LIGHT Illuminance", lux + " lx / " + lightSensor.getMaximumRange() + " lx");
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
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    timer.cancel();

    sensorManager.unregisterListener(listener);

  }
}

