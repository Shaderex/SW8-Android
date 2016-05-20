package dk.aau.sw808f16.datacollection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.HeartRateConsentListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.backgroundservice.BackgroundSensorService;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.AccelerometerSensorProvider;
import dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders.GyroscopeSensorProvider;
import dk.aau.sw808f16.datacollection.fragment.CampaignJoinFragment;
import dk.aau.sw808f16.datacollection.fragment.PrivateCampaignFragment;
import dk.aau.sw808f16.datacollection.fragment.PublicCampaignFragment;
import dk.aau.sw808f16.datacollection.snapshot.JsonValueAble;
import dk.aau.sw808f16.datacollection.snapshot.Sample;
import dk.aau.sw808f16.datacollection.snapshot.measurement.FloatTripleMeasurement;
import dk.aau.sw808f16.datacollection.webutil.CampaignRegistrator;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class MainActivity extends ActionBarActivity implements HeartRateConsentListener, CampaignRegistrator {

  public enum DrawerMenuItems {

    CURRENT_CAMPAIGN(R.drawable.ic_assignment_black_24dp, "Current campaign") {
      @Override
      public void open(final MainActivity activity) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        final long currentlyActiveCampaignId = preferences.getLong(activity.getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1);

        if (currentlyActiveCampaignId != -1) {

          final Fragment fragment = CampaignJoinFragment.newInstance(currentlyActiveCampaignId);
          activity.setContent(fragment);

        } else {
          Toast.makeText(activity, "You are not subscribed to any campaign", Toast.LENGTH_LONG).show();
        }
      }
    },
    PUBLIC_CAMPAIGNS(R.drawable.ic_public_black_24dp, "Browse campaigns") {
      @Override
      public void open(final MainActivity activity) {

        final Fragment fragment = PublicCampaignFragment.newInstance();
        activity.setContent(fragment);
      }
    },
    PRIVATE_CAMPAIGNS(R.drawable.ic_search_black_24dp, "Join specific") {
      @Override
      public void open(final MainActivity activity) {

        final Fragment fragment = PrivateCampaignFragment.newInstance();
        activity.setContent(fragment);
      }
    };

    public final String name;
    public final int iconRessource;

    DrawerMenuItems(final int iconRessource, final String string) {
      this.name = string;
      this.iconRessource = iconRessource;
    }

    public abstract void open(final MainActivity activity);

    @Override
    public String toString() {
      return name;
    }
  }

  private static final String START_FRAGMENT_KEY = "START_FRAGMENT_KEY";

  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle drawerToggle;
  private boolean isBoundToResponder = false;
  private Messenger serviceMessenger = null;

  final String debug = "WEKA";

  private boolean hallonuerviherlige = false;

  private AccelerometerSensorProvider accelerometerSensorProvider;
  private GyroscopeSensorProvider gyroscopeSensorProvider;

  private HashMap<String, List<Sample>> accSamples = new HashMap<>();

  private Classifier classifier = (Classifier) new NaiveBayes();
  Evaluation eTest = null;
  Instances isTrainingSet;
  FastVector fvWekaAttributes;

  private class LongOperation extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      hallonuerviherlige = true;

      final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(2);
      final SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
      accelerometerSensorProvider = new AccelerometerSensorProvider(MainActivity.this, sensorThreadPool, sensorManager);
      gyroscopeSensorProvider = new GyroscopeSensorProvider(MainActivity.this, sensorThreadPool, sensorManager);

      int[] tid = {3000, 3000, 3000, 100};

      Future<List<Sample>> data1 = accelerometerSensorProvider.retrieveSamplesForDuration(tid[0], tid[1], tid[2], tid[3]);
      Future<List<Sample>> data2 = gyroscopeSensorProvider.retrieveSamplesForDuration(tid[0], tid[1], tid[2], tid[3]);

      try {
        if (accSamples.get(params[0]) == null) {
          accSamples.put(params[0], new ArrayList<Sample>());
        }

        accSamples.get(params[0]).addAll(data1.get()); // Only 1 element in this list

      } catch (InterruptedException | ExecutionException exception) {
        exception.printStackTrace();
      }

      return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
      hallonuerviherlige = false;
      Log.d(debug, "Så må du noget igen! " + new Random().nextInt(100));
      Toast.makeText(MainActivity.this, "Så må du noget igen! " + new Random().nextInt(100), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
  }

  private class MoveBody extends AsyncTask<String, Void, Sample> {

    @Override
    protected Sample doInBackground(String... params) {
      final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(2);
      final SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
      accelerometerSensorProvider = new AccelerometerSensorProvider(MainActivity.this, sensorThreadPool, sensorManager);
      gyroscopeSensorProvider = new GyroscopeSensorProvider(MainActivity.this, sensorThreadPool, sensorManager);

      int[] tid = {3000, 3000, 3000, 100};

      Future<List<Sample>> data1 = accelerometerSensorProvider.retrieveSamplesForDuration(tid[0], tid[1], tid[2], tid[3]);
      Future<List<Sample>> data2 = gyroscopeSensorProvider.retrieveSamplesForDuration(tid[0], tid[1], tid[2], tid[3]);

      try {
        final Sample sample = data1.get().get(0);
        return sample;
      } catch (InterruptedException | ExecutionException exception) {
        exception.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(Sample result) {
      Log.d(debug, "Tak fordi du flyttede din krop " + new Random().nextInt(100));

      Instance test = new Instance(result.getMeasurements().size() * 3);
      test.setDataset(isTrainingSet);

      int i = 0;

      for (final JsonValueAble measurement : result.getMeasurements()) {
        final FloatTripleMeasurement floatTripleMeasurement = (FloatTripleMeasurement) measurement;

        Log.d(debug, "SÅ MANGE DATA2: " + i);
        test.setValue((Attribute) fvWekaAttributes.elementAt(i), floatTripleMeasurement.getFirstValue());
        test.setValue((Attribute) fvWekaAttributes.elementAt(i + 1), floatTripleMeasurement.getSecondValue());
        test.setValue((Attribute) fvWekaAttributes.elementAt(i + 2), floatTripleMeasurement.getThirdValue());

        i += 3;
      }


      try {
        double[] fDistribution = classifier.distributionForInstance(test);
        Log.d(debug, Arrays.toString(fDistribution));

        Toast.makeText(MainActivity.this, Arrays.toString(fDistribution), Toast.LENGTH_LONG).show();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    final Button jumpButton = (Button) findViewById(R.id.jumpbutton);
    final Button sitButton = (Button) findViewById(R.id.sitbutton);
    final Button dataKnap = (Button) findViewById(R.id.dataknap);
    final Button trainButton = (Button) findViewById(R.id.trainbutton);
    final Button guessButton = (Button) findViewById(R.id.guessbutton);

    assert jumpButton != null;
    jumpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (hallonuerviherlige) {
          Log.d(debug, "Det var for fast!");
          return;
        }

        LongOperation longOperation = new LongOperation();
        longOperation.execute("jump");
      }
    });

    assert sitButton != null;
    sitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (hallonuerviherlige) {
          Log.d(debug, "Det var for fast!");
          return;
        }

        LongOperation longOperation = new LongOperation();
        longOperation.execute("sit");
      }
    });

    assert dataKnap != null;
    dataKnap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Object data = accSamples;
      }
    });

    assert trainButton != null;
    trainButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(debug, "Training Weka...");

        final Runnable runnable = new Runnable() {
          @Override
          public void run() {

            // Make attributes
            fvWekaAttributes = new FastVector(91);
            for (int i = 0; i < 30; i++) {
              for (int j = 0; j < 3; j++) {
                fvWekaAttributes.addElement(new Attribute("acc" + i + "-" + j));
              }
            }

            // Make class
            final FastVector fvClassVal = new FastVector(accSamples.keySet().size());
            for (final String key : accSamples.keySet()) {
              fvClassVal.addElement(key);
            }
            Attribute classAttribute = new Attribute("class", fvClassVal);
            fvWekaAttributes.addElement(classAttribute);

            // Specify how to train your dragon
            isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);
            isTrainingSet.setClassIndex(fvWekaAttributes.size() - 1); // The class is the last

            Log.d(debug, "Training scheme generated");

            int i = 0;
            for (final String clazz : accSamples.keySet()) {
              for (final Sample sample : accSamples.get(clazz)) {
                i = 0;

                final Instance iExample = new Instance(fvWekaAttributes.size());

                for (final JsonValueAble measurement : sample.getMeasurements()) {
                  final FloatTripleMeasurement floatTripleMeasurement = (FloatTripleMeasurement) measurement;

                  iExample.setValue((Attribute) fvWekaAttributes.elementAt(i), floatTripleMeasurement.getFirstValue());
                  iExample.setValue((Attribute) fvWekaAttributes.elementAt(i + 1), floatTripleMeasurement.getSecondValue());
                  iExample.setValue((Attribute) fvWekaAttributes.elementAt(i + 2), floatTripleMeasurement.getThirdValue());

                  i += 3;
                }

                Log.d(debug, "SÅ MANGE DATA: " + i);

                iExample.setValue((Attribute) fvWekaAttributes.elementAt(i), clazz);
                isTrainingSet.add(iExample);
              }
            }

            try {
              classifier.buildClassifier(isTrainingSet);

              Log.d(debug, "Your training is complete young padawan");
            } catch (Exception exception) {
              Log.d(debug, "Your training FAILED completely young padawan");
              exception.printStackTrace();
            }
          }
        };

        new Thread(runnable).start();
      }
    });

    assert guessButton != null;
    guessButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(debug, "Weka is analysing.. Please move your body");

        MoveBody moveBody = new MoveBody();
        moveBody.execute("");
      }
    });

  }

  public class DrawerButtonsAdapter extends BaseAdapter {

    private final DrawerMenuItems[] items = DrawerMenuItems.values();

    @Override
    public int getCount() {
      return items.length;
    }

    @Override
    public Object getItem(final int position) {
      return items[position];
    }

    @Override
    public long getItemId(final int position) {
      return position;
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

    class ViewHolder {
      TextView menuNameTextView;
      ImageView menuIcon;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

      final DrawerMenuItems item = items[position];

      final ViewHolder holder;

      if (convertView == null) {

        convertView = getLayoutInflater().inflate(R.layout.drawer_menu_item, null);

        holder = new ViewHolder();
        holder.menuNameTextView = (TextView) convertView.findViewById(R.id.menu_item_text_view);
        holder.menuIcon = (ImageView) convertView.findViewById(R.id.drawer_item_icon);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      holder.menuNameTextView.setText(item.name);
      holder.menuIcon.setImageResource(item.iconRessource);

      return convertView;
    }
  }

  @Override
  protected void onPostCreate(final Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // Sync the toggle state after onRestoreInstanceState has occurred.
    // drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(final Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    // Pass the event to ActionBarDrawerToggle, if it returns
    // true, then it has handled the app icon touch event
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    super.onCreateOptionsMenu(menu);

    return true;
  }

  protected void setContent(final Fragment fragment) {

    final FragmentManager fm = getSupportFragmentManager(); // getFragmentManager();

    fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fm.popBackStackImmediate();

    fm.beginTransaction().replace(R.id.content_frame_layout, fragment).addToBackStack(null).commit();

    drawerLayout.closeDrawers();
  }

  @Override
  public void userAccepted(boolean accepted) {
    // handle user's heart rate consent decision
  }

  private void bindToResponder() {
    final Intent serviceIntent = new Intent(this, BackgroundSensorService.class);
    bindService(serviceIntent, serviceConnection, Context.BIND_ABOVE_CLIENT);
  }

  private ServiceConnection serviceConnection = new ServiceConnection() {

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

  public void registerCampaign(final long campaignId) {

    final Messenger replyMessenger = new Messenger(new Handler() {

      @Override
      public void handleMessage(final Message msg) {

        switch (msg.what) {

          case BackgroundSensorService.SERVICE_ACK_BUSY: {

            new Thread(new Runnable() {
              @Override
              public void run() {

                try {
                  Thread.sleep(2000);
                } catch (InterruptedException exception) {
                  exception.printStackTrace();
                }

                post(new Runnable() {
                  @Override
                  public void run() {
                    registerCampaign(campaignId);
                  }
                });
              }
            }).start();
            break;
          }
          case BackgroundSensorService.SERVICE_ACK_OK: {
            final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor.putLong(MainActivity.this.getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), campaignId);
            editor.commit();

            getFragmentManager().executePendingTransactions();

            final ListView listView = (ListView) findViewById(R.id.campaigns_list_view);
            if (listView != null && listView.getAdapter() != null) {
              ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            }

            break;
          }
        }

      }
    });

    final Message msg = Message.obtain(null, BackgroundSensorService.NOTIFY_NEW_CAMPAIGN, 0, 0);
    Bundle data = new Bundle();
    data.putLong(BackgroundSensorService.NOTIFY_QUESTIONNAIRE_COMPLETED_CAMPAIGN_ID, campaignId);
    msg.setData(data);
    msg.replyTo = replyMessenger;

    try {
      serviceMessenger.send(msg);
    } catch (RemoteException exception) {
      exception.printStackTrace();
    }

    return;
  }

  private BandClient bandClient;

  protected boolean getConnectedBandClient() throws InterruptedException, BandException {
    if (bandClient == null) {
      final BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
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

  @Override
  protected void onStop() {
    super.onStop();

    // Unbind from the service
    if (isBoundToResponder && serviceConnection != null) {
      unbindService(serviceConnection);
      isBoundToResponder = false;
    }
  }
}
