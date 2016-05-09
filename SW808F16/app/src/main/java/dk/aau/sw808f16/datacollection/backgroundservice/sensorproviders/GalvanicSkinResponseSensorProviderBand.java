package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;

import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.GsrSampleRate;

import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.snapshot.measurement.IntegerMeasurement;

public class GalvanicSkinResponseSensorProviderBand extends SensorProviderBand<IntegerMeasurement> {
  public GalvanicSkinResponseSensorProviderBand(final Context context, final ExecutorService sensorThreadPool, final SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  public SensorType getSensorType() {
    return SensorType.GALVANIC_SKIN;
  }

  @Override
  protected EventListenerRegistrationManager createRegManager() {
    final BandGsrEventListener listener = new BandGsrEventListener() {

      @Override
      public void onBandGsrChanged(BandGsrEvent event) {
        onNewMeasurement(new IntegerMeasurement(event.getResistance()));
      }
    };

    try {
      getConnectedBandClient();
    } catch (InterruptedException | BandException exception) {
      exception.printStackTrace();
    }

    return new EventListenerRegistrationManager() {
      @Override
      public void register(final int frequency) {
        try {
          bandClient.getSensorManager().registerGsrEventListener(listener, GsrSampleRate.MS200);
        } catch (InvalidBandVersionException | BandIOException exception) {
          exception.printStackTrace();
        }
      }

      @Override
      public void unregister() {
        try {
          bandClient.getSensorManager().unregisterGsrEventListener(listener);
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

