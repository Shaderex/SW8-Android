package dk.aau.sw808f16.datacollection.snapshot.measurement;

import android.test.ApplicationTestCase;

import com.microsoft.band.sensors.HeartRateQuality;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class HeartRateMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  public HeartRateMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new HeartRateMeasurement();
    new HeartRateMeasurement(82, HeartRateQuality.LOCKED);
  }

  public void testExtendsRealmObject() {
    assertTrue(HeartRateMeasurement.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(HeartRateMeasurement.class));
  }

  public void testEqualsNull() {
    final HeartRateMeasurement measurement = new HeartRateMeasurement();

    assertNotSame(measurement, null);
  }

  public void testEqualsSameValues() {
    final HeartRateMeasurement measurement1 = new HeartRateMeasurement(82, HeartRateQuality.LOCKED);
    final HeartRateMeasurement measurement2 = new HeartRateMeasurement(82, HeartRateQuality.LOCKED);

    assertEquals(measurement1, measurement2);
  }

  public void testEqualsDifferentValues() {
    final HeartRateMeasurement measurement1 = new HeartRateMeasurement(82, HeartRateQuality.LOCKED);
    final HeartRateMeasurement measurement2 = new HeartRateMeasurement(28, HeartRateQuality.ACQUIRING);

    assertNotSame(measurement1, measurement2);
  }

  public void testEqualsSameReference() {
    final HeartRateMeasurement measurement1 = new HeartRateMeasurement(82, HeartRateQuality.ACQUIRING);
    final HeartRateMeasurement measurement2 = measurement1;

    assertEquals(measurement1, measurement2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_heart_measurement.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final HeartRateMeasurement measurement = new HeartRateMeasurement(85, HeartRateQuality.ACQUIRING);

    realm.beginTransaction();
    realm.copyToRealm(measurement);
    realm.commitTransaction();

    final HeartRateMeasurement loadedHeartRateMeasurement = realm.where(HeartRateMeasurement.class).findFirst();

    final boolean equals = measurement.equals(loadedHeartRateMeasurement);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }
}
