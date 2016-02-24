package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.SensorManager;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class SensorProvider {

  private final ExecutorService sensorThreadPool;
  protected final SensorManager sensorManager;

  public SensorProvider(final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    this.sensorThreadPool = sensorThreadPool;
    this.sensorManager = sensorManager;
  }

  protected abstract class RetrieveSensorDataCallable implements Callable<List<Float>> {

    final WeakReference<Context> contextWeakReference;
    final long endTime;
    final int samplingPeriod;
    long lastUpdateTime;

    public RetrieveSensorDataCallable(final Context context, final long duration, final int samplingPeriod) {
      contextWeakReference = new WeakReference<>(context);
      endTime = System.currentTimeMillis() + duration;
      this.samplingPeriod = samplingPeriod;
    }
  }

  protected abstract RetrieveSensorDataCallable createCallable(final Context context, final long duration, final int samplingPeriod);

  public final Future<List<Float>> retrieveDataForPeriod(final Context context, final long duration, final int samplingPeriod) {
    return sensorThreadPool.submit(createCallable(context, duration, samplingPeriod));
  }
}
