package dk.aau.sw808f16.datacollection.snapshot;

import android.hardware.Sensor;
import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import io.realm.RealmList;

public class SamplesMapTest extends ApplicationTestCase<DataCollectionApplication> {

  public SamplesMapTest() {
    super(DataCollectionApplication.class);
  }

  public void testSensorTypesDifferentValues() {
    for (SamplesMap.SensorType sensorTypeOuter : SamplesMap.SensorType.values()) {
      for (SamplesMap.SensorType sensorTypeInner : SamplesMap.SensorType.values()) {
        if (sensorTypeOuter.equals(sensorTypeInner)) {
          assertEquals(sensorTypeInner.getIdentifier(), sensorTypeOuter.getIdentifier());
        } else {
          assertNotSame(sensorTypeInner.getIdentifier(), sensorTypeOuter);
        }
      }
    }
  }

  public void testConstructor() {
    new SamplesMap();
  }

  public void testAddSamples() {
    RealmList<Sample> samples = new RealmList<Sample>();
    for (int i = 0; i < 10; i++) {
      Sample sample = new Sample();
      samples.add(sample);
    }

    SamplesMap samplesMap = new SamplesMap();

    samplesMap.put(Sensor.TYPE_ACCELEROMETER, samples);
  }
}
