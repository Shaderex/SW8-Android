package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.AsyncHttpCampaignJoinTask;

public class PrivateCampaignFragment extends Fragment {

  public Menu menu;

  public static PrivateCampaignFragment newInstance() {

    final PrivateCampaignFragment newFragment = new PrivateCampaignFragment();

    /*
        Bundle args = new Bundle();
        args.putInt(DRAWABLE_RESOURCE_ID_TAG, drawableResourcesId);
        newFragment.setArguments(args);
    */

    return newFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    this.menu = menu;
    inflater.inflate(R.menu.main_action_bar, menu);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_private_campaign, container, false);
    final Button submitBtn = (Button) view.findViewById(R.id.private_campaign_join_button);
    final EditText campaignIdField = (EditText) view.findViewById(R.id.private_campaign_edit_text);

    submitBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(getActivity(), "Starter", Toast.LENGTH_SHORT).show();

        final long enteredCampaignId = Long.parseLong(campaignIdField.getText().toString());
        final AsyncHttpCampaignJoinTask joinCampaignTask = new AsyncHttpCampaignJoinTask(getActivity(), enteredCampaignId);
        joinCampaignTask.execute();

      }
    });

    return view;
  }
}
