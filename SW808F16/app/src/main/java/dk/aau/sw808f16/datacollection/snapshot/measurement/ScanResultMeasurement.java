package dk.aau.sw808f16.datacollection.snapshot.measurement;

import io.realm.RealmObject;

public class ScanResultMeasurement extends RealmObject {
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
}

