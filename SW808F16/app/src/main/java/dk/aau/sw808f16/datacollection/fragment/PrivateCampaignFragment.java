package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dk.aau.sw808f16.datacollection.R;

public class PrivateCampaignFragment extends Fragment {

  private static final String PRIVATE_CAMPAIGN_CONFIRMATION_FRAGMENT_KEY = "PRIVATE_CAMPAIGN_CONFIRMATION_FRAGMENT_KEY";

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
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_private_campaign, container, false);
    final Button submitBtn = (Button) view.findViewById(R.id.private_campaign_init_confirmation_button);
    final EditText campaignIdField = (EditText) view.findViewById(R.id.private_campaign_edit_text);

    submitBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        final String enteredCampaignIdText = campaignIdField.getText().toString();

        if (!TextUtils.isEmpty(enteredCampaignIdText) && enteredCampaignIdText.matches("[0-9]+")) {

          try {

            final long enteredCampaignId = Long.parseLong(enteredCampaignIdText);

            final FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                .replace(R.id.content_frame_layout, PrivateCampaignConfirmationFragment.newInstance(enteredCampaignId), PRIVATE_CAMPAIGN_CONFIRMATION_FRAGMENT_KEY)
                .addToBackStack(PRIVATE_CAMPAIGN_CONFIRMATION_FRAGMENT_KEY)
                .commit();
          } catch (NumberFormatException exception) {
            Toast.makeText(getActivity(), "The entered campaign ID is too long", Toast.LENGTH_LONG).show();
          }

        } else {
          Toast.makeText(getActivity(), R.string.private_campaign_invalid_campaign_id_message, Toast.LENGTH_LONG).show();
        }
      }
    });

    return view;
  }
}
