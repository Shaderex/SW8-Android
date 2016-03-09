package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.test.ApplicationTestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class LocationSensorProviderTest extends SensorProviderApplicationTestCase {

  @Override
  protected SensorProvider getSensorProvider() {
    return new LocationSensorProvider(getContext(), sensorThreadPool, sensorManager);
  }

  @Override
  protected void validateMeasurement(Object measurement, String sampleIdentifier) {
    assertTrue("[" + sampleIdentifier + "] measurement is not of type Location", measurement instanceof Location);
  }
}
