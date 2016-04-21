package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;
import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;
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

  public void testExtendsRealmObject() {
    assertTrue(Question.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Question.class));
  }


  public void testAnswerQuestion() {
    Question question = new Question(this.question);
    question.setAnswer(true);
  }

  public void testGetAnswer() {
    Question question = new Question(this.question);
    question.setAnswer(false);

    final Boolean expected = false;
    //noinspection ConstantConditions
    assertEquals("Answer not as expected", expected, question.getAnswer());
  }

  public void testGetAnswerNotYetAnswered() {
    Question question = new Question(this.question);

    assertNull(question.getAnswer());
  }

  public void testEquals() {
    Question question1 = new Question(this.question);
    Question question2 = new Question(this.question);

    assertEquals(question1, question2);
  }

  public void testNotEquals() {
    Question question1 = new Question(this.question);
    Question question2 = new Question(this.question + " How are you feeling?");

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

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_question.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Question question = new Question("Hvorn skern?");

    realm.beginTransaction();
    realm.copyToRealm(question);
    realm.commitTransaction();

    final Question loadedQuestion = realm.where(Question.class).findFirst();

    final boolean equals = question.equals(loadedQuestion);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }
}