package dk.aau.sw808f16.datacollection.snapshot;

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

}
