package dk.aau.sw808f16.datacollection.backgroundservice.sensors;///**

import android.content.Context;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.BuildConfig;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class SensorProviderRoboTest {

  Context context;

  @Before
  public void setUp() {
    context = RuntimeEnvironment.application.getApplicationContext();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRetrieveSamplesFromSensorProviderInvalidParameterPair1() {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);

    final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(context, sensorThreadPool, sensorManager);

    proximitySensorProvider.retrieveSamplesForDuration(100, 1000, 10000, 100000);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRetrieveSamplesFromSensorProviderInvalidParameterPair2() {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);

    final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(context, sensorThreadPool, sensorManager);

    proximitySensorProvider.retrieveSamplesForDuration(1000, 1000, 10000, 100000);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRetrieveSamplesFromSensorProviderInvalidParameterPair3() {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);

    final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(context, sensorThreadPool, sensorManager);

    proximitySensorProvider.retrieveSamplesForDuration(10000, 10000, 10000, 100000);
  }

  @Test
  public void testRetrieveSamplesFromSensorProviderWithFrequencyValidParameter() {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    final ProximitySensorProvider proximitySensorProvider = new ProximitySensorProvider(context, sensorThreadPool, sensorManager);

    proximitySensorProvider.retrieveSamplesForDuration(10000, 1000, 100, 10);
  }

}
