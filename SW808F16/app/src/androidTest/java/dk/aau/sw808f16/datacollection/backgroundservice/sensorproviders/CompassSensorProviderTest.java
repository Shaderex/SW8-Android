package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class CompassSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {

  private static final long sampleDuration = 10000; // In milliseconds
  private static final int measurementFrequency = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public CompassSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testCompassSensorProviderData() throws ExecutionException, InterruptedException {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final CompassSensorProvider compassSensorProvider = new CompassSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final Sample sample1 = compassSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);
    final Sample sample2 = compassSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor sample1 is null", sample1);
    assertFalse("Sensor sample1 is empty", sample1.getMeasurements().isEmpty());

    assertNotNull("Sensor sample2 is null (second measure)", sample2);
    assertFalse("Sensor sample2 is empty (second measure)", sample2.getMeasurements().isEmpty());

    final int maxDegrees = 360;
    final int minDegrees = 0;

    for (final Object measurement : sample1.getMeasurements()) {

      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Compass sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float orientationValue = (Float) measurement;

      assertTrue("Values are too large (not smaller than 360 degrees)", orientationValue < maxDegrees);
      assertTrue("Values are too small (below 0 degrees)", orientationValue >= minDegrees);
    }

    for (final Object measurement : sample2.getMeasurements()) {

      if (!(measurement instanceof Float)) {
        Assert.assertEquals("Compass sensor data is of wrong type.", Float.class, measurement.getClass());
      }

      @SuppressWarnings("ConstantConditions")
      Float orientationValue = (Float) measurement;
      assertTrue("Values are too large (not smaller than 360 degrees) (second measure)", orientationValue < maxDegrees);
      assertTrue("Values are too small (below 0 degrees)) (second measure)", orientationValue >= minDegrees);
    }

    assertTrue("The amount of sample1 and sampling period do not match, not enough sample1", sample1.getMeasurements().size() >= minSize);
    assertTrue("The amount of sample1 and sampling period do not match, too much sample1", sample1.getMeasurements().size() <= maxSize);
    assertTrue("The amount of sample1 and sampling period do not match, not enough sample1 (second measure)", sample2.getMeasurements().size() >= minSize);
    assertTrue("The amount of sample1 and sampling period do not match, too much sample1 (second measure)", sample2.getMeasurements().size() <= maxSize);
  }
}
