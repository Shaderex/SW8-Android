package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.SensorType;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class SampleTest extends ApplicationTestCase<DataCollectionApplication> {


  RealmConfiguration realmConfiguration;

  public SampleTest() {
    super(DataCollectionApplication.class);
  }

  public void setUp() {
    realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_realm.realm").build();
  }

  public void tearDown() {
    Realm.deleteRealm(realmConfiguration);
  }

  public void testConstructor() {
    new Sample();
    new Sample(SensorType.ACCELEROMETER);
    new Sample(SensorType.ACCELEROMETER, new FloatTriple(1f, 2f, 3f));
    new Sample(SensorType.ACCELEROMETER, Arrays.asList(new FloatTriple(1f, 1f, 1f), new FloatTriple(2f, 2f, 2f), new FloatTriple(3f, 3f, 3f)));
  }

  public void testExtendsRealmObject() {
    assertTrue(Sample.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Sample.class));
  }

  public void testGetId() {
    Sample sample = new Sample();
    long id = sample.getId(realmConfiguration);
  }

  public void testAddMeasurements() {
    final Sample sample = new Sample(SensorType.ACCELEROMETER);
    final FloatTriple measurement1 = new FloatTriple(1f, 1f, 1f);
    final FloatTriple measurement2 = new FloatTriple(2f, 2f, 2f);

    final ArrayList<FloatTriple> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.addMeasurements(realmConfiguration, measurements);

    assertEquals("The sample does not contain the correct amount of measurements",
        measurements.size(), sample.getMeasurements(realmConfiguration).size());

    List<Long> ids = new ArrayList<>();

    for (int i = 0; i < measurements.size(); i++) {

      final FloatTriple expected = measurements.get(i);
      final FloatTriple actual = ((FloatTriple) sample.getMeasurements(realmConfiguration).get(i));

      assertEquals("The measurements at position " + i + " are not equal",
          expected.getCompressedValues(), actual.getCompressedValues());

      assertFalse("Identifier class at position " + i + " (sample identifier)", ids.contains(actual.getSampleId()));
      ids.add(actual.getSampleId());
    }
  }

  public void testGetSetMeasurements() {
    final Sample sample = new Sample(SensorType.ACCELEROMETER);
    final FloatTriple measurement1 = new FloatTriple(1f, 1f, 1f);
    final FloatTriple measurement2 = new FloatTriple(2f, 2f, 2f);

    final ArrayList<FloatTriple> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.setMeasurements(measurements);

    assertEquals("The sample does not contain the correct amount of measurements",
        measurements.size(), sample.getMeasurements(realmConfiguration).size());

    List<Long> ids = new ArrayList<>();

    for (int i = 0; i < measurements.size(); i++) {

      final FloatTriple expected = measurements.get(i);
      final FloatTriple actual = ((FloatTriple) sample.getMeasurements(realmConfiguration).get(i));

      assertEquals("The measurements at position " + i + " are not equal",
          expected.getCompressedValues(), actual.getCompressedValues());

      assertFalse("Identifier class at position " + i + " (sample identifier)", ids.contains(actual.getSampleId()));
      ids.add(actual.getSampleId());
    }
  }

  public void testAddMeasurement() {
    final Sample sample = new Sample(SensorType.ACCELEROMETER);
    final FloatTriple measurement = new FloatTriple(1f, 1f, 1f);

    sample.addMeasurement(realmConfiguration, measurement);

    boolean foundActual = false;
    for (final Object m : sample.getMeasurements(realmConfiguration)) {
      final FloatTriple ft = (FloatTriple) m;
      if (ft.getCompressedValues() == measurement.getCompressedValues()) {
        foundActual = true;
        break;
      }
    }

    assertTrue("Added measurement not found in sample", foundActual);
  }

  public void testAddBadMeasurements() {
    final Sample sample = new Sample(SensorType.ACCELEROMETER);
    final Object measurement1 = new Object();
    final Object measurement2 = new Object();

    final ArrayList<Object> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    boolean threwException = false;

    try {
      sample.addMeasurements(realmConfiguration, measurements);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testAddBadMeasurement() {
    final Sample sample = new Sample(SensorType.ACCELEROMETER);
    final Object measurement = new Object();

    boolean threwException = false;

    try {
      sample.addMeasurement(realmConfiguration, measurement);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

}
