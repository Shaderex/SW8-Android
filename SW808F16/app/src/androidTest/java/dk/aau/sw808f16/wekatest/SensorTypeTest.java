package dk.aau.sw808f16.wekatest;

import android.test.ApplicationTestCase;

public class SensorTypeTest extends ApplicationTestCase<DataCollectionApplication> {

  public SensorTypeTest() {
    super(DataCollectionApplication.class);
  }

  public void testSensorTypesDifferentValues() {
    for (SensorType sensorTypeOuter : SensorType.values()) {
      for (SensorType sensorTypeInner : SensorType.values()) {
        if (sensorTypeOuter.equals(sensorTypeInner)) {
          assertEquals(sensorTypeInner.getIdentifier(), sensorTypeOuter.getIdentifier());
        } else {
          assertNotSame(sensorTypeInner.getIdentifier(), sensorTypeOuter);
        }
      }
    }
  }

  public void testGetSensorTypeById() {
    for (SensorType sensorType : SensorType.values()) {
      assertEquals("Wrong sensor type was returned by getSensorTypeById",
          sensorType, SensorType.getSensorTypeById(sensorType.getIdentifier()));
    }
  }

}
