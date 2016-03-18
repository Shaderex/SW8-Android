package dk.aau.sw808f16.datacollection.snapshot;

import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;
import io.realm.RealmList;
import io.realm.RealmObject;

public class Sample extends RealmObject {
  private RealmList<RealmObject> measurements;

  public Sample() {
    this.measurements = new RealmList<>();
  }

  public Sample(final RealmObject initialMeasurement) {
    this();
    addMeasurement(initialMeasurement);
  }

  public Sample(final RealmList<RealmObject> initialMeasurements) {
    this();
    addMeasurements(initialMeasurements);
  }

  public RealmList getMeasurements() {
    return measurements;
  }

  public void setMeasurements(final RealmList<RealmObject> measurements) {
    this.measurements = new RealmList<>();
    addMeasurements(measurements);
  }

  public void addMeasurements(final RealmList<RealmObject> measurements) {
    for (final RealmObject measurement : measurements) {
      addMeasurement(measurement);
    }
  }

  public void addMeasurement(RealmObject measurement) {
    this.measurements.add(measurement);
  }
}
