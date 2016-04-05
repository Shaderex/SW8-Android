package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.WebUtil.AsyncHttpTask;

public class PublicCampaignFragment extends Fragment implements ConfirmSaveSelectionFragment.SaveConfirmedCampaign {

  private static final String CONFIRM_SAVE_SELECTION_FRAGMENT = "confirmSaveSelectionFragment";
  private static final String CURRENTLY_CHECKED_CAMPAIGN_ID_KEY = "CURRENTLY_CHECKED_CAMPAIGN_ID_KEY";
  private static final String campaignListResourcePath = "https://dev.local.element67.dk:8000/campaigns";

  public Menu menu;
  private long currentlyMarkedCampaign;

  public static PublicCampaignFragment newInstance() {

    final PublicCampaignFragment newFragment = new PublicCampaignFragment();

    /*
        Bundle args = new Bundle();
        args.putInt(DRAWABLE_RESOURCE_ID_TAG, drawableResourcesId);
        newFragment.setArguments(args);
    */

    return newFragment;
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    currentlyMarkedCampaign = preferences.getLong(CURRENTLY_CHECKED_CAMPAIGN_ID_KEY, -1);
  }

  @Override
  public void onResume() {
    super.onResume();

    try {

      final AsyncHttpTask task = new AsyncHttpTask(getActivity(), new URL(campaignListResourcePath), HttpURLConnection.HTTP_OK) {

        final JsonCampaingsAdapter adapter = new JsonCampaingsAdapter();

        @Override
        protected void onPreExecute() {
          super.onPreExecute();

          final View activityIndicator = getView().findViewById(R.id.activity_indicator);
          final TextView activityIndicatorTextView = (TextView) activityIndicator.findViewById(R.id.activity_indicator_message_text_view);
          activityIndicatorTextView.setText(R.string.loading_campaigns_message);
          activityIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onResponseCodeMatching(final InputStream in) {

          final View activityIndicator = getView().findViewById(R.id.activity_indicator);
          activityIndicator.setVisibility(View.GONE);

          try {
            final JSONArray data = new JSONArray(convertInputStreamToString(in));
            final ListView listView = (ListView) getView().findViewById(R.id.campaigns_list_view);
            listView.setEmptyView(getView().findViewById(android.R.id.empty));
            listView.setAdapter(adapter);
            adapter.setData(data);

          } catch (JSONException exception) {
            exception.printStackTrace();
          }

        }

        @Override
        protected void onResponseCodeNotMatching(final Integer responseCode) {

          final View activityIndicator = getView().findViewById(R.id.activity_indicator);
          activityIndicator.setVisibility(View.GONE);
          final ListView listView = (ListView) getView().findViewById(R.id.campaigns_list_view);
          listView.setEmptyView(getView().findViewById(android.R.id.empty));
          listView.setAdapter(adapter);
        }
      };

      task.execute();

    } catch (MalformedURLException exception) {
      exception.printStackTrace();
    }

  }

  @Override
  public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    this.menu = menu;

    inflater.inflate(R.menu.main_action_bar, menu);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_public_campaign, container, false);
    //final ListView listView = (ListView) view.findViewById(R.id.campaigns_list_view);

    final Button confirmButton = (Button) view.findViewById(R.id.confirm_button);
    confirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Spawn dialog if there is already a marked campaign
        if (currentlyMarkedCampaign == -1 && preferences.getLong(CURRENTLY_CHECKED_CAMPAIGN_ID_KEY, -1) != -1) {
          Toast.makeText(getActivity(), R.string.select_campaign_first_message, Toast.LENGTH_SHORT).show();
        } else if (currentlyMarkedCampaign == -1) {
          // TODO: byt denne toast ud med en dialog der håndterer ting
          Toast.makeText(getActivity(), R.string.unsubscribe_from_campaign_message, Toast.LENGTH_SHORT).show();
        } else if (preferences.getLong(CURRENTLY_CHECKED_CAMPAIGN_ID_KEY, -1) != -1) {
          ConfirmSaveSelectionFragment confirmSaveSelectionFragment = new ConfirmSaveSelectionFragment();
          confirmSaveSelectionFragment.show(getChildFragmentManager(), CONFIRM_SAVE_SELECTION_FRAGMENT);
          getFragmentManager().executePendingTransactions();
        } else {
          onConfirmedCampaignSave();
        }
      }
    });

    return view;
  }

  @Override
  public void onConfirmedCampaignSave() {
    final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
    editor.putLong(CURRENTLY_CHECKED_CAMPAIGN_ID_KEY, currentlyMarkedCampaign);
    editor.apply();
  }

  class JsonCampaingsAdapter extends BaseAdapter {

    private JSONArray data;

    private CheckBox lastMarkedCheckBox;

    JsonCampaingsAdapter() {
    }

    JsonCampaingsAdapter(final JSONArray data) {
      this.data = data;
    }

    public void setData(final JSONArray data) {
      this.data = data;
      this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
      return data == null ? 0 : data.length();
    }

    @Override
    public Object getItem(final int position) {
      try {
        return data.get(position);
      } catch (JSONException exception) {
        exception.printStackTrace();
      }

      return null;
    }

    @Override
    public long getItemId(final int position) {
      try {
        return data.getJSONObject(position).getLong("id");
      } catch (JSONException exception) {
        exception.printStackTrace();
      }

      return -1;
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

    class ViewHolder {
      TextView idTextView;
      TextView titleTextView;
      CheckBox campaignCheckBox;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

      final JSONObject campaignObject = (JSONObject) getItem(position);

      final ViewHolder holder;

      if (convertView == null) {

        convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_public_campaign_element, null);

        holder = new ViewHolder();

        holder.idTextView = (TextView) convertView.findViewById(R.id.campaign_id_text_view);
        holder.titleTextView = (TextView) convertView.findViewById(R.id.campaign_title_text_view);
        holder.campaignCheckBox = (CheckBox) convertView.findViewById(R.id.campaign_check_box);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      try {
        holder.idTextView.setText("" + campaignObject.getLong("id"));
        holder.titleTextView.setText(campaignObject.getString("name"));

        if (currentlyMarkedCampaign == campaignObject.getLong("id")) {
          holder.campaignCheckBox.setChecked(true);
          lastMarkedCheckBox = holder.campaignCheckBox;
        } else {
          holder.campaignCheckBox.setChecked(false);
        }


        holder.campaignCheckBox.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(final View clickedView) {

            final CheckBox checkBox = (CheckBox) clickedView;

            if (lastMarkedCheckBox != null && lastMarkedCheckBox != checkBox) {
              lastMarkedCheckBox.setChecked(false);
            }

            if (checkBox.isChecked()) {
              try {
                lastMarkedCheckBox = checkBox;
                currentlyMarkedCampaign = campaignObject.getLong("id");
              } catch (JSONException exception) {
                exception.printStackTrace();
                currentlyMarkedCampaign = -1;
                lastMarkedCheckBox = null;
              }
            } else {
              currentlyMarkedCampaign = -1;
              lastMarkedCheckBox = null;
            }
          }
        });



      } catch (JSONException exception) {
        exception.printStackTrace();
      }

      return convertView;
    }
  }

  // Convert inputstream to string
  private String convertInputStreamToString(final InputStream in) {

    final BufferedReader r = new BufferedReader(new InputStreamReader(in));
    final StringBuilder total = new StringBuilder();

    String line;

    try {
      while ((line = r.readLine()) != null) {
        total.append(line);
      }
    } catch (IOException exception) {
      exception.printStackTrace();

      return "";
    }

    return total.toString();
  }
}
