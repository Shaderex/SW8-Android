package dk.aau.sw808f16.wekatest.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;

import junit.framework.Assert;

import dk.aau.sw808f16.wekatest.MainActivity;
import dk.aau.sw808f16.wekatest.R;

public class PrivateCampaignFragmentTest extends ActivityUnitTestCase<MainActivity> {
  public PrivateCampaignFragmentTest() {
    super(MainActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);

    setActivityContext(context);

    final ComponentName componentName = new ComponentName("dk.aau.sw808f16.wekatest", "MainActivity");

    final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    startActivity(mainActivityIntent, null, null);

    final String key = "TEST_KEY";

    final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PrivateCampaignFragment.newInstance(), key).commit();
    fragmentManager.executePendingTransactions();


  }

  public void testAddPrivateCampaignFragmentToActivity() {

    final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);
    Assert.assertEquals(fragment.getClass(), PrivateCampaignFragment.class);
  }
}
