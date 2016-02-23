package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.test.ApplicationTestCase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;

public class CompassSensorTest extends ApplicationTestCase<DataCollectionApplication> {

  public CompassSensorTest() {
    super(DataCollectionApplication.class);
  }

  public void testRetrieveCompassDataForPeriodContainsValues() throws ExecutionException, InterruptedException {

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final CompassSensorProvider compassSensorProvider = new CompassSensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData = compassSensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data = futureData.get();

    assertTrue(data != null && !data.isEmpty());
  }

  public void testRetrieveCompassDataForPeriodValuesInRange() throws ExecutionException, InterruptedException {

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final CompassSensorProvider compassSensorProvider = new CompassSensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData = compassSensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data = futureData.get();

    for (final Float orientationValue : data) {
      assertTrue("Values should be within arc degree range", orientationValue < 360 && orientationValue >= 0);
    }
  }

  public void testRetrieveCompassDataForPeriodReuseSensorProvider() throws ExecutionException, InterruptedException {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final CompassSensorProvider compassSensorProvider = new CompassSensorProvider(sensorThreadPool);

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final Future<List<Float>> futureData1 = compassSensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data1 = futureData1.get();

    final Future<List<Float>> futureData2 = compassSensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data2 = futureData2.get();

    assertTrue(data1 != null && !data1.isEmpty());
    assertTrue(data2 != null && !data2.isEmpty());

    for (final Float orientationValue : data1) {
      assertTrue("Values should be within arc degree range", orientationValue < 360 && orientationValue >= 0);
    }

    for (final Float orientationValue : data2) {
      assertTrue("Values should be within arc degree range", orientationValue < 360 && orientationValue >= 0);
    }
  }

  public void testRetrieveCompassDataForPeriodSamplingPeriod() throws ExecutionException, InterruptedException {

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final CompassSensorProvider compassSensorProvider = new CompassSensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData = compassSensorProvider.retrieveDataForPeriod(getContext(), duration, samplingPeriod);

    final List<Float> data = futureData.get();

    assertTrue(data.size() >= 4 && data.size() <= 6);
  }

}
