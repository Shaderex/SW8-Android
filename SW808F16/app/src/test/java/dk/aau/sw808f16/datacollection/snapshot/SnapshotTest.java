package dk.aau.sw808f16.datacollection.snapshot;

import android.hardware.Sensor;

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
  public void testAddSampleGetSamples() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample = new Sample();
    final int sensorType = Sensor.TYPE_ACCELEROMETER;

    snapshot.addSample(sensorType, sample);

    Assert.assertTrue(snapshot.getSamples(sensorType).contains(sample));
  }

  @Test
  public void testEmptyGetSamples() {
    final Snapshot snapshot = new Snapshot();

    final List<Sample> actual = snapshot.getSamples(Sensor.TYPE_ACCELEROMETER);

    Assert.assertTrue(actual.isEmpty());
  }

  @Test
  public void testAddSamplesGetSamples() {
    final Snapshot snapshot = new Snapshot();
    final int sensorType = Sensor.TYPE_ACCELEROMETER;
    final List<Sample> expected = new ArrayList<Sample>() {
      {
        add(new Sample());
        add(new Sample());
        add(new Sample());
      }
    };

    snapshot.addSamples(sensorType, expected);

    Assert.assertEquals(expected, snapshot.getSamples(sensorType));

  }

}
