package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.BuildConfig;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.CompassSensorProvider;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class CompassSensorRoboTest {

  private SensorManager sensorManager;

  @Before
  public void setup() {
    sensorManager = (SensorManager) RuntimeEnvironment.application.getSystemService(Context.SENSOR_SERVICE);
  }

  @Test
  public void testCompassSensorProviderInstantiable() throws ExecutionException, InterruptedException {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    @SuppressWarnings("unused")
    final CompassSensorProvider compassSensorProvider =
        new CompassSensorProvider(RuntimeEnvironment.application.getApplicationContext(), sensorThreadPool, sensorManager);
  }
}
