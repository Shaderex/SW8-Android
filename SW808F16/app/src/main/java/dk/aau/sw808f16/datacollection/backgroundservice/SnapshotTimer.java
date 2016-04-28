package dk.aau.sw808f16.datacollection.backgroundservice;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.SensorProvider;
import dk.aau.sw808f16.datacollection.campaign.Campaign;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;
import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import io.realm.Realm;

public class SnapshotTimer {

  private static final long INITIAL_TIMER_DELAY = 0;
  private final List<SensorProvider> sensorProviders;
  private static boolean isRunning = false;
  private final Timer timer = new Timer();

  public SnapshotTimer(final List<SensorProvider> sensorProviders) {
    this.sensorProviders = sensorProviders;
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

      long totalDuration = campaign.getSnapshotLength();
      long sampleDuration = campaign.getSampleDuration();
      long sampleFrequency = campaign.getSampleFrequency();
      long measurementFrequency = campaign.getMeasurementFrequency();
      long campaignIdentifier = campaign.getIdentifier();
      Questionnaire questionnaire = new Questionnaire(campaign.getQuestionnaire());

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

      realm.close();

      final Snapshot snapshot = Snapshot.Create();

      // TODO Open the Questionnaire (using with the questions stored above) and get the users answer (if before)

      // Join in the gather sample threads
      for (Pair<SensorType, Future<List<Sample>>> sensorTypeFuturePair : sensorFutures) {
        try {
          List<Sample> sager = sensorTypeFuturePair.second.get();
          snapshot.addSamples(sensorTypeFuturePair.first, sager);
        } catch (InterruptedException | ExecutionException exception) {
          exception.printStackTrace();
        }
      }

      // TODO Open the Questionnaire (using with the questions stored above) and get the users answer (if after)

      for (Question question : questionnaire.getQuestions()) {
        questionnaire.getNextQuestion().setAnswer(true);
      }

      realm = Realm.getDefaultInstance();

      snapshot.setQuestionnaire(questionnaire);

      Log.d("SnapshotTimer", "Added snapshot to campaign with ID: " + campaignIdentifier);
      realm.beginTransaction();
      campaign = realm.where(Campaign.class).findFirst();
      campaign.addSnapshot(snapshot);
      realm.copyToRealm(campaign);
      realm.commitTransaction();
      realm.close();
    }
  }

}
