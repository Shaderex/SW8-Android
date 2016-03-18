package dk.aau.sw808f16.datacollection.snapshot.measurement;

import io.realm.RealmObject;

public class FloatMeasurement extends RealmObject {
  private float value;

  public FloatMeasurement(final float value) {
    this.value = value;
  }

  public FloatMeasurement() {
  }

  public float getValue() {
    return value;
  }

  public void setValue(final float value) {
    this.value = value;
  }
}
