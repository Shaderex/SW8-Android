package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.hardware.Sensor;

import java.util.concurrent.ExecutionException;

import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;

public class AmbientLightSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new AmbientLightSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(final Object measurement, final String sampleIdentifier) {

    final Sensor ambientLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    final float maxValue = ambientLightSensor.getMaximumRange();
    final float minValue = -ambientLightSensor.getMaximumRange();

    if (!(measurement instanceof FloatMeasurement)) {
      assertEquals("[" + sampleIdentifier + "] Ambient light sensor data is of wrong type.", FloatMeasurement.class, measurement.getClass());
    }

    @SuppressWarnings("ConstantConditions")
    final FloatMeasurement ambientValue = (FloatMeasurement) measurement;
    assertTrue("[" + sampleIdentifier + "] The value is below " + minValue, ambientValue.getValue() > minValue);
    assertTrue("[" + sampleIdentifier + "] The value is above " + minValue, ambientValue.getValue() < maxValue);
  }


  @Override
  public void testGetSamples() throws ExecutionException, InterruptedException {
    super.testGetSamples();
  }
}
