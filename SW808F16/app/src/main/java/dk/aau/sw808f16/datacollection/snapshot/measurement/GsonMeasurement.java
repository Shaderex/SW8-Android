package dk.aau.sw808f16.datacollection.snapshot.measurement;

import com.google.gson.GsonBuilder;

import io.realm.RealmObject;

public class GsonMeasurement<T> extends RealmObject {
  private String gsonString;

  public GsonMeasurement(T t) {
    setValue(t);
  }

  public GsonMeasurement() {
  }

  public void setValue(final T value) {
    this.gsonString = new GsonBuilder().create().toJson(value);
  }

  public T getValue(Class<T> objectClass) {
    return new GsonBuilder().create().fromJson(gsonString, objectClass);
  }
}
