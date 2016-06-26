package dk.aau.sw808f16.wekatest;


import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class QuestionnaireActivityNoIntentTest extends ActivityUnitTestCase<QuestionnaireActivity> {

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

      QuestionnaireActivity questionnaireActivity = startActivity(intent, null, null);
      questionnaireActivity.getQuestionnaire(); // This should throw an exception

      fail("QuestionnaireActivity did not throw an IllegalArgumentException when started without require questionnaire bundle");
    } catch (IllegalArgumentException exception) {
      // We want to catch this exception in order for the test to pass
    }
  }

}
