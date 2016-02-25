package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
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

  private class RetrieveGpsDataCallable extends RetrieveSensorDataCallable {

    final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    final List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

    public RetrieveGpsDataCallable(final long sampleDuration, final int measurementFrequency) {
      super(sampleDuration, measurementFrequency);
    }

    @Override
    public List<List<CellInfo>> call() throws Exception {
      return new ArrayList<List<CellInfo>>() {
        {
          add(cellInfoList);
        }
      };
    }
  }

  @Override
  protected RetrieveSensorDataCallable createCallable(long sampleDuration, int measurementFrequency) {
    return new RetrieveGpsDataCallable(sampleDuration, measurementFrequency);
  }


}
