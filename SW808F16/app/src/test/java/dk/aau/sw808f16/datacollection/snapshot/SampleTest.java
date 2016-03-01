package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

public class SampleTest {

  @Test
  public void testConstructor() {
    new Sample();
  }

  @Test
  public void testGetSetMeasurements() {
    final Sample sample = new Sample();
    final Measurement<Integer> measurement1 = new Measurement<>(1);
    final Measurement<Integer> measurement2 = new Measurement<>(2);

    final ArrayList<Measurement> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.setMeasurements(measurements);

    Assert.assertEquals(measurements, sample.getMeasurements());
  }

  @Test
  public void testAddMeasurements() {
    final Sample sample = new Sample();
    final Measurement<Integer> measurement1 = new Measurement<>(1);
    final Measurement<Integer> measurement2 = new Measurement<>(2);

    final ArrayList<Measurement> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.addMeasurements(measurements);

    for (final Measurement measurement : measurements) {
      Assert.assertTrue(sample.getMeasurements().contains(measurement));
    }
  }

  @Test
  public void testAddMeasurement() {
    final Sample sample = new Sample();
    final Measurement<Integer> measurement = new Measurement<>(1);

    sample.addMeasurement(measurement);

    Assert.assertTrue(sample.getMeasurements().contains(measurement));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddMeasurementsWithDifferentTypesCase1() {
    final Sample sample = new Sample();
    final Measurement<Integer> measurement1 = new Measurement<>(1);
    final Measurement<Float> measurement2 = new Measurement<>(1f);

    sample.addMeasurement(measurement1);
    sample.addMeasurement(measurement2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddMeasurementsWithDifferentTypesCase2() {
    final Sample sample = new Sample();
    final Measurement<Integer> measurement1 = new Measurement<>(1);
    final Measurement<Float> measurement2 = new Measurement<>(1f);

    final ArrayList<Measurement> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.addMeasurements(measurements);
  }

}
