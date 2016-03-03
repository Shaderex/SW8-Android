package dk.aau.sw808f16.datacollection.snapshot;

public class FloatTriple {

  private float firstValue;
  private float secondValue;
  private float thirdValue;
  private long compressedValues;

  private static final byte COMMA_POSITION = 2;
  private static final byte BITS_PER_VALUE = 20;

  public FloatTriple(final float v1, final float v2, final float v3) {
    this(new float[] {v1, v2, v3});
  }

  public FloatTriple(final float[] values) {
    if (values.length != 3) {
      throw new IllegalArgumentException("Array must contain exactly 3 entries");
    }

    firstValue = values[0];
    secondValue = values[1];
    thirdValue = values[2];

    //TODO: Hvis nogle af værdierne er større end 524287 (uden komma) så send en exception

    compressValues(values);
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

  public long getCompressedValues() {
    return compressedValues;
  }

  private void compressValues(final float[] values) {
    long compressValue = 0b0;

    // Insert the comma position in the first 3 bits
    compressValue = compressValue | COMMA_POSITION;

    for (float value : values) {
      // Shift 1 to make room for the signed bit
      compressValue = compressValue << 1;

      // If the number is negative, bit shift a 1 into the mix, otherwise 0 (which comes from the default padding in left bit shifting)
      if (value < 0) {
        compressValue = compressValue | 0b1;
        value *= -1; // From now on, we only want positive numbers
      }

      final byte precision = 7 - COMMA_POSITION - 1;
      final int binaryEncoding = (int) Math.round(value * Math.pow(10, precision));

      compressValue = compressValue << 19;
      compressValue = compressValue | binaryEncoding;
    }

    compressedValues = compressValue;
  }
}
