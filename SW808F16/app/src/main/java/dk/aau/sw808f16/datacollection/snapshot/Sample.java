package dk.aau.sw808f16.datacollection.snapshot;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.HeartRateMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.IntegerMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.LocationMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.WifiMeasurement;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;


@SuppressWarnings("CanBeFinal")
public class Sample extends RealmObject implements JsonObjectAble {

  @Ignore
  private Class clazz = null;

  private RealmList<FloatTripleMeasurement> floatTripleMeasurements = new RealmList<>();
  private RealmList<FloatMeasurement> floatMeasurements = new RealmList<>();
  private RealmList<WifiMeasurement> wifiMeasurements = new RealmList<>();
  private RealmList<LocationMeasurement> locationMeasurements = new RealmList<>();
  private RealmList<HeartRateMeasurement> heartRateMeasurements = new RealmList<>();
  private RealmList<IntegerMeasurement> integerMeasurements = new RealmList<>();
  private long timestamp;

  /**
   * @deprecated Do not use this constructor. This is reserved for Realm.io
   */
  @Deprecated
  public Sample() {
  }

  public static Sample Create() {
    Sample sample = new Sample();
    sample.timestamp = Calendar.getInstance().getTimeInMillis();
    return sample;
  }

  public static Sample Create(final Object initialMeasurement) {
    Sample sample = Sample.Create();
    sample.addMeasurement(initialMeasurement);
    return sample;
  }

  public static Sample Create(final List<?> initialMeasurements) {
    Sample sample = Sample.Create();
    for (Object o : initialMeasurements) {
      sample.addMeasurement(o);
    }
    return sample;
  }

  public void addMeasurement(final Object measurement) {
    if (clazz == null) {
      clazz = measurement.getClass();
    } else if (!clazz.equals(measurement.getClass())) {
      throw new IllegalArgumentException("The sample contains measurements of type " + clazz.getName()
          + " you cannot add measurements of type " + measurement.getClass().getName());
    }

    if (measurement instanceof FloatTripleMeasurement) {
      floatTripleMeasurements.add((FloatTripleMeasurement) measurement);
    } else if (measurement instanceof FloatMeasurement) {
      floatMeasurements.add((FloatMeasurement) measurement);
    } else if (measurement instanceof WifiMeasurement) {
      wifiMeasurements.add((WifiMeasurement) measurement);
    } else if (measurement instanceof LocationMeasurement) {
      locationMeasurements.add((LocationMeasurement) measurement);
    } else if (measurement instanceof HeartRateMeasurement) {
      heartRateMeasurements.add((HeartRateMeasurement) measurement);
    } else if (measurement instanceof IntegerMeasurement) {
      integerMeasurements.add((IntegerMeasurement) measurement);
    } else {
      throw new IllegalArgumentException("Type " + measurement.getClass().getName() + " is not a supported measurement type");
    }
  }

  public void addMeasurements(final List<?> measurements) {
    for (Object o : measurements) {
      addMeasurement(o);
    }
  }

  public int size() {
    return floatTripleMeasurements.size() + floatMeasurements.size() + wifiMeasurements.size() + locationMeasurements.size();
  }

  public List<JsonValueAble> getMeasurements() {

    final List<JsonValueAble> result = new ArrayList<>();

    // Concatenate the different lists into a single one (there should only be one list containing elements)
    result.addAll(floatTripleMeasurements);
    result.addAll(floatMeasurements);
    result.addAll(wifiMeasurements);
    result.addAll(locationMeasurements);
    result.addAll(heartRateMeasurements);
    result.addAll(integerMeasurements);

    return result;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || !Sample.class.isAssignableFrom(object.getClass())) {
      return false;
    }

    final Sample that = (Sample) object;

    if (this.timestamp != that.timestamp) {
      return false;
    }

    List<?> ourMeasurements = this.getMeasurements();
    List<?> theirMeasurements = that.getMeasurements();

    if (ourMeasurements.size() != theirMeasurements.size()) {
      return false;
    } else {
      for (int i = 0; i < ourMeasurements.size(); i++) {
        if (!ourMeasurements.get(i).equals(theirMeasurements.get(i))) {
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public JSONObject toJsonObject() throws JSONException {

    final JSONObject sampleJsonObject = new JSONObject();
    final JSONArray jsonMeasurements = new JSONArray();

    for (JsonValueAble object : getMeasurements()) {
      jsonMeasurements.put(object.toJsonValue());
    }

    sampleJsonObject.put("timestamp", timestamp);
    sampleJsonObject.put("measurements", jsonMeasurements);

    return sampleJsonObject;
  }

  public List<RealmObject> children() {
    List<RealmObject> children = new ArrayList<>();
    children.add(this);

    children.addAll(floatTripleMeasurements);
    children.addAll(floatMeasurements);
    children.addAll(locationMeasurements);
    children.addAll(heartRateMeasurements);
    children.addAll(wifiMeasurements);

    for (WifiMeasurement measurement : wifiMeasurements) {
      children.addAll(measurement.children());
    }

    return children;
  }
}
