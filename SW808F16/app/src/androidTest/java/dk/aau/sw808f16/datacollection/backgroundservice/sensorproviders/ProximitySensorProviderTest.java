package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.pm.PackageManager;
import android.hardware.Sensor;

import junit.framework.Assert;

public class ProximitySensorProviderTest extends SensorProviderApplicationTestCase {
  @Override
  protected SensorProvider getSensorProvider() {
    return new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    if (!(measurement instanceof Float)) {
      Assert.assertEquals("[" + sampleIdentifier + "] measurement is of wrong type.", Float.class, measurement.getClass());
    }

    final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    final float maxValue = proximitySensor.getMaximumRange();
    final float minValue = 0;

    @SuppressWarnings("ConstantConditions")
    Float proximityValue = (Float) measurement;
    assertTrue("[" + sampleIdentifier + "] value of measurement must be below or equal to " + maxValue, proximityValue <= maxValue);
    assertTrue("[" + sampleIdentifier + "] value of measurement must be larger or equal to " + maxValue, proximityValue >= minValue);
  }

  @Override
  protected boolean hasSensor() {
    return getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
  }
}
