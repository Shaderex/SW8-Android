package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.hardware.Sensor;

import junit.framework.Assert;

import dk.aau.sw808f16.datacollection.snapshot.FloatTriple;

public class GyroscopeSensorProviderTest extends SensorProviderApplicationTestCase {
  @Override
  protected SensorProvider getSensorProvider() {
    return new GyroscopeSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    if (!(measurement instanceof FloatTriple)) {
      Assert.assertEquals("Compass sensor data is of wrong type.", FloatTriple.class, measurement.getClass());
    }

    final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    final float maxValue = gyroscopeSensor.getMaximumRange();
    final float minValue = -gyroscopeSensor.getMaximumRange();

    @SuppressWarnings("ConstantConditions")
    FloatTriple gyroscopeValues = (FloatTriple) measurement;

    assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
        gyroscopeValues.getFirstValue() <= maxValue && gyroscopeValues.getFirstValue() >= minValue);
    assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
        gyroscopeValues.getSecondValue() <= maxValue && gyroscopeValues.getSecondValue() >= minValue);
    assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
        gyroscopeValues.getThirdValue() <= maxValue && gyroscopeValues.getThirdValue() >= minValue);
  }
}
