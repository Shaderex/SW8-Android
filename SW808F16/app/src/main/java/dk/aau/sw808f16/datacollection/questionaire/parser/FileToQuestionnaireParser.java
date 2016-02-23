package dk.aau.sw808f16.datacollection.questionaire.parser;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;

public class FileToQuestionnaireParser {

  private FileToQuestionnaireParser() {
    throw new RuntimeException("You cannot instantiate this class");
  }

  /**
   * Parses a file from the resources folder with the given resource identifier into a
   * Questionnaire.
   */
  public static Questionnaire parseFile(Context context, int resourceId) throws IOException {
    InputStreamReader streamReader =
        new InputStreamReader(context.getResources().openRawResource(resourceId));

    BufferedReader reader = new BufferedReader(streamReader);

    List<Question> questions = new ArrayList<>();
    String line;

    while ((line = reader.readLine()) != null) {
      Question question = new Question(line);
      questions.add(question);
    }

    streamReader.close();
    reader.close();

    return new Questionnaire(questions);
  }
}
