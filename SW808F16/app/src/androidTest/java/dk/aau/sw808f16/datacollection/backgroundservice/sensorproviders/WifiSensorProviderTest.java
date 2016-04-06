package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import java.util.List;
import java.util.concurrent.ExecutionException;

import dk.aau.sw808f16.datacollection.snapshot.measurement.ScanResultMeasurement;

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
      assertTrue("[" + sampleIdentifier + "] item in measurement is of wrong type",
          ScanResultMeasurement.class.isAssignableFrom(object.getClass()));
    }
  }

  @Override
  public void testGetSample() throws ExecutionException, InterruptedException, ClassCastException {
    super.testGetSample();
  }

  @Override
  public void testGetSamples() throws ExecutionException, InterruptedException {
    super.testGetSamples();
  }
}
