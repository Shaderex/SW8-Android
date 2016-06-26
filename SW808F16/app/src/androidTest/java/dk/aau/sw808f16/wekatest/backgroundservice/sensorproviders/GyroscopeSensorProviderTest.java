package dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders;

import android.hardware.Sensor;

import java.util.concurrent.ExecutionException;

import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatTripleMeasurement;

public class GyroscopeSensorProviderTest extends SensorProviderApplicationTestCase {
  @Override
  protected SensorProvider getSensorProvider() {
    return new GyroscopeSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(final Object measurement, final String sampleIdentifier) {
    if (!(measurement instanceof FloatTripleMeasurement)) {
      assertEquals("Compass sensor data is of wrong type.", FloatTripleMeasurement.class, measurement.getClass());
    }

    final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    final float maxValue = gyroscopeSensor.getMaximumRange();
    final float minValue = -gyroscopeSensor.getMaximumRange();

    @SuppressWarnings("ConstantConditions")
    FloatTripleMeasurement gyroscopeValues = (FloatTripleMeasurement) measurement;

    assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
        gyroscopeValues.getFirstValue() <= maxValue && gyroscopeValues.getFirstValue() >= minValue);
    assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
        gyroscopeValues.getSecondValue() <= maxValue && gyroscopeValues.getSecondValue() >= minValue);
    assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
        gyroscopeValues.getThirdValue() <= maxValue && gyroscopeValues.getThirdValue() >= minValue);
  }

  @Override
  public void testGetSamples() throws ExecutionException, InterruptedException {
    super.testGetSamples();
  }
}
