package dk.aau.sw808f16.wekatest.questionaire.models;

import android.os.Parcel;
import android.test.ApplicationTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import dk.aau.sw808f16.wekatest.DataCollectionApplication;
import dk.aau.sw808f16.wekatest.snapshot.JsonObjectAble;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class QuestionTest extends ApplicationTestCase<DataCollectionApplication> {

  private final String question = "How are you?";

  public QuestionTest() {
    super(DataCollectionApplication.class);
  }

  public void testNormalConstructor() {

    final Question question = new Question(this.question);

    assertEquals("Questions are not the same", this.question, question.getQuestion());
  }

  public void testNormalConstructorEmptyInput() {
    String questionString = "";

    try {
      new Question(questionString);
      fail("The constructor did not throw an exception");
    } catch (Exception exception) {
      assertTrue(exception instanceof IllegalArgumentException);
      assertEquals(exception.getMessage(), "Question cannot be empty");
    }
  }

  public void testNormalConstructorNullInput() {
    String questionString = null;

    try {
      //noinspection ConstantConditions
      new Question(questionString);
      fail("The constructor did not throw an exception");
    } catch (Exception exception) {
      assertTrue(exception instanceof NullPointerException);
      assertEquals(exception.getMessage(), "Question cannot be null");
    }
  }

  public void testCopyConstructor() {
    String questionString = "How are you?";

    Question expected = new Question(questionString);
    Question actual = new Question(question);

    assertEquals("The copy constructor did make exact copy", expected, actual);
  }

  public void testCopyConstructorAndChange() {
    String questionString = "How are you?";

    Question expected = new Question(questionString);
    Question actual = new Question(question);

    expected.setAnswer(true);

    assertFalse("There still exists references", expected.equals(actual));
  }

  public void testConstructorWithQuestionAndId() {
    Question question = new Question(this.question, 255);
  }

  public void testExtendsRealmObject() {
    assertTrue(Question.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Question.class));
  }

  public void testAnswerQuestion() {
    Question question = new Question(this.question);
    question.setAnswer(true);
  }

  public void testGetAnswer() throws NoSuchFieldException, IllegalAccessException {
    Question question = new Question(this.question);
    question.setAnswer(false);

    final Boolean expected = false;
    //noinspection ConstantConditions
    assertEquals("Answer not as expected", expected, question.getAnswer());

    Field field = question.getClass().getDeclaredField("timestamp");
    field.setAccessible(true);

    long timestamp = field.getLong(question);

    field.setAccessible(false);

    boolean timestampSat = timestamp != 0;
    assertTrue("The timestamp was not sat", timestampSat);

  }

  public void testGetAnswerNotYetAnswered() {
    Question question = new Question(this.question);

    assertNull(question.getAnswer());
  }

  public void testGetSetId() {
    Question question = new Question(this.question);

    long expected = 255;
    question.setIdentifier(expected);

    assertEquals(expected, question.getIdentifier());
  }

  public void testEquals() {
    Question question1 = new Question(this.question, 1);
    Question question2 = new Question(this.question, 1);

    assertEquals(question1, question2);
  }

  public void testNotEqualsDifferentQuestions() {
    Question question1 = new Question(this.question, 1);
    Question question2 = new Question(this.question + " How are you feeling?", 1);

    assertFalse(question1.equals(question2));
  }

  public void testNotEqualsDifferentIdentifiers() {
    Question question1 = new Question(this.question, 1);
    Question question2 = new Question(this.question, 2);

    assertFalse(question1.equals(question2));
  }



  public void testReferenceEquals() {
    Question question1 = new Question(this.question);

    assertEquals(question1, question1);
  }

  public void testParcelableWithoutAnswer() {
    Question question = new Question("Are you okay annie?");

    Parcel parcel = Parcel.obtain();
    question.writeToParcel(parcel, 0);

    parcel.setDataPosition(0);

    Question createdFromParcel = Question.CREATOR.createFromParcel(parcel);

    assertEquals(question, createdFromParcel);
  }

  public void testParcelableWithAnswer() {
    Question question = new Question("Are you okay annie?");
    question.setAnswer(true);

    Parcel parcel = Parcel.obtain();
    question.writeToParcel(parcel, 0);

    parcel.setDataPosition(0);

    Question createdFromParcel = Question.CREATOR.createFromParcel(parcel);

    assertEquals(question, createdFromParcel);
  }

  public void testImplementsJsonObjectable() {
    assertTrue(Question.class.getName() + " does not extend " + JsonObjectAble.class.getName(),
        JsonObjectAble.class.isAssignableFrom(Question.class));
  }

  public void testIsJsonObjectable() throws JSONException {
    Question question = new Question(this.question, 1);

    JSONObject jsonObject = question.toJsonObject();

    assertTrue(jsonObject.has("id") && jsonObject.getLong("id") == 1);
    assertTrue(jsonObject.has("answer") && jsonObject.getString("answer") == "undefined");

    question.setAnswer(true);

    jsonObject = question.toJsonObject();

    assertTrue(jsonObject.has("answer") && jsonObject.getString("answer") == "true");
    assertTrue(jsonObject.has("timestamp") && jsonObject.getLong("timestamp") != 0);

    question.setAnswer(false);

    jsonObject = question.toJsonObject();

    assertTrue(jsonObject.has("answer") && jsonObject.getString("answer") == "false");
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_question.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Question question = new Question("Hvorn skern?");

    realm.beginTransaction();
    realm.copyToRealm(question);
    realm.commitTransaction();

    final Question loadedQuestion = realm.where(Question.class).findFirst();

    final boolean equals = question.equals(loadedQuestion);

    final Boolean loadedAnswer = loadedQuestion.getAnswer();

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
    assertNull("The default value for answer is not null", loadedAnswer);
  }
}