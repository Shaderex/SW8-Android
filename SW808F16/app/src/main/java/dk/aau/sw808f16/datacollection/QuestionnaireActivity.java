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
  private Question currentQuestion = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_questionnaire);

    Intent intent = getIntent();
    final Questionnaire questionnaire = intent.getParcelableExtra(QUESTIONNAIRE_PARCEL_IDENTIFIER);

    if (questionnaire == null) {
      throw new IllegalArgumentException("Illegal intent sent to activity. Questionnaire was null");
    }

    currentQuestion = questionnaire.getNextQuestion();

    final TextView questionText = (TextView) findViewById(R.id.questionnaire_question_text);
    questionText.setText(currentQuestion.getQuestion());

    final Button yesAnswerButton = (Button) findViewById(R.id.questionnaire_answer_button_yes);
    final Button noAnswerButton = (Button) findViewById(R.id.questionnaire_answer_button_no);

    yesAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        currentQuestion.setAnswer(true);

        currentQuestion = questionnaire.getNextQuestion();
        questionText.setText(currentQuestion.getQuestion());
      }
    });

    noAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        currentQuestion.setAnswer(false);

        currentQuestion = questionnaire.getNextQuestion();
        questionText.setText(currentQuestion.getQuestion());
      }
    });
  }
}
