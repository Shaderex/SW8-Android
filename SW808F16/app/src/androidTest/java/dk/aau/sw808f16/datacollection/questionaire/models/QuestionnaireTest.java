package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;
import android.test.ApplicationTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.snapshot.JsonObjectAble;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class QuestionnaireTest extends ApplicationTestCase<DataCollectionApplication> {

  public QuestionnaireTest() {
    super(DataCollectionApplication.class);
  }

  private List<Question> questions;
  private List<Question> questionsSame;
  private List<Question> questionsSameDifferentOrder;
  private List<Question> questionsDifferent;

  @Override
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



  public void testEmptyConstructor() {
    Questionnaire questionnaire = new Questionnaire();

    assertNotNull(questionnaire);
  }

  public void testListConstructor() {

    Questionnaire questionnaire = new Questionnaire(questions);

    assertNotNull(questionnaire);
  }

  public void testCopyConstructor() {
    Questionnaire expected = new Questionnaire(questions);
    Questionnaire actual = new Questionnaire(expected);

    assertEquals("The copy constructor did not make an exact match", expected, actual);
  }

  public void testCopyConstructorChange() {
    Questionnaire expected = new Questionnaire(questions);
    Questionnaire actual = new Questionnaire(expected);

    expected.getQuestions().get(0).setAnswer(true);
    assertFalse("There are copied references present", expected.equals(actual));
  }

  public void testExtendsRealmObject() {
    assertTrue(Questionnaire.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Questionnaire.class));
  }

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

  public void testEquals() {
    Questionnaire questionnaire1 = new Questionnaire(questions);
    Questionnaire questionnaire2 = new Questionnaire(questionsSame);

    assertEquals(questionnaire1, questionnaire2);
  }

  public void testEqualsOrderDependent() {
    Questionnaire questionnaire1 = new Questionnaire(questions);
    Questionnaire questionnaire2 = new Questionnaire(questionsSameDifferentOrder);

    assertFalse(questionnaire1.equals(questionnaire2));
  }

  public void testNotEquals() {
    Questionnaire questionnaire1 = new Questionnaire(questions);
    Questionnaire questionnaire2 = new Questionnaire(questionsDifferent);

    assertFalse(questionnaire1.equals(questionnaire2));
  }

  public void testReferenceEquals() {
    Questionnaire questionnaire1 = new Questionnaire(questions);

    assertEquals(questionnaire1, questionnaire1);
  }

  public void testGetNextQuestion() {
    Questionnaire questionnaire = new Questionnaire(questions);

    assertEquals(questions.get(0), questionnaire.getNextQuestion());

    assertEquals(questions.get(1), questionnaire.getNextQuestion());
  }

  public void testParcelable() {
    Questionnaire questionnaire = new Questionnaire();

    Parcel parcel = Parcel.obtain();
    questionnaire.writeToParcel(parcel, 0);

    parcel.setDataPosition(0);

    Questionnaire createdFromParcel = Questionnaire.CREATOR.createFromParcel(parcel);

    assertEquals(questionnaire, createdFromParcel);
  }

  public void testImplementsJsonObjectable() {
    assertTrue(Question.class.getName() + " does not extend " + JsonObjectAble.class.getName(),
        JsonObjectAble.class.isAssignableFrom(Question.class));
  }

  public void testIsJsonObjectable() throws JSONException {
    Questionnaire questionnaire = new Questionnaire(this.questions);

    JSONObject jsonObject = questionnaire.toJsonObject();

    assertTrue(jsonObject.has("questions") && jsonObject.getJSONArray("questions").length() == this.questions.size());
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_questionnaire.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    Questionnaire questionnaire = new Questionnaire(questions);

    realm.beginTransaction();
    realm.copyToRealm(questionnaire);
    realm.commitTransaction();

    final Questionnaire loadedQuestionnaire = realm.where(Questionnaire.class).findFirst();

    final boolean equals = questionnaire.equals(loadedQuestionnaire);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }
}