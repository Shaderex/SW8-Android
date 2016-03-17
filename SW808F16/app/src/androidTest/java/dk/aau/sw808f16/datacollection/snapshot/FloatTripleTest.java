package dk.aau.sw808f16.datacollection.snapshot;

import android.annotation.SuppressLint;
import android.test.ApplicationTestCase;

import java.io.File;
import java.security.SecureRandom;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FloatTripleTest extends ApplicationTestCase<DataCollectionApplication> {

  private RealmConfiguration realmConfiguration;
  private Realm realm;

  public FloatTripleTest() {
    super(DataCollectionApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    final byte[] key = new byte[64];
    new SecureRandom().nextBytes(key);
    realmConfiguration = new RealmConfiguration.Builder(getContext()).encryptionKey(key).build();
    realm = Realm.getInstance(realmConfiguration);
  }

  public void testConstructor() {
    new FloatTriple(3f, 3f, 3f);
    new FloatTriple(new float[] {3f, 3f, 3f});
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

  public void testStorable() {
    final float expected1 = 3.1f;
    final float expected2 = 4.2f;
    final float expected3 = 5.32f;

    final FloatTriple expected = new FloatTriple(expected1, expected2, expected3);

    RealmList<FloatTriple> realmList = new RealmList<>();
    for (int i = 0; i < 10; i++) {
      realmList.add(expected);
    }

    realm.beginTransaction();
    realm.copyToRealm(realmList);
    realm.commitTransaction();

    RealmResults<FloatTriple> extractedFloatTriples = realm.where(FloatTriple.class).findAll();
    for (final FloatTriple actual : extractedFloatTriples) {
      assertEquals(expected.getCompressedValues(), actual.getCompressedValues());
    }

    // These lines can be used to debug the size of the database
    @SuppressLint("SdCardPath")
    final File file = new File("/data/data/dk.aau.sw808f16.datacollection/files/default.realm");
    final long size = file.length();
  }

  @Override
  protected void tearDown() throws Exception {
    realm.close();
    boolean deleteRealmResult = Realm.deleteRealm(realmConfiguration);
    assertTrue(deleteRealmResult);

    super.tearDown();
  }
}
