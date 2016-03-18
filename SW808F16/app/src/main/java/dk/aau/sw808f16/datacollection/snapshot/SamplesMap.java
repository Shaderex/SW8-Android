package dk.aau.sw808f16.datacollection.snapshot;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import io.realm.RealmList;

public class SamplesMap implements Map<Integer, RealmList<Sample>> {

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

    private SensorType(final int i) {
      this.identifier = i;
    }

    public int getIdentifier() {
      return identifier;
    }
  }

  public static final int ACCELEROMETER = 0;


  @Override
  public void clear() {

  }

  @Override
  public boolean containsKey(Object key) {
    return false;
  }

  @Override
  public boolean containsValue(Object value) {
    return false;
  }

  @NonNull
  @Override
  public Set<Entry<Integer, RealmList<Sample>>> entrySet() {
    return null;
  }

  @Override
  public RealmList<Sample> get(Object key) {
    return null;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @NonNull
  @Override
  public Set<Integer> keySet() {
    return null;
  }

  @Override
  public RealmList<Sample> put(Integer key, RealmList<Sample> value) {
    return null;
  }

  @Override
  public void putAll(Map<? extends Integer, ? extends RealmList<Sample>> map) {

  }

  @Override
  public RealmList<Sample> remove(Object key) {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @NonNull
  @Override
  public Collection<RealmList<Sample>> values() {
    return null;
  }
}
