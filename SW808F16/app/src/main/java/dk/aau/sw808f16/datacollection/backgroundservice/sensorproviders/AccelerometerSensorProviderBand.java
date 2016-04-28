package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;

import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;

public class AccelerometerSensorProviderBand extends SensorProviderBand<FloatTripleMeasurement> {
  public AccelerometerSensorProviderBand(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  public SensorType getSensorType() {
    return SensorType.WRIST_ACCELEROMETER;
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {
    final BandAccelerometerEventListener listener = new BandAccelerometerEventListener() {
      @Override
      public void onBandAccelerometerChanged(BandAccelerometerEvent event) {
        if (event != null) {
          onNewMeasurement(new FloatTripleMeasurement(event.getAccelerationX(), event.getAccelerationY(), event.getAccelerationZ()));
        }
      }
    };

    try {
      getConnectedBandClient();
    } catch (InterruptedException | BandException exception) {
      exception.printStackTrace();
    }

    return new EventListenerRegistrationManager() {
      @Override
      public void register(int frequency) {
        try {
          // SampleRate describes the accuracy of the measurement (from Band2)
          bandClient.getSensorManager().registerAccelerometerEventListener(listener, SampleRate.MS128);
        } catch (BandIOException exception) {
          exception.printStackTrace();
        }
      }

      @Override
      public void unregister() {
        try {
          bandClient.getSensorManager().unregisterAccelerometerEventListener(listener);
        } catch (BandIOException exception) {
          exception.printStackTrace();
        }
      }
    };
  }
}
