package dk.aau.sw808f16.datacollection;

import android.app.Application;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.junit.Test;

import java.util.List;

/**
 * Created by Marhlder on 19-02-2016.
 */
public class CellularNetworkTest extends ApplicationTestCase<Application> {

  public CellularNetworkTest() {
    super(Application.class);
  }

  @Test
  public void testCellularNetwork() {

    final TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
    final List<CellInfo> cellInfos = manager.getAllCellInfo();

    for (final CellInfo info : cellInfos) {
      Log.i("CellInfo", info.toString());
    }
  }
}
