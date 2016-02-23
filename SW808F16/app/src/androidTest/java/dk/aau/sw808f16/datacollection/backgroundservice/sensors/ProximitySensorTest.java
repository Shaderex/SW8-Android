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

public class ProximitySensorTest extends ApplicationTestCase<DataCollectionApplication> {

  public ProximitySensorTest() {
    super(DataCollectionApplication.class);
  }

  public void testRetrieveProximityDataForPeriodContainsValues() throws ExecutionException, InterruptedException {

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData = proximitySensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data = futureData.get();

    assertTrue(data != null && !data.isEmpty());
  }

  public void testRetrieveProximityDataForPeriodValuesInRange() throws ExecutionException, InterruptedException {

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    final float maxValue = proximitySensor.getMaximumRange();
    final float minValue = 0;

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData = proximitySensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data = futureData.get();

    for (final Float proximityValue : data) {
      assertTrue("Value must be within " + maxValue + " and " + minValue, proximityValue <= maxValue && proximityValue >= minValue);
    }
  }

  public void testRetrieveProximityDataForPeriodReuseSensorProvider() throws ExecutionException, InterruptedException {

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    final float maxValue = proximitySensor.getMaximumRange();
    final float minValue = 0;

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(sensorThreadPool);

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final Future<List<Float>> futureData1 = proximitySensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);
    final List<Float> data1 = futureData1.get();

    final Future<List<Float>> futureData2 = proximitySensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);
    final List<Float> data2 = futureData2.get();

    assertTrue(data1 != null && !data1.isEmpty());
    assertTrue(data2 != null && !data2.isEmpty());

    for (final Float proximityValue : data1) {
      assertTrue("Data 1 value must be within " + maxValue + " and " + minValue, proximityValue <= maxValue && proximityValue >= minValue);
    }

    for (final Float proximityValue : data2) {
      assertTrue("Data 2 value must be within " + maxValue + " and " + minValue, proximityValue <= maxValue && proximityValue >= minValue);
    }
  }

  public void testRetrieveProximityDataForPeriodSamplingPeriod() throws ExecutionException, InterruptedException {

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData = proximitySensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data = futureData.get();

    assertTrue(data.size() >= 4 && data.size() <= 6);
  }

}
