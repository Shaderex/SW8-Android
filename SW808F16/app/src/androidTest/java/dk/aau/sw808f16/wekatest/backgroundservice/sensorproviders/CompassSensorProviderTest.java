package dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders;

import java.util.concurrent.ExecutionException;

import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatMeasurement;

public class CompassSensorProviderTest extends SensorProviderApplicationTestCase {
  @Override
  protected SensorProvider getSensorProvider() {
    return new CompassSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(final Object measurement, final String sampleIdentifier) {

    if (!(measurement instanceof FloatMeasurement)) {
      assertEquals("[" + sampleIdentifier + "] Measurement is of wrong type.", FloatMeasurement.class, measurement.getClass());
    }

    final int maxDegrees = 360;
    final int minDegrees = 0;

    @SuppressWarnings("ConstantConditions")
    final FloatMeasurement orientationValue = (FloatMeasurement) measurement;

    assertTrue("[" + sampleIdentifier + "] measurement value are too large (not smaller than 360 degrees)",
        orientationValue.getValue() < maxDegrees);

    assertTrue("[" + sampleIdentifier + "] measurement value are too small (below 0 degrees)",
        orientationValue.getValue() >= minDegrees);
  }

  @Override
  public void testGetSamples() throws ExecutionException, InterruptedException {
    super.testGetSamples();
  }
}
