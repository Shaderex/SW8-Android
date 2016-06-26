package dk.aau.sw808f16.wekatest.snapshot.measurement;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ApplicationTestCase;

import dk.aau.sw808f16.wekatest.DataCollectionApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class LocationMeasurementTest extends ApplicationTestCase<DataCollectionApplication> {

  private Location location;
  private boolean missingGps;

  public LocationMeasurementTest() {
    super(DataCollectionApplication.class);
  }

  public void setUp() {
    final LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    missingGps = location == null;
  }

  public void testConstructor() {
    if (missingGps) {
      return;
    }
    new LocationMeasurement();
    new LocationMeasurement(location);
  }

  public void testExtendsRealmObject() {
    assertTrue(LocationMeasurement.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(LocationMeasurement.class));
  }

  public void testSetGetLongitude() {
    double longitude = 13.377777;
    LocationMeasurement locationMeasurement = new LocationMeasurement();
    locationMeasurement.setLongitude(longitude);

    assertEquals("The getter and setter method are inconsistent", longitude, locationMeasurement.getLongitude(), 0.0001);
  }

  public void testSetGetLatitude() {
    double latitude = 13.377777;
    LocationMeasurement locationMeasurement = new LocationMeasurement();
    locationMeasurement.setLatitude(latitude);

    assertEquals("The getter and setter method are inconsistent", latitude, locationMeasurement.getLatitude(), 0.0001);
  }

  public void testSetGetAccuracy() {
    float accuracy = 22.100f;
    LocationMeasurement locationMeasurement = new LocationMeasurement();
    locationMeasurement.setAccuracy(accuracy);

    assertEquals("The getter and setter method are inconsistent", accuracy, locationMeasurement.getAccuracy(), 0.0001f);
  }

  public void testSetGetBearing() {
    float bearing = 22.100f;
    LocationMeasurement locationMeasurement = new LocationMeasurement();
    locationMeasurement.setBearing(bearing);

    assertEquals("The getter and setter method are inconsistent", bearing, locationMeasurement.getBearing(), 0.0001f);
  }

  public void testSetGetSpeed() {
    float speed = 420.100f;
    LocationMeasurement locationMeasurement = new LocationMeasurement();
    locationMeasurement.setSpeed(speed);

    assertEquals("The getter and setter method are inconsistent", speed, locationMeasurement.getSpeed(), 0.0001f);
  }

  public void testEqualsNull() {
    final LocationMeasurement locationMeasurement = new LocationMeasurement();

    assertNotSame(locationMeasurement, null);
  }

  public void testEqualsSameValues() {
    if (missingGps) {
      return;
    }

    final LocationMeasurement locationMeasurement1 = new LocationMeasurement(location);
    final LocationMeasurement locationMeasurement2 = new LocationMeasurement(location);

    assertEquals(locationMeasurement1, locationMeasurement2);
  }

  public void testEqualsDifferentValues() {
    if (missingGps) {
      return;
    }


    final LocationMeasurement locationMeasurement1 = new LocationMeasurement(location);
    Location location2 = location;
    location2.setLatitude(0);
    final LocationMeasurement locationMeasurement2 = new LocationMeasurement(location2);

    assertNotSame(locationMeasurement1, locationMeasurement2);
  }

  public void testEqualsSameReference() {
    if (missingGps) {
      return;
    }

    final LocationMeasurement location1 = new LocationMeasurement(location);
    final LocationMeasurement location2 = location1;

    assertEquals(location1, location2);
  }

  public void testSaveToRealm() {
    if (missingGps) {
      return;
    }

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(
        getContext()).name("location_measurement_test.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final LocationMeasurement locationMeasurement = new LocationMeasurement(location);

    realm.beginTransaction();
    realm.copyToRealm(locationMeasurement);
    realm.commitTransaction();

    final LocationMeasurement loadedLocationMeasurement = realm.where(LocationMeasurement.class).findFirst();

    final boolean equals = locationMeasurement.equals(loadedLocationMeasurement);

    realm.close();

    Realm.deleteRealm(realmConfiguration);

    assertTrue("The loaded measurement was not equal to the original", equals);
  }
}
