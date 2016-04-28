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
import dk.aau.sw808f16.datacollection.webutil.AsyncHttpWebbTask;
import dk.aau.sw808f16.datacollection.webutil.RequestHostResolver;
import io.realm.Realm;
import io.realm.RealmResults;

public class AsyncHttpCampaignJoinTask extends AsyncHttpWebbTask<JSONObject> {

  final WeakReference<Context> weakContextReference;
  final long campaignIdToRegister;

  public AsyncHttpCampaignJoinTask(final Context context, final long campaignIdToRegister) {
    super(AsyncHttpWebbTask.Method.POST, RequestHostResolver.resolveHostForRequest(context, "/campaigns/join"), HttpURLConnection.HTTP_OK);

    weakContextReference = new WeakReference<>(context);
    this.campaignIdToRegister = campaignIdToRegister;
  }

  @Override
  protected Response<JSONObject> sendRequest(Request request) {

    final Context context = weakContextReference.get();

    if (context != null) {
      try {
        final InstanceID instanceId = InstanceID.getInstance(context);
        final String token = instanceId.getToken(
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
        Campaign campaign = new Campaign(response.getBody());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Campaign> results = realm.where(Campaign.class).findAll();
        results.clear();
        realm.copyToRealm(campaign);
        realm.commitTransaction();

        // Manual testing logging
        campaign.log("SavedCampaign");

        realm.close();

        Toast.makeText(context, R.string.campaign_joined_message, Toast.LENGTH_SHORT).show();
      } catch (JSONException exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public void onResponseCodeNotMatching(final Response<JSONObject> response) {
    final Context context = weakContextReference.get();
    if (context != null) {
      Toast.makeText(context, R.string.unable_to_join_campaign_message, Toast.LENGTH_SHORT).show();
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
