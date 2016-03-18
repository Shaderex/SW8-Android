package dk.aau.sw808f16.datacollection.snapshot;

import android.hardware.Sensor;
import android.test.ApplicationTestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.SimpleStorageConfiguration;
import com.sromku.simple.storage.Storage;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.label.Label;

public class SnapshotTest extends ApplicationTestCase<DataCollectionApplication> {

  public SnapshotTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Snapshot();
  }

  public void testGetSetLabel() {
    final Label expectedLabel = new Label();
    final Snapshot snapshot = new Snapshot();

    snapshot.setLabel(expectedLabel);

    assertEquals(expectedLabel, snapshot.getLabel());
  }

  public void testAddSampleGetSamples() {
    final Snapshot snapshot = new Snapshot();
    final Sample sample = new Sample();
    final int sensorType = Sensor.TYPE_ACCELEROMETER;

    snapshot.addSample(sensorType, sample);

    assertTrue(snapshot.getSamples(sensorType).contains(sample));
  }

  public void testEmptyGetSamples() {
    final Snapshot snapshot = new Snapshot();

    final List<Sample> actual = snapshot.getSamples(Sensor.TYPE_ACCELEROMETER);

    assertTrue(actual.isEmpty());
  }

  public void testAddSamplesGetSamples() {

    final Snapshot snapshot = new Snapshot();
    final int sensorType = Sensor.TYPE_ACCELEROMETER;

    final List<Sample> expected = new ArrayList<Sample>() {
      {
        add(new Sample());
        add(new Sample());
        add(new Sample());
      }
    };

    snapshot.addSamples(sensorType, expected);

    assertEquals(expected, snapshot.getSamples(sensorType));
  }

  private static final String TEST_REALM_NAME = "TEST_REALM_NAME";

  private static final String PUBLIC_KEY = "AAAAAAAAAAAAAAAA";
  private static final String SECRET_KEY = "thisisasecret123";

  private static final String DIRECTORY = "myDirectory";
  private static final String FILE = "myFile.data";

  // This test will check if the encrypted version of the snapshot is saved on the device and can be read properly
  public void testSnapshotEncryption() {
    final Snapshot snapshot = new Snapshot();

    for (int i = 0; i < 10; i++) {
      final Sample sample = new Sample();
      for (int j = 0; j < 10; j++) {
        sample.addMeasurement(j);
      }
      snapshot.addSample(Sensor.TYPE_ACCELEROMETER, sample);
    }

    final Gson gson = new GsonBuilder().create();

    // Convert the snapshot to a string
    final String snapshotAsString = gson.toJson(snapshot);
    assertNotNull(snapshotAsString);

    final Storage storage = SimpleStorage.getInternalStorage(getContext());
    assertNotNull(storage);

    final SimpleStorageConfiguration config = new SimpleStorageConfiguration.Builder().setEncryptContent(PUBLIC_KEY, SECRET_KEY).build();
    SimpleStorage.updateConfiguration(config);

    // Create the directory and the file containing the snapshot as a string
    storage.createDirectory(DIRECTORY);
    storage.createFile(DIRECTORY, FILE, snapshotAsString);

    final String readSnapshotFromFile = storage.readTextFile(DIRECTORY, FILE);

    assertEquals(snapshotAsString, readSnapshotFromFile);
  }

  public void testSnapshotSaveLoad() {
    final Snapshot originalSnapshot = new Snapshot();

    // Create 10 samples with 10 measurements each (Mocking accelerometer sensor data)
    for (int i = 0; i < 10; i++) {
      final Sample sample = new Sample();
      for (double j = 0.0; j < 10; j++) {
        sample.addMeasurement(j);
      }
      originalSnapshot.addSample(Sensor.TYPE_ACCELEROMETER, sample);
    }

    final byte[] key = new byte[64];
    new SecureRandom().nextBytes(key);

    // Save the snapshot persistently
    long snapshotIdentifier = originalSnapshot.save(getContext(), TEST_REALM_NAME, key);
    assertTrue("Snapshot ID is not a natural number ( > 0)", snapshotIdentifier > 0);

    // Load the snapshot from file
    final Snapshot loadedSnapshot = Snapshot.load(getContext(), snapshotIdentifier, key, TEST_REALM_NAME);

    // Use gson to check the equality
    // TODO Consider overriding .equals for Snapshot
    final Gson gson = new GsonBuilder().create();
    assertEquals("Original and saved snapshots are different", gson.toJson(originalSnapshot), gson.toJson(loadedSnapshot));
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    final Storage storage = SimpleStorage.getInternalStorage(getContext());
    storage.deleteFile(DIRECTORY, FILE);
    storage.deleteDirectory(DIRECTORY);
  }
}
