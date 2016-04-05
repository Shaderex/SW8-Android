package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.snapshot.Sample;

public abstract class SensorProvider {

  final WeakReference<Context> context;
  private final ExecutorService sensorThreadPool;
  final SensorManager sensorManager;

  public SensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    this.context = new WeakReference<>(context);
    this.sensorThreadPool = sensorThreadPool;
    this.sensorManager = sensorManager;
  }

  protected abstract Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency)
      throws InterruptedException;

  public Future<List<Sample>> retrieveSamplesForDuration(final long totalDuration,
                                                         final long sampleFrequency,
                                                         final long sampleDuration,
                                                         final long measurementFrequency) {

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

        return samples;
      }
    });
  }

  public abstract boolean isSensorAvailable();
}
