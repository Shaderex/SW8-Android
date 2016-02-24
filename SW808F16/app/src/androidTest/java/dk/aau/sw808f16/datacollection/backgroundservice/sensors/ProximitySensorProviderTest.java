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

public class ProximitySensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {

  private static final long duration = 10000; // In milliseconds
  private static final int samplingPeriod = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  public ProximitySensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * duration / samplingPeriod);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public void testProximitySensorProviderData() throws ExecutionException, InterruptedException {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(sensorThreadPool, sensorManager);

    final Future<List<Float>> futureData = proximitySensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);
    final List<Float> data = futureData.get();

    final Future<List<Float>> futureData2 = proximitySensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);
    final List<Float> data2 = futureData2.get();

    assertNotNull("Sensor data is null", data);
    assertFalse("Sensor data is empty", data.isEmpty());

    assertNotNull("Sensor data is null (second measure)", data2);
    assertFalse("Sensor data is empty (second measure)", data2.isEmpty());

    final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    final float maxValue = proximitySensor.getMaximumRange();
    final float minValue = 0;

    for (final Float proximityValue : data) {
      assertTrue("Value must be below or equal to " + maxValue, proximityValue <= maxValue);
      assertTrue("Value must be larger or equal to " + maxValue, proximityValue >= minValue);
    }

    for (final Float proximityValue : data2) {
      assertTrue("Value must be below or equal to " + maxValue, proximityValue <= maxValue);
      assertTrue("Value must be larger or equal to " + maxValue, proximityValue >= minValue);
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", data.size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", data.size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)", data2.size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)", data2.size() <= maxSize);
  }
}
