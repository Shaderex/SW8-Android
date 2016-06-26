package dk.aau.sw808f16.wekatest.snapshot.measurement;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.test.ApplicationTestCase;

import java.util.List;

import dk.aau.sw808f16.wekatest.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class ScanResultMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  private List<ScanResult> realScanResults;
  private boolean missingWifi;

  public ScanResultMeasurementTest() {
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

    new ScanResultMeasurement();
    new ScanResultMeasurement(realScanResults.get(0));
  }

  public void testExtendsRealmObject() {
    if (missingWifi) {
      return;
    }

    assertTrue(ScanResultMeasurement.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(ScanResultMeasurement.class));
  }

  public void testSetGetNetworkName() {
    final ScanResultMeasurement scanResultMeasurement = new ScanResultMeasurement();
    String networkName = "Bubber";

    scanResultMeasurement.setNetworkName(networkName);

    assertEquals("The getter and setter method are inconsistent", networkName, scanResultMeasurement.getNetworkName());
  }

  public void testSetGetSignalStrength() {
    final ScanResultMeasurement scanResultMeasurement = new ScanResultMeasurement();
    int signalStrength = 42;

    scanResultMeasurement.setSignalStrength(signalStrength);

    assertEquals("The getter and setter method are inconsistent", signalStrength, scanResultMeasurement.getSignalStrength());
  }

  public void testEqualsNull() {
    if (missingWifi) {
      return;
    }

    final ScanResultMeasurement scanResult = new ScanResultMeasurement();

    assertNotSame(scanResult, null);
  }

  public void testEqualsSameValues() {
    if (missingWifi) {
      return;
    }

    final ScanResultMeasurement scanResult1 = new ScanResultMeasurement(realScanResults.get(0));
    final ScanResultMeasurement scanResult2 = new ScanResultMeasurement(realScanResults.get(0));

    assertEquals(scanResult1, scanResult2);
  }

  public void testEqualsDifferentValues() {
    if (missingWifi) {
      return;
    }

    final ScanResultMeasurement scanResult1 = new ScanResultMeasurement(realScanResults.get(0));
    final ScanResultMeasurement scanResult2 = new ScanResultMeasurement(realScanResults.get(1));

    assertNotSame(scanResult1, scanResult2);
  }

  public void testEqualsSameReference() {
    if (missingWifi) {
      return;
    }

    final ScanResultMeasurement scanResult1 = new ScanResultMeasurement(realScanResults.get(0));
    final ScanResultMeasurement scanResult2 = scanResult1;

    assertEquals(scanResult1, scanResult2);
  }

  public void testSaveToRealm() {
    if (missingWifi) {
      return;
    }

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(
        getContext()).name("scan_result_measurement_test.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final ScanResultMeasurement scanResultMeasurement = new ScanResultMeasurement(realScanResults.get(0));

    realm.beginTransaction();
    realm.copyToRealm(scanResultMeasurement);
    realm.commitTransaction();

    final ScanResultMeasurement loadedScanResultMeasurement = realm.where(ScanResultMeasurement.class).findFirst();

    final boolean equals = scanResultMeasurement.equals(loadedScanResultMeasurement);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }
}
