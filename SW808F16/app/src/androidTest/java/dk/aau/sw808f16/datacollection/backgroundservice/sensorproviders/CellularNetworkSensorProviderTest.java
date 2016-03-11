package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.telephony.CellInfo;

import java.util.List;

public class CellularNetworkSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new CellularNetworkSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    assertNotNull("[" + sampleIdentifier + "] measurement is null", measurement);

    assertTrue("[" + sampleIdentifier + "] measurement is not a List", List.class.isAssignableFrom(measurement.getClass()));

    List<?> list = (List<?>) measurement;

    for (final Object object : list) {
      assertTrue("[" + sampleIdentifier + "] item in measurement is of wrong type", CellInfo.class.isAssignableFrom(object.getClass()));
    }

  }
}
