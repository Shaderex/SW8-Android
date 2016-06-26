package dk.aau.sw808f16.wekatest.snapshot;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dk.aau.sw808f16.wekatest.DataCollectionApplication;
import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatMeasurement;
import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatTripleMeasurement;
import dk.aau.sw808f16.wekatest.snapshot.measurement.LocationMeasurement;
import dk.aau.sw808f16.wekatest.snapshot.measurement.WifiMeasurement;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

@SuppressWarnings("deprecation")
public class SampleTest extends ApplicationTestCase<DataCollectionApplication> {

  public SampleTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));

    Sample.Create(Arrays.asList(new FloatTripleMeasurement(1f, 1f, 1f),
        new FloatTripleMeasurement(2f, 2f, 2f),
        new FloatTripleMeasurement(3f, 3f, 3f)));
  }

  public void testExtendsRealmObject() {
    assertTrue(Sample.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Sample.class));
  }

  public void testAddFloatTripleMeasurement() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 1f, 1f);
    final Sample sample1 = Sample.Create(floatTripleMeasurement);

    boolean foundActual = false;
    for (final Object m : sample1.getMeasurements()) {
      final FloatTripleMeasurement ft = (FloatTripleMeasurement) m;
      if (ft.equals(floatTripleMeasurement)) {
        foundActual = true;
        break;
      }
    }

    assertTrue("Added measurement not found in sample", foundActual);
  }

  public void testAddFloatMeasurement() {
    final FloatMeasurement floatMeasurement = new FloatMeasurement(1f);
    final Sample sample2 = Sample.Create(floatMeasurement);

    boolean foundActual = false;
    for (final Object m : sample2.getMeasurements()) {
      final FloatMeasurement f = (FloatMeasurement) m;
      if (f.equals(floatMeasurement)) {
        foundActual = true;
        break;
      }
    }

    assertTrue("Added measurement not found in sample", foundActual);
  }

  public void testAddFloatTripleMeasurements() {
    final FloatTripleMeasurement measurement1 = new FloatTripleMeasurement(1f, 1f, 1f);
    final Sample sample = Sample.Create(measurement1);

    final FloatTripleMeasurement measurement2 = new FloatTripleMeasurement(2f, 2f, 2f);
    final FloatTripleMeasurement measurement3 = new FloatTripleMeasurement(3f, 3f, 3f);

    sample.addMeasurements(Arrays.asList(measurement2, measurement3));

    final ArrayList<FloatTripleMeasurement> expectedMeasurements = new ArrayList<>();
    expectedMeasurements.add(measurement1);
    expectedMeasurements.add(measurement2);
    expectedMeasurements.add(measurement3);

    assertEquals("The sample does not contain the correct amount of measurements",
        expectedMeasurements.size(), sample.getMeasurements().size());

    for (int i = 0; i < expectedMeasurements.size(); i++) {
      final FloatTripleMeasurement expected = expectedMeasurements.get(i);
      final FloatTripleMeasurement actual = ((FloatTripleMeasurement) sample.getMeasurements().get(i));

      assertEquals("The measurements at position " + i + " are not equal", expected, actual);
    }
  }

  public void testAddFloatMeasurements() {
    final FloatMeasurement measurement1 = new FloatMeasurement(1f);
    final Sample sample = Sample.Create(measurement1);

    final FloatMeasurement measurement2 = new FloatMeasurement(2f);
    final FloatMeasurement measurement3 = new FloatMeasurement(3f);

    sample.addMeasurements(Arrays.asList(measurement2, measurement3));

    final ArrayList<FloatMeasurement> expectedMeasurements = new ArrayList<>();
    expectedMeasurements.add(measurement1);
    expectedMeasurements.add(measurement2);
    expectedMeasurements.add(measurement3);

    assertEquals("The sample does not contain the correct amount of measurements",
        expectedMeasurements.size(), sample.getMeasurements().size());

    for (int i = 0; i < expectedMeasurements.size(); i++) {
      final FloatMeasurement expected = expectedMeasurements.get(i);
      final FloatMeasurement actual = ((FloatMeasurement) sample.getMeasurements().get(i));

      assertEquals("The measurements at position " + i + " are not equal", expected, actual);
    }
  }

  public void testAddWifiMeasurements() {

    final WifiManager manager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    final List<ScanResult> realScanResults = manager.getScanResults();

    if (realScanResults == null || realScanResults.size() < 3) {
      return;
    }

    final WifiMeasurement measurement1 = new WifiMeasurement(realScanResults.subList(0, 1));
    final Sample sample = Sample.Create(measurement1);

    final WifiMeasurement measurement2 = new WifiMeasurement(realScanResults.subList(1, 2));
    final WifiMeasurement measurement3 = new WifiMeasurement(realScanResults.subList(2, 3));

    sample.addMeasurements(Arrays.asList(measurement2, measurement3));

    final ArrayList<WifiMeasurement> expectedMeasurements = new ArrayList<>();
    expectedMeasurements.add(measurement1);
    expectedMeasurements.add(measurement2);
    expectedMeasurements.add(measurement3);

    assertEquals("The sample does not contain the correct amount of measurements",
        expectedMeasurements.size(), sample.getMeasurements().size());

    for (int i = 0; i < expectedMeasurements.size(); i++) {
      final WifiMeasurement expected = expectedMeasurements.get(i);
      final WifiMeasurement actual = ((WifiMeasurement) sample.getMeasurements().get(i));

      assertEquals("The measurements at position " + i + " are not equal", expected, actual);
    }
  }

  public void testAddLocationMeasurements() {
    final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    final Location location = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

    if (location == null) {
      return;
    }

    final LocationMeasurement measurement1 = new LocationMeasurement(location);
    final Sample sample = Sample.Create(measurement1);

    final LocationMeasurement measurement2 = new LocationMeasurement(location);
    final LocationMeasurement measurement3 = new LocationMeasurement(location);

    sample.addMeasurements(Arrays.asList(measurement2, measurement3));

    final ArrayList<LocationMeasurement> expectedMeasurements = new ArrayList<>();
    expectedMeasurements.add(measurement1);
    expectedMeasurements.add(measurement2);
    expectedMeasurements.add(measurement3);

    assertEquals("The sample does not contain the correct amount of measurements",
        expectedMeasurements.size(), sample.getMeasurements().size());

    for (int i = 0; i < expectedMeasurements.size(); i++) {
      final LocationMeasurement expected = expectedMeasurements.get(i);
      final LocationMeasurement actual = ((LocationMeasurement) sample.getMeasurements().get(i));

      assertEquals("The measurements at position " + i + " are not equal", expected, actual);
    }
  }

  public void testAddMeasurementInconsistentMeasurementTypes() {
    final FloatMeasurement measurement1 = new FloatMeasurement(1f);
    final FloatTripleMeasurement measurement2 = new FloatTripleMeasurement(1f, 2f, 3f);
    final Sample sample = Sample.Create();

    sample.addMeasurement(measurement1);

    boolean threwException = false;
    try {
      sample.addMeasurement(measurement2);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testAddMeasurementsInconsistentMeasurementTypes() {
    final FloatMeasurement measurement1 = new FloatMeasurement(1f);
    final FloatTripleMeasurement measurement2 = new FloatTripleMeasurement(1f, 2f, 3f);
    final Sample sample = Sample.Create();

    sample.addMeasurement(measurement1);

    boolean threwException = false;
    try {
      sample.addMeasurements(Collections.singletonList(measurement2));
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testAddUnsupportedMeasurement() {
    final Sample sample = Sample.Create();
    final Object measurement = new Object();

    boolean threwException = false;
    try {
      sample.addMeasurement(measurement);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testAddUnsupportedMeasurements() {
    final Sample sample = Sample.Create();
    final Object measurement1 = new Object();
    final Object measurement2 = new Object();

    final ArrayList<Object> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);

    boolean threwException = false;

    try {
      sample.addMeasurements(measurements);
    } catch (IllegalArgumentException exception) {
      threwException = true;
    }

    assertTrue(IllegalArgumentException.class.getName() + " was not thrown", threwException);
  }

  public void testEqualsNull() {
    final Sample sample = Sample.Create();

    assertNotSame(sample, null);
  }

  public void testEqualsEmptySample() {
    final Sample sample1 = new Sample();
    final Sample sample2 = new Sample();

    assertEquals(sample1, sample2);
  }

  public void testEqualsEmptyAndNonEmptySample() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);

    final Sample sample1 = new Sample();
    final Sample sample2 = new Sample();
    sample2.addMeasurement(floatTripleMeasurement);

    assertNotSame(sample1, sample2);
  }

  public void testEqualsSingleElementSameReference() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);
    final Sample sample1 = new Sample();
    sample1.addMeasurement(floatTripleMeasurement);
    final Sample sample2 = new Sample();
    sample2.addMeasurement(floatTripleMeasurement);
    assertEquals(sample1, sample2);
  }

  public void testEqualsSingleElementSameValue() {
    final FloatTripleMeasurement floatTripleMeasurement1 = new FloatTripleMeasurement(1f, 2f, 3f);
    final FloatTripleMeasurement floatTripleMeasurement2 = new FloatTripleMeasurement(1f, 2f, 3f);

    Sample sample1 = new Sample();
    sample1.addMeasurement(floatTripleMeasurement1);
    Sample sample2 = new Sample();
    sample2.addMeasurement(floatTripleMeasurement2);

    assertEquals(sample1, sample2);
  }

  public void testEqualsSingleElementDifferentValues() {
    final FloatTripleMeasurement floatTripleMeasurement1 = new FloatTripleMeasurement(1f, 2f, 3f);
    final FloatTripleMeasurement floatTripleMeasurement2 = new FloatTripleMeasurement(1f, 2f, 4f);

    Sample sample1 = new Sample();
    sample1.addMeasurement(floatTripleMeasurement1);
    Sample sample2 = new Sample();
    sample2.addMeasurement(floatTripleMeasurement2);

    assertNotSame(sample1, sample2);
  }

  public void testEqualsDifferentTimestamp() throws InterruptedException {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);
    Sample sample1 = Sample.Create(floatTripleMeasurement);
    Thread.sleep(100);
    Sample sample2 = Sample.Create(floatTripleMeasurement);

    boolean equals = sample1.equals(sample2);

    assertFalse("They are equals", equals);
  }

  public void testEqualsSameReference() {
    final FloatTripleMeasurement floatTripleMeasurement = new FloatTripleMeasurement(1f, 2f, 3f);

    Sample sample1 = Sample.Create(floatTripleMeasurement);
    Sample sample2 = sample1;

    assertEquals(sample1, sample2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_sample.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Sample sample = Sample.Create(new FloatTripleMeasurement(1f, 2f, 3f));

    realm.beginTransaction();
    realm.copyToRealm(sample);
    realm.commitTransaction();

    final Sample loadedSample = realm.where(Sample.class).findFirst();

    final boolean equals = sample.equals(loadedSample);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded sample was not equal to the original", equals);
  }

}
