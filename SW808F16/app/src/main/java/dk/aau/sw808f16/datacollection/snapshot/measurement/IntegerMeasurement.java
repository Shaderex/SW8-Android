package dk.aau.sw808f16.datacollection.snapshot.measurement;

import dk.aau.sw808f16.datacollection.snapshot.JsonValueAble;
import io.realm.RealmObject;

public class IntegerMeasurement extends RealmObject implements JsonValueAble {

  private int value;

  public IntegerMeasurement() {
  }

  public IntegerMeasurement(final int value) {
    this.value = value;
  }

  @Override
  public boolean equals(final Object object) {
    return super.equals(object) || (object instanceof IntegerMeasurement && ((IntegerMeasurement) object).value == this.value);
  }

  @Override
  public String toString() {
    return super.toString();
  }

  @Override
  public String toJsonValue() {
    return Integer.toString(value);
  }
}
