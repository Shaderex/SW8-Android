package dk.aau.sw808f16.datacollection.questionaire;


import android.widget.TextView;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import dk.aau.sw808f16.datacollection.BuildConfig;
import dk.aau.sw808f16.datacollection.QuestionnaireActivity;
import dk.aau.sw808f16.datacollection.R;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class QuestionnaireActivityTest {

  private QuestionnaireActivity questionnaireActivity;

  @Before
  public void setup() {
    questionnaireActivity = Robolectric.buildActivity(QuestionnaireActivity.class).create().get();
  }

  @Test
  public void testQuestionTextViewExists() {

    Assert.assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_question_text) instanceof TextView);

  }

  @Test
  public void testQuestionYesButtonExists() {

    Assert.assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes) instanceof TextView);

  }

  @Test
  public void testQuestionNoButtonExists() {

    Assert.assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_answer_button_no) instanceof TextView);

  }


}
