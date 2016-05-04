package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.measurement.LocationMeasurement;

public class LocationSensorProvider extends SensorProvider<LocationMeasurement> {

  private final HandlerThread handlerThread = new HandlerThread("LocationSensorProvider HandlerThread");

  public LocationSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {

    return new EventListenerRegistrationManager() {

      final LocationManager locationManager = (LocationManager) contextWeakReference.get().getSystemService(Context.LOCATION_SERVICE);
      final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
          onNewMeasurement(new LocationMeasurement(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
      };

      @Override
      public void register(final int frequency) {
        handlerThread.start();
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0.0f, listener , handlerThread.getLooper());
        onNewMeasurement(new LocationMeasurement(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)));
      }

      @Override
      public void unregister() {
        locationManager.removeUpdates(listener);
      }
    };
  }

  @Override
  protected LocationMeasurement getDefaultMeasurement() {
    return new LocationMeasurement();
  }

  @Override
  public boolean isSensorAvailable() {
    LocationManager lm = (LocationManager) contextWeakReference.get().getSystemService(Context.LOCATION_SERVICE);
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

  @Override
  public SensorType getSensorType() {
    return SensorType.LOCATION;
  }
}
