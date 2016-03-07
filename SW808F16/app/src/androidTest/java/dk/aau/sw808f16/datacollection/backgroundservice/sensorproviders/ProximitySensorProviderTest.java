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

public class ProximitySensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {

  private static final long sampleDuration = 10000; // In milliseconds
  private static final int measurementFrequency = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public ProximitySensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testProximitySensorProviderData() throws ExecutionException, InterruptedException {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);

    final Sample sample1 = proximitySensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);
    final Sample sample2 = proximitySensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor data is null", sample1);
    assertFalse("Sensor data is empty", sample1.getMeasurements().isEmpty());

    assertNotNull("Sensor data is null (second measure)", sample2);
    assertFalse("Sensor data is empty (second measure)", sample2.getMeasurements().isEmpty());

    final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    final float maxValue = proximitySensor.getMaximumRange();
    final float minValue = 0;

    for (final Object measurement : sample1.getMeasurements()) {
      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Proximity sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float proximityValue = (Float) measurement;
      assertTrue("Value must be below or equal to " + maxValue, proximityValue <= maxValue);
      assertTrue("Value must be larger or equal to " + maxValue, proximityValue >= minValue);
    }

    for (final Object measurement : sample2.getMeasurements()) {
      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Proximity sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float proximityValue = (Float) measurement;
      assertTrue("Value must be below or equal to " + maxValue, proximityValue <= maxValue);
      assertTrue("Value must be larger or equal to " + maxValue, proximityValue >= minValue);
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", sample1.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", sample1.getMeasurements().size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)",
        sample2.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)",
        sample2.getMeasurements().size() <= maxSize);
  }
}
