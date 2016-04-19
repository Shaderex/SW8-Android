package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import junit.framework.Assert;

import java.util.concurrent.CountDownLatch;

import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.R;

public class StartFragmentTest extends ActivityUnitTestCase<MainActivity> {
  public StartFragmentTest() {
    super(MainActivity.class);
  }

  final String startFragmentTestKey = "StartFragmentTestKey";

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);

    setActivityContext(context);

    final ComponentName componentName = new ComponentName("dk.aau.sw808f16.datacollection", "MainActivity");

    final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    startActivity(mainActivityIntent, null, null);

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, StartFragment.newInstance(), startFragmentTestKey).commit();
    fragmentManager.executePendingTransactions();
  }

  public void testAddStartFragmentToActivity() {

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);

    Assert.assertEquals(fragment.getClass(), StartFragment.class);
  }

  public void testHasNavigationButtons() {
    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    final StartFragment startFragment = (StartFragment) fragmentManager.findFragmentById(R.id.content_frame_layout);

    final View view = startFragment.getView();
    final Button privateCampaignButton = (Button) view.findViewById(R.id.private_campaign_button);
    final Button publicCampaignButton = (Button) view.findViewById(R.id.public_campaign_button);

    assertNotNull(privateCampaignButton);
    assertNotNull(publicCampaignButton);
  }

  public void testClickedOnPrivateButton() throws InterruptedException {
    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    final StartFragment startFragment = (StartFragment) fragmentManager.findFragmentById(R.id.content_frame_layout);

    final View view = startFragment.getView();
    final Button privateCampaignButton = (Button) view.findViewById(R.id.private_campaign_button);

    final CountDownLatch latch = new CountDownLatch(1);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        privateCampaignButton.performClick();
        latch.countDown();
      }
    });

    latch.await();

    fragmentManager.executePendingTransactions();
    final PrivateCampaignFragment privateFragment = (PrivateCampaignFragment) fragmentManager.findFragmentById(R.id.content_frame_layout);

    boolean b1 = privateFragment.isAdded();
    boolean b2 = !privateFragment.isHidden();
    boolean b3 = privateFragment.getView() != null && privateFragment.getView().getVisibility() == View.VISIBLE;

    assertTrue(b1 && b2 && b3);
  }

  public void testClickedOnPublicButton() throws InterruptedException {
    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    final StartFragment startFragment = (StartFragment) fragmentManager.findFragmentById(R.id.content_frame_layout);

    final View view = startFragment.getView();
    final Button publicCampaignButton = (Button) view.findViewById(R.id.public_campaign_button);

    final CountDownLatch latch = new CountDownLatch(1);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        publicCampaignButton.performClick();
        latch.countDown();
      }
    });

    latch.await();

    fragmentManager.executePendingTransactions();
    final PublicCampaignFragment publicFragment = (PublicCampaignFragment) fragmentManager.findFragmentById(R.id.content_frame_layout);

    boolean b1 = publicFragment.isAdded();
    boolean b2 = !publicFragment.isHidden();
    boolean b3 = publicFragment.getView() != null && publicFragment.getView().getVisibility() == View.VISIBLE;

    assertTrue(b1 && b2 && b3);
  }

}
