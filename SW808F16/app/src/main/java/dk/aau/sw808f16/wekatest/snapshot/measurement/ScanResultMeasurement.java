package dk.aau.sw808f16.wekatest.snapshot.measurement;

import org.json.JSONException;
import org.json.JSONObject;

import dk.aau.sw808f16.wekatest.snapshot.JsonObjectAble;
import io.realm.RealmObject;

public class ScanResultMeasurement extends RealmObject implements JsonObjectAble {

  private String networkName;
  private int signalStrength;

  public ScanResultMeasurement() {

  }

  public ScanResultMeasurement(android.net.wifi.ScanResult androidScanResult) {
    setNetworkName(androidScanResult.SSID);
    setSignalStrength(androidScanResult.level);
  }

  public String getNetworkName() {
    return networkName;
  }

  public void setNetworkName(final String networkName) {
    this.networkName = networkName;
  }

  public int getSignalStrength() {
    return signalStrength;
  }

  public void setSignalStrength(final int signalStrength) {
    this.signalStrength = signalStrength;
  }

  @Override
  public boolean equals(Object object) {
    return super.equals(object) || (object instanceof ScanResultMeasurement
        && ((ScanResultMeasurement) object).networkName.equals(this.networkName)
        && ((ScanResultMeasurement) object).signalStrength == this.signalStrength);
  }


  @Override
  public JSONObject toJsonObject() throws JSONException {

    final JSONObject jsonObject = new JSONObject();

    jsonObject.put("networkName", networkName);
    jsonObject.put("signalStrength", signalStrength);

    return jsonObject;
  }
}

