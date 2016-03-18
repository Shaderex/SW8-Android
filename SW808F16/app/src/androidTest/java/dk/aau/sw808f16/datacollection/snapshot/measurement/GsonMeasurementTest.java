package dk.aau.sw808f16.datacollection.snapshot.measurement;

import android.test.ApplicationTestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.RealmObject;

public class GsonMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  public GsonMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void testExtendsRealmObject() {
    assertTrue("GsonMeasurement does not extend RealmObject", GsonMeasurement.class.isAssignableFrom(RealmObject.class));
  }

  public void testConstructor() {
    new GsonMeasurement<>();
    new GsonMeasurement<>(new Object());
  }

  public void testGetterSetter() {
    final GsonMeasurement<Object> gsonMeasurement = new GsonMeasurement<>();
    final Object object = new Object();

    gsonMeasurement.setValue(object);

    final Gson gson = new GsonBuilder().create();

    assertEquals(gson.toJson(object), gson.toJson(gsonMeasurement.getValue(Object.class)));

  }
}
