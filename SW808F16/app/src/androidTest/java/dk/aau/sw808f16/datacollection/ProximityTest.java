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

@SuppressWarnings("unused")
public class ProximityTest extends ApplicationTestCase<Application> {

  public ProximityTest() {
    super(Application.class);
  }

  public void testProximity() {

    final long now = System.currentTimeMillis();
    final long whenToStop = now + 30000;
    final CountDownLatch latch = new CountDownLatch(1);

    final Timer timer = new Timer();

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(final SensorEvent event) {

        final float distance = event.values[0];
        Log.i("PROXIMITY DISTANCE", distance + " cm / " + proximitySensor.getMaximumRange() + " cm");
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {

      }
    };

    sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

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
