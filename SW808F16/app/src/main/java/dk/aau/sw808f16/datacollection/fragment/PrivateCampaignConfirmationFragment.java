package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.goebl.david.Response;

import org.json.JSONObject;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.AsyncHttpCampaignJoinTask;
import dk.aau.sw808f16.datacollection.campaign.Campaign;

public class PrivateCampaignConfirmationFragment extends Fragment {

  private static final String CAMPAIGN_ID_TAG = "CAMPAIGN_ID_TAG";
  private static final String CAMPAIGN_SPECIFICATION_FRAGMENT_KEY = "CAMPAIGN_SPECIFICATION_FRAGMENT_KEY";

  public static PrivateCampaignConfirmationFragment newInstance(final long campaignId) {

    final PrivateCampaignConfirmationFragment newFragment = new PrivateCampaignConfirmationFragment();

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

    final Button submitBtn = (Button) view.findViewById(R.id.private_campaign_join_button);

    submitBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        final AsyncHttpCampaignJoinTask joinCampaignTask = new AsyncHttpCampaignJoinTask(getActivity(), campaignId);
        joinCampaignTask.execute();

        getFragmentManager().popBackStackImmediate();
      }
    });

    return view;
  }
}
