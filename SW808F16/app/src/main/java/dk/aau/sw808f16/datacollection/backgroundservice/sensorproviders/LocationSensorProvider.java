package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.measurement.LocationMeasurement;

public class LocationSensorProvider extends SensorProvider {

  private final Timer locationMeasureTimer;

  public LocationSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    locationMeasureTimer = new Timer(true);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency)
      throws InterruptedException {

    final LocationManager locationManager = (LocationManager) context.get().getSystemService(Context.LOCATION_SERVICE);
    final long endTime = System.currentTimeMillis() + sampleDuration;
    final CountDownLatch latch = new CountDownLatch(1);
    final List<LocationMeasurement> locations = new ArrayList<>();

    final TimerTask cellNetworkMeasurementTask = new TimerTask() {
      @Override
      public void run() {
        if (System.currentTimeMillis() > endTime) {
          this.cancel();
          latch.countDown();
          return;
        }

        // Do the measurements
        locations.add(new LocationMeasurement(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)));
      }
    };

    locationMeasureTimer.scheduleAtFixedRate(cellNetworkMeasurementTask, 0, measurementFrequency);

    latch.await();

    return new Sample(locations);


  }

  @Override
  public boolean isSensorAvailable() {
    LocationManager lm = (LocationManager) context.get().getSystemService(Context.LOCATION_SERVICE);
    boolean gpsEnabled = false;

    try {
      gpsEnabled = lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (gpsEnabled) {
      return lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) != null;
    }

    return gpsEnabled;
  }
}
