package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;

import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatMeasurement;

public class SkinTemperatureSensorProviderBand extends SensorProviderBand<FloatMeasurement> {
  public SkinTemperatureSensorProviderBand(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {
    final BandSkinTemperatureEventListener listener = new BandSkinTemperatureEventListener() {

      @Override
      public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event) {
        onNewMeasurement(new FloatMeasurement(event.getTemperature()));
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
          bandClient.getSensorManager().registerSkinTemperatureEventListener(listener);
        } catch (BandIOException exception) {
          exception.printStackTrace();
        }
      }

      @Override
      public void unregister() {
        try {
          bandClient.getSensorManager().unregisterSkinTemperatureEventListener(listener);
        } catch (BandIOException exception) {
          exception.printStackTrace();
        }
      }
    };
  }
}

