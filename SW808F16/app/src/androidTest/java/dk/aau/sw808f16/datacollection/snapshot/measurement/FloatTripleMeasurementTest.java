package dk.aau.sw808f16.datacollection.snapshot.measurement;

import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class FloatTripleMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  public FloatTripleMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new FloatTripleMeasurement();
    new FloatTripleMeasurement(3f, 3f, 3f);
    new FloatTripleMeasurement(new float[] {3f, 3f, 3f});
  }

  public void testExtendsRealmObject() {
    assertTrue(FloatTripleMeasurement.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(FloatTripleMeasurement.class));
  }

  public void testConstructorInvalidInputTooLargeArray() {

    try {
      new FloatTripleMeasurement(new float[] {3f, 3f, 3f, 3f});
    } catch (IllegalArgumentException exception) {
      return;
    }

    fail("Did not throw IllegalArgumentException with illegal arguments ");
  }

  public void testConstructorInvalidInputTooSmallArray() {

    try {
      new FloatTripleMeasurement(new float[] {3f, 3f});
    } catch (IllegalArgumentException exception) {
      return;
    }

    fail("Did not throw IllegalArgumentException with illegal arguments ");
  }

  public void testGetIndividualValuesThreeArgumentConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(expected1, expected2, expected3);

    assertEquals("First value is not set properly", expected1, floatTripleMeasurement.getFirstValue());
    assertEquals("Second value is not set properly", expected2, floatTripleMeasurement.getSecondValue());
    assertEquals("Third value is not set properly", expected3, floatTripleMeasurement.getThirdValue());
  }

  public void testGetIndividualValuesArrayConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(new float[] {expected1, expected2, expected3});

    assertEquals("First value is not set properly", expected1, floatTripleMeasurement.getFirstValue());
    assertEquals("Second value is not set properly", expected2, floatTripleMeasurement.getSecondValue());
    assertEquals("Third value is not set properly", expected3, floatTripleMeasurement.getThirdValue());
  }

  public void testGetValueArrayThreeArgumentConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(expected1, expected2, expected3);

    assertEquals(expected1, floatTripleMeasurement.getValues()[0]);
    assertEquals(expected2, floatTripleMeasurement.getValues()[1]);
    assertEquals(expected3, floatTripleMeasurement.getValues()[2]);
  }

  public void testGetValueArrayArrayConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(new float[] {expected1, expected2, expected3});

    assertEquals(expected1, floatTripleMeasurement.getValues()[0]);
    assertEquals(expected2, floatTripleMeasurement.getValues()[1]);
    assertEquals(expected3, floatTripleMeasurement.getValues()[2]);
  }

  public void testGetCompressedValues() {
    final float expected1 = -08.7845f; // 10010101011100100101
    final float expected2 = 11.1234f; // 00011011001010000010
    final float expected3 = 09.7864f; // 00010111111001001000
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001001010001101100101000001000010111111001001000L;

    assertEquals(expected, floatTripleMeasurement.getCompressedValues());
  }

  public void testGetCompressedValuesMoreDigits() {
    final float expected1 = -08.784545f; // 10010101011100100101
    final float expected2 = 11.123436f; // 00011011001010000010
    final float expected3 = 09.786427f; // 00010111111001001000
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001001010001101100101000001000010111111001001000L;

    assertEquals(expected, floatTripleMeasurement.getCompressedValues());
  }

  public void testGetCompressedValuesFewerDigits() {
    final float expected1 = -08.784f; // 10010101011100100000
    final float expected2 = 11.123f; // 00011011001001111110
    final float expected3 = 09.786f; // 00010111111001000100
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001000000001101100100111111000010111111001000100L;

    assertEquals(expected, floatTripleMeasurement.getCompressedValues());
  }

  public void testEqualsNull() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement();

    assertNotSame(floatTripleMeasurement, null);
  }

  public void testEqualsSameValues() {
    final FloatTripleMeasurement floatTripleMeasurement1 = new FloatTripleMeasurement(1f, 2f, 3f);
    final FloatTripleMeasurement floatTripleMeasurement2 = new FloatTripleMeasurement(1f, 2f, 3f);

    assertEquals(floatTripleMeasurement1, floatTripleMeasurement2);
  }

  public void testEqualsDifferentValues() {
    final FloatTripleMeasurement floatTripleMeasurement1 = new FloatTripleMeasurement(1f, 2f, 3f);
    final FloatTripleMeasurement floatTripleMeasurement2 = new FloatTripleMeasurement(4f, 5f, 7f);

    assertNotSame(floatTripleMeasurement1, floatTripleMeasurement2);
  }

  public void testEqualsSameReference() {
    final FloatTripleMeasurement floatTripleMeasurement1 = new FloatTripleMeasurement(1f, 2f, 3f);
    final FloatTripleMeasurement floatTripleMeasurement2 = floatTripleMeasurement1;

    assertEquals(floatTripleMeasurement1, floatTripleMeasurement2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(
        getContext()).name("test_float_triple_measurement.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);

    realm.beginTransaction();
    realm.copyToRealm(floatTripleMeasurement);
    realm.commitTransaction();

    final FloatTripleMeasurement loadedFloatTripleMeasurement = realm.where(FloatTripleMeasurement.class).findFirst();

    final boolean equals = floatTripleMeasurement.equals(loadedFloatTripleMeasurement);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }

  public void testFloatTripleMeasurementPerformance() {
    // Increase this number if you want to perform this test (Approximate 500.000)
    // Values too large will make the device run out of memory (Approximate 4.000.000, Nexus 5)
    final int floatIterations = 100;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    final ArrayList<float[]> floatArrays = new ArrayList<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    final ArrayList<FloatTripleMeasurement> floatTripleMeasurements = new ArrayList<>();

    final Random rng = new Random();

    long time0 = System.currentTimeMillis();

    for (int i = 0; i < floatIterations; i++) {
      final float[] newFloats = new float[] {rng.nextFloat(), rng.nextFloat(), rng.nextFloat()};
      floatArrays.add(newFloats);
      @SuppressWarnings("UnusedAssignment")
      final float result = newFloats[0] * newFloats[1] * newFloats[2];
    }

    long time1 = System.currentTimeMillis();

    Log.d("FloatPerformance", "Float[] time: " + (time1 - time0) + " ms");

    long time2 = System.currentTimeMillis();

    for (int i = 0; i < floatIterations; i++) {
      final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
      floatTripleMeasurements.add(floatTripleMeasurement);
      @SuppressWarnings("UnusedAssignment")
      final float result = floatTripleMeasurement.getFirstValue()
          * floatTripleMeasurement.getSecondValue()
          * floatTripleMeasurement.getThirdValue();
    }

    long time3 = System.currentTimeMillis();

    Log.d("FloatPerformance", "FloatTriple time: " + (time3 - time2) + " ms");
  }
}
