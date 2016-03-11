package dk.aau.sw808f16.datacollection.encryption;

import android.hardware.Sensor;
import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.SimpleStorageConfiguration;
import com.sromku.simple.storage.Storage;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;

public class SnapshotEncryptionTest extends AndroidTestCase {

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

    for (int i = 0; i < 10; i++) {
      final Sample sample = new Sample();
      for (double j = 0.0; j < 10; j++) {
        sample.addMeasurement(j);
      }
      originalSnapshot.addSample(Sensor.TYPE_ACCELEROMETER, sample);
    }

    // Save the snapshot persistently
    boolean saveResult = originalSnapshot.save(getContext(), DIRECTORY, FILE, PUBLIC_KEY, SECRET_KEY);
    assertTrue("Could not save snapshot", saveResult);

    // Load the snapshot from file
    final Snapshot loadedSnapshot = new Snapshot(getContext(), DIRECTORY, FILE, PUBLIC_KEY, SECRET_KEY);

    final Gson gson = new GsonBuilder().create();

    // Consider overriding .equals for Snapshot
    assertEquals("Original and saved snapshots are different", gson.toJson(originalSnapshot), gson.toJson(loadedSnapshot));
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    final Storage storage = SimpleStorage.getInternalStorage(getContext());
    storage.deleteFile(DIRECTORY,FILE);
    storage.deleteDirectory(DIRECTORY);
  }
}
