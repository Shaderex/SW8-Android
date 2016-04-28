package dk.aau.sw808f16.datacollection.backgroundservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.QuestionnaireActivity;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.SensorProvider;
import dk.aau.sw808f16.datacollection.campaign.Campaign;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;
import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import io.realm.Realm;
import io.realm.Sort;

public class SnapshotTimer {

  private static final long INITIAL_TIMER_DELAY = 0;
  private final List<SensorProvider> sensorProviders;
  private static boolean isRunning = false;
  private final Timer timer = new Timer();
  private final Context context;

  public SnapshotTimer(final Context context, final List<SensorProvider> sensorProviders) {
    this.sensorProviders = sensorProviders;
    this.context = context;
  }

  public void start() {

    if (!isRunning) {
      Realm realm = Realm.getDefaultInstance();
      Campaign campaign = realm.where(Campaign.class).findFirst();

      Log.d("SnapshotTimer", "SnapshotTimer started for campaign ID: " + campaign.getIdentifier());
      long snapshotLength = campaign.getSnapshotLength();

      realm.close();

      timer.scheduleAtFixedRate(new SnapshotTimerTask(), INITIAL_TIMER_DELAY, snapshotLength);
      isRunning = true;
    }

  }

  public void stop() {
    if (isRunning) {

      Log.d("SnapshotTimer", "SnapshotTimer stopped");
      timer.cancel();
      isRunning = false;
    }
  }

  private class SnapshotTimerTask extends TimerTask {

    @Override
    public void run() {

      Realm realm = Realm.getDefaultInstance();
      Campaign campaign = realm.where(Campaign.class).findFirst();

      final long totalDuration = campaign.getSnapshotLength();
      final long sampleDuration = campaign.getSampleDuration();
      final long sampleFrequency = campaign.getSampleFrequency();
      final long measurementFrequency = campaign.getMeasurementFrequency();
      final long campaignIdentifier = campaign.getIdentifier();
      Questionnaire questionnaire = new Questionnaire(campaign.getQuestionnaire());

      Snapshot snapshot = Snapshot.Create();

      realm.beginTransaction();
      realm.copyToRealm(snapshot);
      realm.commitTransaction();

      startQuestionnaire(questionnaire);

      final List<Pair<SensorType, Future<List<Sample>>>> sensorFutures = new ArrayList<>();

      for (SensorProvider sensorProvider : sensorProviders) {

        final SensorType sensorType = sensorProvider.getSensorType();
        // Check if the sensor is required for this campaign
        if (!campaign.getSensors().contains(sensorType) || !sensorProvider.isSensorAvailable()) {
          continue;
        }
        // Start gathering the samples
        Future<List<Sample>> samples = sensorProvider.retrieveSamplesForDuration(totalDuration, sampleFrequency, sampleDuration, measurementFrequency);
        sensorFutures.add(new Pair<>(sensorType, samples));
      }


      // Join in the gather sample threads
      List<Pair<SensorType, List<Sample>>> sensorSamplesForSnapshot = new ArrayList<>();
      for (final Pair<SensorType, Future<List<Sample>>> sensorTypeFuturePair : sensorFutures) {
        try {
          List<Sample> samples = sensorTypeFuturePair.second.get();
          sensorSamplesForSnapshot.add(new Pair<>(sensorTypeFuturePair.first, samples));
        } catch (InterruptedException | ExecutionException exception) {
          exception.printStackTrace();
        }
      }

      realm = Realm.getDefaultInstance();

      realm.beginTransaction();
      snapshot = realm.where(Snapshot.class).findAllSorted("timestamp", Sort.DESCENDING).first();

      for (final Pair<SensorType, List<Sample>> sensorSamples : sensorSamplesForSnapshot) {
        snapshot.addSamples(sensorSamples.first, sensorSamples.second);
      }

      Log.d("SnapshotTimer", "Added snapshot to campaign with ID: " + campaignIdentifier);

      campaign = realm.where(Campaign.class).findFirst();
      campaign.addSnapshot(snapshot);
      realm.copyToRealm(campaign);
      realm.commitTransaction();
      realm.close();
    }
  }

  private void startQuestionnaire(final Questionnaire questionnaire) {
    final Intent intent = new Intent(context, QuestionnaireActivity.class);
    intent.putExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER, questionnaire);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
        PendingIntent.FLAG_ONE_SHOT);

    final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Questionnaire")
        .setVibrate(new long[] {10, 100, 200, 40, 55, 200})
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent);

    final NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }

}
