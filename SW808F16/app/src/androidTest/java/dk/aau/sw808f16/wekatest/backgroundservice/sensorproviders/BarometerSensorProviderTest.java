package dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders;

import android.hardware.Sensor;

import java.util.concurrent.ExecutionException;

import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatMeasurement;

public class BarometerSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new BarometerSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(final Object measurement, final String sampleIdentifier) {
    final Sensor barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    final float maxValue = barometerSensor.getMaximumRange();
    final float minValue = -barometerSensor.getMaximumRange();

    if (!(measurement instanceof FloatMeasurement)) {
      assertEquals("[" + sampleIdentifier + "] Barometer measurement is of wrong type.", FloatMeasurement.class, measurement.getClass());
    }

    @SuppressWarnings("ConstantConditions")
    FloatMeasurement pressure = (FloatMeasurement) measurement;
    assertTrue("[" + sampleIdentifier + "] Measurement value is below " + minValue, pressure.getValue() > minValue);
    assertTrue("[" + sampleIdentifier + "] Measurement value is above " + minValue, pressure.getValue() < maxValue);
  }

  @Override
  public void testGetSamples() throws ExecutionException, InterruptedException {
    super.testGetSamples();
  }
}
