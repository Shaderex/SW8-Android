package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.List;

public class WifiSensorProviderTest extends SensorProviderApplicationTestCase {
  @Override
  protected SensorProvider getSensorProvider() {
    return new WifiSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    assertTrue("[" + sampleIdentifier + "] measurement is not a List", List.class.isAssignableFrom(measurement.getClass()));

    List<?> list = (List<?>) measurement;

    for (final Object object : list) {
      assertTrue("[" + sampleIdentifier + "] item in measurement is of wrong type", ScanResult.class.isAssignableFrom(object.getClass()));
    }
  }

  @Override
  protected boolean hasSensor() {
    return ((WifiManager) getContext().getSystemService(Context.WIFI_SERVICE)).getWifiState() == WifiManager.WIFI_STATE_ENABLED;
  }
}
