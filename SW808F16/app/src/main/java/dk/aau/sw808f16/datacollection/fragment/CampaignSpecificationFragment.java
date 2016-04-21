package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.Campaign;

public class CampaignSpecificationFragment extends Fragment {

  public Menu menu;

  public static final String CAMPAIGN_ID_TAG = "CAMPAIGN_ID_TAG";
  public static final String CAMPAIGN_JSON_TAG = "CAMPAIGN_JSON_TAG";

  public static CampaignSpecificationFragment newInstance(final long campaignID) {

    final CampaignSpecificationFragment newFragment = new CampaignSpecificationFragment();

    final Bundle args = new Bundle();
    args.putLong(CAMPAIGN_ID_TAG, campaignID);
    newFragment.setArguments(args);

    return newFragment;
  }

  public static CampaignSpecificationFragment newInstance(final Campaign campaign) {

    final CampaignSpecificationFragment newFragment = new CampaignSpecificationFragment();
    final Bundle args = new Bundle();

    try {
      args.putString(CAMPAIGN_JSON_TAG, campaign.toJsonObject().toString());
    } catch (JSONException exception) {
      exception.printStackTrace();
    }
    newFragment.setArguments(args);

    return newFragment;
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
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

  private static class CampaignSpecificationListingsAdapter extends BaseAdapter {

    JSONArray namesArray = new JSONArray();
    JSONArray values = new JSONArray();

    @Override
    public int getCount() {
      return namesArray.length();
    }

    @Override
    public Object getItem(final int position) {
      try {
        return namesArray.getString(position);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public boolean hasStableIds() {
      return false;
    }

    class ViewHolder {
      TextView campaignSpecificationItemTitle;
      TextView campaignSpecificationItemValue;
      TextView campaignSpecificationItemLargeTextBlock;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

      if (convertView == null) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        convertView = inflater.inflate(R.layout.fragment_campaign_specification_item, parent);

        final ViewHolder holder = new ViewHolder();

        holder.campaignSpecificationItemTitle = (TextView) convertView.findViewById(R.id.campaign_specification_item_title);
        holder.campaignSpecificationItemValue = (TextView) convertView.findViewById(R.id.campaign_specification_item_value);
        holder.campaignSpecificationItemLargeTextBlock = (TextView) convertView.findViewById(R.id.campaign_specification_item_large_text_block);
      }

      final ViewHolder holder = (ViewHolder) convertView.getTag();

      try {
        final String name = namesArray.getString(position);

        holder.campaignSpecificationItemTitle.setText(name);

        switch (name) {
          case "Description": {

            holder.campaignSpecificationItemValue.setVisibility(View.GONE);
            holder.campaignSpecificationItemLargeTextBlock.setVisibility(View.VISIBLE);
            holder.campaignSpecificationItemLargeTextBlock.setText(values.getString(position));

            return convertView;
          }
          default: {

            holder.campaignSpecificationItemValue.setVisibility(View.VISIBLE);
            holder.campaignSpecificationItemValue.setText(values.getString(position));
            holder.campaignSpecificationItemLargeTextBlock.setVisibility(View.GONE);

            return convertView;
          }
        }

      } catch (JSONException exception) {
        exception.printStackTrace();
      }

      return null;
    }

    public void setCampaignSpecification(final JSONObject campaignSpecification) {

      namesArray = campaignSpecification.names();

      try {
        values = campaignSpecification.toJSONArray(namesArray);
      } catch (JSONException exception) {
        exception.printStackTrace();
      }

      this.notifyDataSetChanged();
    }
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

    final Bundle arguments = getArguments();

    if (arguments == null) {
      return null;
    }

    final View view = inflater.inflate(R.layout.fragment_campaign_specification, container, false);
    final ListView campaignSpecificationListing = (ListView) view.findViewById(R.id.fragment_campaign_specification_listing);

    final CampaignSpecificationListingsAdapter adapter = new CampaignSpecificationListingsAdapter();

    campaignSpecificationListing.setAdapter(adapter);

    if (arguments.containsKey(CAMPAIGN_ID_TAG)) {
      // Start AsyncTask

      //adapter.setCampaignSpecification(new JSONObject(arguments.getString(CAMPAIGN_JSON_TAG)));


    } else if (arguments.containsKey(CAMPAIGN_JSON_TAG)) {
      try {
        adapter.setCampaignSpecification(new JSONObject(arguments.getString(CAMPAIGN_JSON_TAG)));
      } catch (JSONException exception) {
        exception.printStackTrace();
      }
    } else {
      return null;
    }

    return view;
  }
}
