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

  @Test
  public void testGetCompressedValues() {
    final float expected1 = -08.7845f; // 10010101011100100101
    final float expected2 = 11.1234f; // 00011011001010000010
    final float expected3 = 09.7864f; // 00010111111001001000
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001001010001101100101000001000010111111001001000L;

    Assert.assertEquals(expected, floatTriple.getCompressedValues());
  }

  @Test
  public void testGetCompressedValuesMoreDigits() {
    final float expected1 = -08.784545f; // 10010101011100100101
    final float expected2 = 11.123436f; // 00011011001010000010
    final float expected3 = 09.786427f; // 00010111111001001000
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001001010001101100101000001000010111111001001000L;

    Assert.assertEquals(expected, floatTriple.getCompressedValues());
  }

  @Test
  public void testGetCompressedValuesFewerDigits() {
    final float expected1 = -08.784f; // 10010101011100100000
    final float expected2 = 11.123f; // 00011011001001111110
    final float expected3 = 09.786f; // 00010111111001000100
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001000000001101100100111111000010111111001000100L;

    Assert.assertEquals(expected, floatTriple.getCompressedValues());
  }

}
