package dk.aau.sw808f16.datacollection.campaign;

import android.content.Context;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;

public abstract class AsyncHttpGetCampaignSpecificationTask extends AsyncHttpWebbTask<JSONObject> {

  public AsyncHttpGetCampaignSpecificationTask(final Context context, final long campaignId) {

    super(AsyncHttpWebbTask.Method.GET,
        RequestHostResolver.resolveHostForRequest(context, "/campaigns/" + campaignId), HttpURLConnection.HTTP_OK, context);
  }

  @Override
  protected Response<JSONObject> sendRequest(Request webb) {
    return webb.asJsonObject();
  }

  @Override
  public void onResponseCodeMatching(final Response<JSONObject> response) {
    try {
      final Campaign campaign = new Campaign(response.getBody());
      campaign.log("CampaignSpecification");

    } catch (JSONException exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void onResponseCodeNotMatching(final Response<JSONObject> response) {
    Log.d("CampaignSpecification", "Did not get the correct response code");
  }

  @Override
  public void onConnectionFailure() {
    Log.d("CampaignSpecification", "Unable to connect to the server");
  }

}