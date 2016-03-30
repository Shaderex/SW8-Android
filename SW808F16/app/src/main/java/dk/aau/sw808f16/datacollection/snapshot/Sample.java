package dk.aau.sw808f16.datacollection.snapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;


public class Sample extends RealmObject {

  private RealmList<FloatTriple> floatTriples = new RealmList<>();

  public Sample() {
  }

  public Sample(final Object initialMeasurement) {
    this(Collections.singletonList(initialMeasurement));
  }

  public Sample(final List<?> initialMeasurements) {
    addMeasurements(initialMeasurements);
  }

  public void addMeasurement(final Object measurement) {
    if (measurement instanceof FloatTriple) {
      floatTriples.add((FloatTriple) measurement);
    } else {
      throw new IllegalArgumentException("Type " + measurement.getClass().getName() + " is not supported measurement type");
    }
  }

  public void addMeasurements(final List<?> measurements) {
    for (final Object measurement : measurements) {
      addMeasurement(measurement);
    }
  }

  public List<?> getMeasurements() {
    List<Object> result = new ArrayList<>();

    // Concatenate the different lists into a single one (there should only be one list containing elements)
    result.addAll(floatTriples);

    return result;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || !Sample.class.isAssignableFrom(o.getClass())) {
      return false;
    }

    final Sample that = (Sample) o;

    List<?> ourMeasurements = this.getMeasurements();
    List<?> theirMeasurements = that.getMeasurements();

    if (ourMeasurements.size() != theirMeasurements.size()) {
      return false;
    } else {
      for (int i = 0; i < ourMeasurements.size(); i++) {
        if (!ourMeasurements.get(i).equals(theirMeasurements.get(i))) {
          return false;
        }
      }
    }

    return true;
  }
}
