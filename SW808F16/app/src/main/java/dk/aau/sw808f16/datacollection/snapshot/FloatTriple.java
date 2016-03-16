package dk.aau.sw808f16.datacollection.snapshot;

import io.realm.RealmObject;

public class FloatTriple extends RealmObject {

  private static final byte FIRST_VALUE = 2;
  private static final byte SECOND_VALUE = 1;
  private static final byte THIRD_VALUE = 0;
  private static final byte DECIMAL_POSITION = 2;
  private static final byte BITS_PER_VALUE = 19;

  private static final long SIGN_MASK = 0b10000000000000000000L;
  private static final long VALUE_MASK = 0b1111111111111111111L;

  private long compressedValues;

  // Do not use this is needed to store it using Realm.io
  @SuppressWarnings("unused")
  public FloatTriple() {
  }

  public FloatTriple(final float v1, final float v2, final float v3) {
    this(new float[] {v1, v2, v3});
  }

  public FloatTriple(final float[] values) {
    if (values.length != 3) {
      throw new IllegalArgumentException("Array must contain exactly 3 entries");
    }

    compressValues(values);
  }

  public float getFirstValue() {
    return decompressValue(FIRST_VALUE);
  }

  public float getSecondValue() {
    return decompressValue(SECOND_VALUE);
  }

  public float getThirdValue() {
    return decompressValue(THIRD_VALUE);
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
    compressValue = compressValue | DECIMAL_POSITION;

    for (float value : values) {
      // Shift 1 to make room for the signed bit
      compressValue = compressValue << 1;

      // If the number is negative, bit shift a 1 into the mix, otherwise 0 (which comes from the default padding in left bit shifting)
      if (value < 0) {
        compressValue = compressValue | 0b1;
        value *= -1; // From now on, we only want positive numbers
      }

      final byte precision = 7 - DECIMAL_POSITION - 1;
      final int binaryEncoding = (int) Math.round(value * Math.pow(10, precision));


      if (binaryEncoding > 0b1111111111111111111) {
        throw new IllegalArgumentException("Cannot compress value. The float seen as an integer is larger than 52487.");
      }

      compressValue = compressValue << BITS_PER_VALUE;
      compressValue = compressValue | binaryEncoding;
    }

    compressedValues = compressValue;
  }

  private float decompressValue(byte valuePosition) {
    // Calculate the amount of bits to be shifted based on the position of the value
    final int shiftAmount = valuePosition * 20;

    // Create a bitmask the finds 20 bits encoding the float
    final long valueMask = 0b11111111111111111111L << shiftAmount;

    // Apply value mask and shift to get the value as an integer
    final long valueData = (compressedValues & valueMask) >>> shiftAmount;

    // Apply mask to find the actual value of the float (ignoring signing)
    float value = (float) (valueData & VALUE_MASK);

    // Apply mask to find the sign bit and move it to the first position (if this value is 1 then the number is negative)
    final boolean isNegative = ((valueData & SIGN_MASK) >> BITS_PER_VALUE) == 1L;

    if (isNegative) {
      value *= -1;
    }

    // Move the decimal point to the correct place in the float
    final byte precision = 7 - DECIMAL_POSITION - 1;
    value /= Math.pow(10, precision);

    return value;
  }
}
