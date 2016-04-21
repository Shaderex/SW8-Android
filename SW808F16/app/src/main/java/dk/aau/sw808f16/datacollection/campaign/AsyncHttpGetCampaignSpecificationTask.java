package dk.aau.sw808f16.datacollection.campaign;

import android.content.Context;
import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Response;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;
import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;

public abstract class AsyncHttpGetCampaignSpecificationTask extends AsyncHttpWebbTask<JSONObject> {


  public AsyncHttpGetCampaignSpecificationTask(final Context context, final long campaignId) {

    super(AsyncHttpWebbTask.Method.GET, RequestHostResolver.resolveHostForRequest(context, "/campaigns/" + campaignId), 200);
  }

  @Override
  protected Response<JSONObject> sendRequest(Request webb) {
    return webb.asJsonObject();
  }

  @Override
  public void onResponseCodeMatching(Response<JSONObject> response) {
    try {
      Campaign campaign = new Campaign(response.getBody());

      Log.d("CampaignSpecification", "Campaign Specification Retrieved");
      Log.d("CampaignSpecification", "name: " + campaign.getName());
      Log.d("CampaignSpecification", "description: " + campaign.getDescription());
      Log.d("CampaignSpecification", "private: " + campaign.isPrivate());
      Log.d("CampaignSpecification", "sensors: " + campaign.getSensors());
      Log.d("CampaignSpecification", "snapshotLength: " + campaign.getSnapshotLength());
      Log.d("CampaignSpecification", "sampleDuration: " + campaign.getSampleDuration());
      Log.d("CampaignSpecification", "sampleFrequency: " + campaign.getSampleFrequency());
      Log.d("CampaignSpecification", "measurementFrequency: " + campaign.getMeasurementFrequency());

      String questions = "";
      for (Question question : campaign.getQuestionnaire().getQuestions()) {
        questions += question.getQuestion() + ",";
      }
      Log.d("CampaignSpecification", "questions: " + questions);

      onResult(campaign);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onResponseCodeNotMatching(Response<JSONObject> response) {
    Log.d("CampaignSpecification", "Did not get the correct response code");
  }

  @Override
  public void onConnectionFailure() {
    Log.d("CampaignSpecification", "Unable to connect to the server");
  }

  public abstract void onResult(Campaign campaign);
}