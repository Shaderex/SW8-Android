package dk.aau.sw808f16.wekatest;

public enum SensorType {

  ACCELEROMETER(0, "Accelerometer", SensorCategory.MOVEMENT),
  AMBIENT_LIGHT(1, "Ambient Light", SensorCategory.MISC),
  BAROMETER(2, "Barometer", SensorCategory.LOCATION),
  CELLULAR(3, "Cellular Network", SensorCategory.LOCATION),
  COMPASS(4, "Compass", SensorCategory.LOCATION),
  GYROSCOPE(5, "Gyroscope", SensorCategory.MOVEMENT),
  LOCATION(6, "GPS", SensorCategory.LOCATION),
  PROXIMITY(7, "Proximity", SensorCategory.MISC),
  WIFI(8, "Wi-Fi", SensorCategory.LOCATION),
  WRIST_ACCELEROMETER(9, "Wrist Accelerometer", SensorCategory.MOVEMENT),
  GALVANIC_SKIN(10, "Galvanic Skin Response", SensorCategory.PERSONAL_INFORMATION),
  UV(11, "UV Lightning", SensorCategory.MISC),
  HEARTBEAT(12, "Heart Rate", SensorCategory.PERSONAL_INFORMATION);

  public enum SensorCategory {
    LOCATION,
    MOVEMENT,
    PERSONAL_INFORMATION,
    MISC
  }

  private final int identifier;
  private final String name;
  private final SensorCategory category;

  SensorType(final int id, final String name, final SensorCategory category) {
    this.identifier = id;
    this.name = name;
    this.category = category;
  }

  public int getIdentifier() {
    return identifier;
  }

  public SensorCategory getCategory() {
    return category;
  }

  public static SensorType getSensorTypeById(final int id) {
    for (final SensorType sensorType : SensorType.values()) {
      if (sensorType.getIdentifier() == id) {
        return sensorType;
      }
    }

    return null;
  }

  @Override
  public String toString() {
    return name;
  }
}

