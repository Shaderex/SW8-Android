package dk.aau.sw808f16.datacollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;

public class QuestionnaireActivity extends Activity {

  public static final String QUESTIONNAIRE_PARCEL_IDENTIFIER = "QUESTIONNAIRE_PARCEL_IDENTIFIER";

  // Questionnaire
  private Questionnaire questionnaire;
  private Question currentQuestion = null;

  // Views
  private TextView questionText;

  @Override
  protected final void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_questionnaire);

    Intent intent = getIntent();
    questionnaire = intent.getParcelableExtra(QUESTIONNAIRE_PARCEL_IDENTIFIER);

    if (questionnaire == null) {
      throw new IllegalArgumentException("Illegal intent sent to activity. Questionnaire was null");
    }

    currentQuestion = questionnaire.getNextQuestion();

    questionText = (TextView) findViewById(R.id.questionnaire_question_text);
    questionText.setText(currentQuestion.getQuestion());

    final Button yesAnswerButton = (Button) findViewById(R.id.questionnaire_answer_button_yes);
    final Button noAnswerButton = (Button) findViewById(R.id.questionnaire_answer_button_no);

    yesAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        currentQuestion.setAnswer(true);
        goToNextQuestion();
      }
    });

    noAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        currentQuestion.setAnswer(false);
        goToNextQuestion();
      }
    });
  }

  private void goToNextQuestion() {
    try {
      currentQuestion = questionnaire.getNextQuestion();
      questionText.setText(currentQuestion.getQuestion());
    } catch (IndexOutOfBoundsException exception) {
      // There are no more questions

      final Intent resultIntent = new Intent();
      resultIntent.putExtra(QUESTIONNAIRE_PARCEL_IDENTIFIER, questionnaire);

      setResult(Activity.RESULT_OK, resultIntent);

      finishActivity(Activity.RESULT_OK);
    }
  }

  @Override
  public final void onBackPressed() {

    final Intent resultIntent = new Intent();
    resultIntent.putExtra(QUESTIONNAIRE_PARCEL_IDENTIFIER, questionnaire);

    setResult(Activity.RESULT_CANCELED, resultIntent);

    super.onBackPressed();
    finishActivity(Activity.RESULT_CANCELED);
  }

  public Questionnaire getQuestionnaire() {
    return questionnaire;
  }
}
