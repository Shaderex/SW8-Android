package dk.aau.sw808f16.datacollection.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.R;

public class GoogleCloudService extends GcmListenerService {
  private static final String TAG = "GoogleCloudService";

  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   *             For Set of keys use data.keySet().
   */
  // [START receive_message]
  @Override
  public void onMessageReceived(final String from, final Bundle data) {
    final String message = data.getString("message");
    Log.d(TAG, "From: " + from);
    Log.d(TAG, "Message: " + message);

    if (from.startsWith("/topics/")) {
      // message received from some topic.
    }
    else {
      // normal downstream message.
    }

    // [START_EXCLUDE]
    /**
     * Production applications would usually process the message here.
     * Eg: - Syncing with server.
     *     - Store message in local database.
     *     - Update UI.
     */

    /**
     * In some cases it may be useful to show a notification indicating to the user
     * that a message was received.
     */
    sendNotification(message);
    // [END_EXCLUDE]
  }
  // [END receive_message]

  /**
   * Create and show a simple notification containing the received GCM message.
   *
   * @param message GCM message received.
   */
  private void sendNotification(final String message) {
    final Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        PendingIntent.FLAG_ONE_SHOT);

    final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("GCM Message")
        .setContentText(message)
        .setAutoCancel(true)
        .setVibrate(new long[] {10, 100, 200, 40, 55, 200})
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent);

    final NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }
}