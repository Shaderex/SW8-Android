package dk.aau.sw808f16.datacollection.snapshot.measurement;

import io.realm.RealmObject;

public class FloatMeasurement extends RealmObject {

  private float value;

  public FloatMeasurement() {
  }

  public FloatMeasurement(final float value) {
    this.value = value;
  }

  public FloatMeasurement(final Float value) {
    this.value = value;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  @Override
  public boolean equals(final Object object) {
    return super.equals(object) || (object instanceof FloatMeasurement && ((FloatMeasurement) object).value == this.value);
  }
}
