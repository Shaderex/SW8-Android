package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Arrays;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class SampleTest extends ApplicationTestCase<DataCollectionApplication> {

  RealmConfiguration realmConfiguration;

  public SampleTest() {
    super(DataCollectionApplication.class);
  }

  public void setUp() {
    realmConfiguration = new RealmConfiguration.Builder(getContext()).name(this.getClass().getName() + ".realm").build();
  }

  public void tearDown() {
    Realm.deleteRealm(realmConfiguration);
  }

  public void testConstructor() {
    new Sample();

    new Sample(new FloatTriple(1f, 2f, 3f));

    new Sample(Arrays.asList(new FloatTriple(1f, 1f, 1f), new FloatTriple(2f, 2f, 2f), new FloatTriple(3f, 3f, 3f)));
  }

  public void testExtendsRealmObject() {
    assertTrue(Sample.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Sample.class));
  }

  public void testAddMeasurement() {
    final FloatTriple measurement = new FloatTriple(1f, 1f, 1f);
    final Sample sample = new Sample(measurement);

    boolean foundActual = false;
    for (final Object m : sample.getMeasurements()) {
      final FloatTriple ft = (FloatTriple) m;
      if (ft.getCompressedValues() == measurement.getCompressedValues()) {
        foundActual = true;
        break;
      }
    }

    assertTrue("Added measurement not found in sample", foundActual);
  }

  public void testAddMeasurements() {
    final FloatTriple measurement1 = new FloatTriple(1f, 1f, 1f);
    final Sample sample = new Sample(measurement1);

    final FloatTriple measurement2 = new FloatTriple(2f, 2f, 2f);
    final FloatTriple measurement3 = new FloatTriple(3f, 3f, 3f);

    sample.addMeasurements(Arrays.asList(measurement2, measurement3));

    final ArrayList<FloatTriple> expectedMeasurements = new ArrayList<>();
    expectedMeasurements.add(measurement1);
    expectedMeasurements.add(measurement2);
    expectedMeasurements.add(measurement3);

    assertEquals("The sample does not contain the correct amount of measurements",
        expectedMeasurements.size(), sample.getMeasurements().size());

    for (int i = 0; i < expectedMeasurements.size(); i++) {
      final FloatTriple expected = expectedMeasurements.get(i);
      final FloatTriple actual = ((FloatTriple) sample.getMeasurements().get(i));

      assertEquals("The measurements at position " + i + " are not equal", expected.getCompressedValues(), actual.getCompressedValues());
    }
  }

  public void testAddBadMeasurement() {
    final Sample sample = new Sample();
    final Object measurement = new Object();

    boolean threwException = false;
    try {
      sample.addMeasurement(measurement);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testAddBadMeasurements() {
    final Sample sample = new Sample();
    final Object measurement1 = new Object();
    final Object measurement2 = new Object();

    final ArrayList<Object> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    boolean threwException = false;

    try {
      sample.addMeasurements(measurements);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Sample sample = new Sample(new FloatTriple(1f, 2f, 3f));

    realm.beginTransaction();
    realm.copyToRealm(sample);
    realm.commitTransaction();

    final Sample loadedSample = realm.where(Sample.class).findFirst();

    assertEquals(sample, loadedSample);

    realm.close();

    Realm.deleteRealm(realmConfiguration);
  }

  public void testEqualsNull() {
    final Sample sample = new Sample();

    assertNotSame(sample, null);
  }

  public void testEqualsEmptySample() {
    final Sample sample1 = new Sample();
    final Sample sample2 = new Sample();

    assertEquals(sample1, sample2);
  }

  public void testEqualsEmptyAndNonEmptySample() {
    final FloatTriple floatTriple = new FloatTriple(1f, 2f, 3f);

    final Sample sample1 = new Sample();
    final Sample sample2 = new Sample(floatTriple);

    assertNotSame(sample1, sample2);
  }

  public void testEqualsSingleElementSameReference() {
    final FloatTriple floatTriple = new FloatTriple(1f, 2f, 3f);

    final Sample sample1 = new Sample(floatTriple);
    final Sample sample2 = new Sample(floatTriple);

    assertEquals(sample1, sample2);
  }

  public void testEqualsSingleElementSameValue() {
    final FloatTriple floatTriple1 = new FloatTriple(1f, 2f, 3f);
    final FloatTriple floatTriple2 = new FloatTriple(1f, 2f, 3f);

    Sample sample1 = new Sample(floatTriple1);
    Sample sample2 = new Sample(floatTriple2);

    assertEquals(sample1, sample2);
  }

  public void testEqualsSingleElementDifferentValues() {
    final FloatTriple floatTriple1 = new FloatTriple(1f, 2f, 3f);
    final FloatTriple floatTriple2 = new FloatTriple(1f, 2f, 4f);

    Sample sample1 = new Sample(floatTriple1);
    Sample sample2 = new Sample(floatTriple2);

    assertNotSame(sample1, sample2);
  }

  public void testEqualsSameReference() {
    final FloatTriple floatTriple = new FloatTriple(1f, 2f, 3f);

    Sample sample1 = new Sample(floatTriple);
    Sample sample2 = sample1;

    assertEquals(sample1, sample2);
  }

}
