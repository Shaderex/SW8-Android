package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;///**

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


  @Override
  protected void setUp() {

  }

  public void testRetrieveSamplesFromSensorProviderInvalidParameterPair1() {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);

    try {
      proximitySensorProvider.retrieveSamplesForDuration(100, 1000, 10000, 100000);
    } catch (IllegalArgumentException exception) {
      return;
    }
    fail("Expected illegal argument exception");
  }

  public void testRetrieveSamplesFromSensorProviderInvalidParameterPair2() {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);

    try {
      proximitySensorProvider.retrieveSamplesForDuration(1000, 1000, 10000, 100000);
    } catch (IllegalArgumentException exception) {
      return;
    }
    fail("Expected illegal argument exception");
  }

  public void testRetrieveSamplesFromSensorProviderInvalidParameterPair3() {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);

    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);

    try {
      proximitySensorProvider.retrieveSamplesForDuration(10000, 10000, 10000, 100000);
    } catch (IllegalArgumentException exception) {
      return;
    }
    fail("Expected illegal argument exception");
  }

  public void testRetrieveSamplesFromSensorProviderWithFrequencyValidParameter() {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager);

    proximitySensorProvider.retrieveSamplesForDuration(10000, 1000, 100, 10);
  }

}
