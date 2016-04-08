package dk.aau.sw808f16.datacollection;

public enum SensorType {

  ACCELEROMETER(0),
  AMBIENT_LIGHT(1),
  BAROMETER(2),
  CELLULAR(3),
  COMPASS(4),
  GYROSCOPE(5),
  LOCATION(6),
  PROXIMITY(7),
  WIFI(8);

  private int identifier;

  SensorType(final int id) {
    this.identifier = id;
  }

  public int getIdentifier() {
    return identifier;
  }

  public static SensorType getSensorTypeById(final int id) {
    for (final SensorType sensorType : SensorType.values()) {
      if (sensorType.getIdentifier() == id) {
        return sensorType;
      }
    }

    return null;
  }

}

