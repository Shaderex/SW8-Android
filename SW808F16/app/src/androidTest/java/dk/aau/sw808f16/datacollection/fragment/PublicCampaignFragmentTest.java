package dk.aau.sw808f16.datacollection.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.DataSetObserver;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.R;

public class PublicCampaignFragmentTest extends ActivityUnitTestCase<MainActivity> {
  public PublicCampaignFragmentTest() {
    super(MainActivity.class);
  }

  static final String TEST_KEY = "TEST_KEY";

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);

    setActivityContext(context);

    final ComponentName componentName = new ComponentName("dk.aau.sw808f16.datacollection", "MainActivity");

    final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    startActivity(mainActivityIntent, null, null);
  }

  public void testAddPublicCampaignFragmentToActivity() {

    final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PublicCampaignFragment.newInstance(), TEST_KEY).commit();
    fragmentManager.executePendingTransactions();

    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);

    Assert.assertEquals(fragment.getClass(), PublicCampaignFragment.class);
  }

  /*
  public void testPublicCampaignFragmentListViewEmptyViewVisible() throws InterruptedException {

    final WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

    wifiManager.setWifiEnabled(false);

    // Wait for wifi to be disabled
    Thread.sleep(1000);

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PublicCampaignFragment.newInstance(), TEST_KEY).commit();
    fragmentManager.executePendingTransactions();

    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);

    fragment.onResume();

    final TextView emptyView = (TextView) fragment.getView().findViewById(android.R.id.empty);

    assertEquals(View.VISIBLE, emptyView.getVisibility());

    wifiManager.setWifiEnabled(true);
  }
  */

  /*
  public void testPublicCampaignFragmentListViewEmptyViewHiddenOrGone() throws InterruptedException {

    final WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

    wifiManager.setWifiEnabled(true);

    // Wait for wifi to be enabled
    Thread.sleep(30000);


    ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

    assertTrue("device does not have wifi connection", manager.isDefaultNetworkActive());


    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PublicCampaignFragment.newInstance(), TEST_KEY).commit();
    fragmentManager.executePendingTransactions();

    final PublicCampaignFragment fragment = (PublicCampaignFragment) fragmentManager.findFragmentById(R.id.content_frame_layout);
    fragment.onResume();

    // Wait for http request in AsyncHttpTask to complete
    Thread.sleep(30000);

    final TextView no_data_emptyView = (TextView) fragment.getView().findViewById(R.id.empty_no_data);
    final TextView no_connection_emptyView = (TextView) fragment.getView().findViewById(R.id.empty_no_connection);
    final TextView unexpected_response_emptyView = (TextView) fragment.getView().findViewById(R.id.empty_unexpected_response);

    assertTrue("no_data_emptyView was visible", no_data_emptyView.getVisibility() == View.INVISIBLE
    || no_data_emptyView.getVisibility() == View.GONE);
    assertTrue("no_connection_emptyView was visible", no_connection_emptyView.getVisibility() == View.INVISIBLE
    || no_connection_emptyView.getVisibility() == View.GONE);
    assertTrue("unexpected_response_emptyView was visible", unexpected_response_emptyView.getVisibility() == View.INVISIBLE
    || unexpected_response_emptyView.getVisibility() == View.GONE);
  }
  */
}
