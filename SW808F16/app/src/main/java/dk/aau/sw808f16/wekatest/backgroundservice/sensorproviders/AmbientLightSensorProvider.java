package dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.wekatest.SensorType;
import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatMeasurement;

public class AmbientLightSensorProvider extends SensorProvider<FloatMeasurement> {

  public AmbientLightSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {

    final SensorEventListener listener = new SensorEventListener() {

      @Override
      public void onSensorChanged(final SensorEvent event) {
        onNewMeasurement(new FloatMeasurement(event.values[0]));
      }

      @Override
      public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
      }
    };

    return new SensorEventListenerRegistrationManager(sensorManager, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), listener);
  }

  @Override
  protected FloatMeasurement getDefaultMeasurement() {
    return new FloatMeasurement();
  }

  @Override
  public boolean isSensorAvailable() {
    return contextWeakReference.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
  }

  @Override
  public SensorType getSensorType() {
    return SensorType.AMBIENT_LIGHT;
  }
}
