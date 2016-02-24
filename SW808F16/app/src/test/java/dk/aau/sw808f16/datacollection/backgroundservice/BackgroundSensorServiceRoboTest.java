package dk.aau.sw808f16.datacollection.backgroundservice;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ServiceController;

import dk.aau.sw808f16.datacollection.BuildConfig;

@SuppressWarnings("unused")
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class BackgroundSensorServiceRoboTest {

  private BackgroundSensorService backgroundSensorService;

  @Before
  public void setup() {
    final ServiceController serviceController = Robolectric.buildService(BackgroundSensorService.class).create();
    backgroundSensorService = (BackgroundSensorService) serviceController.get();
  }

  @Test
  public void testServiceInstantiable() {
    Assert.assertNotNull(backgroundSensorService);
  }
}
