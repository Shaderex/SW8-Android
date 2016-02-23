package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.BuildConfig;

@SuppressWarnings("unused")
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class QuestionnaireTest extends TestCase {

  private List<Question> questions;
  private List<Question> questionsSame;
  private List<Question> questionsSameDifferentOrder;
  private List<Question> questionsDifferent;

  @Before
  public void setUp() {
    Question question1 = new Question("How are you?");
    Question question2 = new Question("Are you okay?");

    questions = new ArrayList<>();
    questions.add(question1);
    questions.add(question2);

    questionsSame = new ArrayList<>();
    questionsSame.add(question1);
    questionsSame.add(question2);

    questionsSameDifferentOrder = new ArrayList<>();
    questionsSameDifferentOrder.add(question2);
    questionsSameDifferentOrder.add(question1);

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
  public void testEqualsOrderDependent() {
    Questionnaire questionnaire1 = new Questionnaire(questions);
    Questionnaire questionnaire2 = new Questionnaire(questionsSameDifferentOrder);

    assertFalse(questionnaire1.equals(questionnaire2));
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

  @Test
  public void testParcelable() {
    Questionnaire questionnaire = new Questionnaire();

    Parcel parcel = Parcel.obtain();
    questionnaire.writeToParcel(parcel, 0);

    parcel.setDataPosition(0);

    Questionnaire createdFromParcel = Questionnaire.CREATOR.createFromParcel(parcel);

    assertEquals(questionnaire, createdFromParcel);
  }
}