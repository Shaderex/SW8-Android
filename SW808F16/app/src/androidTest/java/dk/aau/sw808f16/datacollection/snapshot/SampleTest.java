package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class SampleTest extends ApplicationTestCase<DataCollectionApplication> {

  public SampleTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Sample();

    new Sample(new FloatTripleMeasurement(1f, 2f, 3f));

    new Sample(Arrays.asList(new FloatTripleMeasurement(1f, 1f, 1f), new FloatTripleMeasurement(2f, 2f, 2f), new FloatTripleMeasurement(3f, 3f, 3f)));
  }

  public void testExtendsRealmObject() {
    assertTrue(Sample.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Sample.class));
  }

  public void testAddFloatTripleMeasurement() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 1f, 1f);
    final Sample sample1 = new Sample(floatTripleMeasurement);

    boolean foundActual = false;
    for (final Object m : sample1.getMeasurements()) {
      final FloatTripleMeasurement ft = (FloatTripleMeasurement) m;
      if (ft.equals(floatTripleMeasurement)) {
        foundActual = true;
        break;
      }
    }

    assertTrue("Added measurement not found in sample", foundActual);
  }

  public void testAddFloatMeasurement() {
    final FloatMeasurement floatMeasurement = new FloatMeasurement(1f);
    final Sample sample2 = new Sample(floatMeasurement);

    boolean foundActual = false;
    for (final Object m : sample2.getMeasurements()) {
      final FloatMeasurement f = (FloatMeasurement) m;
      if (f.equals(floatMeasurement)) {
        foundActual = true;
        break;
      }
    }

    assertTrue("Added measurement not found in sample", foundActual);
  }

  public void testAddFloatTripleMeasurements() {
    final FloatTripleMeasurement measurement1 = new FloatTripleMeasurement(1f, 1f, 1f);
    final Sample sample = new Sample(measurement1);

    final FloatTripleMeasurement measurement2 = new FloatTripleMeasurement(2f, 2f, 2f);
    final FloatTripleMeasurement measurement3 = new FloatTripleMeasurement(3f, 3f, 3f);

    sample.addMeasurements(Arrays.asList(measurement2, measurement3));

    final ArrayList<FloatTripleMeasurement> expectedMeasurements = new ArrayList<>();
    expectedMeasurements.add(measurement1);
    expectedMeasurements.add(measurement2);
    expectedMeasurements.add(measurement3);

    assertEquals("The sample does not contain the correct amount of measurements",
        expectedMeasurements.size(), sample.getMeasurements().size());

    for (int i = 0; i < expectedMeasurements.size(); i++) {
      final FloatTripleMeasurement expected = expectedMeasurements.get(i);
      final FloatTripleMeasurement actual = ((FloatTripleMeasurement) sample.getMeasurements().get(i));

      assertEquals("The measurements at position " + i + " are not equal", expected, actual);
    }
  }

  public void testAddFloatMeasurements() {
    final FloatMeasurement measurement1 = new FloatMeasurement(1f);
    final Sample sample = new Sample(measurement1);

    final FloatMeasurement measurement2 = new FloatMeasurement(2f);
    final FloatMeasurement measurement3 = new FloatMeasurement(3f);

    sample.addMeasurements(Arrays.asList(measurement2, measurement3));

    final ArrayList<FloatMeasurement> expectedMeasurements = new ArrayList<>();
    expectedMeasurements.add(measurement1);
    expectedMeasurements.add(measurement2);
    expectedMeasurements.add(measurement3);

    assertEquals("The sample does not contain the correct amount of measurements",
        expectedMeasurements.size(), sample.getMeasurements().size());

    for (int i = 0; i < expectedMeasurements.size(); i++) {
      final FloatMeasurement expected = expectedMeasurements.get(i);
      final FloatMeasurement actual = ((FloatMeasurement) sample.getMeasurements().get(i));

      assertEquals("The measurements at position " + i + " are not equal", expected, actual);
    }
  }

  public void testAddMeasurementInconsistentMeasurementTypes() {
    final FloatMeasurement measurement1 = new FloatMeasurement(1f);
    final FloatTripleMeasurement measurement2 = new FloatTripleMeasurement(1f, 2f, 3f);
    final Sample sample = new Sample();

    sample.addMeasurement(measurement1);

    boolean threwException = false;
    try {
      sample.addMeasurement(measurement2);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testAddMeasurementsInconsistentMeasurementTypes() {
    final FloatMeasurement measurement1 = new FloatMeasurement(1f);
    final FloatTripleMeasurement measurement2 = new FloatTripleMeasurement(1f, 2f, 3f);
    final Sample sample = new Sample();

    sample.addMeasurement(measurement1);

    boolean threwException = false;
    try {
      sample.addMeasurements(Collections.singletonList(measurement2));
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testAddUnsupportedMeasurement() {
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

  public void testAddUnsupportedMeasurements() {
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
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);

    final Sample sample1 = new Sample();
    final Sample sample2 = new Sample(floatTripleMeasurement);

    assertNotSame(sample1, sample2);
  }

  public void testEqualsSingleElementSameReference() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);

    final Sample sample1 = new Sample(floatTripleMeasurement);
    final Sample sample2 = new Sample(floatTripleMeasurement);

    assertEquals(sample1, sample2);
  }

  public void testEqualsSingleElementSameValue() {
    final FloatTripleMeasurement floatTripleMeasurement1 = new FloatTripleMeasurement(1f, 2f, 3f);
    final FloatTripleMeasurement floatTripleMeasurement2 = new FloatTripleMeasurement(1f, 2f, 3f);

    Sample sample1 = new Sample(floatTripleMeasurement1);
    Sample sample2 = new Sample(floatTripleMeasurement2);

    assertEquals(sample1, sample2);
  }

  public void testEqualsSingleElementDifferentValues() {
    final FloatTripleMeasurement floatTripleMeasurement1 = new FloatTripleMeasurement(1f, 2f, 3f);
    final FloatTripleMeasurement floatTripleMeasurement2 = new FloatTripleMeasurement(1f, 2f, 4f);

    Sample sample1 = new Sample(floatTripleMeasurement1);
    Sample sample2 = new Sample(floatTripleMeasurement2);

    assertNotSame(sample1, sample2);
  }

  public void testEqualsSameReference() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);

    Sample sample1 = new Sample(floatTripleMeasurement);
    Sample sample2 = sample1;

    assertEquals(sample1, sample2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Sample sample = new Sample(new FloatTripleMeasurement(1f, 2f, 3f));

    realm.beginTransaction();
    realm.copyToRealm(sample);
    realm.commitTransaction();

    final Sample loadedSample = realm.where(Sample.class).findFirst();

    final boolean equals = sample.equals(loadedSample);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded sample was not equal to the original", equals);
  }

}
