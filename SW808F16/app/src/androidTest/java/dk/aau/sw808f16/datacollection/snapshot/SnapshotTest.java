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
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class SnapshotTest extends ApplicationTestCase<DataCollectionApplication> {

  public SnapshotTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Snapshot();
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

  public void testAddSampleGetSamples() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));
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
        add(new Sample(new FloatTripleMeasurement(1f, 2f, 3f)));
        add(new Sample(new FloatTripleMeasurement(1f, 2f, 3f)));
        add(new Sample(new FloatTripleMeasurement(1f, 2f, 3f)));
      }
    };

    snapshot.addSamples(sensorType, expected);

    assertEquals(expected, snapshot.getSamples(sensorType));
  }

  public void testEqualsNull() {
    final Snapshot snapshot = new Snapshot();

    assertNotSame(snapshot, null);
  }

  public void testEqualsEmptySnapshot() {
    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();

    assertEquals(snapshot1, snapshot2);
  }

  public void testEqualsEmptyAndNonEmptySnapshot() {
    final Sample sample = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));

    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();
    snapshot2.addSample(SensorType.ACCELEROMETER, sample);

    assertNotSame(snapshot1, snapshot2);
  }

  public void testEqualsSingleElementSameReference() {
    final Sample sample = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));

    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();
    snapshot1.addSample(SensorType.ACCELEROMETER, sample);
    snapshot2.addSample(SensorType.ACCELEROMETER, sample);

    assertEquals(snapshot1, snapshot2);
  }

  public void testEqualsSingleElementSameValue() {
    final Sample sample1 = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample sample2 = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));

    final Snapshot snapshot1 = new Snapshot();
    final Snapshot snapshot2 = new Snapshot();
    snapshot1.addSample(SensorType.ACCELEROMETER, sample1);
    snapshot2.addSample(SensorType.ACCELEROMETER, sample2);

    assertEquals(snapshot1, snapshot2);
  }

  public void testEqualsSingleElementDifferentValues() {
    final Sample sample1 = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample sample2 = new Sample(new FloatTripleMeasurement(4f, 5f, 6f));

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

    final Sample sample = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));
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
    final Sample accelerometerSample = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample gyroscopeSample = new Sample(new FloatTripleMeasurement(4f, 5f, 6f));
    final Sample barometerSample = new Sample(new FloatMeasurement(4f));

    final Sample largeCompassSample = new Sample(Arrays.asList(
        new FloatMeasurement(4f),
        new FloatMeasurement(42f),
        new FloatMeasurement(130f),
        new FloatMeasurement(222f),
        new FloatMeasurement(99f),
        new FloatMeasurement(100f),
        new FloatMeasurement(329f)
    ));

    final Snapshot snapshot = new Snapshot();

    snapshot.addSample(SensorType.ACCELEROMETER, accelerometerSample);
    snapshot.addSample(SensorType.GYROSCOPE, gyroscopeSample);
    snapshot.addSample(SensorType.BAROMETER, barometerSample);
    snapshot.addSample(SensorType.COMPASS, largeCompassSample);


    final JSONObject snapshotJsonObject = snapshot.toJSONObject();
    final String snapshotJsonObjectStringRepresentation = snapshotJsonObject.toString();


    assertNotNull(snapshotJsonObjectStringRepresentation);

    assertTrue(snapshotJsonObjectStringRepresentation.contains("measurements"));
    assertTrue(snapshotJsonObjectStringRepresentation.contains("compass"));
    assertTrue(snapshotJsonObjectStringRepresentation.contains("gyroscope"));
    assertTrue(snapshotJsonObjectStringRepresentation.contains("barometer"));
    assertTrue(snapshotJsonObjectStringRepresentation.contains("accelerometer"));
  }

  public void testCanBecomeJsonAndRealmAndJson() throws JSONException {
    final Sample accelerometerSample = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));
    final Sample gyroscopeSample = new Sample(new FloatTripleMeasurement(4f, 5f, 6f));
    final Sample barometerSample = new Sample(new FloatMeasurement(4f));

    final Sample largeCompassSample = new Sample(Arrays.asList(
        new FloatMeasurement(4f),
        new FloatMeasurement(42f),
        new FloatMeasurement(130f),
        new FloatMeasurement(222f),
        new FloatMeasurement(99f),
        new FloatMeasurement(100f),
        new FloatMeasurement(329f)
    ));

    final Snapshot originalSnapshot = new Snapshot();

    originalSnapshot.addSample(SensorType.ACCELEROMETER, accelerometerSample);
    originalSnapshot.addSample(SensorType.GYROSCOPE, gyroscopeSample);
    originalSnapshot.addSample(SensorType.BAROMETER, barometerSample);
    originalSnapshot.addSample(SensorType.COMPASS, largeCompassSample);

    final JSONObject originalSnapshotJsonObject = originalSnapshot.toJSONObject();
    final String originalSnapshotJsonObjectStringRepresentation = originalSnapshotJsonObject.toString();

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_snapshot.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    realm.beginTransaction();
    realm.copyToRealm(originalSnapshot);
    realm.commitTransaction();

    final Snapshot loadedSnapshot = realm.where(Snapshot.class).findFirst();
    final String loadedSnapShotStringRepresentation = loadedSnapshot.toJSONObject().toString();

    final boolean equals = loadedSnapShotStringRepresentation.equals(originalSnapshotJsonObjectStringRepresentation);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded snapshot JSON string was not equal to the original", equals);
  }

}
