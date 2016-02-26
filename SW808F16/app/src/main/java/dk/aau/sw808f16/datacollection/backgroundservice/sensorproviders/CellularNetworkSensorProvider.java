package dk.aau.sw808f16.datacollection.backgroundservice.sensorproviders;

import android.content.Context;
import android.hardware.SensorManager;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class CellularNetworkSensorProvider extends SensorProvider<List<List<CellInfo>>> {

  private final Context context;

  public CellularNetworkSensorProvider(Context context, ExecutorService sensorThreadPool, SensorManager sensorManager) {
    super(context, sensorThreadPool, sensorManager);
    this.context = context;
  }

  @Override
  protected List<List<CellInfo>> retrieveSampleForDuration(final long sampleDuration, final int measurementFrequency) throws InterruptedException {

    final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    final List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

    return new ArrayList<List<CellInfo>>() {
      {
        add(cellInfoList);
      }
    };
  }
}
