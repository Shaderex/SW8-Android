package dk.aau.sw808f16.datacollection.questionaire.parser;

import android.content.Context;
import android.test.ApplicationTestCase;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;

public final class FileToQuestionnaireParserTest extends ApplicationTestCase<DataCollectionApplication> {

  private Context context;
  private Questionnaire questionnaire;

  public FileToQuestionnaireParserTest() {
    super(DataCollectionApplication.class);
  }

  public void setUp() throws URISyntaxException {
    Question question1 = new Question("How are you?");
    Question question2 = new Question("How are you feeling?");

    questionnaire = new Questionnaire();
    questionnaire.getQuestions().add(question1);
    questionnaire.getQuestions().add(question2);
  }

  public void testCannotBeInstantiated() {
    try {
      Constructor<FileToQuestionnaireParser> con = FileToQuestionnaireParser.class.getDeclaredConstructor();
      con.setAccessible(true); // bypass "private"
      con.newInstance();
      fail("The parser should not be instantiable");
    } catch (Exception exception) {
      assertTrue("Exception not correct type", exception.getCause() instanceof RuntimeException);
      assertEquals("Exception not correct message", exception.getCause().getMessage(), "You cannot instantiate this class");
    }
  }

  public void testFileParser() throws IOException {
    Questionnaire actual = FileToQuestionnaireParser.parseFile(context, R.raw.questionnaire);

    assertEquals(questionnaire, actual);
  }
}
