package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.label.Label;

public class SnapshotTest {

  @Test
  public void testConstructor() {
    new Snapshot();
  }

  @Test
  public void testGetSetLabel() {
    final Label expectedLabel = new Label();
    final Snapshot snapshot = new Snapshot();

    snapshot.setLabel(expectedLabel);

    Assert.assertEquals(expectedLabel, snapshot.getLabel());
  }

  @Test
  public void testGetSetSample() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample1 = new Sample();
    final Sample sample2 = new Sample();

    final List<Sample> samples = new ArrayList<>();
    samples.add(sample1);
    samples.add(sample2);

    snapshot.setSamples(samples);

    Assert.assertEquals(snapshot.getSamples(), samples);
  }

  @Test
  public void testAddSample() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample = new Sample();

    snapshot.addSample(sample);

    Assert.assertTrue(snapshot.getSamples().contains(sample));
  }

  @Test
  public void testAddSamples() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample1 = new Sample();
    final Sample sample2 = new Sample();

    final List<Sample> samples = new ArrayList<>();
    samples.add(sample1);
    samples.add(sample2);

    snapshot.addSamples(samples);

    for (final Sample sample : samples) {
      Assert.assertTrue(snapshot.getSamples().contains(sample));
    }
  }

}
