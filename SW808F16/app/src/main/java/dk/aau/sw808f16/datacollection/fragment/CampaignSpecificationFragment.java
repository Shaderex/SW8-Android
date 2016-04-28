package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.goebl.david.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.campaign.AsyncHttpGetCampaignSpecificationTask;
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

        convertView = inflater.inflate(R.layout.fragment_campaign_specification_item, null);

        final ViewHolder holder = new ViewHolder();

        holder.campaignSpecificationItemTitle = (TextView) convertView.findViewById(R.id.campaign_specification_item_title);
        holder.campaignSpecificationItemValue = (TextView) convertView.findViewById(R.id.campaign_specification_item_value);
        holder.campaignSpecificationItemLargeTextBlock =
            (TextView) convertView.findViewById(R.id.campaign_specification_item_large_text_block);

        convertView.setTag(holder);
      }

      final ViewHolder holder = (ViewHolder) convertView.getTag();

      try {
        String name = namesArray.getString(position);

        // Remove underscores and make first letter uppercase
        name = name.replace('_', ' ');
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        holder.campaignSpecificationItemTitle.setText(name);

        switch (name) {
          case "Sensors": {

            final JSONArray array = values.getJSONArray(position);

            holder.campaignSpecificationItemValue.setVisibility(View.VISIBLE);
            holder.campaignSpecificationItemLargeTextBlock.setVisibility(View.GONE);

            final StringBuilder builder = new StringBuilder();

            for (int counter = 0; counter < array.length(); counter++) {

              builder.append(array.getJSONObject(counter).getString("name"));

              if (counter != array.length() - 1) {
                builder.append('\n');
              }
            }

            holder.campaignSpecificationItemValue.setText(builder.toString());

            return convertView;
          }
          case "Questions": {

            final JSONArray array = values.getJSONArray(position);

            holder.campaignSpecificationItemValue.setVisibility(View.VISIBLE);
            holder.campaignSpecificationItemLargeTextBlock.setVisibility(View.GONE);

            final StringBuilder builder = new StringBuilder();

            for (int counter = 0; counter < array.length(); counter++) {

              builder.append(array.getJSONObject(counter).getString("question"));

              if (counter != array.length() - 1) {
                builder.append('\n');
              }
            }

            holder.campaignSpecificationItemValue.setText(builder.toString());

            return convertView;
          }
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

            /*
            if (Number.class.isAssignableFrom(values.get(position).getClass())) {
              holder.campaignSpecificationItemValue.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
              holder.campaignSpecificationItemValue.setGravity(Gravity.START);
            }*/

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
    final ContentLoadingProgressBar activityIndicator = (ContentLoadingProgressBar) view.findViewById(R.id.activity_indicator);
    final ListView campaignSpecificationListing = (ListView) view.findViewById(R.id.fragment_campaign_specification_listing);

    final CampaignSpecificationListingsAdapter adapter = new CampaignSpecificationListingsAdapter();
    campaignSpecificationListing.setAdapter(adapter);
    activityIndicator.show();

    if (arguments.containsKey(CAMPAIGN_ID_TAG)) {
      // Start AsyncTask

      final long campaignId = arguments.getLong(CAMPAIGN_ID_TAG);

      final AsyncHttpGetCampaignSpecificationTask task = new AsyncHttpGetCampaignSpecificationTask(getActivity(), campaignId) {

        @Override
        public void onResponseCodeMatching(Response<JSONObject> response) {
          super.onResponseCodeMatching(response);
          adapter.setCampaignSpecification(response.getBody());
          activityIndicator.hide();
        }

        @Override
        public void onResponseCodeNotMatching(final Response<JSONObject> response) {
          super.onResponseCodeNotMatching(response);

          final TextView textView = (TextView) getView().findViewById(R.id.error_message_textview);

          activityIndicator.hide();

          switch (response.getStatusCode()) {
            case HttpURLConnection.HTTP_NOT_FOUND: {

              textView.setText(R.string.unable_to_locate_campaign_message);
              break;
            }
            default: {
              textView.setText(R.string.generic_something_went_wrong_message);
              break;
            }
          }

          textView.setVisibility(View.VISIBLE);

        }
      };

      task.execute();

    } else if (arguments.containsKey(CAMPAIGN_JSON_TAG)) {
      try {
        adapter.setCampaignSpecification(new JSONObject(arguments.getString(CAMPAIGN_JSON_TAG)));
        activityIndicator.hide();
      } catch (JSONException exception) {
        exception.printStackTrace();
      }
    } else {
      return null;
    }

    return view;
  }
}
