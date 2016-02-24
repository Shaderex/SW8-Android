package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;

public class AccelerometerSensorTest extends ApplicationTestCase<DataCollectionApplication> {

  public AccelerometerSensorTest() {
    super(DataCollectionApplication.class);
  }

  public void testRetrieveAccelerometerDataForPeriodContainsValues() throws ExecutionException, InterruptedException {

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final AccelerometerSensorProvider compassSensorProvider = new AccelerometerSensorProvider(sensorThreadPool, sensorManager);

    final Future<List<float[]>> futureData = compassSensorProvider.retrieveSampleForDuration(getContext(), duration, samplingPeriod);

    final List<float[]> data = futureData.get();

    assertTrue(data != null && !data.isEmpty());
  }

  public void testRetrieveAccelerometerDataForPeriodValuesInRange() throws ExecutionException, InterruptedException {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final float maxValue = accelerometerSensor.getMaximumRange();
    final float minValue = -accelerometerSensor.getMaximumRange();

    final AccelerometerSensorProvider compassSensorProvider = new AccelerometerSensorProvider(sensorThreadPool, sensorManager);

    final Future<List<float[]>> futureData = compassSensorProvider.retrieveSampleForDuration(getContext(), duration, samplingPeriod);

    final List<float[]> data = futureData.get();

    for (final float[] accelerometerValues : data) {
      assertTrue("Data for index 0 value must be within " + maxValue + " and " + minValue, accelerometerValues[0] <= maxValue && accelerometerValues[0] >= minValue);
      assertTrue("Data for index 1 value must be within " + maxValue + " and " + minValue, accelerometerValues[1] <= maxValue && accelerometerValues[1] >= minValue);
      assertTrue("Data for index 2 value must be within " + maxValue + " and " + minValue, accelerometerValues[2] <= maxValue && accelerometerValues[2] >= minValue);
    }
  }

  public void testRetrieveAccelerometerDataForPeriodReuseSensorProvider() throws ExecutionException, InterruptedException {

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    final AccelerometerSensorProvider compassSensorProvider = new AccelerometerSensorProvider(sensorThreadPool, sensorManager);

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final float maxValue = accelerometerSensor.getMaximumRange();
    final float minValue = -accelerometerSensor.getMaximumRange();

    final Future<List<float[]>> futureData1 = compassSensorProvider.retrieveSampleForDuration(getContext(), duration, samplingPeriod);

    final List<float[]> data1 = futureData1.get();

    final Future<List<float[]>> futureData2 = compassSensorProvider.retrieveSampleForDuration(getContext(), duration, samplingPeriod);

    final List<float[]> data2 = futureData2.get();

    assertTrue(data1 != null && !data1.isEmpty());
    assertTrue(data2 != null && !data2.isEmpty());

    for (final float[] accelerometerValues : data1) {
      assertTrue("Data 1 for index 0 value must be within " + maxValue + " and " + minValue, accelerometerValues[0] <= maxValue && accelerometerValues[0] >= minValue);
      assertTrue("Data 1 for index 1 value must be within " + maxValue + " and " + minValue, accelerometerValues[1] <= maxValue && accelerometerValues[1] >= minValue);
      assertTrue("Data 1 for index 2 value must be within " + maxValue + " and " + minValue, accelerometerValues[2] <= maxValue && accelerometerValues[2] >= minValue);
    }

    for (final float[] accelerometerValues : data2) {
      assertTrue("Data 2 for index 0 value must be within " + maxValue + " and " + minValue, accelerometerValues[0] <= maxValue && accelerometerValues[0] >= minValue);
      assertTrue("Data 2 for index 1 value must be within " + maxValue + " and " + minValue, accelerometerValues[1] <= maxValue && accelerometerValues[1] >= minValue);
      assertTrue("Data 2 for index 2 value must be within " + maxValue + " and " + minValue, accelerometerValues[2] <= maxValue && accelerometerValues[2] >= minValue);
    }
  }

  public void testRetrieveAccelerometerDataForPeriodSamplingPeriod() throws ExecutionException, InterruptedException {

    final long duration = 10000; // In milliseconds
    final int samplingPeriod = 2000000; // In microseconds

    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final AccelerometerSensorProvider accelerometerSensorProvider = new AccelerometerSensorProvider(sensorThreadPool, sensorManager);

    final Future<List<float[]>> futureData = accelerometerSensorProvider.retrieveSampleForDuration(getContext(), duration, samplingPeriod);

    final List<float[]> data = futureData.get();

    assertTrue(data.size() >= 4 && data.size() <= 6);
  }

}
