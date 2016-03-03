package dk.aau.sw808f16.datacollection.snapshot;

public class FloatTriple {

  private float firstValue;
  private float secondValue;
  private float thirdValue;

  public FloatTriple(final float v, final float v1, final float v2) {
    firstValue = v;
    secondValue = v1;
    thirdValue = v2;
  }

  public FloatTriple(final float[] floats) {
    if (floats.length != 3) {
      throw new IllegalArgumentException("Array must contain exactly 3 entries");
    }

    firstValue = floats[0];
    secondValue = floats[1];
    thirdValue = floats[2];
  }

  public float getFirstValue() {
    return firstValue;
  }

  public float getSecondValue() {
    return secondValue;
  }

  public float getThirdValue() {
    return thirdValue;
  }

  public float[] getValues() {
    return new float[] {getFirstValue(), getSecondValue(), getThirdValue()};
  }
}
