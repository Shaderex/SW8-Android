package dk.aau.sw808f16.datacollection.snapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.aau.sw808f16.datacollection.SensorType;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;


public class Sample extends RealmObject {
  private long id;

  private int sensorTypeId;

  @Ignore
  private SensorType sensorType;

  public Sample() {
  }

  public Sample(SensorType sensorType) {
    setSensorType(sensorType);
  }

  public Sample(final SensorType sensorType, final Object initialMeasurement) {
    this(sensorType);
    addMeasurement(initialMeasurement);
  }

  public Sample(final SensorType sensorType, final List<?> initialMeasurements) {
    this(sensorType);
    addMeasurements(initialMeasurements);
  }

  public List<?> getMeasurements(final RealmConfiguration realmConfiguration) {

    final Realm realm = getRealmFromConfiguration(realmConfiguration);

    if (sensorType == SensorType.ACCELEROMETER || sensorType == SensorType.GYROSCOPE) {
      return realm.where(FloatTriple.class).equalTo("sampleId", id).findAll();
    }

    realm.close();

    return new ArrayList<>();
  }

  public List<?> getMeasurements() {
    return getMeasurements(null);
  }

  public void setMeasurements(final RealmConfiguration realmConfiguration, final List<?> measurements) {
    ((RealmList) getMeasurements(realmConfiguration)).clear();
    addMeasurements(realmConfiguration, measurements);
  }

  public void setMeasurements(final List<?> measurements) {
    setMeasurements(null, measurements);
  }

  public void addMeasurements(final RealmConfiguration realmConfiguration, final List<?> measurements) {


    Realm realm = getRealmFromConfiguration(realmConfiguration);

    realm.beginTransaction();

    for (final Object measurement : measurements) {
      if (measurement instanceof FloatTriple) {
        final FloatTriple ft = ((FloatTriple) measurement);
        ft.setSampleId(getId(realmConfiguration));

        realm.copyToRealm(ft);
      } else {
        realm.cancelTransaction();
        realm.close();
        throw new IllegalArgumentException(measurement.getClass().getName() + " is not a supported measurement type");
      }
    }

    realm.commitTransaction();
    realm.close();
  }

  public void addMeasurements(final List<?> measurements) {
    addMeasurements(null, measurements);
  }

  public void addMeasurement(final RealmConfiguration realmConfiguration, final Object measurement) {
    addMeasurements(realmConfiguration, Collections.singletonList(measurement));
  }

  public void addMeasurement(final Object measurement) {
    addMeasurement(null, measurement);
  }

  public long getId(final RealmConfiguration realmConfiguration) {

    final Realm realm = getRealmFromConfiguration(realmConfiguration);
    if (id == 0) {
      id = realm.where(Sample.class).max("id").longValue() + 1;
    }
    realm.close();

    return id;
  }

  public long getId() {
    return getId(null);
  }

  public SensorType getSensorType() {

    if (sensorType == null) {
      sensorType = SensorType.ACCELEROMETER;
    }

    return sensorType;
  }

  public void setSensorType(SensorType sensorType) {
    this.sensorType = sensorType;
    this.sensorTypeId = sensorType.getIdentifier();
  }

  private Realm getRealmFromConfiguration(RealmConfiguration realmConfiguration) {
    if (realmConfiguration == null) {
      return Realm.getDefaultInstance();
    } else {
      return Realm.getInstance(realmConfiguration);
    }
  }
}
