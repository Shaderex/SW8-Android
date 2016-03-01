package dk.aau.sw808f16.datacollection.snapshot;

import java.util.ArrayList;
import java.util.List;

public class Sample {
  private List<Measurement> measurements;

  public Sample() {
    this.measurements = new ArrayList<>();
  }

  public void setMeasurements(final List<Measurement> measurements) {
    this.measurements = measurements;
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }

  public void addMeasurements(final List<Measurement> measurements) {
    for (final Measurement measurement : measurements) {
      addMeasurement(measurement);
    }
  }

  public void addMeasurement(Measurement measurement) {
    if (this.measurements.size() > 0 && !this.measurements.get(0).getDataType().equals(measurement.getDataType())) {
      throw new IllegalArgumentException("Type of new measurement is incompatible with current measures");
    }

    this.measurements.add(measurement);
  }
}
