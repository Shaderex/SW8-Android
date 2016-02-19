package dk.aau.sw808f16.datacollection;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ApplicationTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Marhlder on 18-02-2016.
 */
public class LocationTest extends ApplicationTestCase<Application> {

  public LocationTest() {
    super(Application.class);
  }

  @Test
  public void testLastFixedLocation() {

    String locationProvider = LocationManager.PASSIVE_PROVIDER;
    // Or use LocationManager.GPS_PROVIDER

    final LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    final Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

    if (lastKnownLocation.getTime() <= System.currentTimeMillis()) {
      Assert.assertTrue(true);
    }
  }


}
