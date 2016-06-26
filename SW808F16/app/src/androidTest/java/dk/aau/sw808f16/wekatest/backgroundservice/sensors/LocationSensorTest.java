package dk.aau.sw808f16.wekatest.backgroundservice.sensors;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ApplicationTestCase;


@SuppressWarnings("unused")
public class LocationSensorTest extends ApplicationTestCase<Application> {

  public LocationSensorTest() {
    super(Application.class);
  }

  public void testLastFixedLocation() {

    String locationProvider = LocationManager.PASSIVE_PROVIDER;
    // Or use LocationManager.GPS_PROVIDER

    final LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    final Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

    if (lastKnownLocation != null) {
      if (lastKnownLocation.getTime() <= System.currentTimeMillis()) {
        assertTrue(true);
      }
    }
  }


}
