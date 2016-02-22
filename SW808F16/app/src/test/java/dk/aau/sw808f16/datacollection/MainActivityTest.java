package dk.aau.sw808f16.datacollection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {

  private MainActivity mainActivity;

  @Before
  public void setup() {
    mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
  }

  @Test
  public void testStartQuestionnaireButtonExists() {
    Assert.assertNotNull(mainActivity.findViewById(R.id.start_questionnaire_button));
  }

}
