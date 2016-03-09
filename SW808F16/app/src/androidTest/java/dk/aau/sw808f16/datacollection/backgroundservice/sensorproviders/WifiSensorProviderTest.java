package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.net.wifi.ScanResult;

import java.util.List;

public class WifiSensorProviderTest extends SensorProviderApplicationTestCase {
  @Override
  protected SensorProvider getSensorProvider() {
    return new WifiSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    assertTrue("[" + sampleIdentifier + "] measurement is not a List", List.class.isAssignableFrom(measurement.getClass()));

    List<?> list = (List<?>) measurement;

    for (final Object object : list) {
      assertTrue("[" + sampleIdentifier + "] item in measurement is of wrong type", ScanResult.class.isAssignableFrom(object.getClass()));
    }
  }
}
