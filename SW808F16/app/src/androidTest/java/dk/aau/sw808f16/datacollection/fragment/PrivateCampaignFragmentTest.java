package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;

import junit.framework.Assert;

import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.R;

public class PrivateCampaignFragmentTest extends ActivityUnitTestCase<MainActivity> {
  public PrivateCampaignFragmentTest() {
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

    final String key = "TEST_KEY";

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PrivateCampaignFragment.newInstance(), key).commit();
    fragmentManager.executePendingTransactions();


  }

  public void testAddPrivateCampaignFragmentToActivity() {

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);
    Assert.assertEquals(fragment.getClass(), PrivateCampaignFragment.class);
  }

  public void testIfComponentsArePresent() {
    final View editText = getActivity().findViewById(R.id.private_campaign_edit_text);
    final View joinButton = getActivity().findViewById(R.id.private_campaign_join_button);

    Assert.assertNotNull(editText);
    Assert.assertNotNull(joinButton);
  }
}
