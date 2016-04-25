package dk.aau.sw808f16.datacollection;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.HeartRateConsentListener;

import dk.aau.sw808f16.datacollection.fragment.StartFragment;

public class MainActivity extends Activity implements HeartRateConsentListener {

  private static final String START_FRAGMENT_KEY = "START_FRAGMENT_KEY";

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.activity_main);

    final FragmentManager fragmentManager = getFragmentManager();

    fragmentManager.beginTransaction().replace(R.id.content_frame_layout, StartFragment.newInstance(), START_FRAGMENT_KEY).commit();

    Thread getConsent = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          getConnectedBandClient();

          if (bandClient != null && bandClient.getSensorManager().getCurrentHeartRateConsent() != UserConsent.GRANTED) {
            // user has not consented, request consent
            // the calling class is an Activity and implements
            // HeartRateConsentListener
            bandClient.getSensorManager().requestHeartRateConsent(MainActivity.this, MainActivity.this);
          }

          bandClient = null;
        } catch (InterruptedException | BandException exception) {
          exception.printStackTrace();
        }
      }
    });
    getConsent.start();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    return true;
  }

  @Override
  public void userAccepted(boolean accepted) {
    // handle user's heart rate consent decision
  }

  private BandClient bandClient;

  protected boolean getConnectedBandClient() throws InterruptedException, BandException {
    if (bandClient == null) {
      BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
      if (devices.length == 0) {
        Log.d("Band2", "Band isn't paired with your phone (from " + this.getClass().getName() + ").");
        return false;
      }
      bandClient = BandClientManager.getInstance().create(this, devices[0]);
    } else if (ConnectionState.CONNECTED == bandClient.getConnectionState()) {
      return true;
    }

    Log.d("Band2", "Band is connecting...  (from " + this.getClass().getName() + ")");
    final ConnectionState result = bandClient.connect().await();

    Log.d("Band2", "Band connection status: " + result + " (from " + this.getClass().getName() + ")");

    return ConnectionState.CONNECTED == result;
  }
}
