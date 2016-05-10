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
import dk.aau.sw808f16.datacollection.campaign.QuestionnairePlacement;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;
import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import io.realm.Realm;

public class SnapshotTimer {

  private static final long INITIAL_TIMER_DELAY = 0;
  private final List<SensorProvider> sensorProviders;
  private static boolean isRunning = false;
  private Timer timer;
  private final Context context;

  public SnapshotTimer(final Context context, final List<SensorProvider> sensorProviders) {
    this.sensorProviders = sensorProviders;
    this.context = context;
  }

  public synchronized void start() {

    if (!isRunning) {
      final Realm realm = Realm.getDefaultInstance();
      final Campaign campaign = realm.where(Campaign.class).findFirst();

      Log.d("SnapshotTimer", "SnapshotTimer started for campaign ID: " + campaign.getIdentifier());
      final long snapshotLength = campaign.getSnapshotLength();

      realm.close();
      timer = new Timer();
      timer.scheduleAtFixedRate(new SnapshotTimerTask(), INITIAL_TIMER_DELAY, snapshotLength);
      isRunning = true;
    }

  }

  public synchronized void stop() {
    if (isRunning) {

      Log.d("SnapshotTimer", "SnapshotTimer stopped");
      timer.cancel();
      isRunning = false;
    }
  }

  private class SnapshotTimerTask extends TimerTask {

    @Override
    public void run() {

      Realm realm = null;

      try {

        realm = Realm.getDefaultInstance();

        Campaign campaign = realm.where(Campaign.class).findFirst();

        final long totalDuration = campaign.getSnapshotLength();
        final long sampleDuration = campaign.getSampleDuration();
        final long sampleFrequency = campaign.getSampleFrequency();
        final long measurementFrequency = campaign.getMeasurementFrequency();
        final long campaignIdentifier = campaign.getIdentifier();
        final Questionnaire questionnaire = new Questionnaire(campaign.getQuestionnaire());

        Snapshot snapshot = Snapshot.Create();
        final long snapshotTimestamp = snapshot.getTimestamp();

        try {
          realm.beginTransaction();
          realm.copyToRealmOrUpdate(campaign);
          realm.commitTransaction();
        } catch (Exception exception) {
          realm.cancelTransaction();
          throw exception;
        }

        if (campaign.getQuestionnairePlacement() == QuestionnairePlacement.START) {
          startQuestionnaire(questionnaire, snapshotTimestamp, totalDuration);
        }

        final List<Pair<SensorType, Future<List<Sample>>>> sensorFutures = new ArrayList<>();

        for (SensorProvider sensorProvider : sensorProviders) {

          final SensorType sensorType = sensorProvider.getSensorType();
          // Check if the sensor is required for this campaign
          if (!campaign.getSensors().contains(sensorType) || !sensorProvider.isSensorAvailable()) {
            continue;
          }
          // Start gathering the samples
          final Future<List<Sample>> samples = sensorProvider.retrieveSamplesForDuration(totalDuration, sampleFrequency, sampleDuration, measurementFrequency);
          sensorFutures.add(new Pair<>(sensorType, samples));
        }

        // Join in the gather sample threads
        final List<Pair<SensorType, List<Sample>>> sensorSamplesForSnapshot = new ArrayList<>();

        for (final Pair<SensorType, Future<List<Sample>>> sensorTypeFuturePair : sensorFutures) {
          try {
            final List<Sample> samples = sensorTypeFuturePair.second.get();
            sensorSamplesForSnapshot.add(new Pair<>(sensorTypeFuturePair.first, samples));
          } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
          }
        }

        try {
          realm.beginTransaction();

          for (final Pair<SensorType, List<Sample>> sensorSamples : sensorSamplesForSnapshot) {
            snapshot.addSamples(sensorSamples.first, sensorSamples.second);
          }

          Log.d("SnapshotTimer", "Added snapshot to campaign with ID: " + campaignIdentifier);

          if (campaign.getQuestionnairePlacement() == QuestionnairePlacement.END) {
            startQuestionnaire(questionnaire, snapshotTimestamp, totalDuration);
          }

          campaign = realm.where(Campaign.class).findFirst();
          campaign.addSnapshot(snapshot);
          realm.copyToRealm(campaign);
          realm.commitTransaction();
        } catch (Exception exception) {
          realm.cancelTransaction();
          throw exception;
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        if (realm != null) {
          realm.close();
        }
      }

    }
  }

  private void startQuestionnaire(final Questionnaire questionnaire, final long snapshotTimestamp, final long snapshotDuration) {

    // If there are no questions do not prompt the user
    if (questionnaire.getQuestions().size() == 0) {
      return;
    }

    final Intent intent = new Intent(context, QuestionnaireActivity.class);
    intent.putExtra(QuestionnaireActivity.QUESTIONNAIRE_PARCEL_IDENTIFIER_KEY, questionnaire);
    intent.putExtra(QuestionnaireActivity.SNAPSHOT_TIMESTAMP_KEY, snapshotTimestamp);
    intent.putExtra(QuestionnaireActivity.QUESTIONNAIRE_TTL_KEY, snapshotDuration);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(context.getResources().getString(R.string.app_name))
        .setContentText("We have some questions")
        .setVibrate(new long[] {10, 100, 200, 40, 55, 200})
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)
        .setColor(context.getResources().getColor(R.color.light_blue_dark))
        .setLights(context.getResources().getColor(R.color.light_blue_dark), 500, 1000);

    final NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(QuestionnaireActivity.QUESTIONNAIRE_NOTIFICATION_ID, notificationBuilder.build());
  }

}
