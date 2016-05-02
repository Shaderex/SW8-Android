package dk.aau.sw808f16.datacollection.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    implements SwipeRefreshLayout.OnRefreshListener {

  private static final String JOIN_CAMPAIGN_FRAGMENT_KEY = "JOIN_CAMPAIGN_FRAGMENT_KEY";
  private static final String PRIVATE_CAMPAIGN_FRAGMENT_KEY = "PRIVATE_CAMPAIGN_FRAGMENT_KEY";

  private long currentlyMarkedCampaign;
  private AsyncHttpWebbTask<JSONArray> currentGetCampaignsTask;

  public static PublicCampaignFragment newInstance() {
    return new PublicCampaignFragment();
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

    final LinearLayout continueButton = (LinearLayout) view.findViewById(R.id.confirm_button_container).findViewById(R.id.campaign_text_container);

    final TextView campaignIdTextView = (TextView) continueButton.findViewById(R.id.campaign_id_text_view);
    campaignIdTextView.setText("Know what you're doing?");
    campaignIdTextView.setTextColor(getResources().getColor(R.color.white));

    final TextView campaignTitleTextView = (TextView) continueButton.findViewById(R.id.campaign_by_line);
    campaignTitleTextView.setText("Join a specific campaign by clicking here");
    campaignTitleTextView.setTextColor(getResources().getColor(R.color.white));

    final ImageView infoArrow = (ImageView) continueButton.findViewById(R.id.info_button);
    infoArrow.setColorFilter(Color.WHITE);

    continueButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame_layout, PrivateCampaignFragment.newInstance(),
                PRIVATE_CAMPAIGN_FRAGMENT_KEY)
            .addToBackStack(PRIVATE_CAMPAIGN_FRAGMENT_KEY)
            .commit();
      }
    });

    return view;
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

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentlyMarkedCampaign = preferences.getLong(getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1);

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

        currentGetCampaignsTask = null;

        listView.setEmptyView(getView().findViewById(R.id.empty_unexpected_response));
        listView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
      }

      @Override
      public void onConnectionFailure() {
        final ListView listView = (ListView) getView().findViewById(R.id.campaigns_list_view);

        if (listView.getEmptyView() != null) {
          listView.getEmptyView().setVisibility(View.GONE);
        }

        currentGetCampaignsTask = null;

        listView.setEmptyView(getView().findViewById(R.id.empty_no_connection));
        listView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
      }
    };

    currentGetCampaignsTask.execute();
  }

  public class JsonCampaignsAdapter extends BaseAdapter {
    public JSONArray data;

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
      TextView byLineTextView;
      LinearLayout campaignTextContainer;
      ImageView infoArrow;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
      final JSONObject campaignObject = (JSONObject) getItem(position);

      final ViewHolder holder;

      try {
        if (convertView == null) {
          convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_public_campaign_element, null);

          holder = new ViewHolder();
          holder.idTextView = (TextView) convertView.findViewById(R.id.campaign_id_text_view);
          holder.byLineTextView = (TextView) convertView.findViewById(R.id.campaign_by_line);
          holder.campaignTextContainer = (LinearLayout) convertView.findViewById(R.id.campaign_text_container);
          holder.infoArrow = (ImageView) convertView.findViewById(R.id.info_button);

          convertView.setTag(holder);
        } else {
          holder = (ViewHolder) convertView.getTag();
        }

        // Mark the view if the campaign is joined
        if (currentlyMarkedCampaign == campaignObject.getInt("id")) {
          //holder.campaignTextContainer.setBackgroundColor(getResources().getColor(R.color.light_blue_light));
          holder.idTextView.setTextColor(getResources().getColor(R.color.light_blue));
          holder.byLineTextView.setTextColor(getResources().getColor(R.color.light_blue));
          holder.infoArrow.setColorFilter(getResources().getColor(R.color.light_blue));


        } else {
          holder.idTextView.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
          holder.byLineTextView.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
          holder.infoArrow.setColorFilter(getResources().getColor(R.color.abc_primary_text_material_light));
        }

        Log.d("data", campaignObject.toString());
        holder.idTextView.setText(campaignObject.getString("name"));
        holder.byLineTextView.setText(campaignObject.getString("user"));
        holder.campaignTextContainer.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            try {
              final CampaignJoinFragment fragment = CampaignJoinFragment.newInstance(campaignObject.getLong("id"));

              getFragmentManager().beginTransaction()
                  .replace(R.id.content_frame_layout, fragment, JOIN_CAMPAIGN_FRAGMENT_KEY)
                  .addToBackStack(JOIN_CAMPAIGN_FRAGMENT_KEY)
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