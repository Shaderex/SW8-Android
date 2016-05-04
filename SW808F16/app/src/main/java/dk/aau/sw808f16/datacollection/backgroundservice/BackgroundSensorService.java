package dk.aau.sw808f16.datacollection.backgroundservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AccelerometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AmbientLightSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.BarometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.CompassSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.GyroscopeSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.ProximitySensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.SensorProvider;
import dk.aau.sw808f16.datacollection.campaign.Campaign;
import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class BackgroundSensorService extends IntentService implements QuestionnaireResponder, ConfigurationResponder {

  public static final String BINDER_REQUEST_SENDER_CLASS_KEY = "BINDER_REQUEST_SENDER_CLASS_KEY";
  private static final String REALM_NAME = DataCollectionApplication.TAG + ".realm";
  private static final long SYNCHRONIZATION_INTERVAL = 10000;
  private byte[] encryptionKey = null;

  private ServiceHandler serviceHandler;

  private final ExecutorService sensorThreadPool;

  private AccelerometerSensorProvider accelerometerSensorProvider;
  private AmbientLightSensorProvider ambientLightSensorProvider;
  private BarometerSensorProvider barometerSensorProvider;
  private CompassSensorProvider compassSensorProvider;
  private GyroscopeSensorProvider gyroscopeSensorProvider;
  private ProximitySensorProvider proximitySensorProvider;
  // TODO The LocationSensorProvider and WifiSensorProvider is broken (thread already started exception is thrown)
  // private WifiSensorProvider wifiSensorProvider;
  // private LocationSensorProvider locationSensorProvider;

  private SnapshotTimer snapshotTimer;
  private SynchronizationTimer synchronizationTimer;

  public BackgroundSensorService() {
    super("BackgroundSensorService");
    // The number of threads in the pool should correspond to the number of SensorProvider instances
    // this service maintains
    // Dynamically (Reflection) counts the number of SensorProvider instances this service maintains
    final int numberOfSensorProviders = getNumberOfSensorProviders();
    // Create a thread pool to be shared by all sensor providers
    sensorThreadPool = Executors.newFixedThreadPool(numberOfSensorProviders);
  }

  public class LocalBinder extends Binder {

    public ConfigurationResponder getConfigurationResponder() {
      return BackgroundSensorService.this;
    }

    public QuestionnaireResponder getQuestionnaireResponder() {
      return BackgroundSensorService.this;
    }
  }

  @Override
  public void notifyNewCampaign() {
    snapshotTimer.stop();
    snapshotTimer.start();
  }

  @Override
  public void notifyQuestionnaireCompleted(final long snapshotTimeStamp, Questionnaire questionnaire) {

    Realm realm = Realm.getDefaultInstance();

    final Snapshot snapshot = realm.where(Snapshot.class).equalTo("timestamp", snapshotTimeStamp).findFirst();

    if (snapshot != null) {
      realm.beginTransaction();
      questionnaire = realm.copyToRealm(questionnaire);
      realm.commitTransaction();

      realm.beginTransaction();
      snapshot.setQuestionnaire(questionnaire);
      realm.copyToRealmOrUpdate(snapshot);
      realm.commitTransaction();
    }

    realm.close();
  }

  private final class ServiceHandler extends Handler {

    public ServiceHandler(final Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(final Message msg) {

    }
  }

  @Override
  protected void onHandleIntent(final Intent intent) {

  }

  @Override
  public IBinder onBind(final Intent intent) {
    return new LocalBinder();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    final SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

    // Initialize SensorProvider instances with the shared thread pool
    accelerometerSensorProvider = new AccelerometerSensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);
    ambientLightSensorProvider = new AmbientLightSensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);
    barometerSensorProvider = new BarometerSensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);
    compassSensorProvider = new CompassSensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);
    gyroscopeSensorProvider = new GyroscopeSensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);
    proximitySensorProvider = new ProximitySensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);
    // TODO The LocationSensorProvider and WifiSensorProvider is broken (thread already started exception is thrown)
    // wifiSensorProvider = new WifiSensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);
    // locationSensorProvider = new LocationSensorProvider(BackgroundSensorService.this, sensorThreadPool, sensorManager);

    snapshotTimer = new SnapshotTimer(BackgroundSensorService.this, getSensorProviders());
    synchronizationTimer = new SynchronizationTimer(BackgroundSensorService.this, SYNCHRONIZATION_INTERVAL);

    // Start up the thread running the service.  Note that we create a
    // separate thread because the service normally runs in the process's
    // main thread, which we don't want to block.  We also make it
    // background priority so CPU-intensive work will not disrupt our UI.
    final HandlerThread thread = new HandlerThread("ServiceStartArguments",
        android.os.Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();

    // Get the HandlerThread's Looper and use it for our Handler
    final Looper serviceLooper = thread.getLooper();
    serviceHandler = new ServiceHandler(serviceLooper);

    Log.d("BackgroundSensorService", "BackgroundSensorService onCreate() called");

    serviceHandler.post(new Runnable() {
      @Override
      public void run() {

        final String campaignListResourcePath = RequestHostResolver.resolveHostForRequest(BackgroundSensorService.this, "/key");
        final WeakReference<Context> weakContextReference = new WeakReference<Context>(BackgroundSensorService.this.getBaseContext());

        final CountDownLatch gotKey = new CountDownLatch(1);

        final AsyncHttpWebbTask<String> keyTask = new AsyncHttpWebbTask<String>(AsyncHttpWebbTask.Method.GET,
            campaignListResourcePath,
            HttpURLConnection.HTTP_OK) {
          @Override
          protected Response<String> sendRequest(Request request) {
            final Context context = weakContextReference.get();

            if (context != null) {
              try {
                final InstanceID instanceId = InstanceID.getInstance(context);
                final String token = instanceId.getToken(
                    context.getString(R.string.defaultSenderID),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
                );

                return request.param("device_id", token).asString();

              } catch (IOException exception) {
                exception.printStackTrace();
                return null;
              }
            }
            return null;
          }

          @Override
          public void onResponseCodeMatching(Response<String> response) {
            String encryptStr = response.getBody();
            int len = encryptStr.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
              data[i / 2] = (byte) ((Character.digit(encryptStr.charAt(i), 16) << 4)
                  + Character.digit(encryptStr.charAt(i + 1), 16));
            }
            encryptionKey = data;
            gotKey.countDown();
          }

          @Override
          public void onResponseCodeNotMatching(Response<String> response) {

          }

          @Override
          public void onConnectionFailure() {

          }
        };

        keyTask.execute();
        try {
          gotKey.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(BackgroundSensorService.this)
            .name(REALM_NAME)
            .encryptionKey(encryptionKey)
            .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        // Check if device is subscribed to a campaign and then continue that campaign
        Realm realm = Realm.getDefaultInstance();
        final Campaign campaign = realm.where(Campaign.class).findFirst();

        if (campaign != null) {
          serviceHandler.post(new Runnable() {
            @Override
            public void run() {
              snapshotTimer.start();
            }
          });
        }

        realm.close();

        // Start the sync of snapshots
        serviceHandler.post(new Runnable() {
          @Override
          public void run() {
            synchronizationTimer.start();
          }
        });
      }
    });
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    Log.d("BackgroundSensorService", "BackgroundSensorService onStartCommand() called");
    return START_STICKY;
  }


  @Override
  public void onDestroy() {
    Log.d("BackgroundSensorService", "BackgroundSensorService onDestroy() called");
    snapshotTimer.stop();
    synchronizationTimer.stop();
  }

  private static int getNumberOfSensorProviders() {

    int sensorProviderCount = 0;

    for (final Field field : BackgroundSensorService.class.getDeclaredFields()) {

      final Class type = field.getType();

      if (SensorProvider.class.isAssignableFrom(type)) {
        sensorProviderCount++;
      }
    }

    return sensorProviderCount;
  }

  private List<SensorProvider> getSensorProviders() {

    final List<SensorProvider> sensorProvides = new ArrayList<>();

    for (final Field field : BackgroundSensorService.class.getDeclaredFields()) {

      final Class type = field.getType();

      if (SensorProvider.class.isAssignableFrom(type)) {
        try {
          sensorProvides.add((SensorProvider) field.get(this));
        } catch (IllegalAccessException exception) {
          exception.printStackTrace();
        }
      }
    }

    return sensorProvides;
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
          + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }
}
