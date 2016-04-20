package dk.aau.sw808f16.datacollection.campaign;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dk.aau.sw808f16.datacollection.snapshot.JsonObjectAble;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Campaign extends RealmObject implements JsonObjectAble {

  @PrimaryKey
  private int identifier;
  private RealmList<Snapshot> snapshots;

  public Campaign() {
    if (snapshots == null) {
      snapshots = new RealmList<>();
    }
  }

  public Campaign(final int identifier) {
    this();
    setIdentifier(identifier);
  }

  public void addSnapshot(final Snapshot snapshot) {
    snapshots.add(snapshot);
  }

  public List<Snapshot> getSnapshots() {
    return snapshots;
  }

  @Override
  public boolean equals(final Object object) {
    if (!(object instanceof Campaign)) {
      return false;
    }

    final Campaign that = (Campaign) object;

    if (this.getIdentifier() != that.getIdentifier()) {
      return false;
    } else {
      final List<Snapshot> ourSnapshots = this.getSnapshots();
      final List<Snapshot> theirSnapshots = that.getSnapshots();

      if (theirSnapshots == null) {
        return ourSnapshots == null;
      } else {
        return theirSnapshots.equals(ourSnapshots);
      }
    }
  }

  public void setIdentifier(final int identifier) {
    this.identifier = identifier;
  }

  public int getIdentifier() {
    return identifier;
  }

  @Override
  public JSONObject toJsonObject() throws JSONException {
    final JSONObject jsonObject = new JSONObject();

    JSONArray snapshotArray = new JSONArray();
    for (Snapshot snapshot : getSnapshots()) {
      snapshotArray.put(snapshot.toJsonObject());
    }

    jsonObject.put("snapshots", snapshotArray);

    return jsonObject;
  }

}
