package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class SensorProvider {

  protected final ExecutorService sensorThreadPool;

  public SensorProvider(final ExecutorService sensorThreadPool) {
    this.sensorThreadPool = sensorThreadPool;
  }

  protected abstract class RetrieveSensorDataCallable implements Callable<List<Float>>
  {
    protected final WeakReference<Context> contextWeakReference;
    protected final long endTime;
    protected final int samplingPeriod;
    protected long lastUpdateTime;

    public RetrieveSensorDataCallable(final Context context, final long duration, final int samplingPeriod) {
      contextWeakReference = new WeakReference<>(context);
      endTime = System.currentTimeMillis() + duration;
      this.samplingPeriod = samplingPeriod;
    }
  }

  protected abstract RetrieveSensorDataCallable createCallable(final Context context, final long duration, final int samplingPeriod);

  public Future<List<Float>> retrieveDataForPeriod(final Context context, final long duration, final int samplingPeriod) {
    return sensorThreadPool.submit(createCallable(context, duration, samplingPeriod));
  }
}
