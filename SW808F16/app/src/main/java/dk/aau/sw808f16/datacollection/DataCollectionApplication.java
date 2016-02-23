package dk.aau.sw808f16.datacollection;

import android.app.Application;
import android.content.Intent;

import dk.aau.sw808f16.datacollection.backgroundservice.BackgroundSensorService;

public class DataCollectionApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    startService(new Intent(this, BackgroundSensorService.class));
  }
}
