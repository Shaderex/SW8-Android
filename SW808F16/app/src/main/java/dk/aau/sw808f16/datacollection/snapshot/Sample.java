package dk.aau.sw808f16.datacollection.snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sample {
  private List<Object> measurements;

  public Sample() {
    this.measurements = new ArrayList<>();
  }

  public Sample(final Object initialMeasurement) {
    this();
    addMeasurement(initialMeasurement);
  }

  public Sample(final List<?> initialMeasurements) {
    this();
    addMeasurements(initialMeasurements);
  }

  public List<?> getMeasurements() {
    return measurements;
  }

  public void setMeasurements(final List<?> measurements) {
    this.measurements = new ArrayList<>();
    addMeasurements(measurements);
  }

  public void addMeasurements(final List<?> measurements) {
    for (final Object measurement : measurements) {
      addMeasurement(measurement);
    }
  }

  public void addMeasurement(Object measurement) {
    this.measurements.add(measurement);
  }
}
