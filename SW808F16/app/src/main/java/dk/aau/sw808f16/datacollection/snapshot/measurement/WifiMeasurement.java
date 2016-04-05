package dk.aau.sw808f16.datacollection.snapshot.measurement;

import android.net.wifi.ScanResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dk.aau.sw808f16.datacollection.snapshot.JsonValueAble;
import io.realm.RealmList;
import io.realm.RealmObject;

public class WifiMeasurement extends RealmObject implements JsonValueAble {

  private RealmList<ScanResultMeasurement> scanResultMeasurements = new RealmList<>();

  public WifiMeasurement() {
  }

  public WifiMeasurement(List<ScanResult> androidScanResults) {
    for (ScanResult scanResult : androidScanResults) {
      scanResultMeasurements.add(new ScanResultMeasurement(scanResult));
    }
  }

  public void setScanResultMeasurements(List<ScanResult> androidScanResults) {
    RealmList<ScanResultMeasurement> newScanResultMeasurements = new RealmList<>();

    for (ScanResult scanResult : androidScanResults) {
      newScanResultMeasurements.add(new ScanResultMeasurement(scanResult));
    }

    scanResultMeasurements = newScanResultMeasurements;

  }

  public List<ScanResultMeasurement> getScanResultMeasurements() {
    return scanResultMeasurements;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || !WifiMeasurement.class.isAssignableFrom(object.getClass())) {
      return false;
    }

    WifiMeasurement that = ((WifiMeasurement) object);

    if (this.getScanResultMeasurements().size() != that.getScanResultMeasurements().size()) {
      return false;
    }


    if (!this.getScanResultMeasurements().equals(that.getScanResultMeasurements())) {
      return false;
    }

    return true;
  }

  @Override
  public String toJSONValue() throws JSONException {

    final JSONObject jsonObject = new JSONObject();
    final JSONArray scanResultMeasurementsJSONArray = new JSONArray();

    for (final ScanResultMeasurement scanResultMeasurement : scanResultMeasurements) {
      scanResultMeasurementsJSONArray.put(scanResultMeasurement.toJSONObject());
    }

    jsonObject.put("scanResultMeasurements", scanResultMeasurementsJSONArray);

    return jsonObject.toString();
  }
}
