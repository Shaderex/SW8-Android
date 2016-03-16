package dk.aau.sw808f16.datacollection.snapshot;

import android.hardware.Sensor;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.label.Label;

public class SnapshotTest extends ApplicationTestCase<DataCollectionApplication> {

  public SnapshotTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Snapshot();
  }

  public void testGetSetLabel() {
    final Label expectedLabel = new Label();
    final Snapshot snapshot = new Snapshot();

    snapshot.setLabel(expectedLabel);

    assertEquals(expectedLabel, snapshot.getLabel());
  }

  public void testAddSampleGetSamples() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample = new Sample();
    final int sensorType = Sensor.TYPE_ACCELEROMETER;

    snapshot.addSample(sensorType, sample);

    assertTrue(snapshot.getSamples(sensorType).contains(sample));
  }

  public void testEmptyGetSamples() {
    final Snapshot snapshot = new Snapshot();

    final List<Sample> actual = snapshot.getSamples(Sensor.TYPE_ACCELEROMETER);

    assertTrue(actual.isEmpty());
  }

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

    assertEquals(expected, snapshot.getSamples(sensorType));
  }

}
