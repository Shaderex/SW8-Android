package dk.aau.sw808f16.datacollection.snapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.aau.sw808f16.datacollection.label.Label;

public class Snapshot {

  private Label label;
  private final Map<Integer, List<Sample>> samplesMap;

  public Snapshot() {
    samplesMap = new HashMap<>();
  }

  public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public void addSample(int sensorType, Sample sample) {
    if (!samplesMap.containsKey(sensorType)) {
      samplesMap.put(sensorType, new ArrayList<Sample>());
    }
    samplesMap.get(sensorType).add(sample);
  }

  public List<Sample> getSamples(int sensorType) {
    if (!samplesMap.containsKey(sensorType)) {
      // Return an empty list to indicate that no samples are available for the requested sensor
      return new ArrayList<>();
    }

    return samplesMap.get(sensorType);
  }

  public void addSamples(int sensorType, List<Sample> samples) {
    for (final Sample sample : samples) {
      addSample(sensorType, sample);
    }
  }
}
