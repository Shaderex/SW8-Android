package dk.aau.sw808f16.wekatest;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.HeartRateConsentListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders.AccelerometerSensorProvider;
import dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders.CompassSensorProvider;
import dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders.GyroscopeSensorProvider;
import dk.aau.sw808f16.wekatest.snapshot.JsonValueAble;
import dk.aau.sw808f16.wekatest.snapshot.Sample;
import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatMeasurement;
import dk.aau.sw808f16.wekatest.snapshot.measurement.FloatTripleMeasurement;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class MainActivity extends ActionBarActivity implements HeartRateConsentListener {




  private static final String START_FRAGMENT_KEY = "START_FRAGMENT_KEY";

  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle drawerToggle;
  private boolean isBoundToResponder = false;
  private Messenger serviceMessenger = null;

  final String debug = "WEKA";

  private boolean hallonuerviherlige = false;

  private HashMap<String, List<Sample>> samplesMap = new HashMap<>();

  private Classifier classifier = (Classifier) new NaiveBayes();
  Instances isTrainingSet;
  FastVector fvWekaAttributes;

  private class LongOperation extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      hallonuerviherlige = true;

      try {
        Resources res = getResources();
        InputStream inputStream = res.openRawResource(R.raw.training_data);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
          stringBuilder.append(line);
        }

        JSONArray snapshotsArray = new JSONArray(stringBuilder.toString());

        for (int i = 0; i < snapshotsArray.length(); i++) {
          JSONObject snapshotObject = snapshotsArray.getJSONObject(i).getJSONObject("data");

          Sample sample = Sample.Create();

          int sampleCounter = 0;
          JSONArray accelerometerSamples = snapshotObject.getJSONArray("accelerometerSamples");
          JSONObject accelerometerData = accelerometerSamples.getJSONObject(0);
          JSONArray accelerometerMeasurements = accelerometerData.getJSONArray("measurements");
          for (int j = 0; j < accelerometerMeasurements.length(); j++) {
            if (sampleCounter == 30) {
              break;
            }
            JSONArray measurement = accelerometerMeasurements.getJSONArray(j);
            float v1 = (float) measurement.getDouble(0);
            float v2 = (float) measurement.getDouble(1);
            float v3 = (float) measurement.getDouble(2);
            sample.addMeasurement(new FloatTripleMeasurement(v1, v2, v3));
            sampleCounter++;
          }


          sampleCounter = 0;
          JSONArray gyroscopeSamples = snapshotObject.getJSONArray("gyroscopeSamples");
          JSONObject gyroscopeData = gyroscopeSamples.getJSONObject(0);
          JSONArray gyroscopeMeasurements = gyroscopeData.getJSONArray("measurements");
          for (int j = 0; j < gyroscopeMeasurements.length(); j++) {
            if (sampleCounter == 30) {
              break;
            }
            JSONArray measurement = gyroscopeMeasurements.getJSONArray(j);
            float v1 = (float) measurement.getDouble(0);
            float v2 = (float) measurement.getDouble(1);
            float v3 = (float) measurement.getDouble(2);
            sample.addMeasurement(new FloatTripleMeasurement(v1, v2, v3));
            sampleCounter++;
          }

          sampleCounter = 0;
          JSONArray compassSamples = snapshotObject.getJSONArray("compassSamples");
          JSONObject compassData = compassSamples.getJSONObject(0);
          JSONArray compassMeasurements = compassData.getJSONArray("measurements");
          for (int j = 0; j < compassMeasurements.length(); j++) {
            if (sampleCounter == 30) {
              break;
            }
            double measurement = compassMeasurements.getDouble(j);
            float v1 = (float) measurement / 10;
            float v2 = (float) measurement / 10;
            float v3 = (float) measurement / 10;
            sample.addMeasurement(new FloatTripleMeasurement(v1, v2, v3));
            sampleCounter++;
          }

          JSONObject questionnaire = snapshotObject.getJSONObject("questionnaire");
          JSONArray questions = questionnaire.getJSONArray("questions");

          String answer = questions.getJSONObject(0).getString("answer");

          samplesMap.get(answer).add(sample);

        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      Log.d(debug, "Training Weka...");

      final Runnable runnable = new Runnable() {
        @Override
        public void run() {
          // Make attributes
          fvWekaAttributes = new FastVector(90 * 3 + 1); // TODO: Tal her
          for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 3; j++) {
              fvWekaAttributes.addElement(new Attribute("acc" + i + "-" + j));
            }
          }

          for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 3; j++) {
              fvWekaAttributes.addElement(new Attribute("gyr" + i + "-" + j));
            }
          }
          for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 3; j++) {
              fvWekaAttributes.addElement(new Attribute("com" + i + "-" + j));
            }
          }


          // Make class
          final FastVector fvClassVal = new FastVector(samplesMap.keySet().size());
          for (final String key : samplesMap.keySet()) {
            fvClassVal.addElement(key);
          }
          Attribute classAttribute = new Attribute("class", fvClassVal);
          fvWekaAttributes.addElement(classAttribute);

          // Specify how to train your dragon
          isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);
          isTrainingSet.setClassIndex(fvWekaAttributes.size() - 1); // The class is the last

          Log.d(debug, "Training scheme generated");

          int i = 0;
          for (final String clazz : samplesMap.keySet()) {
            for (final Sample sample : samplesMap.get(clazz)) {
              i = 0;

              final Instance iExample = new Instance(fvWekaAttributes.size());

              for (final JsonValueAble measurement : sample.getMeasurements()) {
                if (measurement instanceof FloatTripleMeasurement) {
                  final FloatTripleMeasurement floatTripleMeasurement = (FloatTripleMeasurement) measurement;
                  iExample.setValue((Attribute) fvWekaAttributes.elementAt(i), floatTripleMeasurement.getFirstValue());
                  iExample.setValue((Attribute) fvWekaAttributes.elementAt(i + 1), floatTripleMeasurement.getSecondValue());
                  iExample.setValue((Attribute) fvWekaAttributes.elementAt(i + 2), floatTripleMeasurement.getThirdValue());

                  i += 3;
                }
              }

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

      return "!";
    }

    @Override
    protected void onPostExecute(String result) {
      hallonuerviherlige = false;
      Log.d(debug, "Results Fetched!");
      Toast.makeText(MainActivity.this, "Results Fetched!", Toast.LENGTH_LONG).show();
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

    final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);

    samplesMap.put("true", new ArrayList<Sample>());
    samplesMap.put("false", new ArrayList<Sample>());

    LongOperation longOperation = new LongOperation();
    longOperation.execute("jump");

    final int[] counter = {0};

    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        counter[0]++;

        assert progress != null;
        progress.setProgress(counter[0]);

        if (counter[0] == 100) {
          counter[0] = 0;
        } else {
          return;
        }

        Log.d(debug, "Startede tråd");

        Runnable trainModelAndUseIt = new Runnable() {
          @Override
          public void run() {
            final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(3);
            final SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

            AccelerometerSensorProvider accelerometerSensorProvider = new AccelerometerSensorProvider(MainActivity.this, sensorThreadPool, sensorManager);
            GyroscopeSensorProvider gyroscopeSensorProvider = new GyroscopeSensorProvider(MainActivity.this, sensorThreadPool, sensorManager);
            CompassSensorProvider compassSensorProvider = new CompassSensorProvider(MainActivity.this, sensorThreadPool, sensorManager);

            int[] tid = {250, 250, 250, 5};

            Future<List<Sample>> data1 = accelerometerSensorProvider.retrieveSamplesForDuration(tid[0], tid[1], tid[2], tid[3]);
            Future<List<Sample>> data2 = gyroscopeSensorProvider.retrieveSamplesForDuration(tid[0], tid[1], tid[2], tid[3]);
            Future<List<Sample>> data3 = compassSensorProvider.retrieveSamplesForDuration(tid[0], tid[1], tid[2], tid[3]);

            List<FloatTripleMeasurement> compassMeasurements = new ArrayList<>();

            try {
              final Sample sample = Sample.Create();

              for (JsonValueAble compassMeasurement : data3.get().get(0).getMeasurements().subList(0, 29)) {
                compassMeasurements.add(new FloatTripleMeasurement(
                    ((FloatMeasurement) compassMeasurement).getValue() / 10,
                    ((FloatMeasurement) compassMeasurement).getValue() / 10,
                    ((FloatMeasurement) compassMeasurement).getValue() / 10
                ));
              }

              sample.addMeasurements(data1.get().get(0).getMeasurements().subList(0, 29));
              sample.addMeasurements(data2.get().get(0).getMeasurements().subList(0, 29));
              sample.addMeasurements(compassMeasurements);

              Instance test = new Instance(90 * 3); // TODO: Tal her
              test.setDataset(isTrainingSet);

              int i = 0;

              for (final JsonValueAble measurement : sample.getMeasurements()) {
                if (measurement instanceof FloatTripleMeasurement) {
                  final FloatTripleMeasurement floatTripleMeasurement = (FloatTripleMeasurement) measurement;
                  test.setValue((Attribute) fvWekaAttributes.elementAt(i), floatTripleMeasurement.getFirstValue());
                  test.setValue((Attribute) fvWekaAttributes.elementAt(i + 1), floatTripleMeasurement.getSecondValue());
                  test.setValue((Attribute) fvWekaAttributes.elementAt(i + 2), floatTripleMeasurement.getThirdValue());

                  i += 3;
                }
              }

              double[] distribution = classifier.distributionForInstance(test);
              Log.d(debug, Arrays.toString(distribution));

              // Toast.makeText(MainActivity.this, Arrays.toString(distribution), Toast.LENGTH_LONG).show();

              if (distribution[0] > distribution[1]) {
                MainActivity.this.updateText("On the table");
              } else {
                MainActivity.this.updateText("In the pocket");
              }
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }
        };
        new Thread(trainModelAndUseIt).start();
      }
    }, 0, 10);

  }

  private void updateText(final String s) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final TextView whereResult = (TextView) findViewById(R.id.where_result);
        whereResult.setText(s);
      }
    });

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
