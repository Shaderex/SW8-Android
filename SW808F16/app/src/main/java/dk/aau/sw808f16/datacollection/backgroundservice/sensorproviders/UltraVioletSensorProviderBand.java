package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.notifications.VibrationType;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandUVEventListener;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.measurement.IntegerMeasurement;

public class UltraVioletSensorProviderBand extends SensorProviderBand<IntegerMeasurement> {
  public UltraVioletSensorProviderBand(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  public SensorType getSensorType() {
    return SensorType.UV;
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {
    final BandUVEventListener listener = new BandUVEventListener() {
      @Override
      public void onBandUVChanged(BandUVEvent event) {
        int measurement;

        switch (event.getUVIndexLevel()) {
          case NONE:
            measurement = 0;
            break;
          case LOW:
            measurement = 1;
            break;
          case MEDIUM:
            measurement = 2;
            break;
          case HIGH:
            measurement = 3;
            break;
          case VERY_HIGH:
            measurement = 4;
            break;
          default:
            measurement = -1;
            break;
        }

        try {
          getConnectedBandClient();
          bandClient.getNotificationManager().vibrate(VibrationType.RAMP_UP).await();
          bandClient.getNotificationManager().vibrate(VibrationType.RAMP_DOWN).await();
        } catch (InterruptedException | BandException exception) {
          // Do nothing
        }

        onNewMeasurement(new IntegerMeasurement(measurement));
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
          bandClient.getSensorManager().registerUVEventListener(listener);
        } catch (BandException exception) {
          exception.printStackTrace();
        }
      }

      @Override
      public void unregister() {
        try {
          bandClient.getSensorManager().unregisterUVEventListener(listener);
        } catch (BandIOException exception) {
          exception.printStackTrace();
        }
      }
    };
  }

  @Override
  protected IntegerMeasurement getDefaultMeasurement() {
    return new IntegerMeasurement();
  }
}

