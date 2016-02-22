package dk.aau.sw808f16.datacollection;


import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.BuildConfig;
import dk.aau.sw808f16.datacollection.QuestionnaireActivity;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class QuestionnaireActivityTest {

  private QuestionnaireActivity questionnaireActivity;
  private Questionnaire questionnaire;

  @Before
  public void setup() {
    List<Question> questions = new ArrayList<Question>() {
      {
        add(new Question("Annie are you okay?"));
        add(new Question("Are you okay Annie?"));
      }
    };

    questionnaire = new Questionnaire(questions);

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.putExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER, questionnaire);

    questionnaireActivity = Robolectric.buildActivity(QuestionnaireActivity.class).withIntent(intent).create().get();
  }

  @Test
  public void testQuestionnaireParcelIdentifier() {
    String questionnaireParcelIdentifier = QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER;

    Assert.assertNotNull(questionnaireParcelIdentifier);
  }

  @Test
  public void testQuestionTextViewExists() {
    Assert.assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_question_text) instanceof TextView);
  }

  @Test
  public void testQuestionYesButtonExists() {
    Assert.assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes) instanceof Button);
  }

  @Test
  public void testQuestionNoButtonExists() {
    Assert.assertTrue(questionnaireActivity.findViewById(R.id.questionnaire_answer_button_no) instanceof Button);
  }

  @Test
  public void testTextViewSetGivenQuestion() {
    TextView questionText = (TextView) questionnaireActivity.findViewById(R.id.questionnaire_question_text);

    String expected = questionnaire.getQuestions().get(0).getQuestion();

    Assert.assertEquals(expected, questionText.getText());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoIntentSentToActivity() {
    questionnaireActivity = Robolectric.buildActivity(QuestionnaireActivity.class).create().get();
  }

  @Test
  public void testYesAnswerButtonAnnotatesQuestion() {
    Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    yesButton.performClick();

    final Boolean expected = true;
    Assert.assertEquals(expected, questionnaire.getQuestions().get(0).getAnswer());
  }

  @Test
  public void testNoAnswerButtonAnnotatesQuestion() {
    Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_no);

    yesButton.performClick();

    final Boolean expected = false;
    Assert.assertEquals(expected, questionnaire.getQuestions().get(0).getAnswer());
  }

  @Test
  public void testQuestionNotAnsweredYet() {
    final Boolean expected = null;
    Assert.assertEquals(expected, questionnaire.getQuestions().get(0).getAnswer());
  }

  @Test
  public void testYesAnswerButtonGoesToNextQuestion() {
    TextView questionTextView = (TextView) questionnaireActivity.findViewById(R.id.questionnaire_question_text);
    Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    yesButton.performClick();

    final String expected = questionnaire.getQuestions().get(1).getQuestion();
    final String actual = (String) questionTextView.getText();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testNoAnswerButtonGoesToNextQuestion() {
    TextView questionTextView = (TextView) questionnaireActivity.findViewById(R.id.questionnaire_question_text);
    Button noButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_no);

    noButton.performClick();

    final String expected = questionnaire.getQuestions().get(1).getQuestion();
    final String actual = (String) questionTextView.getText();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testAnsweredLastQuestion() {
    Button yesButton = (Button) questionnaireActivity.findViewById(R.id.questionnaire_answer_button_yes);

    for(int i = 0; i < questionnaire.getQuestions().size(); i++) {
      yesButton.performClick();
    }

    Assert.assertTrue(questionnaireActivity.isFinishing());
  }

}
