package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.test.ApplicationTestCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;

public class WifiSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {

  private static final long sampleDuration = 0; // In milliseconds
  private static final int measurementFrequency = 0; // In microseconds

  // GPS will always return exactly one GPS
  private static final int expectedSize = 1;

  public WifiSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  public void testGpsSensorProviderData() throws Exception {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final WifiSensorProvider wifiSensorProvider = new WifiSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final List<List<ScanResult>> data1 = wifiSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    final List<List<ScanResult>> data2 = wifiSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor data is null", data1);
    assertFalse("Sensor data is empty", data1.isEmpty());

    assertNotNull("Sensor data (second measure) is null", data2);
    assertFalse("Sensor data (second measure) is empty", data2.isEmpty());

    assertTrue("The amount of data and sampling period do not match, they are not exactly one", data1.size() == expectedSize);
    assertTrue("The amount of data and sampling period do not match, they are not exactly one", data2.size() == expectedSize);
  }
}
