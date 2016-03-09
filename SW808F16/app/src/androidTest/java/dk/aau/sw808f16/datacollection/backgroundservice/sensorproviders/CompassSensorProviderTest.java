package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import junit.framework.Assert;

public class CompassSensorProviderTest extends SensorProviderApplicationTestCase {
  @Override
  protected SensorProvider getSensorProvider() {
    return new CompassSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    if (!(measurement instanceof Float)) {
      Assert.assertEquals("[" + sampleIdentifier + "] Measurement is of wrong type.", Float.class, measurement.getClass());
    }

    final int maxDegrees = 360;
    final int minDegrees = 0;

    @SuppressWarnings("ConstantConditions")
    Float orientationValue = (Float) measurement;

    assertTrue("[" + sampleIdentifier + "] measurement value are too large (not smaller than 360 degrees)", orientationValue < maxDegrees);
    assertTrue("[" + sampleIdentifier + "] measurement value are too small (below 0 degrees)", orientationValue >= minDegrees);
  }
}
