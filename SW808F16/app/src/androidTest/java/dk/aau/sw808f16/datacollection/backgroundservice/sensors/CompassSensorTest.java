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

  public void testRetrieveCompassDataForPeriodContainsValues()
      throws ExecutionException, InterruptedException {

    final long startTime = System.currentTimeMillis();
    final long duration = 60000;

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final CompassSensorProvider compassSensorProvider = new CompassSensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData =
        compassSensorProvider.retrieveCompassDataForPeriod(getContext(), startTime, duration);

    final List<Float> data = futureData.get();

    assertTrue(data != null && !data.isEmpty());
  }

  public void testRetrieveCompassDataForPeriodValuesInRange()
      throws ExecutionException, InterruptedException {

    final long startTime = System.currentTimeMillis();
    final long duration = 60000;

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final CompassSensorProvider compassSensorProvider = new CompassSensorProvider(sensorThreadPool);

    final Future<List<Float>> futureData
        = compassSensorProvider.retrieveCompassDataForPeriod(getContext(), startTime, duration);

    final List<Float> data = futureData.get();

    for (final Float orientationValue : data) {
      assertTrue("Values should be within arc degree range", orientationValue < 360 && orientationValue >= 0);
    }

    assertTrue(data != null && !data.isEmpty());
  }

}
