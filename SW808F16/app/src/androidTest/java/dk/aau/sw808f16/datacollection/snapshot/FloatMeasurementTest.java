package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class FloatMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  public FloatMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new FloatMeasurement();
    new FloatMeasurement(3f);
    new FloatMeasurement(Float.valueOf(3f));
  }

  public void testExtendsRealmObject() {
    assertTrue(FloatMeasurement.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(FloatMeasurement.class));
  }

  public void testEqualsNull() {
    final FloatMeasurement floatMeasurement = new FloatMeasurement();

    assertNotSame(floatMeasurement, null);
  }

  public void testEqualsSameValues() {
    final FloatMeasurement floatMeasurement1 = new FloatMeasurement(2f);
    final FloatMeasurement floatMeasurement2 = new FloatMeasurement(2f);

    assertEquals(floatMeasurement1, floatMeasurement2);
  }

  public void testEqualsDifferentValues() {
    final FloatMeasurement floatMeasurement1 = new FloatMeasurement(2f);
    final FloatMeasurement floatMeasurement2 = new FloatMeasurement(3f);

    assertNotSame(floatMeasurement1, floatMeasurement2);
  }

  public void testEqualsSameReference() {
    final FloatMeasurement floatMeasurement1 = new FloatMeasurement(4f);
    final FloatMeasurement floatMeasurement2 = floatMeasurement1;

    assertEquals(floatMeasurement1, floatMeasurement2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final FloatMeasurement floatMeasurement = new FloatMeasurement(13.37f);

    realm.beginTransaction();
    realm.copyToRealm(floatMeasurement);
    realm.commitTransaction();

    final FloatMeasurement loadedFloatMeasurement = realm.where(FloatMeasurement.class).findFirst();

    assertEquals(floatMeasurement, loadedFloatMeasurement);

    realm.close();

    Realm.deleteRealm(realmConfiguration);
  }
}
