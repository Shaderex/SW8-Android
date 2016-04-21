package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;

public class AccelerometerSensorProvider extends SensorProvider<FloatTripleMeasurement> {

  public AccelerometerSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {
    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(final SensorEvent event) {
        onNewMeasurement(new FloatTripleMeasurement(event.values[0], event.values[1], event.values[2]));
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };

    return new SensorEventListenerRegistrationManager(sensorManager, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), listener);
  }

  @Override
  public boolean isSensorAvailable() {
    return contextWeakReference.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
  }
}
