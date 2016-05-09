package dk.aau.sw808f16.datacollection.snapshot.measurement;

import com.microsoft.band.sensors.HeartRateQuality;

import org.json.JSONException;
import org.json.JSONObject;

import dk.aau.sw808f16.datacollection.snapshot.JsonValueAble;
import io.realm.RealmObject;

public class HeartRateMeasurement extends RealmObject implements JsonValueAble {

  private int heartRate;
  private boolean heartRateQualityLocked;

  public HeartRateMeasurement() {
    // Do not use this
  }

  public HeartRateMeasurement(final int heartRate, final HeartRateQuality heartRateQuality) {
    this.heartRate = heartRate;
    this.heartRateQualityLocked = heartRateQuality == HeartRateQuality.LOCKED;
  }

  @Override
  public boolean equals(final Object object) {
    return super.equals(object)
        || (object instanceof HeartRateMeasurement && ((HeartRateMeasurement) object).heartRate == this.heartRate
        && ((HeartRateMeasurement) object).heartRateQualityLocked == this.heartRateQualityLocked);
  }

  public Object toJsonValue() throws JSONException {
    final JSONObject jsonObject = new JSONObject();

    jsonObject.put("heartRate", heartRate);
    jsonObject.put("heartRateQualityLocked", heartRateQualityLocked);

    return jsonObject;
  }
}
