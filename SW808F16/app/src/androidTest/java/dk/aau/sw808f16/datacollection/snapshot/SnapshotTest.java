package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.label.Label;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

@SuppressWarnings("deprecation")
public class SnapshotTest extends ApplicationTestCase<DataCollectionApplication> {

  public SnapshotTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Snapshot();
    Snapshot.Create();
  }

  public void testExtendsRealmObject() {
    assertTrue(Snapshot.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Snapshot.class));
  }

  public void testGetSetLabel() {
    final Label expectedLabel = new Label();
    final Snapshot snapshot = new Snapshot();

    snapshot.setLabel(expectedLabel);

    assertEquals(expectedLabel, snapshot.getLabel());
  }

  public void testGetSetQuestionnaire() {
    final Snapshot snapshot = new Snapshot();
    final Question question = new Question("Question");

    ArrayList<Question> questions = new ArrayList<>();
    questions.add(question);

    final Questionnaire expected = new Questionnaire(questions);

    snapshot.setQuestionnaire(expected);

    assertEquals(expected, snapshot.getQuestionnaire());
  }

  public void testAddSampleGetSamples() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));
    final SensorType sensorType = SensorType.ACCELEROMETER;

    snapshot.addSample(sensorType, sample);

    assertTrue(snapshot.getSamples(sensorType).contains(sample));
  }

  public void testEmptyGetSamples() {
    final Snapshot snapshot = new Snapshot();

    final List<Sample> actual = snapshot.getSamples(SensorType.ACCELEROMETER);

    assertTrue(actual.isEmpty());
  }

  public void testAddSamplesGetSamples() {

    final Snapshot snapshot = new Snapshot();
    final SensorType sensorType = SensorType.ACCELEROMETER;

    final List<Sample> expected = new ArrayList<Sample>() {
      {
        add(Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f)));
        add(Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f)));
        add(Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f)));
      }
    };

    snapshot.addSamples(sensorType, expected);

    assertEquals(expected, snapshot.getSamples(sensorType));
  }

  public void testEqualsNull() {
    final Snapshot snapshot = new Snapshot();

    assertNotSame(snapshot, null);
  }

  public void testEqualsDifferentTimestamp() throws InterruptedException {

    Snapshot snapshot1 = Snapshot.Create();
    Thread.sleep(100);
    Snapshot snapshot2 = Snapshot.Create();

    boolean equals = snapshot1.equals(snapshot2);

    assertFalse("They are equals", equals);
  }

  public void testEqualsEmptySnapshot() {
    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();

    assertEquals(snapshot1, snapshot2);
  }

  public void testEqualsEmptyAndNonEmptySnapshot() {
    final Sample sample = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));

    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();
    snapshot2.addSample(SensorType.ACCELEROMETER, sample);

    assertNotSame(snapshot1, snapshot2);
  }

  public void testEqualsSingleElementSameReference() {
    final Sample sample = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));

    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();
    snapshot1.addSample(SensorType.ACCELEROMETER, sample);
    snapshot2.addSample(SensorType.ACCELEROMETER, sample);

    assertEquals(snapshot1, snapshot2);
  }

  public void testEqualsSingleElementSameValue() {
    final Sample sample1 = new Sample();
    sample1.addMeasurement(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample sample2 = new Sample();
    sample2.addMeasurement(new FloatTripleMeasurement(1f, 2f, 3f));

    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();
    snapshot1.addSample(SensorType.ACCELEROMETER, sample1);
    snapshot2.addSample(SensorType.ACCELEROMETER, sample2);

    assertEquals(snapshot1, snapshot2);
  }

  public void testEqualsSingleElementDifferentValues() {
    final Sample sample1 = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample sample2 = Sample.Create(new FloatTripleMeasurement(4f, 5f, 6f));

    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();
    snapshot1.addSample(SensorType.ACCELEROMETER, sample1);
    snapshot2.addSample(SensorType.ACCELEROMETER, sample2);

    assertNotSame(snapshot1, snapshot2);
  }

  public void testEqualsSameReference() {
    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = snapshot1;

    assertEquals(snapshot1, snapshot2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_snapshot.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Sample sample = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));
    final Snapshot snapshot = new Snapshot();
    snapshot.addSample(SensorType.ACCELEROMETER, sample);

    realm.beginTransaction();
    realm.copyToRealm(snapshot);
    realm.commitTransaction();

    final Snapshot loadedSnapshot = realm.where(Snapshot.class).findFirst();

    final boolean equals = snapshot.equals(loadedSnapshot);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded snapshot was not equal to the original", equals);
  }

  public void testCanBecomeJson() throws JSONException {
    final Sample accelerometerSample = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample gyroscopeSample = Sample.Create(new FloatTripleMeasurement(4f, 5f, 6f));
    final Sample barometerSample = Sample.Create(new FloatMeasurement(4f));

    final Sample largeCompassSample = Sample.Create(Arrays.asList(
        new FloatMeasurement(4f),
        new FloatMeasurement(42f),
        new FloatMeasurement(130f),
        new FloatMeasurement(222f),
        new FloatMeasurement(99f),
        new FloatMeasurement(100f),
        new FloatMeasurement(329f)
    ));

    final Snapshot snapshot = Snapshot.Create();

    snapshot.addSample(SensorType.ACCELEROMETER, accelerometerSample);
    snapshot.addSample(SensorType.GYROSCOPE, gyroscopeSample);
    snapshot.addSample(SensorType.BAROMETER, barometerSample);
    snapshot.addSample(SensorType.COMPASS, largeCompassSample);

    final List<Question> questions = new ArrayList<Question>() {
      {
        new Question("Annie are you okay?", 1);
        new Question("Are you okay Annie?", 2);
      }
    };
    final Questionnaire questionnaire = new Questionnaire(questions);

    snapshot.setQuestionnaire(questionnaire);

    final JSONObject snapshotJsonObject = snapshot.toJsonObject();
    final String snapshotJsonObjectStringRepresentation = snapshotJsonObject.toString();

    assertNotNull(snapshotJsonObjectStringRepresentation);

    assertTrue("Does not contain measurements", snapshotJsonObjectStringRepresentation.contains("measurements"));
    assertTrue("Does not contain compass", snapshotJsonObjectStringRepresentation.contains("compass"));
    assertTrue("Does not contain gyroscope", snapshotJsonObjectStringRepresentation.contains("gyroscope"));
    assertTrue("Does not contain barometer", snapshotJsonObjectStringRepresentation.contains("barometer"));
    assertTrue("Does not contain accelerometer", snapshotJsonObjectStringRepresentation.contains("accelerometer"));
    assertTrue("Does not contain timestamp", snapshotJsonObjectStringRepresentation.contains("timestamp"));
    assertTrue("Does not contain questionnaire", snapshotJsonObjectStringRepresentation.contains("questionnaire"));
  }

  public void testCanBecomeJsonAndRealmAndJson() throws JSONException {
    final Sample accelerometerSample = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample gyroscopeSample = Sample.Create(new FloatTripleMeasurement(4f, 5f, 6f));
    final Sample barometerSample = Sample.Create(new FloatMeasurement(4f));

    final Sample largeCompassSample = Sample.Create(Arrays.asList(
        new FloatMeasurement(4f),
        new FloatMeasurement(42f),
        new FloatMeasurement(130f),
        new FloatMeasurement(222f),
        new FloatMeasurement(99f),
        new FloatMeasurement(100f),
        new FloatMeasurement(329f)
    ));

    final Snapshot originalSnapshot = Snapshot.Create();

    originalSnapshot.addSample(SensorType.ACCELEROMETER, accelerometerSample);
    originalSnapshot.addSample(SensorType.GYROSCOPE, gyroscopeSample);
    originalSnapshot.addSample(SensorType.BAROMETER, barometerSample);
    originalSnapshot.addSample(SensorType.COMPASS, largeCompassSample);

    final List<Question> questions = new ArrayList<Question>() {
      {
        new Question("Annie are you okay?", 1);
        new Question("Are you okay Annie?", 2);
      }
    };
    final Questionnaire questionnaire = new Questionnaire(questions);

    originalSnapshot.setQuestionnaire(questionnaire);

    final JSONObject originalSnapshotJsonObject = originalSnapshot.toJsonObject();
    final String originalSnapshotJsonObjectStringRepresentation = originalSnapshotJsonObject.toString();

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_snapshot.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    realm.beginTransaction();
    realm.copyToRealm(originalSnapshot);
    realm.commitTransaction();

    final Snapshot loadedSnapshot = realm.where(Snapshot.class).findFirst();
    final String loadedSnapShotStringRepresentation = loadedSnapshot.toJsonObject().toString();

    final boolean equals = loadedSnapShotStringRepresentation.equals(originalSnapshotJsonObjectStringRepresentation);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded snapshot JSON string was not equal to the original", equals);
  }

}
