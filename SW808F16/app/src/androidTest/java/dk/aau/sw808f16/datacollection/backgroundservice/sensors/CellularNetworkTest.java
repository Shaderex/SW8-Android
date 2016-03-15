package dk.aau.sw808f16.datacollection.backgroundservice.sensors;

import android.app.Application;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

@SuppressWarnings("unused")
public class CellularNetworkTest extends ApplicationTestCase<Application> {

  public CellularNetworkTest() {
    super(Application.class);
  }

  public void testCellularNetwork() {

    final TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
    final List<CellInfo> cellInfoList = manager.getAllCellInfo();

    if (cellInfoList != null) {
      for (final CellInfo info : cellInfoList) {
        Log.i("CellInfo", info.toString());
      }
    } else {
      Log.i("CellInfo", "Could not retrieve cell info");
    }
  }
}
