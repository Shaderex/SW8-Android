package dk.aau.sw808f16.datacollection.questionaire;


import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import dk.aau.sw808f16.datacollection.BuildConfig;
import dk.aau.sw808f16.datacollection.QuestionnaireActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class QuestionnaireActivityTest {

  private QuestionnaireActivity questionnaireActivity;

  @Before
  public void setup() {
    questionnaireActivity = Robolectric.buildActivity(QuestionnaireActivity.class).create().get();
  }

}
