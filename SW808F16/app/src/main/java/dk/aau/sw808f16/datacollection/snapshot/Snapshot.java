package dk.aau.sw808f16.datacollection.snapshot;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.label.Label;

public class Snapshot {

  private Label label;
  private List<Sample> samples;

  public Snapshot() {
    this.samples = new ArrayList<>();
  }

  public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public List<Sample> getSamples() {
    return samples;
  }

  public void setSamples(final List<Sample> samples) {
    this.samples = samples;
  }

  public void addSamples(final List<Sample> samples) {
    for (final Sample sample : samples) {
      addSample(sample);
    }
  }

  public void addSample(Sample samples) {
    this.samples.add(samples);
  }
}
