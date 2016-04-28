package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;

import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.measurement.HeartRateMeasurement;

public class HeartbeatSensorProviderBand extends SensorProviderBand<HeartRateMeasurement> {
  public HeartbeatSensorProviderBand(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  public SensorType getSensorType() {
    return SensorType.HEARTBEAT;
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {
    final BandHeartRateEventListener listener = new BandHeartRateEventListener() {
      @Override
      public void onBandHeartRateChanged(BandHeartRateEvent event) {
        if (event != null) {
          onNewMeasurement(new HeartRateMeasurement(event.getHeartRate(), event.getQuality()));
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
          bandClient.getSensorManager().registerHeartRateEventListener(listener);
        } catch (BandException exception) {
          exception.printStackTrace();
        }
      }

      @Override
      public void unregister() {
        try {
          bandClient.getSensorManager().unregisterHeartRateEventListener(listener);
        } catch (BandIOException exception) {
          exception.printStackTrace();
        }
      }
    };
  }
}

