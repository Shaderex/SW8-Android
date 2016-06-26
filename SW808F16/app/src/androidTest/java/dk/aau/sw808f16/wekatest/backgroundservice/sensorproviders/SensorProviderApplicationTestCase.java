package dk.aau.sw808f16.wekatest.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.aau.sw808f16.wekatest.DataCollectionApplication;
import dk.aau.sw808f16.wekatest.snapshot.Sample;

public abstract class SensorProviderApplicationTestCase extends ApplicationTestCase<DataCollectionApplication> {
  SensorProviderApplicationTestCase() {
    super(DataCollectionApplication.class);
  }

  // Times given in milliseconds
  private static final long totalDuration = 12 * 1000;
  private static final long sampleFrequency = 4 * 1000;
  private static final long sampleDuration = 2000;
  private static final long measurementFrequency = 500;

  private int minSize;
  private int maxSize;

  protected SensorProvider sensorProvider;

  ExecutorService sensorThreadPool;
  SensorManager sensorManager;

  protected abstract SensorProvider getSensorProvider();

  protected abstract void validateMeasurement(Object measurement, String sampleIdentifier);

  @Override
  protected void setUp() throws Exception {

    // Calculate the expected size(s), because of real time issues the size may differ +/- 1
    final int expectedSize = (int) (sampleDuration / measurementFrequency);
    minSize = expectedSize;
    maxSize = expectedSize;

    sensorThreadPool = Executors.newFixedThreadPool(1);
    sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    sensorProvider = getSensorProvider();
  }

  private void validateSample(final Sample sample, final String sampleIdentifier) {
    assertNotNull("[" + sampleIdentifier + "] Sample data is null", sample);
    assertFalse("[" + sampleIdentifier + "] Sample data is empty", sample.getMeasurements().isEmpty());

    for (final Object measurement : sample.getMeasurements()) {
      // Check the type of the sample
      validateMeasurement(measurement, sampleIdentifier);
    }

    // Check the amount of measurements of the sample
    assertTrue("[" + sampleIdentifier + "] The amount of data and sampling period do not match. "
            + "Expected: " + minSize + " - " + maxSize + ", Actual: " + sample.getMeasurements().size(),
        sample.getMeasurements().size() <= maxSize && sample.getMeasurements().size() >= minSize);
  }

  public void testGetSamples() throws ExecutionException, InterruptedException {
    if (sensorProvider.isSensorAvailable()) {
      final Future<List<Sample>> futureSamples = sensorProvider.retrieveSamplesForDuration(totalDuration,
          sampleFrequency,
          sampleDuration,
          measurementFrequency);

      // Run through the samples from the future list of samples
      List<Sample> samples = futureSamples.get();
      for (int i = 0; i < samples.size(); i++) {
        Sample sample = samples.get(i);

        for (final Object measurement : sample.getMeasurements()) {
          if (measurement != null) {
            validateSample(sample, "sample" + i);
            break;
          }
        }
      }
    }
  }
}
