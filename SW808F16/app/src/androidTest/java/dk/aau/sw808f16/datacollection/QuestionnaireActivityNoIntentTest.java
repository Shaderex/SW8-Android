package dk.aau.sw808f16.datacollection;


import android.content.Intent;
import android.test.ActivityUnitTestCase;

import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;


public class QuestionnaireActivityNoIntentTest extends ActivityUnitTestCase<QuestionnaireActivity> {

  private QuestionnaireActivity questionnaireActivity;
  private Questionnaire questionnaire;

  public QuestionnaireActivityNoIntentTest() {
    super(QuestionnaireActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  public void testNoIntentSentToActivity() {

    try {

      final Intent intent = new Intent(getInstrumentation().getTargetContext(), QuestionnaireActivity.class);

      questionnaireActivity = startActivity(intent, null, null);  //launchActivityWithIntent(, MainActivity.class,);
      questionnaire = questionnaireActivity.getQuestionnaire();

    } catch (IllegalArgumentException e) {
      return;
    }

    fail("QuestionnaireActivity did not throw an IllegalArgumentException when started without require questionnaire bundle");
  }

}
