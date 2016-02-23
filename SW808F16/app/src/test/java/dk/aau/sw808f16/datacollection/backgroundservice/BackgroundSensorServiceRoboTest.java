package dk.aau.sw808f16.datacollection.backgroundservice;

import android.content.Intent;
import android.widget.Button;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.util.ActivityController;
import org.robolectric.util.ServiceController;

import dk.aau.sw808f16.datacollection.BuildConfig;
import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.QuestionnaireActivity;
import dk.aau.sw808f16.datacollection.R;

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
