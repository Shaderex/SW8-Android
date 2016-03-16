package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Arrays;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;

public class SampleTest extends ApplicationTestCase<DataCollectionApplication> {

  public SampleTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Sample();
  }

  public void testConstructorWithInitialMeasurement() {
    new Sample(2);
  }

  public void testConstructorWithInitialMeasurements() {
    new Sample(Arrays.asList(1, 2, 3));
  }

  public void testGetSetMeasurements() {
    final Sample sample = new Sample();
    final Integer measurement1 = 1;
    final Integer measurement2 = 2;

    final ArrayList<Integer> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.setMeasurements(measurements);

    assertEquals(measurements, sample.getMeasurements());
  }

  public void testAddMeasurements() {
    final Sample sample = new Sample();
    final Integer measurement1 = 1;
    final Integer measurement2 = 2;

    final ArrayList<Integer> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    sample.addMeasurements(measurements);

    for (final Integer measurement : measurements) {
      assertTrue(sample.getMeasurements().contains(measurement));
    }
  }

  public void testAddMeasurement() {
    final Sample sample = new Sample();
    final Integer measurement = 1;

    sample.addMeasurement(measurement);

    assertTrue(sample.getMeasurements().contains(measurement));
  }

}
