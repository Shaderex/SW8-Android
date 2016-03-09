package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class LocationSensorProvider extends SensorProvider {

  private final Context context;
  private final Timer locationMeasureTimer;

  public LocationSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    this.context = context;
    locationMeasureTimer = new Timer(true);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency)
      throws InterruptedException {

    final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    final long endTime = System.currentTimeMillis() + sampleDuration;
    final CountDownLatch latch = new CountDownLatch(1);
    final List<Location> locations = new ArrayList<>();

    final TimerTask cellNetworkMeasurementTask = new TimerTask() {
      @Override
      public void run() {
        if (System.currentTimeMillis() > endTime) {
          this.cancel();
          latch.countDown();
          return;
        }

        // Do the measurements
        locations.add(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
      }
    };

    final int micro_per_milli = context.getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    locationMeasureTimer.scheduleAtFixedRate(cellNetworkMeasurementTask, 0, measurementFrequency / micro_per_milli);

    latch.await();

    return new Sample(locations);


  }
}
