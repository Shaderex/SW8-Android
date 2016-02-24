package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;

public class AccelerometerSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {
  private static final long duration = 10000; // In milliseconds
  private static final int samplingPeriod = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public AccelerometerSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * duration / samplingPeriod);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testAccelerometerSensorProviderData() throws ExecutionException, InterruptedException, Exception {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final AccelerometerSensorProvider accelerometerSensorProvider = new AccelerometerSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final List<float[]> data1 = accelerometerSensorProvider.retrieveSampleForDuration(duration, samplingPeriod);

    final List<float[]> data2 = accelerometerSensorProvider.retrieveSampleForDuration(duration, samplingPeriod);

    assertNotNull("Sensor data is null", data1);
    assertFalse("Sensor data is empty", data1.isEmpty());

    assertNotNull("Sensor data (second measure) is null", data2);
    assertFalse("Sensor data (second measure) is empty", data2.isEmpty());

    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    final float maxValue = accelerometerSensor.getMaximumRange();
    final float minValue = -accelerometerSensor.getMaximumRange();

    for (final float[] accelerometerValues : data1) {
      assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
          accelerometerValues[0] <= maxValue && accelerometerValues[0] >= minValue);
      assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
          accelerometerValues[1] <= maxValue && accelerometerValues[1] >= minValue);
      assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
          accelerometerValues[2] <= maxValue && accelerometerValues[2] >= minValue);
    }

    for (final float[] accelerometerValues : data2) {
      assertTrue("Data for index 0 value must be between " + minValue + " and " + maxValue,
          accelerometerValues[0] <= maxValue && accelerometerValues[0] >= minValue);
      assertTrue("Data for index 1 value must be between " + minValue + " and " + maxValue,
          accelerometerValues[1] <= maxValue && accelerometerValues[1] >= minValue);
      assertTrue("Data for index 2 value must be between " + minValue + " and " + maxValue,
          accelerometerValues[2] <= maxValue && accelerometerValues[2] >= minValue);
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", data1.size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", data1.size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)", data2.size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)", data2.size() <= maxSize);
  }
}
