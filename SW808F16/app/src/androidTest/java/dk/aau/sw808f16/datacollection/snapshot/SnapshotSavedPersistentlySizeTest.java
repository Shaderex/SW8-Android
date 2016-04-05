package dk.aau.sw808f16.datacollection.snapshot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AccelerometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AmbientLightSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.BarometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.CompassSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.GyroscopeSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.LocationSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.ProximitySensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.SensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.WifiSensorProvider;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SnapshotSavedPersistentlySizeTest extends ApplicationTestCase<DataCollectionApplication> {

  public SnapshotSavedPersistentlySizeTest() {
    super(DataCollectionApplication.class);
  }

  public void testSnapshotSavedPersistentlySizeSmallEnough() throws ExecutionException, InterruptedException {
    final int runTestForMinutes = 3; // Minutes
    final int maxSizePerMinute = 10000000 / (60 * 24); // Bytes (10 MB per 24 hours)
    final int numberOfSensors = 9; // # Sensors. Do not change this

    assertTrue("Test must run for at least 1 minute", runTestForMinutes >= 1);

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(numberOfSensors);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    // Create all providers
    final HashMap<SensorType, SensorProvider> providers = new HashMap<>();
    providers.put(SensorType.ACCELEROMETER, new AccelerometerSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SensorType.GYROSCOPE, new GyroscopeSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SensorType.AMBIENT_LIGHT, new AmbientLightSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SensorType.BAROMETER, new BarometerSensorProvider(getContext(), sensorThreadPool, sensorManager));

    // TODO Consider removing this
    // providers.put(SensorType.CELLULAR, new CellularNetworkSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SensorType.COMPASS, new CompassSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SensorType.LOCATION, new LocationSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SensorType.PROXIMITY, new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SensorType.WIFI, new WifiSensorProvider(getContext(), sensorThreadPool, sensorManager));

    int availableSensors = 0;
    for (SensorProvider provider : providers.values()) {
      if (provider.isSensorAvailable()) {
        assertNotNull("Provider is null", provider);
        availableSensors++;
      }
    }

    if (availableSensors != numberOfSensors) {
      Log.i("SnapshotSizeTest", "The device does not facilitate all of the required sensors");
    }

    final HashMap<SensorType, Future<List<Sample>>> samplesRetrieved = new HashMap<>();

    // Get the samples for the different sensors
    for (Map.Entry<SensorType, SensorProvider> entry : providers.entrySet()) {
      final SensorType key = entry.getKey();
      final SensorProvider value = entry.getValue();
      if (value.isSensorAvailable()) {
        samplesRetrieved.put(key, value.retrieveSamplesForDuration(runTestForMinutes * 60 * 1000, 60 * 1000, 1000, 100));
      }
    }

    // ------------------------------- //
    // Sensors are now collecting data //
    // ------------------------------- //

    final Snapshot snapshot = new Snapshot();
    for (Map.Entry<SensorType, Future<List<Sample>>> entry : samplesRetrieved.entrySet()) {
      final SensorType key = entry.getKey();
      final List<Sample> value = entry.getValue().get();
      snapshot.addSamples(key, value);
    }

    final String realmName = "test_size.realm";

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name(realmName).build();

    final Runnable realmRunnable = new Runnable() {
      @Override
      public void run() {
        final Realm realm = Realm.getInstance(realmConfiguration);

        realm.beginTransaction();
        realm.copyToRealm(snapshot);
        realm.commitTransaction();

        realm.close();
      }
    };

    final Thread thread = new Thread(realmRunnable);
    thread.start();
    thread.join();


    @SuppressLint("SdCardPath")
    final File file = new File("/data/data/dk.aau.sw808f16.datacollection/files/" + realmName);
    final long size = file.length();

    Realm.deleteRealm(realmConfiguration);

    final long expectedSize = maxSizePerMinute * runTestForMinutes;
    final long percentageSize = Math.round(((double) size) / ((double) expectedSize) * 100);

    String error = "File size is larger than expected.";
    error += " Should be maximally " + expectedSize + " bytes, but is " + size + " bytes";
    error += " (" + percentageSize + "%)";

    assertTrue(error, size <= expectedSize);
  }
}
