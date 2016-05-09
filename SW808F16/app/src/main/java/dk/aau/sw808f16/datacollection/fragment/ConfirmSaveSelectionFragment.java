package dk.aau.sw808f16.datacollection.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import dk.aau.sw808f16.datacollection.R;

public class ConfirmSaveSelectionFragment extends DialogFragment {

  public static final String CAMPAIGN_ID_TAG = "CAMPAIGN_ID_TAG";

  public static ConfirmSaveSelectionFragment newInstance(final long campaignIdentifier) {
    final ConfirmSaveSelectionFragment newFragment = new ConfirmSaveSelectionFragment();

    final Bundle args = new Bundle();
    args.putLong(CAMPAIGN_ID_TAG, campaignIdentifier);
    newFragment.setArguments(args);

    return newFragment;
  }

  @Override
  public Dialog onCreateDialog(final Bundle savedInstanceState) {

    // Use the Builder class for convenient dialog construction
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.confirm_save_selection)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
          public void onClick(final DialogInterface dialog, final int id) {

            dismiss();

            ((SaveConfirmedCampaign) getParentFragment()).onConfirmedCampaignSave(getArguments().getLong(CAMPAIGN_ID_TAG));
          }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
          public void onClick(final DialogInterface dialog, final int id) {

            dismiss();
          }
        });
    // Create the AlertDialog object and return it
    return builder.create();
  }

  public interface SaveConfirmedCampaign {
    void onConfirmedCampaignSave(final long campaignId);
  }

}