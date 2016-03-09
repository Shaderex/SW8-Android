package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

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
}
