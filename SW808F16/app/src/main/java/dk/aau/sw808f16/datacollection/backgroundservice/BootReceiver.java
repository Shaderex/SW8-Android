package dk.aau.sw808f16.datacollection.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(final Context context, final Intent intent) {

    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      context.startService(new Intent(context, BackgroundSensorService.class));
    }
  }
}
