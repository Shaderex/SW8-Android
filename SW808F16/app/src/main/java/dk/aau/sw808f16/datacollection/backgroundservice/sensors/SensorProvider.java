package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class SensorProvider<T> {

  private final ExecutorService sensorThreadPool;
  protected final SensorManager sensorManager;

  public SensorProvider(final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    this.sensorThreadPool = sensorThreadPool;
    this.sensorManager = sensorManager;
  }

  protected abstract class RetrieveSensorDataCallable implements Callable<T> {

    final long endTime;
    final int measurementFrequency;
    long lastUpdateTime;

    public RetrieveSensorDataCallable(final long sampleDuration, final int measurementFrequency) {
      endTime = System.currentTimeMillis() + sampleDuration;
      this.measurementFrequency = measurementFrequency;
    }
  }

  protected abstract RetrieveSensorDataCallable createCallable(final long sampleDuration, final int measurementFrequency);

  public final T retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency) throws Exception {
    return createCallable(sampleDuration, measurementFrequency).call();  //sensorThreadPool.submit();
  }

  public Future<List<T>> retrieveSamplesForDuration(final long totalDuration, final long sampleFrequency, final long sampleDuration, final int measurementFrequency) {

    if (!(totalDuration >= sampleFrequency && sampleFrequency >= sampleDuration && sampleDuration >= measurementFrequency)) {
      throw new IllegalArgumentException("The following must hold for the given arguments: totalDuration >= sampleFrequency >= sampleDuration >= measurementFrequency");
    }

    final Timer timer = new Timer(true);

    final Future<List<T>> futureListOfSamples = sensorThreadPool.submit(new Callable<List<T>>() {

      @Override
      public List<T> call() throws Exception {

        final List<T> samples = new ArrayList<>();

        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + totalDuration;

        final CountDownLatch latch = new CountDownLatch(1);

        timer.scheduleAtFixedRate(new TimerTask() {
          @Override
          public void run()  {

            try
            {
              final long currentTime = System.currentTimeMillis();

              if(currentTime > endTime)
              {
                cancel();
                latch.countDown();
              }

              final T sample = retrieveSampleForDuration(sampleDuration, measurementFrequency);
              samples.add(sample);
            }
            catch (Exception exception)
            {

            }
          }
        }, 0, sampleFrequency);

        latch.await();

        return samples;
      }
    });

    return futureListOfSamples;
  }
}
