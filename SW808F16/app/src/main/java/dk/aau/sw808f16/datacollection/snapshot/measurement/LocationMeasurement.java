package dk.aau.sw808f16.datacollection.snapshot.measurement;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import dk.aau.sw808f16.datacollection.snapshot.JsonValueAble;
import io.realm.RealmObject;

public class LocationMeasurement extends RealmObject implements JsonValueAble {

  private double longitude;
  private double latitude;
  private float accuracy;
  private float speed;
  private float bearing;

  public LocationMeasurement() {

  }

  public LocationMeasurement(final Location location) {
    if (location == null) {
      return;
    }

    setLongitude(location.getLongitude());
    setLatitude(location.getLatitude());
    setAccuracy(location.getAccuracy());
    setSpeed(location.getSpeed());
    setBearing(location.getBearing());
  }

  public float getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(float accuracy) {
    this.accuracy = accuracy;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public float getBearing() {
    return bearing;
  }

  public void setBearing(float bearing) {
    this.bearing = bearing;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  @Override
  public boolean equals(Object object) {
    return super.equals(object)
        || (object instanceof LocationMeasurement
        && ((LocationMeasurement) object).longitude == this.longitude
        && ((LocationMeasurement) object).latitude == this.latitude
        && ((LocationMeasurement) object).accuracy == this.accuracy
        && ((LocationMeasurement) object).speed == this.speed
        && ((LocationMeasurement) object).bearing == this.bearing);
  }

  @Override
  public String toJsonValue() throws JSONException {

    final JSONObject jsonObject = new JSONObject();

    jsonObject.put("longitude", longitude);
    jsonObject.put("latitude", latitude);
    jsonObject.put("accuracy", accuracy);
    jsonObject.put("speed", speed);
    jsonObject.put("bearing", bearing);

    return jsonObject.toString();
  }
}
