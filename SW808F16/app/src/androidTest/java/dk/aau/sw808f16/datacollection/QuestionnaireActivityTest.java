package dk.aau.sw808f16.datacollection;


import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;


public class QuestionnaireActivityTest extends ActivityUnitTestCase<QuestionnaireActivity> {

  private QuestionnaireActivity questionnaireActivity;
  private Questionnaire questionnaire;

  public QuestionnaireActivityTest() {
    super(QuestionnaireActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final List<Question> questions = new ArrayList<Question>() {
      {
        add(new Question("Annie are you okay?"));
        add(new Question("Are you okay Annie?"));
      }
    };

    final Intent intent = new Intent(getInstrumentation().getTargetContext(), QuestionnaireActivity.class);
    intent.putExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER, new Questionnaire(questions));

    //final ComponentName componentName = new ComponentName("dk.aau.sw808f16.datacollection", "MainActivity");
    //final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    //mainActivity

    //setActivityIntent(intent);
    //setActivity(null);

    questionnaireActivity = startActivity(intent, null, null);  //launchActivityWithIntent(, MainActivity.class,);
    questionnaire = questionnaireActivity.getQuestionnaire();
  }

  public void testQuestionnaireParcelIdentifier() {

    final String questionnaireParcelIdentifier = QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER;

    assertNotNull(questionnaireParcelIdentifier);
  }

  public void testQuestionTextViewExists() {
    assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_question_text) instanceof TextView);
  }

  public void testQuestionYesButtonExists() {
    assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes) instanceof Button);
  }

  public void testQuestionNoButtonExists() {
    assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_answer_button_no) instanceof Button);
  }

  public void testTextViewSetGivenQuestion() {

    final TextView questionText = (TextView) questionnaireActivity.findViewById(R.id.questionnaire_question_text);
    final String expected = questionnaireActivity.getQuestionnaire().getQuestions().get(0).getQuestion();

    assertEquals(expected, questionText.getText());
  }

  public void testYesAnswerButtonAnnotatesQuestion() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    questionnaireActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        yesButton.performClick();
        latch.countDown();
      }
    });

    latch.await();

    final Boolean answer = questionnaire.getQuestions().get(0).getAnswer();

    assertEquals(Boolean.TRUE, answer);
  }

  public void testNoAnswerButtonAnnotatesQuestion() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_no);

    questionnaireActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        yesButton.performClick();
        latch.countDown();
      }
    });

    latch.await();

    assertEquals(Boolean.FALSE, questionnaire.getQuestions().get(0).getAnswer());
  }

  public void testQuestionNotAnsweredYet() {

    final Boolean expected = null;
    assertEquals(expected, questionnaire.getQuestions().get(0).getAnswer());
  }

  public void testYesAnswerButtonGoesToNextQuestion() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final TextView questionTextView = (TextView) questionnaireActivity.findViewById(R.id.questionnaire_question_text);
    final Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    questionnaireActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        yesButton.performClick();
        latch.countDown();
      }
    });

    latch.await();

    final String expected = questionnaire.getQuestions().get(1).getQuestion();
    final String actual = (String) questionTextView.getText();

    assertEquals(expected, actual);
  }

  public void testNoAnswerButtonGoesToNextQuestion() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final TextView questionTextView = (TextView) questionnaireActivity.findViewById(R.id.questionnaire_question_text);
    final Button noButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_no);

    questionnaireActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        noButton.performClick();
        latch.countDown();
      }
    });

    latch.await();

    final String expected = questionnaire.getQuestions().get(1).getQuestion();
    final String actual = (String) questionTextView.getText();

    assertEquals(expected, actual);
  }

  public void testAnsweredLastQuestion() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    questionnaireActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {

        for (int i = 0; i < questionnaire.getQuestions().size(); i++) {
          yesButton.performClick();
        }

        latch.countDown();
      }
    });

    latch.await();

    assertTrue(questionnaireActivity.isFinishing());
  }

  public void testActivityFinishedWithCorrectResultCompleted() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    questionnaireActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < questionnaire.getQuestions().size(); i++) {
          yesButton.performClick();
          questionnaire.getQuestions().get(i).setAnswer(true);
        }
        latch.countDown();
      }
    });

    latch.await();

    final Intent resultIntent = getStartedActivityIntent();
    final Questionnaire resultQuestionnaire = resultIntent.getParcelableExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER);

    assertNotNull(getStartedActivityRequest());
    assertEquals(Activity.RESULT_OK, getStartedActivityRequest());
    assertNotNull(resultQuestionnaire);
    assertEquals(questionnaire, resultQuestionnaire);
  }

  public void testActivityFinishedWithCorrectResultCancelled() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);

    questionnaireActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        questionnaireActivity.onBackPressed();
        latch.countDown();
      }
    });

    latch.await();

    final Intent resultIntent = this.getStartedActivityIntent();
    final Questionnaire resultQuestionnaire = resultIntent.getParcelableExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER);

    assertNotNull(getStartedActivityRequest());
    assertEquals(Activity.RESULT_CANCELED, getStartedActivityRequest());
    assertNotNull(resultQuestionnaire);
    assertEquals(questionnaire, resultQuestionnaire);
  }

  public void testActivityFinishedWithCorrectResultNotFullyCompleted() {

    final Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    for (int i = 0; i < questionnaire.getQuestions().size() - 1; i++) {
      yesButton.performClick();
      questionnaire.getQuestions().get(i).setAnswer(true);
    }

    questionnaireActivity.onBackPressed();

    final Intent resultIntent = getStartedActivityIntent();
    final Questionnaire resultQuestionnaire = resultIntent.getParcelableExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER);

    assertNotNull(getFinishedActivityRequest());
    assertEquals(Activity.RESULT_CANCELED, getFinishedActivityRequest());
    assertNotNull(resultQuestionnaire);
    assertEquals(questionnaire, resultQuestionnaire);
  }

}
