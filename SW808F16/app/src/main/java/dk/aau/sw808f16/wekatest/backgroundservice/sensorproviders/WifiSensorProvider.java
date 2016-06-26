package dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.wekatest.SensorType;
import dk.aau.sw808f16.wekatest.snapshot.measurement.WifiMeasurement;

public class WifiSensorProvider extends SensorProvider<WifiMeasurement> {

  private HandlerThread handlerThread;

  public WifiSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {

    return new EventListenerRegistrationManager() {

      final WifiManager wifiManager = (WifiManager) contextWeakReference.get().getSystemService(Context.WIFI_SERVICE);
      final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {

          onNewMeasurement(new WifiMeasurement(wifiManager.getScanResults()));
          wifiManager.startScan(); //request a scan for access points
        }
      };

      @Override
      public void register(final int frequency) {
        handlerThread = new HandlerThread("WifiSensorProvider HandlerThread");
        handlerThread.start();
        final Context context = contextWeakReference.get();

        if (context != null) {
          context.registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, new Handler(handlerThread.getLooper()));
          wifiManager.startScan();
        }
      }

      @Override
      public void unregister() {
        final Context context = contextWeakReference.get();

        if (context != null) {
          context.unregisterReceiver(broadcastReceiver);
        }

        handlerThread.quitSafely();
      }
    };
  }

  @Override
  protected WifiMeasurement getDefaultMeasurement() {
    return new WifiMeasurement();
  }

  @Override
  public boolean isSensorAvailable() {
    return ((WifiManager) contextWeakReference.get().getSystemService(Context.WIFI_SERVICE)).getWifiState() == WifiManager.WIFI_STATE_ENABLED;
  }

  @Override
  public SensorType getSensorType() {
    return SensorType.WIFI;
  }
}
