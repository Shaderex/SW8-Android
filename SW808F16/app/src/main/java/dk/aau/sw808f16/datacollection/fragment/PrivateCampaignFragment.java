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

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.WebUtil.AsyncHttpWebbTask;

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

        AsyncHttpWebbTask<JSONObject> task = new AsyncHttpWebbTask<JSONObject>(AsyncHttpWebbTask.Method.POST, "https://dev.local.element67.dk:8000/campaigns/join", HttpURLConnection.HTTP_OK) {
          @Override
          protected Response<JSONObject> sendRequest(Request request) {

            try {
              final InstanceID instanceID = InstanceID.getInstance(getActivity());
              final String token = instanceID.getToken(
                  getString(R.string.defaultSenderID),
                  GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                  null
              );
              return request
                  .param("device_id", token)
                  .param("campaign_id", campaignIdField.getText().toString())
                  .retry(2, false)
                  .asJsonObject();

            } catch (IOException exception) {
              exception.printStackTrace();
              return null;
            }
          }

          @Override
          public void onResponseCodeMatching(final Response<JSONObject> response) {
            try {
              Toast.makeText(getActivity(), response.getBody().getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException exception) {
              exception.printStackTrace();
            }
          }

          @Override
          public void onResponseCodeNotMatching(final Response<JSONObject> response) {
            Toast.makeText(getActivity(), response.getErrorBody().toString(), Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onConnectionFailure() {
            Toast.makeText(getActivity(), "Could not connect", Toast.LENGTH_SHORT).show();
          }
        };

        task.execute();
      }
    });

    return view;
  }
}
