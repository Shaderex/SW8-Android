package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import dk.aau.sw808f16.datacollection.snapshot.Sample;

public abstract class SensorProvider<MeasurementType> {

  final WeakReference<Context> contextWeakReference;
  private final ExecutorService sensorThreadPool;
  final SensorManager sensorManager;

  private MeasurementType cachedMeasurement = null;
  private final Object firstMeasurementLock = new Object();
  private Timer measurementTimer;

  final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

  protected abstract EventListenerRegistrationManager createRegManager();

  protected void onNewMeasurement(final MeasurementType newMeasurement) {

    cachedMeasurement = newMeasurement;

    if(!atomicBoolean.getAndSet(true)) {
      synchronized (firstMeasurementLock) {
        firstMeasurementLock.notify();
      }
    }
  }

  public SensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    this.contextWeakReference = new WeakReference<>(context);
    this.sensorThreadPool = sensorThreadPool;
    this.sensorManager = sensorManager;

    measurementTimer = new Timer(true);
  }

  // Arguments are given in milliseconds
  private Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency)
      throws InterruptedException {

    if(cachedMeasurement == null)
    {
      synchronized (firstMeasurementLock) {
        firstMeasurementLock.wait();
      }
    }

    final CountDownLatch latch = new CountDownLatch(1);
    final Sample sensorValues = new Sample();

    final TimerTask sampleTimerTask = new TimerTask() {
      @Override
      public void run() {
        latch.countDown();
      }
    };

    final long measurementsPerSample = sampleDuration / measurementFrequency;

    measurementTimer.schedule(sampleTimerTask, sampleDuration);

    final TimerTask measurementTimerTask = new TimerTask() {
      @Override
      public void run() {

        if(sensorValues.size() == measurementsPerSample)
        {
          return;
        }

        sensorValues.addMeasurement(cachedMeasurement);
      }
    };

    measurementTimer.schedule(measurementTimerTask, 0, measurementFrequency);

    latch.await();
    measurementTimerTask.cancel();

    return sensorValues;
  }

  // Arguments are given in milliseconds
  public Future<List<Sample>> retrieveSamplesForDuration(final long totalDuration,
                                                         final long sampleFrequency,
                                                         final long sampleDuration,
                                                         final long measurementFrequency) {

    final EventListenerRegistrationManager registrationManager = createRegManager();

    registrationManager.register(SensorManager.SENSOR_DELAY_FASTEST);

    if (!(totalDuration >= sampleFrequency)) {
      throw new IllegalArgumentException("Total duration must be greater than or equal to sample frequency");
    }
    if (!(sampleFrequency >= sampleDuration)) {
      throw new IllegalArgumentException("Sample frequency must be greater than or equal to sample duration");
    }
    if (!(sampleDuration >= measurementFrequency)) {
      throw new IllegalArgumentException("Sample duration must be greater than or equal to measurement frequency");
    }

    return sensorThreadPool.submit(new Callable<List<Sample>>() {

      final Timer timer = new Timer(true);

      @Override
      public List<Sample> call() throws InterruptedException {

        final List<Sample> samples = new ArrayList<>();

        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + totalDuration;

        final CountDownLatch latch = new CountDownLatch(1);

        timer.scheduleAtFixedRate(new TimerTask() {
          @Override
          public void run() {
            try {
              final long currentTime = System.currentTimeMillis();

              if (currentTime > endTime) {
                cancel();
                latch.countDown();
              }

              final Sample sample = retrieveSampleForDuration(sampleDuration, measurementFrequency);
              samples.add(sample);
            } catch (InterruptedException exception) {
              // Do nothing
            }
          }
        }, 0, sampleFrequency); // Zero indicates start immediately

        latch.await();

        registrationManager.unregister();

        return samples;
      }
    });
  }

  public abstract boolean isSensorAvailable();

  interface EventListenerRegistrationManager {
    void register(final int frequency);
    void unregister();
  }

  protected static class SensorEventListenerRegistrationManager implements EventListenerRegistrationManager {

    private final Sensor sensor;
    private final SensorManager manager;
    private final SensorEventListener listener;
    private HandlerThread thread;

    public SensorEventListenerRegistrationManager(final SensorManager manager, final Sensor sensor, final SensorEventListener listener) {

      this.sensor = sensor;
      this.manager = manager;
      this.listener = listener;

      thread = new HandlerThread("SensorEventListenerRegistrationManager HandlerThread");
    }

    public void register(final int frequency) {
      thread.start();
      manager.registerListener(listener, sensor, frequency, new Handler(thread.getLooper()));
    }

    public void unregister() {
      manager.unregisterListener(listener);
      thread.quit();
    }
  }
}
