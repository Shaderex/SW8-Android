package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class FloatTripleTest extends ApplicationTestCase<DataCollectionApplication> {

  public FloatTripleTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new FloatTriple();
    new FloatTriple(3f, 3f, 3f);
    new FloatTriple(new float[] {3f, 3f, 3f});
  }

  public void testExtendsRealmObject() {
    assertTrue(FloatTriple.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(FloatTriple.class));
  }

  public void testConstructorInvalidInputTooLargeArray() {

    try {
      new FloatTriple(new float[] {3f, 3f, 3f, 3f});
    } catch (IllegalArgumentException e) {
      return;
    }

    fail("Did not throw IllegalArgumentException with illegal arguments ");
  }

  public void testConstructorInvalidInputTooSmallArray() {

    try {
      new FloatTriple(new float[] {3f, 3f});
    } catch (IllegalArgumentException e) {
      return;
    }

    fail("Did not throw IllegalArgumentException with illegal arguments ");
  }

  public void testGetIndividualValuesThreeArgumentConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    assertEquals("First value is not set properly", expected1, floatTriple.getFirstValue());
    assertEquals("Second value is not set properly", expected2, floatTriple.getSecondValue());
    assertEquals("Third value is not set properly", expected3, floatTriple.getThirdValue());
  }

  public void testGetIndividualValuesArrayConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(new float[] {expected1, expected2, expected3});

    assertEquals("First value is not set properly", expected1, floatTriple.getFirstValue());
    assertEquals("Second value is not set properly", expected2, floatTriple.getSecondValue());
    assertEquals("Third value is not set properly", expected3, floatTriple.getThirdValue());
  }

  public void testGetValueArrayThreeArgumentConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    assertEquals(expected1, floatTriple.getValues()[0]);
    assertEquals(expected2, floatTriple.getValues()[1]);
    assertEquals(expected3, floatTriple.getValues()[2]);
  }

  public void testGetValueArrayArrayConstructor() {
    final float expected1 = 3f;
    final float expected2 = 4f;
    final float expected3 = 5f;
    final FloatTriple floatTriple = new FloatTriple(new float[] {expected1, expected2, expected3});

    assertEquals(expected1, floatTriple.getValues()[0]);
    assertEquals(expected2, floatTriple.getValues()[1]);
    assertEquals(expected3, floatTriple.getValues()[2]);
  }

  public void testGetCompressedValues() {
    final float expected1 = -08.7845f; // 10010101011100100101
    final float expected2 = 11.1234f; // 00011011001010000010
    final float expected3 = 09.7864f; // 00010111111001001000
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001001010001101100101000001000010111111001001000L;

    assertEquals(expected, floatTriple.getCompressedValues());
  }

  public void testGetCompressedValuesMoreDigits() {
    final float expected1 = -08.784545f; // 10010101011100100101
    final float expected2 = 11.123436f; // 00011011001010000010
    final float expected3 = 09.786427f; // 00010111111001001000
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001001010001101100101000001000010111111001001000L;

    assertEquals(expected, floatTriple.getCompressedValues());
  }

  public void testGetCompressedValuesFewerDigits() {
    final float expected1 = -08.784f; // 10010101011100100000
    final float expected2 = 11.123f; // 00011011001001111110
    final float expected3 = 09.786f; // 00010111111001000100
    final FloatTriple floatTriple = new FloatTriple(expected1, expected2, expected3);

    final long expected = 0b0010100101010111001000000001101100100111111000010111111001000100L;

    assertEquals(expected, floatTriple.getCompressedValues());
  }

  public void testEqualsNull() {
    final FloatTriple floatTriple = new FloatTriple();

    assertNotSame(floatTriple, null);
  }

  public void testEqualsSameValues() {
    final FloatTriple floatTriple1 = new FloatTriple(1f, 2f, 3f);
    final FloatTriple floatTriple2 = new FloatTriple(1f, 2f, 3f);

    assertEquals(floatTriple1, floatTriple2);
  }

  public void testEqualsDifferentValues() {
    final FloatTriple floatTriple1 = new FloatTriple(1f, 2f, 3f);
    final FloatTriple floatTriple2 = new FloatTriple(4f, 5f, 7f);

    assertNotSame(floatTriple1, floatTriple2);
  }

  public void testEqualsSameReference() {
    final FloatTriple floatTriple1 = new FloatTriple(1f, 2f, 3f);
    final FloatTriple floatTriple2 = floatTriple1;

    assertEquals(floatTriple1, floatTriple2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final FloatTriple floatTriple = new FloatTriple(1f, 2f, 3f);

    realm.beginTransaction();
    realm.copyToRealm(floatTriple);
    realm.commitTransaction();

    final FloatTriple loadedFloatTriple = realm.where(FloatTriple.class).findFirst();

    assertEquals(floatTriple, loadedFloatTriple);

    realm.close();

    Realm.deleteRealm(realmConfiguration);
  }
}
