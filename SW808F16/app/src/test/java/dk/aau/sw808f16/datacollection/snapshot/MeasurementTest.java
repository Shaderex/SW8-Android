package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

public class MeasurementTest {

  @Test
  public void testConstructor() {
    final Integer expectedData = 42;
    final Measurement<Integer> measurement = new Measurement<>(expectedData);

    Assert.assertEquals(expectedData, measurement.getData());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullInput() {
    new Measurement<>(null);
  }

  @Test
  public void testSetDataCase1() {
    final Integer expectedData = 42;
    final Integer unexpectedData = expectedData + 1;
    final Measurement<Integer> measurement = new Measurement<>(unexpectedData);

    measurement.setData(expectedData);

    Assert.assertEquals(expectedData, measurement.getData());
  }

  @Test
  public void testSetDataCase2() {
    final String expectedData = "hej";
    final String unexpectedData = expectedData + "1";
    final Measurement<String> measurement = new Measurement<>(unexpectedData);

    measurement.setData(expectedData);

    Assert.assertEquals(expectedData, measurement.getData());
  }

  @Test
  public void testSetDataCase3() {
    final Float[] expectedData = new Float[] {-1f, 0f, 1f, 2f, 3f, 3.14f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY};
    final Float[] unexpectedData = new Float[] {-1f};
    final Measurement<Float[]> measurement = new Measurement<>(unexpectedData);

    measurement.setData(expectedData);

    Assert.assertEquals(expectedData, measurement.getData());
  }

  @Test
  public void testCorrectDataType() {
    final Class<?> expectedDataType = Integer.class;
    final Measurement<Integer> measurement = new Measurement<>(2);

    Assert.assertEquals(expectedDataType, measurement.getDataType());
  }

}
