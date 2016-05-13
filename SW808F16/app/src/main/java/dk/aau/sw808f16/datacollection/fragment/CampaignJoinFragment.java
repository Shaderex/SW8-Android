package dk.aau.sw808f16.datacollection.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.webutil.CampaignRegistrator;

public class CampaignJoinFragment extends Fragment implements ConfirmSaveSelectionFragment.SaveConfirmedCampaign {

  private static final String CAMPAIGN_ID_TAG = "CAMPAIGN_ID_TAG";
  private static final String CAMPAIGN_SPECIFICATION_FRAGMENT_KEY = "CAMPAIGN_SPECIFICATION_FRAGMENT_KEY";

  public static CampaignJoinFragment newInstance(final long campaignId) {

    final CampaignJoinFragment newFragment = new CampaignJoinFragment();

    final Bundle args = new Bundle();
    args.putLong(CAMPAIGN_ID_TAG, campaignId);
    newFragment.setArguments(args);

    return newFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    final Bundle arguments = getArguments();

    final long campaignId = arguments.getLong(CAMPAIGN_ID_TAG);
    final View view = inflater.inflate(R.layout.fragment_private_campaign_confirmation, container, false);

    final FragmentManager childFragmentManager = getChildFragmentManager();

    childFragmentManager.beginTransaction()
        .replace(R.id.campaign_specification_fragment_container, CampaignSpecificationFragment.newInstance(campaignId),
            CAMPAIGN_SPECIFICATION_FRAGMENT_KEY)
        .addToBackStack(CAMPAIGN_SPECIFICATION_FRAGMENT_KEY)
        .commit();

    final FloatingActionButton joinBtn = (FloatingActionButton) view.findViewById(R.id.private_campaign_join_button);
    joinBtn.setColorPressedResId(R.color.light_green_dark);

    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    final boolean joinedThisCampaign = preferences.getLong(getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1L) == campaignId;

    if (!joinedThisCampaign) {
      joinBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
          // Spawn dialog if there is already a marked campaign
          if (preferences.getLong(getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1L) == -1L) {
            onConfirmedCampaignSave(campaignId);
          } else {
            final ConfirmSaveSelectionFragment confirmSaveSelectionFragment = ConfirmSaveSelectionFragment.newInstance(campaignId);
            confirmSaveSelectionFragment.show(getChildFragmentManager(), null);
            getFragmentManager().executePendingTransactions();
          }
        }
      });
    } else {
      joinBtn.setColorNormalResId(R.color.red);
      joinBtn.setColorPressedResId(R.color.red_dark);
      joinBtn.setIconDrawable(getResources().getDrawable(R.drawable.ic_block_white_24dp));
      joinBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          final ConfirmSaveSelectionFragment confirmSaveSelectionFragment = ConfirmSaveSelectionFragment.newInstance(-1L);
          confirmSaveSelectionFragment.show(getChildFragmentManager(), null);
          getFragmentManager().executePendingTransactions();
        }
      });
    }

    return view;
  }

  @Override
  public void onConfirmedCampaignSave(final long campaignId) {
    final CampaignRegistrator parentRegistrator = (CampaignRegistrator) getActivity();
    parentRegistrator.registerCampaign(campaignId);

    getFragmentManager().popBackStackImmediate();
  }
}
