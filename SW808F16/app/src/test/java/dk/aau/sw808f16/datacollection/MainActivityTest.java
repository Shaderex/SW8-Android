package dk.aau.sw808f16.datacollection;

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

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class MainActivityTest {

  private MainActivity mainActivity;

  @Before
  public void setup() {
    final ActivityController activityController = Robolectric.buildActivity(MainActivity.class).create();
    mainActivity = (MainActivity) activityController.get();

  }

  @Test
  public void testStartQuestionnaireButtonExists() {
    Assert.assertNotNull(mainActivity.findViewById(R.id.start_questionnaire_button));
  }

  @Test
  public void testStartQuestionnaireWhenButtonIsPressed() {
    final Button startQuestionnaireButton = (Button) mainActivity.findViewById(R.id.start_questionnaire_button);

    startQuestionnaireButton.performClick();

    ShadowActivity shadowActivity = Shadows.shadowOf(mainActivity);
    Intent startedIntent = shadowActivity.getNextStartedActivity();
    ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
    Assert.assertEquals(shadowIntent.getComponent().getClassName(), QuestionnaireActivity.class.getName());
  }

}
