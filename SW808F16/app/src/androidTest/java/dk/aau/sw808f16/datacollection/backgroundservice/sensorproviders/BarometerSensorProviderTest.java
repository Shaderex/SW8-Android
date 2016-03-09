package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.pm.PackageManager;
import android.hardware.Sensor;

import junit.framework.Assert;

public class BarometerSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new BarometerSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    final Sensor barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    final float maxValue = barometerSensor.getMaximumRange();
    final float minValue = -barometerSensor.getMaximumRange();

    if (!(measurement instanceof Float)) {
      Assert.assertEquals("[" + sampleIdentifier + "] Barometer measurement is of wrong type.", Float.class, measurement.getClass());
    }

    @SuppressWarnings("ConstantConditions")
    Float pressure = (Float) measurement;
    assertTrue("[" + sampleIdentifier + "] Measurement value is below " + minValue, pressure > minValue);
    assertTrue("[" + sampleIdentifier + "] Measurement value is above " + minValue, pressure < maxValue);

  }

  @Override
  protected boolean hasSensor() {
    return getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
  }
}
