package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class LocationSensorProvider extends SensorProvider<Sample> {

  public LocationSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency)
      throws InterruptedException {

    final LocationManager locationManager = (LocationManager) context.get().getSystemService(Context.LOCATION_SERVICE);
    final Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

    return new Sample(lastKnownLocation);
  }
}
