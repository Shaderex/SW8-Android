package dk.aau.sw808f16.datacollection.backgroundservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AccelerometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AmbientLightSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.BarometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.CellularNetworkSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.CompassSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.GyroscopeSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.LocationSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.ProximitySensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.SensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.WifiSensorProvider;
import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;

public class SnapshotSavedPersistentlySizeTest extends ApplicationTestCase<DataCollectionApplication> {
  private static final int SENSOR_ACCELEROMETER = 0;
  private static final int SENSOR_GYROSCOPE = 1;
  private static final int SENSOR_AMBIENT_LIGHT = 2;
  private static final int SENSOR_BAROMETER = 3;
  private static final int SENSOR_CELLULAR_NETWORK = 4;
  private static final int SENSOR_COMPASS = 5;
  private static final int SENSOR_LOCATION = 6;
  private static final int SENSOR_PROXIMITY = 7;
  private static final int SENSOR_WIFI = 8;

  private static final String PUBLIC_KEY = "AAAAAAAAAAAAAAAA";
  private static final String SECRET_KEY = "thisisasecret123";
  private static final String DIRECTORY = SnapshotSavedPersistentlySizeTest.class.getName();
  private static final String FILE = SnapshotSavedPersistentlySizeTest.class.getName() + ".snapshot";

  public SnapshotSavedPersistentlySizeTest() {
    super(DataCollectionApplication.class);
  }

  public void testSnapshotSavedPersistentlySizeSmallEnough() throws ExecutionException, InterruptedException, IOException {
    final int runTestForMinutes = 1; // Minutes
    final int maxSizePerMinute = 10000000 / (60 * 24); // Bytes (10 MB per 24 hours)
    final int numberOfSensors = 9; // # Sensors. Do not change this

    assertTrue("Test must run for at least 1 minute", runTestForMinutes >= 1);

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(numberOfSensors);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

    // Create all providers
    final HashMap<Integer, SensorProvider> providers = new HashMap<>();
    providers.put(SENSOR_ACCELEROMETER, new AccelerometerSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_GYROSCOPE, new GyroscopeSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_AMBIENT_LIGHT, new AmbientLightSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_BAROMETER, new BarometerSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_CELLULAR_NETWORK, new CellularNetworkSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_COMPASS, new CompassSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_LOCATION, new LocationSensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_PROXIMITY, new ProximitySensorProvider(getContext(), sensorThreadPool, sensorManager));
    providers.put(SENSOR_WIFI, new WifiSensorProvider(getContext(), sensorThreadPool, sensorManager));

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

    final HashMap<Integer, Future<List<Sample>>> samplesRetrieved = new HashMap<>();

    // Get the samples for the different sensors
    for (Map.Entry<Integer, SensorProvider> entry : providers.entrySet()) {
      final Integer key = entry.getKey();
      final SensorProvider value = entry.getValue();
      samplesRetrieved.put(key, value.retrieveSamplesForDuration(runTestForMinutes * 60 * 1000, 60 * 1000, 1000, 100));
    }

    // ------------------------------- //
    // Sensors are now collecting data //
    // ------------------------------- //

    final Snapshot snapshot = new Snapshot();
    for (Map.Entry<Integer, Future<List<Sample>>> entry : samplesRetrieved.entrySet()) {
      final Integer key = entry.getKey();
      final List<Sample> value = entry.getValue().get();
      snapshot.addSamples(key, value);
    }

    final boolean isSavedSuccessfully = snapshot.save(getContext(), DIRECTORY, FILE, PUBLIC_KEY, SECRET_KEY);
    assertTrue("The snapshot could not be saved", isSavedSuccessfully);

    @SuppressLint("SdCardPath")
    final File file = new File("/data/data/dk.aau.sw808f16.datacollection/app_" + DIRECTORY + "/" + FILE);
    final long size = file.length();

    final long expectedSize = maxSizePerMinute * runTestForMinutes;
    final double percentageSize = Math.round(((double) size) / ((double) expectedSize) * 100);

    String error = "File size is larger than expected.";
    error += " Should be maximally " + expectedSize + " bytes, but is " + size + " bytes";
    error += " (" + percentageSize + "%)";

    assertTrue(error, size <= expectedSize);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    final Storage storage = SimpleStorage.getInternalStorage(getContext());
    storage.deleteFile(DIRECTORY, FILE);
    storage.deleteDirectory(DIRECTORY);
  }
}
