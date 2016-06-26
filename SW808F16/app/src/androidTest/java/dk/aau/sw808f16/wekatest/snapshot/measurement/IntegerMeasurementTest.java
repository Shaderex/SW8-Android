package dk.aau.sw808f16.wekatest.snapshot.measurement;

import android.test.ApplicationTestCase;

import dk.aau.sw808f16.wekatest.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class IntegerMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  public IntegerMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new IntegerMeasurement();
    new IntegerMeasurement(3);
  }

  public void testExtendsRealmObject() {
    assertTrue(IntegerMeasurement.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(IntegerMeasurement.class));
  }

  public void testEqualsNull() {
    final IntegerMeasurement integerMeasurement = new IntegerMeasurement();

    assertNotSame(integerMeasurement, null);
  }

  public void testEqualsSameValues() {
    final IntegerMeasurement integerMeasurement1 = new IntegerMeasurement(2);
    final IntegerMeasurement integerMeasurement2 = new IntegerMeasurement(2);

    assertEquals(integerMeasurement1, integerMeasurement2);
  }

  public void testEqualsDifferentValues() {
    final IntegerMeasurement integerMeasurement1 = new IntegerMeasurement(2);
    final IntegerMeasurement integerMeasurement2 = new IntegerMeasurement(3);

    assertNotSame(integerMeasurement1, integerMeasurement2);
  }

  public void testEqualsSameReference() {
    final IntegerMeasurement integerMeasurement1 = new IntegerMeasurement(4);
    final IntegerMeasurement integerMeasurement2 = integerMeasurement1;

    assertEquals(integerMeasurement1, integerMeasurement2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_integer_measurement.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final IntegerMeasurement integerMeasurement = new IntegerMeasurement(13);

    realm.beginTransaction();
    realm.copyToRealm(integerMeasurement);
    realm.commitTransaction();

    final IntegerMeasurement loadedIntegerMeasurement = realm.where(IntegerMeasurement.class).findFirst();

    final boolean equals = integerMeasurement.equals(loadedIntegerMeasurement);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }
}
