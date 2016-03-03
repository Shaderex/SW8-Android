package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

public class FloatTripleTest {

  @Test
  public void testConstructor() {
    new FloatTriple(3f, 3f, 3f);
    new FloatTriple(new float[] {3f, 3f, 3f});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorInvalidInputTooLargeArray() {
    new FloatTriple(new float[] {3f, 3f, 3f, 3f});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorInvalidInputTooSmallArray() {
    new FloatTriple(new float[] {3f, 3f});
  }

  @Test
  public void testGetIndividualValuesThreeArgumentConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    Assert.assertEquals("First value is not set properly", expected1, floatTriple.getFirstValue());
    Assert.assertEquals("Second value is not set properly", expected2, floatTriple.getSecondValue());
    Assert.assertEquals("Third value is not set properly", expected3, floatTriple.getThirdValue());
  }

  @Test
  public void testGetIndividualValuesArrayConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(new float[] {expected1, expected2, expected3});

    Assert.assertEquals("First value is not set properly", expected1, floatTriple.getFirstValue());
    Assert.assertEquals("Second value is not set properly", expected2, floatTriple.getSecondValue());
    Assert.assertEquals("Third value is not set properly", expected3, floatTriple.getThirdValue());
  }

  @Test
  public void testGetValueArrayThreeArgumentConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    Assert.assertEquals(expected1, floatTriple.getValues()[0]);
    Assert.assertEquals(expected2, floatTriple.getValues()[1]);
    Assert.assertEquals(expected3, floatTriple.getValues()[2]);
  }

  @Test
  public void testGetValueArrayArrayConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(new float[] {expected1, expected2, expected3});

    Assert.assertEquals(expected1, floatTriple.getValues()[0]);
    Assert.assertEquals(expected2, floatTriple.getValues()[1]);
    Assert.assertEquals(expected3, floatTriple.getValues()[2]);
  }

}
