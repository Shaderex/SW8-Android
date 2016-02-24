package dk.aau.sw808f16.datacollection.backgroundservice;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.backgroundservice.sensors.CompassSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensors.ProximitySensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensors.SensorProvider;

public final class BackgroundSensorService extends Service {

  private ServiceHandler serviceHandler;
  @SuppressWarnings("FieldCanBeLocal")
  private final ExecutorService sensorThreadPool;
  @SuppressWarnings("FieldCanBeLocal")
  private CompassSensorProvider compassSensor;
  @SuppressWarnings("FieldCanBeLocal")
  private ProximitySensorProvider proximitySensor;

  public BackgroundSensorService() {
    // The number of threads in the pool should correspond to the number of SensorProvider instances
    // this service maintains
    // Dynamically (Reflection) counts the number of SensorProvider instances this service maintains
    final int numberOfSensorProviders = getNumberOfSensorProviders();
    // Create a thread pool to be shared by all sensor providers
    sensorThreadPool = Executors.newFixedThreadPool(numberOfSensorProviders);
  }

  private final class ServiceHandler extends Handler {
    public ServiceHandler(final Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(final Message msg) {
      // Normally we would do some work here, like download a file.
      // For our sample, we just sleep for 5 seconds.
      try {
        Thread.sleep(5000);
      } catch (InterruptedException exception) {
        // Restore interrupt status.
        Thread.currentThread().interrupt();
      }
      // Stop the service using the startId, so that we don't stop
      // the service in the middle of handling another job
      stopSelf(msg.arg1);
    }
  }

  public class LocalBinder extends Binder {
    public BackgroundSensorService getService() {
      return BackgroundSensorService.this;
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();

    final SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

    // Initialize SensorProvider instances with the shared thread pool
    compassSensor = new CompassSensorProvider(sensorThreadPool, sensorManager);
    proximitySensor = new ProximitySensorProvider(sensorThreadPool, sensorManager);

    // Start up the thread running the service.  Note that we create a
    // separate thread because the service normally runs in the process's
    // main thread, which we don't want to block.  We also make it
    // background priority so CPU-intensive work will not disrupt our UI.
    final HandlerThread thread = new HandlerThread("ServiceStartArguments",
        android.os.Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();

    // Get the HandlerThread's Looper and use it for our Handler
    final Looper serviceLooper = thread.getLooper();
    serviceHandler = new ServiceHandler(serviceLooper);
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

    // For each start request, send a message to start a job and deliver the
    // start ID so we know which request we're stopping when we finish the job
    final Message msg = serviceHandler.obtainMessage();
    msg.arg1 = startId;
    serviceHandler.sendMessage(msg);

    // If we get killed, after returning from here, restart
    return START_STICKY;
  }

  @Override
  public IBinder onBind(final Intent intent) {
    // We don't provide binding, so return null
    return null;
  }

  @Override
  public void onDestroy() {
    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
  }

  private static int getNumberOfSensorProviders() {

    int sensorProviderCount = 0;

    for (final Field field : BackgroundSensorService.class.getDeclaredFields()) {

      final Class type = field.getType();

      if (SensorProvider.class.isAssignableFrom(type)) {
        sensorProviderCount++;
      }
    }

    return sensorProviderCount;
  }

}