package dk.aau.sw808f16.datacollection.campaign;

import android.content.Context;
import android.widget.Toast;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.WebUtil.AsyncHttpWebbTask;

/**
 * Created by Marhlder on 07-Apr-16.
 */
public class AsyncHttpCampaignJoinTask extends AsyncHttpWebbTask<JSONObject> {

  final WeakReference<Context> weakContextReference;
  final long campaignIdToRegister;

  public AsyncHttpCampaignJoinTask(final Context context, final long campaignIdToRegister) {
    super(AsyncHttpWebbTask.Method.POST, "https://dev.local.element67.dk:8000/campaigns/join", HttpURLConnection.HTTP_OK);

    weakContextReference = new WeakReference<>(context);
    this.campaignIdToRegister = campaignIdToRegister;
  }

  @Override
  protected Response<JSONObject> sendRequest(Request request) {

    final Context context = weakContextReference.get();

    if (context != null) {
      try {
        final InstanceID instanceID = InstanceID.getInstance(context);
        final String token = instanceID.getToken(
            context.getString(R.string.defaultSenderID),
            GoogleCloudMessaging.INSTANCE_ID_SCOPE,
            null
        );
        return request
            .param("device_id", token)
            .param("campaign_id", campaignIdToRegister)
            .retry(2, false)
            .asJsonObject();

      } catch (IOException exception) {
        exception.printStackTrace();
        return null;
      }
    }
    return null;
  }

  @Override
  public void onResponseCodeMatching(final Response<JSONObject> response) {

    final Context context = weakContextReference.get();
    if (context != null) {
      try {
        Toast.makeText(context, response.getBody().getString("message"), Toast.LENGTH_SHORT).show();
      } catch (JSONException exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public void onResponseCodeNotMatching(final Response<JSONObject> response) {
    final Context context = weakContextReference.get();
    if (context != null) {
      Toast.makeText(context, response.getErrorBody().toString(), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onConnectionFailure() {
    final Context context = weakContextReference.get();
    if (context != null) {
      Toast.makeText(context, "Could not connect", Toast.LENGTH_SHORT).show();
    }
  }
}
