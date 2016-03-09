package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.pm.PackageManager;
import android.location.Location;

public class LocationSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new LocationSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    assertTrue("[" + sampleIdentifier + "] measurement is not of type Location", measurement instanceof Location);
  }

  @Override
  protected boolean hasSensor() {
    return getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
  }
}
