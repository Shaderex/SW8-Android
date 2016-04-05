package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dk.aau.sw808f16.datacollection.R;

public class StartFragment extends Fragment {

  private static final String PRIVATE_CAMPAIGN_FRAGMENT_KEY = "PRIVATE_CAMPAIGN_FRAGMENT_KEY";
  private static final String PUBLIC_CAMPAIGN_FRAGMENT_KEY = "PUBLIC_CAMPAIGN_FRAGMENT_KEY";

  public static StartFragment newInstance() {

    final StartFragment newFragment = new StartFragment();

    /*
        Bundle args = new Bundle();
        args.putInt(DRAWABLE_RESOURCE_ID_TAG, drawableResourcesId);
        newFragment.setArguments(args);
    */

    return newFragment;
  }


  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_start, container, false);

    final Button privateFragmentButton = (Button) view.findViewById(R.id.private_campaign_button);
    final Button publicFragmentButton = (Button) view.findViewById(R.id.public_campaign_button);

    privateFragmentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame_layout, PrivateCampaignFragment.newInstance(), PRIVATE_CAMPAIGN_FRAGMENT_KEY)
            .addToBackStack(PRIVATE_CAMPAIGN_FRAGMENT_KEY)
            .commit();
      }
    });

    publicFragmentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame_layout, PublicCampaignFragment.newInstance(), PUBLIC_CAMPAIGN_FRAGMENT_KEY)
            .addToBackStack(PUBLIC_CAMPAIGN_FRAGMENT_KEY)
            .commit();
      }
    });

    return view;

  }

}
