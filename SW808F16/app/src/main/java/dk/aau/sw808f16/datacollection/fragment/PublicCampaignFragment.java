package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

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

public class PublicCampaignFragment extends Fragment {

  private static final String campaignListResourcePath = "https://dev.local.element67.dk:8000/campaigns";

  public Menu menu;

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
  }

  @Override
  public void onResume() {
    super.onResume();

    try {

      final AsyncHttpTask<Void, Void> task = new AsyncHttpTask<Void, Void>(getActivity(), new URL(campaignListResourcePath), HttpURLConnection.HTTP_OK) {
        @Override
        protected void onResponseCodeMatching(final InputStream in) {


          try {
            final JSONArray data = new JSONArray(convertInputStreamToString(in));


          } catch (JSONException e) {
            e.printStackTrace();
          }

        }

        @Override
        protected void onResponseCodeNotMatching(final int responseCode) {

        }
      };

      task.execute(null, null);

    } catch (MalformedURLException e) {
      e.printStackTrace();
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
    return inflater.inflate(R.layout.fragment_public_campaign, container, false);
  }

  class JSONCampaingsAdapter extends BaseAdapter {

    private JSONArray data;

    JSONCampaingsAdapter(final JSONArray data) {
      this.data = data;
    }

    @Override
    public int getCount() {
      return data.length();
    }

    @Override
    public Object getItem(final int position) {
      try {
        return data.get(position);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    public long getItemId(final int position) {
      try {
        return data.getJSONObject(position).getLong("id");
      } catch (JSONException e) {
        e.printStackTrace();
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
      }
      else {
        holder = (ViewHolder) convertView.getTag();
      }

      try {
        holder.idTextView.setText("" + campaignObject.getLong("id"));
        holder.titleTextView.setText("" + campaignObject.getLong("title"));

      } catch (JSONException e) {
        e.printStackTrace();
      }


      //holder.campaignCheckBox;

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
    } catch (IOException e) {
      e.printStackTrace();

      return "";
    }

    return total.toString();
  }
}
