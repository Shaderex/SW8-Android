package dk.aau.sw808f16.datacollection.questionaire.parser;

import android.content.Context;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;

import dk.aau.sw808f16.datacollection.BuildConfig;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;

@SuppressWarnings("unused")
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public final class FileToQuestionnaireParserTest extends TestCase {

  private Context context;
  private Questionnaire questionnaire;

  @Before
  public void setUp() throws URISyntaxException {
    context = RuntimeEnvironment.application;

    Question question1 = new Question("How are you?");
    Question question2 = new Question("How are you feeling?");

    questionnaire = new Questionnaire();
    questionnaire.getQuestions().add(question1);
    questionnaire.getQuestions().add(question2);
  }

  @Test
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

  @Test
  public void testFileParser() throws IOException {
    Questionnaire actual = FileToQuestionnaireParser.parseFile(context, R.raw.questionnaire);

    assertEquals(questionnaire, actual);
  }
}
