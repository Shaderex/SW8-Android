package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.hardware.Sensor;

import junit.framework.Assert;

import java.util.concurrent.ExecutionException;

public class AmbientLightSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new AmbientLightSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {

    final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    final float maxValue = gyroscopeSensor.getMaximumRange();
    final float minValue = -gyroscopeSensor.getMaximumRange();

    if (!(measurement instanceof Float)) {
      Assert.assertEquals("[" + sampleIdentifier + "] Ambient light sensor data is of wrong type.", Float.class, measurement.getClass());
    }

    @SuppressWarnings("ConstantConditions")
    Float ambientValue = (Float) measurement;
    assertTrue("[" + sampleIdentifier + "] The value is below " + minValue, ambientValue > minValue);
    assertTrue("[" + sampleIdentifier + "] The value is above " + minValue, ambientValue < maxValue);
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
