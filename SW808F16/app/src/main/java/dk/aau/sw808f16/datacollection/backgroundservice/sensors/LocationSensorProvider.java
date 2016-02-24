package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class LocationSensorProvider extends SensorProvider<List<Location>> {

  private final Context context;

  public LocationSensorProvider(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    this.context = context;
  }

  private class RetrieveGpsDataCallable extends RetrieveSensorDataCallable {

    final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    final Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

    public RetrieveGpsDataCallable(final long sampleDuration, final int measurementFrequency) {
      super(sampleDuration, measurementFrequency);
    }

    @Override
    public List<Location> call() throws Exception {
      return new ArrayList<Location>() {
        {
          add(lastKnownLocation);
        }
      };
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(long sampleDuration, int measurementFrequency) {
    return new RetrieveGpsDataCallable(sampleDuration, measurementFrequency);
  }


}
