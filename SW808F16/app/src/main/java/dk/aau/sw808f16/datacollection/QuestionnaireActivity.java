package dk.aau.sw808f16.datacollection;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import dk.aau.sw808f16.datacollection.backgroundservice.BackgroundSensorService;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;

public class QuestionnaireActivity extends Activity {

  public static final String QUESTIONNAIRE_PARCEL_IDENTIFIER_KEY = "QUESTIONNAIRE_PARCEL_IDENTIFIER_KEY";
  public static final String SNAPSHOT_TIMESTAMP_KEY = "SNAPSHOT_TIMESTAMP_KEY";
  public static final String QUESTIONNAIRE_TTL_KEY = "QUESTIONNAIRE_TTL_KEY";
  public static final int QUESTIONNAIRE_NOTIFICATION_ID = 42;

  // Questionnaire
  private Questionnaire questionnaire;
  private Question currentQuestion = null;
  private long snapshotTimestamp;
  private boolean isBoundToResponder = false;
  private long timeToLive;
  private Messenger serviceMessenger = null;

  private Timer expirationTimer;

  // Views
  private TextView questionText;

  @Override
  protected final void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_questionnaire);

    final Intent spawnerIntent = getIntent();
    final Bundle extras = spawnerIntent.getExtras();
    expirationTimer = new Timer();

    questionnaire = extras.getParcelable(QUESTIONNAIRE_PARCEL_IDENTIFIER_KEY);
    snapshotTimestamp = extras.getLong(SNAPSHOT_TIMESTAMP_KEY);
    timeToLive = extras.getLong(QUESTIONNAIRE_TTL_KEY);

    if (questionnaire == null) {
      throw new IllegalArgumentException("Illegal intent sent to activity. Questionnaire was null");
    }

    currentQuestion = questionnaire.getNextQuestion();

    questionText = (TextView) findViewById(R.id.questionnaire_question_text);
    questionText.setText(currentQuestion.getQuestion());

    final Button yesAnswerButton = (Button) findViewById(R.id.questionnaire_answer_button_yes);
    final Button noAnswerButton = (Button) findViewById(R.id.questionnaire_answer_button_no);

    yesAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        currentQuestion.setAnswer(true);
        goToNextQuestion();
      }
    });

    noAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        currentQuestion.setAnswer(false);
        goToNextQuestion();
      }
    });

    bindToResponder();


    expirationTimer.schedule(new TimerTask() {
      @Override
      public void run() {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {

            Toast.makeText(getApplicationContext(), "Questionnaire Expired", Toast.LENGTH_LONG).show();
            finish();

          }
        });

      }
    }, timeToLive);

  }

  private void bindToResponder() {
    final Intent serviceIntent = new Intent(this, BackgroundSensorService.class);
    bindService(serviceIntent, mConnection, Context.BIND_NOT_FOREGROUND);
  }

  private ServiceConnection mConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(final ComponentName className, final IBinder binder) {

      // We've bound to LocalService, cast the IBinder and get LocalService instance
      serviceMessenger = new Messenger(binder);
      isBoundToResponder = true;
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
      isBoundToResponder = false;
      serviceMessenger = null;
    }
  };

  private void goToNextQuestion() {
    try {
      currentQuestion = questionnaire.getNextQuestion();
      questionText.setText(currentQuestion.getQuestion());
    } catch (IndexOutOfBoundsException exception) {

      // There are no more questions
      onQuestionnaireCompleted();
    }
  }

  private void onQuestionnaireCompleted() {
    final Intent resultIntent = new Intent();
    resultIntent.putExtra(QUESTIONNAIRE_PARCEL_IDENTIFIER_KEY, questionnaire);

    setResult(Activity.RESULT_OK, resultIntent);

    if (isBoundToResponder) {
      notifyQuestionnaireCompleted(snapshotTimestamp, questionnaire);
    }

    final NotificationManager notificationManager =
        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.cancel(QUESTIONNAIRE_NOTIFICATION_ID);

    finishActivity(Activity.RESULT_OK);

    finish();
  }

  private void notifyQuestionnaireCompleted(final long snapshotTimestamp, final Questionnaire questionnaire) {

    final Message msg = Message.obtain(null, BackgroundSensorService.NOTIFY_QUESTIONNAIRE_COMPLETED, 0, 0);

    final Bundle data = new Bundle();

    data.putLong(BackgroundSensorService.NOTIFY_QUESTIONNAIRE_COMPLETED_TIMESTAMP, snapshotTimestamp);
    data.putParcelable(BackgroundSensorService.NOTIFY_QUESTIONNAIRE_COMPLETED_QUESTIONNAIRE, questionnaire);

    try {
      serviceMessenger.send(msg);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  @Override
  public final void onBackPressed() {

    final Intent resultIntent = new Intent();
    resultIntent.putExtra(QUESTIONNAIRE_PARCEL_IDENTIFIER_KEY, questionnaire);

    setResult(Activity.RESULT_CANCELED, resultIntent);

    super.onBackPressed();
    finishActivity(Activity.RESULT_CANCELED);
    finish();
  }

  public Questionnaire getQuestionnaire() {
    return questionnaire;
  }

  @Override
  protected void onStop() {
    super.onStop();
    // Unbind from the service
    if (isBoundToResponder) {
      unbindService(mConnection);
      isBoundToResponder = false;
    }

    expirationTimer.cancel();
  }
}
