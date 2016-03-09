package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.hardware.Sensor;

import junit.framework.Assert;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.snapshot.FloatTriple;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class AccelerometerSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new AccelerometerSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    if (!(measurement instanceof FloatTriple)) {
      Assert.assertEquals("[" + sampleIdentifier + "] Measurement in sample is of wrong type.", FloatTriple.class, measurement.getClass());
    }

    @SuppressWarnings("ConstantConditions")
    FloatTriple accelerometerValues = (FloatTriple) measurement;

    final Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    final float maxValue = sensor.getMaximumRange();
    final float minValue = -sensor.getMaximumRange();

    // Check the value boundaries of the sample
    assertTrue("[" + sampleIdentifier + "] first value of measurement must be between " + minValue + " and " + maxValue,
        accelerometerValues.getFirstValue() <= maxValue && accelerometerValues.getFirstValue() >= minValue);
    assertTrue("[" + sampleIdentifier + "] second value of measurement must be between " + minValue + " and " + maxValue,
        accelerometerValues.getSecondValue() <= maxValue && accelerometerValues.getSecondValue() >= minValue);
    assertTrue("[" + sampleIdentifier + "] third value of measurement must be between " + minValue + " and " + maxValue,
        accelerometerValues.getThirdValue() <= maxValue && accelerometerValues.getThirdValue() >= minValue);
  }

  public void testGetSample() throws ExecutionException, InterruptedException, ClassCastException {
    final Sample sample1 = sensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);
    final Sample sample2 = sensorProvider.retrieveSampleForDuration(sampleDuration, measurementFrequency);

    validateSample(sample1, "sample1");
    validateSample(sample2, "sample2");
  }

  public void testGetSamples() throws InterruptedException, ExecutionException {
    final Future<List<Sample>> futureSamples = sensorProvider.retrieveSamplesForDuration(totalDuration,
                                                                                         sampleFrequency,
                                                                                         sampleDuration,
                                                                                         measurementFrequency);

    // Run through the samples from the future list of samples
    List<Sample> samples = futureSamples.get();
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      validateSample(sample, "sample" + i);
    }
  }
}
