package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class SampleTest {

  @Test
  public void testConstructor() {
    new Sample();
  }

  @Test
  public void testConstructorWithInitialMeasurement() {
    new Sample(2);
  }

  @Test
  public void testConstructorWithInitialMeasurements() {
    new Sample(Arrays.asList(1, 2, 3));
  }

  @Test
  public void testGetSetMeasurements() {
    final Sample sample = new Sample();
    final Integer measurement1 = 1;
    final Integer measurement2 = 2;

    final ArrayList<Integer> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.setMeasurements(measurements);

    Assert.assertEquals(measurements, sample.getMeasurements());
  }

  @Test
  public void testAddMeasurements() {
    final Sample sample = new Sample();
    final Integer measurement1 = 1;
    final Integer measurement2 = 2;

    final ArrayList<Integer> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.addMeasurements(measurements);

    for (final Integer measurement : measurements) {
      Assert.assertTrue(sample.getMeasurements().contains(measurement));
    }
  }

  @Test
  public void testAddMeasurement() {
    final Sample sample = new Sample();
    final Integer measurement = 1;

    sample.addMeasurement(measurement);

    Assert.assertTrue(sample.getMeasurements().contains(measurement));
  }

}
