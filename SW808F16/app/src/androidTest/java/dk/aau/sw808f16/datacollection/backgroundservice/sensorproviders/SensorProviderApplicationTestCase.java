package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public abstract class SensorProviderApplicationTestCase extends ApplicationTestCase<DataCollectionApplication> {
  public SensorProviderApplicationTestCase() {
    super(DataCollectionApplication.class);
  }

  // Times given in milliseconds
  protected static final long totalDuration = 10 * 1000;
  protected static final long sampleFrequency = 2 * 1000;
  protected static final long sampleDuration = 1000;
  protected static final long measurementFrequency = 200;

  protected int minSize;
  protected int maxSize;

  protected ExecutorService sensorThreadPool;
  protected SensorManager sensorManager;
  protected SensorProvider sensorProvider;

  protected abstract SensorProvider getSensorProvider();

  protected abstract void validateMeasurement(Object measurement, String sampleIdentifier);

  @Override
  protected void setUp() throws Exception {
    // Calculate the expected size(s), because of real time issues the size may differ +/- 1
    final int expectedSize = (int) (sampleDuration / measurementFrequency);
    minSize = expectedSize - 1;
    maxSize = expectedSize + 1;

    sensorThreadPool = Executors.newFixedThreadPool(1);
    sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    sensorProvider = getSensorProvider();
  }

  protected void validateSample(Sample sample, String sampleIdentifier) {
    assertNotNull("[" + sampleIdentifier + "] Sample data is null", sample);
    assertFalse("[" + sampleIdentifier + "] Sample data is empty", sample.getMeasurements().isEmpty());

    for (final Object measurement : sample.getMeasurements()) {
      // Check the type of the sample
      validateMeasurement(measurement, sampleIdentifier);
    }

    // Check the amount of measurements of the sample
    assertTrue("[" + sampleIdentifier + "] The amount of data and sampling period do not match, not enough data",
        sample.getMeasurements().size() >= minSize);
    assertTrue("[" + sampleIdentifier + "] The amount of data and sampling period do not match, too much data",
        sample.getMeasurements().size() <= maxSize);
  }
}
