package dk.aau.sw808f16.datacollection.snapshot;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.SimpleStorageConfiguration;
import com.sromku.simple.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.aau.sw808f16.datacollection.label.Label;

public class Snapshot {

  private Label label;
  private final Map<Integer, List<Sample>> samplesMap;

  public Snapshot() {
    samplesMap = new HashMap<>();
  }

  public Snapshot(final Context context, final String directory, final String file, final String publicKey, final String secretKey) {
    final Gson gson = new GsonBuilder().create();

    final Storage storage = SimpleStorage.getInternalStorage(context);

    final String extractedData = storage.readTextFile(directory, file);
    final Snapshot newSnapshot = gson.fromJson(extractedData, Snapshot.class);

    // Update the label accordingly to the restored snapshot
    this.setLabel(newSnapshot.getLabel());

    // Update the samples accordingly to the restored snapshot
    samplesMap = new HashMap<>();
    samplesMap.putAll(newSnapshot.samplesMap);
  }

  public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public void addSample(int sensorType, Sample sample) {
    if (!samplesMap.containsKey(sensorType)) {
      samplesMap.put(sensorType, new ArrayList<Sample>());
    }
    samplesMap.get(sensorType).add(sample);
  }

  public List<Sample> getSamples(int sensorType) {
    if (!samplesMap.containsKey(sensorType)) {
      // Return an empty list to indicate that no samples are available for the requested sensor
      return new ArrayList<>();
    }

    return samplesMap.get(sensorType);
  }

  public void addSamples(int sensorType, List<Sample> samples) {
    for (final Sample sample : samples) {
      addSample(sensorType, sample);
    }
  }

  public boolean save(final Context context, final String directory, final String file, final String publicKey, final String secretKey) {
    final Storage storage = SimpleStorage.getInternalStorage(context);

    final SimpleStorageConfiguration config = new SimpleStorageConfiguration.Builder().setEncryptContent(publicKey, secretKey).build();
    SimpleStorage.updateConfiguration(config);

    // Convert the snapshot to a GSON (json) string
    final String snapshotAsString = new GsonBuilder().create().toJson(this);

    // Create the directory and the file containing the snapshot as a string
    return storage.createDirectory(directory) && storage.createFile(directory, file, snapshotAsString);
  }
}
