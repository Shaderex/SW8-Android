package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;

public class GyroscopeSensorProvider extends SensorProvider<FloatTripleMeasurement> {

  public GyroscopeSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected EventListenerRegistrationManager createSensorAndEventListenerPairs() {
    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        onNewMeasurement(new FloatTripleMeasurement(event.values));
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };

    return new SensorEventListenerRegistrationManager(sensorManager, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), listener);
  }

  @Override
  public boolean isSensorAvailable() {
    return context.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
  }
}
