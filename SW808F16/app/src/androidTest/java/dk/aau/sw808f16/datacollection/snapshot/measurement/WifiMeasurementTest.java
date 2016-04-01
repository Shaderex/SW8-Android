package dk.aau.sw808f16.datacollection.snapshot.measurement;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.test.ApplicationTestCase;

import java.util.List;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;

public class WifiMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  private List<ScanResult> realScanResults;
  private boolean missingWifi;

  public WifiMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void setUp() {
    final WifiManager manager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    realScanResults = manager.getScanResults();
    missingWifi = realScanResults == null || realScanResults.size() < 2;
  }

  public void testConstructor() {
    if (missingWifi) {
      return;
    }

    new WifiMeasurement();
    new WifiMeasurement(realScanResults);
  }

  public void testExtendsRealmObject() {
    assertTrue(WifiMeasurement.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(WifiMeasurement.class));
  }

  public void testSetGetScanResults() {
    if (missingWifi) {
      return;
    }

    WifiMeasurement wifiMeasurement = new WifiMeasurement();

    List<ScanResultMeasurement> scanResults = new RealmList<>();
    scanResults.add(new ScanResultMeasurement(realScanResults.get(0)));

    wifiMeasurement.setScanResultMeasurements(realScanResults.subList(0, 1));

    assertEquals("The scan results from the getter is different from the one passed to the setter",
        scanResults, wifiMeasurement.getScanResultMeasurements());

  }

  public void testEqualsNull() {
    final WifiMeasurement wifiMeasurement = new WifiMeasurement();

    assertNotSame(wifiMeasurement, null);
  }

  public void testEqualsSameValues() {
    if (missingWifi) {
      return;
    }

    final WifiMeasurement wifiMeasurement1 = new WifiMeasurement(realScanResults);
    final WifiMeasurement wifiMeasurement2 = new WifiMeasurement(realScanResults);

    assertEquals(wifiMeasurement1, wifiMeasurement2);
  }

  public void testEqualsDifferentValues() {
    if (missingWifi) {
      return;
    }

    final WifiMeasurement wifiMeasurement1 = new WifiMeasurement(realScanResults.subList(0, 1));
    final WifiMeasurement wifiMeasurement2 = new WifiMeasurement(realScanResults.subList(1, 2));

    assertNotSame(wifiMeasurement1, wifiMeasurement2);
  }

  public void testEqualsSameReference() {
    if (missingWifi) {
      return;
    }

    final WifiMeasurement wifiMeasurement1 = new WifiMeasurement(realScanResults);
    final WifiMeasurement wifiMeasurement2 = wifiMeasurement1;

    assertEquals(wifiMeasurement1, wifiMeasurement2);
  }

  public void testSaveToRealm() {
    if (missingWifi) {
      return;
    }

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final WifiMeasurement wifiMeasurement = new WifiMeasurement(realScanResults);

    realm.beginTransaction();
    realm.copyToRealm(wifiMeasurement);
    realm.commitTransaction();

    final WifiMeasurement loadedWifiMeasurement = realm.where(WifiMeasurement.class).findFirst();
    final boolean equals = wifiMeasurement.equals(loadedWifiMeasurement);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }
}
