package dk.aau.sw808f16.datacollection.snapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.label.Label;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Snapshot extends RealmObject implements JsonObjectAble {

  private Label label;
  private long timestamp;
  private RealmList<Sample> accelerometerSamples = new RealmList<>();
  private RealmList<Sample> ambientLightSamples = new RealmList<>();
  private RealmList<Sample> barometerSamples = new RealmList<>();
  private RealmList<Sample> cellularSamples = new RealmList<>();
  private RealmList<Sample> compassSamples = new RealmList<>();
  private RealmList<Sample> gyroscopeSamples = new RealmList<>();
  private RealmList<Sample> locationSamples = new RealmList<>();
  private RealmList<Sample> proximitySamples = new RealmList<>();
  private RealmList<Sample> wifiSamples = new RealmList<>();

  @Ignore
  private Map<SensorType, RealmList<Sample>> sensorSampleMap = null;

  @SuppressWarnings("deprecation")
  public static Snapshot Create() {
    Snapshot snapshot = new Snapshot();
    snapshot.timestamp = Calendar.getInstance().getTimeInMillis();
    return snapshot;
  }

  /**
   * @deprecated Use the {@link #Create()} factory method. This is reserved for Realm.io
   */
  @Deprecated
  public Snapshot() {
  }

  public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public void addSample(SensorType sensorType, Sample sample) {
    populateMapIfNull();
    if (sensorSampleMap.containsKey(sensorType)) {
      sensorSampleMap.get(sensorType).add(sample);
    } else {
      throw new IllegalArgumentException(sensorType.getClass().getName() + " is not yet a supported sensor");
    }
  }

  public List<Sample> getSamples(SensorType sensorType) {
    populateMapIfNull();
    if (sensorSampleMap.containsKey(sensorType)) {
      return sensorSampleMap.get(sensorType);
    } else {
      throw new IllegalArgumentException(sensorType.getClass().getName() + " is not yet a supported sensor");
    }
  }

  public void addSamples(SensorType sensorType, List<Sample> samples) {
    for (final Sample sample : samples) {
      addSample(sensorType, sample);
    }
  }

  @Override
  public boolean equals(Object object) {

    if (this == object) {
      return true;
    }

    if (object == null || !Snapshot.class.isAssignableFrom(object.getClass())) {
      return false;
    }

    final Snapshot that = (Snapshot) object;

    boolean isSame = this.getLabel() != null ? this.getLabel().equals(that.getLabel()) : that.getLabel() == null &&
        this.timestamp == that.timestamp;

    if (!isSame) {
      return false;
    }

    populateMapIfNull();

    for (Map.Entry<SensorType, RealmList<Sample>> mapEntry : sensorSampleMap.entrySet()) {
      RealmList<Sample> theirSamples = (RealmList<Sample>) that.getSamples(mapEntry.getKey());
      RealmList<Sample> ourSamples = mapEntry.getValue();

      if (ourSamples.size() != theirSamples.size() || !ourSamples.equals(theirSamples)) {
        return false;
      }
    }
    return true;
  }

  private void populateMapIfNull() {
    if (sensorSampleMap != null) {
      return;
    }

    // Populate the map of sensor samples
    sensorSampleMap = new HashMap<>();
    sensorSampleMap.put(SensorType.ACCELEROMETER, accelerometerSamples);
    sensorSampleMap.put(SensorType.AMBIENT_LIGHT, ambientLightSamples);
    sensorSampleMap.put(SensorType.BAROMETER, barometerSamples);
    sensorSampleMap.put(SensorType.CELLULAR, cellularSamples);
    sensorSampleMap.put(SensorType.COMPASS, compassSamples);
    sensorSampleMap.put(SensorType.GYROSCOPE, gyroscopeSamples);
    sensorSampleMap.put(SensorType.LOCATION, locationSamples);
    sensorSampleMap.put(SensorType.PROXIMITY, proximitySamples);
    sensorSampleMap.put(SensorType.WIFI, wifiSamples);
  }

  @Override
  public JSONObject toJsonObject() throws JSONException {

    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("timestamp", this.timestamp);
    addSampleListToJsonObject(jsonObject, "accelerometerSamples", accelerometerSamples);
    addSampleListToJsonObject(jsonObject, "ambientLightSamples", ambientLightSamples);
    addSampleListToJsonObject(jsonObject, "barometerSamples", barometerSamples);
    addSampleListToJsonObject(jsonObject, "cellularSamples", cellularSamples);
    addSampleListToJsonObject(jsonObject, "compassSamples", compassSamples);
    addSampleListToJsonObject(jsonObject, "gyroscopeSamples", gyroscopeSamples);
    addSampleListToJsonObject(jsonObject, "locationSamples", locationSamples);
    addSampleListToJsonObject(jsonObject, "proximitySamples", proximitySamples);
    addSampleListToJsonObject(jsonObject, "wifiSamples", wifiSamples);

    return jsonObject;
  }

  private void addSampleListToJsonObject(final JSONObject targetJsonObject, final String key, final RealmList<Sample> samples) {
    if (!samples.isEmpty()) {

      final JSONArray samplesJsonArray = new JSONArray();

      for (final Sample sample : samples) {
        try {
          samplesJsonArray.put(sample.toJsonObject());
        } catch (JSONException exception) {
          exception.printStackTrace();
        }
      }

      try {
        targetJsonObject.put(key, samplesJsonArray);
      } catch (JSONException exception) {
        exception.printStackTrace();
      }
    }

  }
}
