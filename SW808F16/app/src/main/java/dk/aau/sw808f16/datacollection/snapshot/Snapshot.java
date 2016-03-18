package dk.aau.sw808f16.datacollection.snapshot;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.aau.sw808f16.datacollection.label.Label;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Snapshot extends RealmObject {
  private static final String DEFAULT_REALM_NAME = "DEFAULT_REALM_NAME";

  private long timestamp;

  @Ignore
  private Label label;

  @Ignore
  private Map<Integer, List<Sample>> samplesMap;

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

  public long save(final Context context, final byte[] encryptionKey) {
    return save(context, DEFAULT_REALM_NAME, encryptionKey);
  }

  public long save(final Context context, final String realmName, final byte[] encryptionKey) {
    this.timestamp = System.currentTimeMillis();

    RealmConfiguration config = new RealmConfiguration.Builder(context)
        .name(realmName + ".realm")
        .encryptionKey(encryptionKey)
        .build();

    Realm realm = Realm.getInstance(config);

    // Store this snapshot in the realm
    realm.beginTransaction();
    realm.copyToRealm(this);
    realm.commitTransaction();

    realm.close();

    return timestamp;
  }

  public static Snapshot load(final Context context, final byte[] encryptionKey, final long snapshotIdentifier) {
    return load(context, snapshotIdentifier, encryptionKey, DEFAULT_REALM_NAME);
  }

  public static Snapshot load(final Context context, final long snapshotIdentifier, final byte[] encryptionKey, final String realmName) {
    RealmConfiguration config = new RealmConfiguration.Builder(context)
        .name(realmName + ".realm")
        .encryptionKey(encryptionKey)
        .build();

    Realm realm = Realm.getInstance(config);

    // Load the snapshot with that timestamp
    Snapshot snapshot = realm.where(Snapshot.class).equalTo("timestamp", snapshotIdentifier).findFirst();

    realm.close();

    return snapshot;
  }
}
