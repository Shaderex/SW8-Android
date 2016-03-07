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
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class BarometerSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {
  private static final long sampleDuration = 10000; // In milliseconds
  private static final int measurementFrequency = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public BarometerSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testBarometerSensorProviderData() throws Exception {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final BarometerSensorProvider barometerSensorProvider = new BarometerSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final Sample sample1 = barometerSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);
    final Sample sample2 = barometerSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor data is null", sample1);
    assertFalse("Sensor data is empty", sample1.getMeasurements().isEmpty());

    assertNotNull("Sensor data is null (second measure)", sample2);
    assertFalse("Sensor data is empty (second measure)", sample2.getMeasurements().isEmpty());

    final Sensor barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    final float maxValue = barometerSensor.getMaximumRange();
    final float minValue = -barometerSensor.getMaximumRange();

    for (final Object measurement : sample1.getMeasurements()) {
      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Barometer sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float pressure = (Float) measurement;
      assertTrue("The value is below " + minValue, pressure > minValue);
      assertTrue("The value is above " + minValue, pressure < maxValue);
    }

    for (final Object measurement : sample1.getMeasurements()) {
      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Barometer sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float pressure = (Float) measurement;
      assertTrue("The value is below " + minValue, pressure > minValue);
      assertTrue("The value is above " + minValue, pressure < maxValue);
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", sample1.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", sample1.getMeasurements().size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)",
        sample2.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)",
        sample2.getMeasurements().size() <= maxSize);
  }
}
