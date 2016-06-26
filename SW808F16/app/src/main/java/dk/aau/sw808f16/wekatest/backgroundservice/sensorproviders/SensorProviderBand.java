package dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;

import java.util.concurrent.ExecutorService;

public abstract class SensorProviderBand<MeasurementT> extends SensorProvider<MeasurementT> {

  protected BandClient bandClient = null;

  public SensorProviderBand(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
  }

  @Override
  public boolean isSensorAvailable() {
    try {
      return bandClient != null || getConnectedBandClient();
    } catch (InterruptedException | BandException exception) {
      return false;
    }
  }

  protected boolean getConnectedBandClient() throws InterruptedException, BandException {
    if (bandClient == null) {
      final BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
      if (devices.length == 0) {
        Log.d("Band2", "Band isn't paired with your phone (from " + this.getClass().getName() + ").");
        return false;
      }
      final Context context = contextWeakReference.get();
      bandClient = BandClientManager.getInstance().create(context, devices[0]);
    } else if (ConnectionState.CONNECTED == bandClient.getConnectionState()) {
      return true;
    }

    Log.d("Band2", "Band is connecting...  (from " + this.getClass().getName() + ")");
    final ConnectionState result = bandClient.connect().await();

    Log.d("Band2", "Band connection status: " + result + " (from " + this.getClass().getName() + ")");

    return ConnectionState.CONNECTED == result;
  }
}
