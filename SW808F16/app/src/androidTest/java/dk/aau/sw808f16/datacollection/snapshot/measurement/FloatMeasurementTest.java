package dk.aau.sw808f16.datacollection.snapshot.measurement;

import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.RealmObject;

public class FloatMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  public FloatMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void testExtendsRealmObject() {
    assertTrue("FloatMeasurement does not extend RealmObject", FloatMeasurement.class.isAssignableFrom(RealmObject.class));
  }

  public void testConstructors() {
    final float value = 2f;
    new FloatMeasurement();
    new FloatMeasurement(value);
  }

  public void testSetterAndGetter() {
    final float value = 2f;
    FloatMeasurement floatMeasurement = new FloatMeasurement();
    floatMeasurement.setValue(value);
    assertEquals(value, floatMeasurement.getValue());
  }


}