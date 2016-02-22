package dk.aau.sw808f16.datacollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;

public class QuestionnaireActivity extends Activity {

  public static final String QUESTIONNAIRE_PARCEL_IDENTIFIER = "QUESTIONNAIRE_PARCEL_IDENTIFIER";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_questionnaire);

    Intent intent = getIntent();
    Questionnaire questionnaire = intent.getParcelableExtra(QUESTIONNAIRE_PARCEL_IDENTIFIER);

    if (questionnaire == null) {
      throw new IllegalArgumentException("Illegal intent sent to activity. Questionnaire was null");
    }

    TextView questionText = (TextView) findViewById(R.id.questionnaire_question_text);
    questionText.setText(questionnaire.getNextQuestion().getQuestion());
  }
}
