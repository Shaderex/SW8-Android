package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import dk.aau.sw808f16.datacollection.snapshot.Sample;

public class CellularNetworkSensorProvider extends SensorProvider<Sample> {

  private final Context context;

  public CellularNetworkSensorProvider(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    this.context = context;
  }

  @Override
  protected Sample retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency)
      throws InterruptedException {

    final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    final List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

    return new Sample(Arrays.asList(cellInfoList));
  }
}
