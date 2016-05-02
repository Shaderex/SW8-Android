package dk.aau.sw808f16.datacollection.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;

import junit.framework.Assert;

import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.Campaign;

public class CampaignSpecificationFragmentTest extends ActivityUnitTestCase<MainActivity> {
  public CampaignSpecificationFragmentTest() {
    super(MainActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);

    setActivityContext(context);

    final ComponentName componentName = new ComponentName("dk.aau.sw808f16.datacollection", "MainActivity");

    final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    startActivity(mainActivityIntent, null, null);
  }

  public void testAddCampaignSpecificationFragmentToActivityWithId() {

    final String key = "TEST_KEY";

    final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, CampaignSpecificationFragment.newInstance(1), key).commit();
    fragmentManager.executePendingTransactions();

    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);
    Assert.assertEquals(fragment.getClass(), CampaignSpecificationFragment.class);
  }

  public void testAddCampaignSpecificationFragmentToActivityWithCampaign() {

    final String key = "TEST_KEY";

    final Campaign campaign = new Campaign();

    final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, CampaignSpecificationFragment.newInstance(campaign), key).commit();
    fragmentManager.executePendingTransactions();

    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);
    Assert.assertEquals(fragment.getClass(), CampaignSpecificationFragment.class);
  }
}
