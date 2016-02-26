package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;

public class GyroscopeSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {

  private static final long sampleDuration = 10000; // In milliseconds
  private static final int measurementFrequency = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public GyroscopeSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testAccelerometerSensorProviderData() throws ExecutionException, InterruptedException, Exception {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final GyroscopeSensorProvider gyroscopeSensorProvider = new GyroscopeSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final List<float[]> data1 = gyroscopeSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    final List<float[]> data2 = gyroscopeSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor data is null", data1);
    assertFalse("Sensor data is empty", data1.isEmpty());

    assertNotNull("Sensor data (second measure) is null", data2);
    assertFalse("Sensor data (second measure) is empty", data2.isEmpty());

    final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    final float maxValue = gyroscopeSensor.getMaximumRange();
    final float minValue = -gyroscopeSensor.getMaximumRange();

    for (final float[] values : data1) {
      assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
          values[0] <= maxValue && values[0] >= minValue);
      assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
          values[1] <= maxValue && values[1] >= minValue);
      assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
          values[2] <= maxValue && values[2] >= minValue);
    }

    for (final float[] values : data2) {
      assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
          values[0] <= maxValue && values[0] >= minValue);
      assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
          values[1] <= maxValue && values[1] >= minValue);
      assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
          values[2] <= maxValue && values[2] >= minValue);
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", data1.size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", data1.size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)", data2.size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)", data2.size() <= maxSize);
  }
}
