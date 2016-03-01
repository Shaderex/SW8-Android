package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

public class MeasurementTest {

  @Test
  public void testConstructor() {
    new Measurement<Object>();
  }

  @Test
  public void testSetDataCase1() {
    final Measurement<Integer> measurement = new Measurement<>();
    final Integer expectedData = 42;

    measurement.setData(expectedData);

    Assert.assertEquals(expectedData, measurement.getData());
  }

  @Test
  public void testSetDataCase2() {
    final Measurement<String> measurement = new Measurement<>();
    final String expectedData = "hej";

    measurement.setData(expectedData);

    Assert.assertEquals(expectedData, measurement.getData());
  }

  @Test
  public void testSetDataCase3() {
    final Measurement<Float[]> measurement = new Measurement<>();
    final Float[] expectedData = new Float[]{ -1f, 0f, 1f, 2f, 3f, 3.14f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY };

    measurement.setData(expectedData);

    Assert.assertEquals(expectedData, measurement.getData());
  }

}
