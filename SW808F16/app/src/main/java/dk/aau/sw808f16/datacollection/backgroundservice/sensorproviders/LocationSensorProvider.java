package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;
import dk.aau.sw808f16.datacollection.snapshot.measurement.LocationMeasurement;

public class LocationSensorProvider extends SensorProvider<LocationMeasurement> {

  private final Timer locationMeasureTimer;

  public LocationSensorProvider(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    locationMeasureTimer = new Timer(true);
  }

  @Override
  protected List<Pair<Sensor, SensorEventListener>> createSensorAndEventListenerPairs() {
    final SensorEventListener listener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        final LocationManager locationManager = (LocationManager) context.get().getSystemService(Context.LOCATION_SERVICE);
        onNewMeasurement(new LocationMeasurement(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)));
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };

    return Arrays.asList(new Pair<>( , listener));
  }


  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final long measurementFrequency)
      throws InterruptedException {

    final LocationManager locationManager = (LocationManager) context.get().getSystemService(Context.LOCATION_SERVICE);
    final long endTime = System.currentTimeMillis() + sampleDuration;
    final CountDownLatch latch = new CountDownLatch(1);
    final List<LocationMeasurement> locations = new ArrayList<>();

    final TimerTask cellNetworkMeasurementTask = new TimerTask() {
      @Override
      public void run() {
        if (System.currentTimeMillis() > endTime) {
          this.cancel();
          latch.countDown();
          return;
        }

        // Do the measurements
        locations.add;
      }
    };

    locationMeasureTimer.scheduleAtFixedRate(cellNetworkMeasurementTask, 0, measurementFrequency);

    latch.await();

    return new Sample(locations);


  }

  @Override
  public boolean isSensorAvailable() {
    LocationManager lm = (LocationManager) context.get().getSystemService(Context.LOCATION_SERVICE);
    boolean gpsEnabled = false;

    try {
      gpsEnabled = lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (gpsEnabled) {
      return lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) != null;
    }

    return gpsEnabled;
  }
}
