package dk.aau.sw808f16.datacollection.backgroundservice;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AccelerometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AmbientLightSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.BarometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.CompassSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.GyroscopeSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.LocationSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.ProximitySensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.SensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.WifiSensorProvider;
import dk.aau.sw808f16.datacollection.campaign.Campaign;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class BackgroundSensorService extends Service {
  public static final String SNAPSHOT_REALM_NAME = "snapshot.realm";
  private static final String REALM_NAME = DataCollectionApplication.TAG + ".realm";
  private static final long SYNCHRONIZATION_INTERVAL = 5000;

  private ServiceHandler serviceHandler;

  private final ExecutorService sensorThreadPool;

  private AccelerometerSensorProvider accelerometerSensorProvider;
  private AmbientLightSensorProvider ambientLightSensorProvider;
  private BarometerSensorProvider barometerSensorProvider;
  private CompassSensorProvider compassSensorProvider;
  private GyroscopeSensorProvider gyroscopeSensorProvider;
  private ProximitySensorProvider proximitySensorProvider;
  private WifiSensorProvider wifiSensorProvider;
  private LocationSensorProvider locationSensorProvider;


  public BackgroundSensorService() {
    // The number of threads in the pool should correspond to the number of SensorProvider instances
    // this service maintains
    // Dynamically (Reflection) counts the number of SensorProvider instances this service maintains
    final int numberOfSensorProviders = getNumberOfSensorProviders();
    // Create a thread pool to be shared by all sensor providers
    sensorThreadPool = Executors.newFixedThreadPool(numberOfSensorProviders);
  }

  private final class ServiceHandler extends Handler {

    public ServiceHandler(final Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(final Message msg) {


    }
  }

  public class LocalBinder extends Binder {
    public BackgroundSensorService getService() {
      return BackgroundSensorService.this;
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(BackgroundSensorService.this)
        .name(REALM_NAME)
        .encryptionKey(getSecretKey())
        .build();

    Realm.setDefaultConfiguration(realmConfiguration);

    final SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

    // Initialize SensorProvider instances with the shared thread pool
    accelerometerSensorProvider = new AccelerometerSensorProvider(this, sensorThreadPool, sensorManager);
    ambientLightSensorProvider = new AmbientLightSensorProvider(this, sensorThreadPool, sensorManager);
    barometerSensorProvider = new BarometerSensorProvider(this, sensorThreadPool, sensorManager);
    compassSensorProvider = new CompassSensorProvider(this, sensorThreadPool, sensorManager);
    gyroscopeSensorProvider = new GyroscopeSensorProvider(this, sensorThreadPool, sensorManager);
    proximitySensorProvider = new ProximitySensorProvider(this, sensorThreadPool, sensorManager);
    wifiSensorProvider = new WifiSensorProvider(this, sensorThreadPool, sensorManager);
    locationSensorProvider = new LocationSensorProvider(this, sensorThreadPool, sensorManager);

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
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {

    Toast.makeText(this, "BackgroundSensorService was started", Toast.LENGTH_SHORT).show();

    // Check if device is subscribed to a campaign and then continue that campaign
    Realm realm = null;

    try {
      realm = Realm.getDefaultInstance();

      final Campaign campaign = realm.where(Campaign.class).findFirst();

      if (campaign != null) {
        activateCampaign(campaign);
      }
    } finally {
      if (realm != null) {
        realm.close();
      }
    }

    return START_STICKY;

  }

  private byte[] getSecretKey() {
    // TODO Use the correct encryption key provided by the server
    return new byte[] {-92, -42, -86, 62, 15, 2, -92, 79,
        31, 46, 76, 81, -25, -39, 50, 77,
        30, -2, -54, 48, 107, -115, 56, 125,
        -119, 90, 11, -108, -120, -103, -38, 126,
        -92, 120, 15, 100, -74, 41, -108, -70,
        -95, 83, -96, 64, -70, -98, -73, 89,
        -62, 51, -25, 37, 119, 53, -59, 4,
        0, -74, 47, 13, -124, 0, 117, 9};
  }

  @Override
  public IBinder onBind(final Intent intent) {
    return new LocalBinder();
  }

  @Override
  public void onDestroy() {
    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
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

  private final HashSet<Campaign> activeCampaigns = new HashSet<>();

  private void activateCampaign(final Campaign campaign) {
    serviceHandler.post(new Runnable() {
      @Override
      public void run() {
        List<SensorProvider> sensorProvides = new ArrayList<>();
        sensorProvides.add(accelerometerSensorProvider);
        sensorProvides.add(ambientLightSensorProvider);
        sensorProvides.add(barometerSensorProvider);
        sensorProvides.add(compassSensorProvider);
        sensorProvides.add(gyroscopeSensorProvider);
        sensorProvides.add(proximitySensorProvider);
        sensorProvides.add(wifiSensorProvider);
        sensorProvides.add(locationSensorProvider);
        SnapshotTimer snapshotTimer = new SnapshotTimer(sensorProvides);
        snapshotTimer.start();
      }
    });

    startSynchronization();
  }

  private void startSynchronization() {
    serviceHandler.post(new Runnable() {
      @Override
      public void run() {
        new SynchronizationTimer(BackgroundSensorService.this, SYNCHRONIZATION_INTERVAL).start();
      }
    });
  }

}
