package dk.aau.sw808f16.datacollection.questionaire.models;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rex on 19-02-2016.
 */
public class QuestionnaireTest extends TestCase {

  Question question1;
  Question question2;
  List<Question> questions;
  List<Question> questionsSame;
  List<Question> questionsDifferent;

  @Before
  public void setUp() {
    question1 = new Question("How are you?");
    question2 = new Question("Are you okay?");

    questions = new ArrayList<>();
    questions.add(question1);
    questions.add(question2);

    questionsSame = new ArrayList<>();
    questionsSame.add(question1);
    questionsSame.add(question2);

    questionsDifferent = new ArrayList<>();
    questionsDifferent.add(new Question("Annie are you okay?"));
    questionsDifferent.add(new Question("Are you okay, Annie?"));
  }

  @Test
  public void testEmptyConstructor() {
    Questionnaire questionnaire = new Questionnaire();

    assertNotNull(questionnaire);
  }

  @Test
  public void testListConstructor() {

    Questionnaire questionnaire = new Questionnaire(questions);

    assertNotNull(questionnaire);
  }

  @Test
  public void testGetQuestionsList() {
    Questionnaire questionnaire = new Questionnaire(questions);

    List<Question> actual = questionnaire.getQuestions();

    for (Question question : actual) {
      assertTrue(questions.contains(question));
    }
    for (Question question : questions) {
      assertTrue(actual.contains(question));
    }
  }

  @Test
  public void testEquals() {
    Questionnaire questionnaire1 = new Questionnaire(questions);
    Questionnaire questionnaire2 = new Questionnaire(questionsSame);

    assertEquals(questionnaire1, questionnaire2);
  }

  @Test
  public void testNotEquals() {
    Questionnaire questionnaire1 = new Questionnaire(questions);
    Questionnaire questionnaire2 = new Questionnaire(questionsDifferent);

    assertFalse(questionnaire1.equals(questionnaire2));
  }

  @Test
  public void testReferenceEquals() {
    Questionnaire questionnaire1 = new Questionnaire(questions);

    assertEquals(questionnaire1, questionnaire1);
  }

  @Test
  public void testGetNextQuestion() {
    Questionnaire questionnaire = new Questionnaire(questions);

    assertEquals(questions.get(0), questionnaire.getNextQuestion());

    assertEquals(questions.get(1), questionnaire.getNextQuestion());
  }
}