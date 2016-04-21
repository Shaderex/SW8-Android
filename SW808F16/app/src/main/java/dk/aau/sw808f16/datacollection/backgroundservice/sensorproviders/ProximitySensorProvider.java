package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Timer;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;

public class ProximitySensorProvider extends SensorProvider<FloatMeasurement> {

  private final Timer proximitySamplingTimer;

  public ProximitySensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    proximitySamplingTimer = new Timer(true);
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {
    SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        onNewMeasurement(new FloatMeasurement(event.values[0]));
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };

    return new SensorEventListenerRegistrationManager(sensorManager, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), listener);
  }

  @Override
  public boolean isSensorAvailable() {
    return contextWeakReference.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
  }
}
