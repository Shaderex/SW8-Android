package dk.aau.sw808f16.datacollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;
import dk.aau.sw808f16.datacollection.questionaire.parser.FileToQuestionnaireParser;

public class MainActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final Button startQuestionnaireButton = (Button) findViewById(R.id.start_questionnaire_button);

    startQuestionnaireButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          final Questionnaire questionnaire = FileToQuestionnaireParser.parseFile(MainActivity.this, R.raw.questionnaire);
          final Intent intent = new Intent(MainActivity.this, QuestionnaireActivity.class);
          intent.putExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER, questionnaire);

          startActivity(intent);
        } catch (IOException e) {
          Toast.makeText(MainActivity.this, "Could not start questionnaire", Toast.LENGTH_LONG).show();
        }
      }
    });
  }
}
