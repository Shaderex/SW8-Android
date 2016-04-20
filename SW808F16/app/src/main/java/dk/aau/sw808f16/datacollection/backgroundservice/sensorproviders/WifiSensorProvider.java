package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.measurement.WifiMeasurement;

public class WifiSensorProvider extends SensorProvider<WifiMeasurement> {

  private final Timer wifiMeasurementTimer;

  public WifiSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    wifiMeasurementTimer = new Timer(true);
  }

  @Override
  protected List<Pair<Sensor, SensorEventListener>> createSensorAndEventListenerPairs() {
    SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        final WifiManager wifiManager = (WifiManager) context.get().getSystemService(Context.WIFI_SERVICE);
        onNewMeasurement(new WifiMeasurement(wifiManager.getScanResults()));
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };

    // TODO: FIX THIS WHEN IT WORKS
    return Arrays.asList(new Pair<>((Sensor) null, listener));
  }

  @Override
  public boolean isSensorAvailable() {
    return ((WifiManager) context.get().getSystemService(Context.WIFI_SERVICE)).getWifiState() == WifiManager.WIFI_STATE_ENABLED;
  }
}
