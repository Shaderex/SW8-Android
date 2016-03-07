package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.telephony.CellInfo;
import android.test.ApplicationTestCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class CellularNetworkSensorProviderTest extends ApplicationTestCase<DataCollectionApplication> {
  private static final long sampleDuration = 10000; // In milliseconds
  private static final int measurementFrequency = 2000000; // In microseconds

  // Because of real time issues the size may differ +/- 1
  private int minSize;
  private int maxSize;

  @Override
  protected void setUp() throws Exception {
    final int microPerMilli = this.getContext().getResources().getInteger(R.integer.micro_seconds_per_milli_second);
    final int expectedSize = (int) (microPerMilli * sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;
  }

  public CellularNetworkSensorProviderTest() {
    super(DataCollectionApplication.class);
  }

  public void testCellularSensorProvider() throws Exception {
    final ExecutorService sensorThreadPool = Executors.newFixedThreadPool(1);
    final SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    final CellularNetworkSensorProvider cellularNetworkSensorProvider =
        new CellularNetworkSensorProvider(getContext(), sensorThreadPool, sensorManager);

    final Sample sample1 = cellularNetworkSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);
    final Sample sample2 = cellularNetworkSensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    assertNotNull("Sensor data is null", sample1);
    assertFalse("Sensor data is empty", sample1.getMeasurements().isEmpty());

    assertNotNull("Sensor data (second measure) is null", sample2);
    assertFalse("Sensor data (second measure) is empty", sample2.getMeasurements().isEmpty());

    for (final Object measurement : sample1.getMeasurements()) {

      assertTrue("sample1 is not of type List", measurement instanceof List);

      List<?> aids = (List<?>) measurement;

      for (Object o : aids) {
        assertTrue("Item in sample1 is not of type CellInfo", o instanceof CellInfo);
      }
    }

    for (final Object measurement : sample2.getMeasurements()) {

      assertTrue("sample2 is not of type List", measurement instanceof List);

      List<?> aids = (List<?>) measurement;

      for (Object o : aids) {
        assertTrue("Item in sample2 is not of type CellInfo", o instanceof CellInfo);
      }
    }

    assertTrue("The amount of data and sampling period do not match, not enough data", sample1.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data", sample1.getMeasurements().size() <= maxSize);
    assertTrue("The amount of data and sampling period do not match, not enough data (second measure)",
        sample2.getMeasurements().size() >= minSize);
    assertTrue("The amount of data and sampling period do not match, too much data (second measure)",
        sample2.getMeasurements().size() <= maxSize);
  }
}
