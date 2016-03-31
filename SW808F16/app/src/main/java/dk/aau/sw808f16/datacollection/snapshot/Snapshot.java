package dk.aau.sw808f16.datacollection.snapshot;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.label.Label;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Snapshot extends RealmObject {

  private Label label;
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
      Log.d("LORT", "1");
      return true;
    }

    if (object == null || !Snapshot.class.isAssignableFrom(object.getClass())) {
      Log.d("LORT", "2");
      return false;
    }

    final Snapshot that = (Snapshot) object;

    if ((this.getLabel() == null || that.getLabel() == null) && this.getLabel() != that.getLabel()) {
      Log.d("LORT", "3");
      return false;
    }

    if (this.getLabel() != null && !this.getLabel().equals(that.getLabel())) {
      Log.d("LORT", "4");
      return false;
    }

    for (Map.Entry<SensorType, RealmList<Sample>> mapEntry : sensorSampleMap.entrySet()) {
      RealmList<Sample> theirSamples = (RealmList<Sample>) that.getSamples(mapEntry.getKey());
      RealmList<Sample> ourSamples = mapEntry.getValue();

      if (ourSamples.size() != theirSamples.size() || !ourSamples.equals(theirSamples)) {
        Log.d("LORT", "5");
        return false;
      }
    }
    Log.d("LORT", "6");
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
}
