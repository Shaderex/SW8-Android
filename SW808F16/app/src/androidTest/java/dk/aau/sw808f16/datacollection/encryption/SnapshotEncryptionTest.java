package dk.aau.sw808f16.datacollection.encryption;

import android.hardware.Sensor;
import android.test.AndroidTestCase;

import com.google.gson.GsonBuilder;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.SimpleStorageConfiguration;
import com.sromku.simple.storage.Storage;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;

public class SnapshotEncryptionTest extends AndroidTestCase {

  // This test will check if the encrypted version of the snapshot is saved on the device and can be read properly
  public void testSnapshotEncryption() {
    final String publicKey = "AAAAAAAAAAAAAAAA";
    final String secretKey = "thisisasecret123";

    final Snapshot snapshot = new Snapshot();

    for (int i = 0; i < 10; i++) {
      final Sample sample = new Sample();
      for (int j = 0; j < 10; j++) {
        sample.addMeasurement(j);
      }
      snapshot.addSample(Sensor.TYPE_ACCELEROMETER, sample);
    }

    // Convert the snapshot to a string
    final String snapshotAsString = new GsonBuilder().create().toJson(snapshot);
    assertNotNull(snapshotAsString);

    final Storage storage = SimpleStorage.getInternalStorage(getContext());
    assertNotNull(storage);

    final SimpleStorageConfiguration config = new SimpleStorageConfiguration.Builder().setEncryptContent(publicKey, secretKey).build();
    SimpleStorage.updateConfiguration(config);

    final String directory = "myDirectory";
    final String file = "myFile.data";

    // Create the directory and the file containing the snapshot as a string
    storage.createDirectory(directory);
    storage.createFile(directory, file, snapshotAsString);

    final String readSnapshotFromFile = storage.readTextFile(directory, file);

    assertEquals(snapshotAsString, readSnapshotFromFile);
  }

}
