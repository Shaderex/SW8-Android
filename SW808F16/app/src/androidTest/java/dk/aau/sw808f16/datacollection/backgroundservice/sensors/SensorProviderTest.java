package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;

public class SensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {


  public SensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  public void testRetrieveSamplesFromSensorProviderWithFrequencyInvalidParameter() {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);

    try {
      proximitySensorProvider.retrieveSamplesForDuration(100, 1000, 10000, 100000);
      fail("retrieveSamplesForDuration accepted illegal parameters");
    } catch (IllegalArgumentException exception) {

    }
  }

  public void testRetrieveSamplesFromSensorProviderWithFrequencyValidParameter() {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);

    proximitySensorProvider.retrieveSamplesForDuration(10000, 1000, 100, 10);
  }

}
