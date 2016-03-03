package dk.aau.sw808f16.datacollection.snapshot;

public class FloatTriple {

  public FloatTriple(final float v, final float v1, final float v2) {

  }

  public FloatTriple(final float[] floats) {
    if (floats.length != 3) {
      throw new IllegalArgumentException("Array must contain exactly 3 entries");
    }
  }
}
