package dk.aau.sw808f16.datacollection.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.goebl.david.Response;

import org.json.JSONObject;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.AsyncHttpCampaignJoinTask;

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
        .replace(R.id.campaign_specification_fragment_container, CampaignSpecificationFragment.newInstance(campaignId), CAMPAIGN_SPECIFICATION_FRAGMENT_KEY)
        .addToBackStack(CAMPAIGN_SPECIFICATION_FRAGMENT_KEY)
        .commit();

    final FloatingActionButton joinBtn = (FloatingActionButton) view.findViewById(R.id.private_campaign_join_button);

    joinBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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

    return view;
  }

  @Override
  public void onConfirmedCampaignSave(final long campaignId) {

    final Context context = getActivity();

    final AsyncHttpCampaignJoinTask joinCampaignTask = new AsyncHttpCampaignJoinTask(context, campaignId) {
      @Override
      public void onResponseCodeMatching(final Response<JSONObject> response) {
        super.onResponseCodeMatching(response);

        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(context.getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), campaignId);
        editor.apply();
      }
    };

    joinCampaignTask.execute();
    getFragmentManager().popBackStackImmediate();

  }
}