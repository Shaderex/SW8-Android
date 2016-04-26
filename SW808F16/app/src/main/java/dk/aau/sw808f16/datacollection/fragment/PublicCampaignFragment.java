package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.Request;
import com.goebl.david.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;

public class PublicCampaignFragment extends Fragment
    implements ConfirmSaveSelectionFragment.SaveConfirmedCampaign, SwipeRefreshLayout.OnRefreshListener {

  private static final String CONFIRM_SAVE_SELECTION_FRAGMENT = "confirmSaveSelectionFragment";
  private static final String CAMPAIGN_SPECIFICATION_FRAGMENT_KEY = "CAMPAIGN_SPECIFICATION_FRAGMENT_KEY";

  private long currentlyMarkedCampaign;
  private AsyncHttpWebbTask<JSONArray> currentGetCampaignsTask;

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
    currentlyMarkedCampaign = preferences.getLong(getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1);
  }

  @Override
  public void onResume() {
    super.onResume();

    onRefresh();
  }

  @Override
  public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.main_action_bar, menu);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_public_campaign, container, false);

    final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_container);
    swipeRefreshLayout.setOnRefreshListener(this);

    final Button continueButton = (Button) view.findViewById(R.id.confirm_button);
    continueButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Spawn dialog if there is already a marked campaign
        if (currentlyMarkedCampaign == -1 && preferences.getLong(getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1L) == -1L) {
          Toast.makeText(getActivity(), R.string.select_campaign_first_message, Toast.LENGTH_SHORT).show();
        } else if (currentlyMarkedCampaign == -1) {

          // TODO: byt denne toast ud med en dialog der håndterer ting
          Toast.makeText(getActivity(), R.string.unsubscribe_from_campaign_message, Toast.LENGTH_SHORT).show();

        } else if (preferences.getLong(getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1) != -1) {

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


    final FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.content_frame_layout, CampaignConfirmationFragment.newInstance(currentlyMarkedCampaign),
            getString(R.string.CAMPAIGN_CONFIRMATION_FRAGMENT_KEY))
        .addToBackStack(getString(R.string.CAMPAIGN_CONFIRMATION_FRAGMENT_KEY))
        .commit();
  }

  @Override
  public void onRefresh() {

    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_container);

    if (currentGetCampaignsTask != null) {
      //currentGetCampaignsTask.cancel(true);
      currentGetCampaignsTask = null;
    }

    final String campaignListResourcePath = RequestHostResolver.resolveHostForRequest(getActivity(), "/campaigns");

    currentGetCampaignsTask = new AsyncHttpWebbTask<JSONArray>(AsyncHttpWebbTask.Method.GET,
        campaignListResourcePath,
        HttpURLConnection.HTTP_OK) {

      final JsonCampaignsAdapter adapter = new JsonCampaignsAdapter();

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
      }

      @Override
      protected Response<JSONArray> sendRequest(final Request webb) {
        return webb.retry(10, true).asJsonArray();
      }

      @Override
      public void onResponseCodeMatching(final Response<JSONArray> response) {

        final JSONArray data = response.getBody();
        final ListView listView = (ListView) getView().findViewById(R.id.campaigns_list_view);

        if (listView.getEmptyView() != null) {
          listView.getEmptyView().setVisibility(View.GONE);
        }

        listView.setEmptyView(getView().findViewById(R.id.empty_no_data));

        listView.setAdapter(adapter);
        adapter.setData(data);
        refreshLayout.setRefreshing(false);
      }

      @Override
      public void onResponseCodeNotMatching(final Response<JSONArray> response) {

        final ListView listView = (ListView) getView().findViewById(R.id.campaigns_list_view);

        if (listView.getEmptyView() != null) {
          listView.getEmptyView().setVisibility(View.GONE);
        }

        listView.setEmptyView(getView().findViewById(R.id.empty_unexpected_response));
        listView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
        currentGetCampaignsTask = null;
      }

      @Override
      public void onConnectionFailure() {

        final ListView listView = (ListView) getView().findViewById(R.id.campaigns_list_view);

        if (listView.getEmptyView() != null) {
          listView.getEmptyView().setVisibility(View.GONE);
        }

        listView.setEmptyView(getView().findViewById(R.id.empty_no_connection));
        listView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
        currentGetCampaignsTask = null;
      }
    };

    currentGetCampaignsTask.execute();
  }

  public class JsonCampaignsAdapter extends BaseAdapter {

    public JSONArray data;

    private CheckBox lastMarkedCheckBox;

    JsonCampaignsAdapter() {
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
      LinearLayout campaignTextContainer;
      CheckBox campaignCheckBox;
      ImageButton infoButton;
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
        holder.campaignTextContainer = (LinearLayout) convertView.findViewById(R.id.campaign_text_container);
        holder.infoButton = (ImageButton) convertView.findViewById(R.id.info_button);

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

        final View.OnClickListener markCheckBoxListener = new View.OnClickListener() {
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
        };

        holder.campaignCheckBox.setOnClickListener(markCheckBoxListener);
        holder.campaignTextContainer.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            holder.campaignCheckBox.performClick();
          }
        });

        holder.infoButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            final CampaignSpecificationFragment fragment;
            try {
              fragment = CampaignSpecificationFragment.newInstance(campaignObject.getLong("id"));

              getFragmentManager().beginTransaction()
                  .replace(R.id.content_frame_layout, fragment, CAMPAIGN_SPECIFICATION_FRAGMENT_KEY)
                  .addToBackStack(CAMPAIGN_SPECIFICATION_FRAGMENT_KEY)
                  .commit();
            } catch (JSONException exception) {
              exception.printStackTrace();
            }
          }
        });

      } catch (JSONException exception) {
        exception.printStackTrace();
      }

      return convertView;
    }
  }
}