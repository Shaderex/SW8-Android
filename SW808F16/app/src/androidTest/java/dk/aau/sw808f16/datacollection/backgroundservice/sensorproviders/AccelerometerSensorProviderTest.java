package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.FloatTriple;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class AccelerometerSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {

  private static final long sampleDuration = 10000; // In milliseconds
  private static final int measurementFrequency = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public AccelerometerSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testAccelerometerSensorProviderData() throws ExecutionException, InterruptedException, ClassCastException {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final AccelerometerSensorProvider accelerometerSensorProvider =
        new AccelerometerSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final Sample sample1 = accelerometerSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    final Sample sample2 = accelerometerSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor data is null", sample1);
    assertFalse("Sensor data is empty", sample1.getMeasurements().isEmpty());

    assertNotNull("Sensor data (second measure) is null", sample2);
    assertFalse("Sensor data (second measure) is empty", sample2.getMeasurements().isEmpty());

    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    final float maxValue = accelerometerSensor.getMaximumRange();
    final float minValue = -accelerometerSensor.getMaximumRange();

    for (final Object measurement : sample1.getMeasurements()) {

      if (!(measurement instanceof FloatTriple)) {
        Assert.assertEquals("Accelerometer sensor data is of wrong type.", FloatTriple.class, measurement.getClass());
      }

      FloatTriple accelerometerValues = (FloatTriple) measurement;

      assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
          accelerometerValues.getFirstValue() <= maxValue && accelerometerValues.getFirstValue() >= minValue);
      assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
          accelerometerValues.getSecondValue() <= maxValue && accelerometerValues.getSecondValue() >= minValue);
      assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
          accelerometerValues.getThirdValue() <= maxValue && accelerometerValues.getThirdValue() >= minValue);
    }

    for (final Object measurement : sample1.getMeasurements()) {

      if (!(measurement instanceof FloatTriple)) {
        Assert.assertEquals("Accelerometer sensor data is of wrong type.", FloatTriple.class, measurement.getClass());
      }

      FloatTriple accelerometerValues = (FloatTriple) measurement;

      assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
          accelerometerValues.getFirstValue() <= maxValue && accelerometerValues.getFirstValue() >= minValue);
      assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
          accelerometerValues.getSecondValue() <= maxValue && accelerometerValues.getSecondValue() >= minValue);
      assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
          accelerometerValues.getThirdValue() <= maxValue && accelerometerValues.getThirdValue() >= minValue);
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", sample1.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", sample1.getMeasurements().size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)",
        sample2.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)",
        sample2.getMeasurements().size() <= maxSize);
  }
}
