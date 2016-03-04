package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.FloatTriple;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class AmbientLightSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {
  private static final long sampleDuration = 10000; // In milliseconds
  private static final int measurementFrequency = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public AmbientLightSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testCompassSensorProviderData() throws Exception {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final AmbientLightSensorProvider ambientLightSensorProvider =
        new AmbientLightSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final Sample data1 = ambientLightSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);
    final Sample data2 = ambientLightSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor data is null", data1);
    assertFalse("Sensor data is empty", data1.getMeasurements().isEmpty());

    assertNotNull("Sensor data is null (second measure)", data2);
    assertFalse("Sensor data is empty (second measure)", data2.getMeasurements().isEmpty());

    final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    final float maxValue = gyroscopeSensor.getMaximumRange();
    final float minValue = -gyroscopeSensor.getMaximumRange();

    for (final Object measurement : data1.getMeasurements()) {
      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Ambient light sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float ambientValue = (Float) measurement;

      assertTrue("The value is below " + minValue, ambientValue > minValue);
      assertTrue("The value is above " + minValue, ambientValue < maxValue);
    }

    for (final Object measurement : data1.getMeasurements()) {
      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Ambient light sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float ambientValue = (Float) measurement;
      assertTrue("The value is below " + minValue, ambientValue > minValue);
      assertTrue("The value is above " + minValue, ambientValue < maxValue);
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", data1.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", data1.getMeasurements().size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)",
        data2.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)",
        data2.getMeasurements().size() <= maxSize);


  }
}
