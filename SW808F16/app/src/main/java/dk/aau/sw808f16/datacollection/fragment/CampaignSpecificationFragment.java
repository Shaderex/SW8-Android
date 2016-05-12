package dk.aau.sw808f16.datacollection.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.goebl.david.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.aau.sw808f16.datacollection.R;
import dk.aau.sw808f16.datacollection.SensorType;
import dk.aau.sw808f16.datacollection.campaign.AsyncHttpGetCampaignSpecificationTask;
import dk.aau.sw808f16.datacollection.campaign.Campaign;
import dk.aau.sw808f16.datacollection.questionaire.models.Question;

public class CampaignSpecificationFragment extends Fragment {

  public Menu menu;

  public static final String CAMPAIGN_ID_TAG = "CAMPAIGN_ID_TAG";
  public static final String CAMPAIGN_JSON_TAG = "CAMPAIGN_JSON_TAG";

  public static CampaignSpecificationFragment newInstance(final long campaignIdentifier) {
    final CampaignSpecificationFragment newFragment = new CampaignSpecificationFragment();

    final Bundle args = new Bundle();
    args.putLong(CAMPAIGN_ID_TAG, campaignIdentifier);
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

  private void updateHeadline(final View parent, final int headlineResourceIdentifier, final String headline, final String description) {
    final LinearLayout headlineView = (LinearLayout) parent.findViewById(headlineResourceIdentifier);

    final TextView headlineTextView = (TextView) headlineView.findViewById(R.id.specification_headline);
    headlineTextView.setText(headline);

    final TextView descriptionTextView = (TextView) headlineView.findViewById(R.id.specification_description);
    descriptionTextView.setText(description);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    final Bundle arguments = getArguments();

    if (arguments == null) {
      return null;
    }

    final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_campaign_specification, container, false);
    final ContentLoadingProgressBar activityIndicator = (ContentLoadingProgressBar) view.findViewById(R.id.activity_indicator);

    // Set headlines
    updateHeadline(view, R.id.information_gathered_headline, "Information Gathered", "The following information will be collected");
    updateHeadline(view, R.id.measurements_headline, "Measurements", "Will affect the battery consumption");
    updateHeadline(view, R.id.questions_headline, "Questions", "You will be asked questions such as");

    activityIndicator.show();

    if (arguments.containsKey(CAMPAIGN_ID_TAG)) {
      final long campaignId = arguments.getLong(CAMPAIGN_ID_TAG);

      final AsyncHttpGetCampaignSpecificationTask task = new AsyncHttpGetCampaignSpecificationTask(getActivity(), campaignId) {
        @Override
        public void onResponseCodeMatching(Response<JSONObject> response) {
          super.onResponseCodeMatching(response);
          view.setVisibility(View.VISIBLE);
          updateCampaignSpecification(view, response.getBody());
          activityIndicator.hide();
        }

        @Override
        public void onResponseCodeNotMatching(final Response<JSONObject> response) {
          super.onResponseCodeNotMatching(response);

          view.setVisibility(View.VISIBLE);

          final View getView = getView();
          final TextView errorMessageTextView = getView == null ? null : (TextView) getView.findViewById(R.id.error_message_textview);
          final ScrollView scrollingView = getView == null ? null : (ScrollView) getView.findViewById(R.id.specification_scroller);

          activityIndicator.hide();

          switch (response.getStatusCode()) {
            case HttpURLConnection.HTTP_NOT_FOUND: {
              if (errorMessageTextView != null) {
                errorMessageTextView.setText(R.string.unable_to_locate_campaign_message);
              }
              break;
            }
            default: {
              if (errorMessageTextView != null) {
                errorMessageTextView.setText(R.string.generic_something_went_wrong_message);
              }
              break;
            }
          }

          if (errorMessageTextView != null) {
            errorMessageTextView.setVisibility(View.VISIBLE);
          }

          if (scrollingView != null) {
            scrollingView.setVisibility(View.GONE);
          }
        }
      };

      view.setVisibility(View.INVISIBLE);
      task.execute();

    } else if (arguments.containsKey(CAMPAIGN_JSON_TAG)) {
      try {
        updateCampaignSpecification(view, new JSONObject(arguments.getString(CAMPAIGN_JSON_TAG)));
        activityIndicator.hide();
      } catch (JSONException exception) {
        exception.printStackTrace();
      }
    } else {
      return null;
    }

    return view;
  }

  private void updateCampaignSpecification(final ViewGroup parent, final JSONObject campaignSpecification) {

    try {
      final Campaign campaign = new Campaign(campaignSpecification);

      final String campaignTitle = campaign.getName();
      final TextView campaignTitleTextView = (TextView) parent.findViewById(R.id.campaign_specification_title);
      campaignTitleTextView.setText(campaignTitle);

      final String campaignAuthor = campaignSpecification.getJSONObject("user").getString("name");
      final TextView campaignAuthorTextView = (TextView) parent.findViewById(R.id.campaign_specification_by_line);
      campaignAuthorTextView.setText("by " + campaignAuthor);

      final String campaignDescription = campaign.getDescription();
      final TextView campaignDescriptionTextView = (TextView) parent.findViewById(R.id.campaign_specification_description);
      campaignDescriptionTextView.setText(campaignDescription);

      final HashMap<SensorType.SensorCategory, List<SensorType>> sensorsUsed = new HashMap<>();
      for (final SensorType sensor : campaign.getSensors()) {
        if (sensorsUsed.get(sensor.getCategory()) == null) {
          sensorsUsed.put(sensor.getCategory(), new ArrayList<SensorType>());
        }
        sensorsUsed.get(sensor.getCategory()).add(sensor);
      }

      if (sensorsUsed.isEmpty()) {
        parent.findViewById(R.id.measurements_headline).setVisibility(View.GONE);
      }

      updateCampaignMeasurementsCategory(parent,
          R.id.measurement_category_location,
          R.drawable.ic_room_black_24dp,
          "Location",
          sensorsUsed.get(SensorType.SensorCategory.LOCATION));

      updateCampaignMeasurementsCategory(parent,
          R.id.measurement_category_movement,
          R.drawable.ic_directions_run_black_24dp,
          "Movement",
          sensorsUsed.get(SensorType.SensorCategory.MOVEMENT));

      updateCampaignMeasurementsCategory(parent,
          R.id.measurement_category_personal_information,
          R.drawable.ic_favorite_black_24dp,
          "Personal Information",
          sensorsUsed.get(SensorType.SensorCategory.PERSONAL_INFORMATION));

      updateCampaignMeasurementsCategory(parent,
          R.id.measurement_category_misc,
          R.drawable.ic_polymer_black_24dp,
          "Miscellaneous",
          sensorsUsed.get(SensorType.SensorCategory.MISC));

      final int sampleDuration = campaignSpecification.getInt("sample_duration");
      final int sampleFrequency = campaignSpecification.getInt("sample_frequency");
      final int measurementFrequency = campaignSpecification.getInt("measurement_frequency");
      final int measurementsPerHour =
          (int) ((double) 3600000 / (double) sampleFrequency * (double) sampleDuration / (double) measurementFrequency);

      final TextView measurementsRateTextView = (TextView) parent.findViewById(R.id.measurements_rate);
      measurementsRateTextView.setText(measurementsPerHour + " measurements per hour");

      final TextView QuestionnaireInterval = (TextView) parent.findViewById(R.id.questions_questionnaire_interval);
      QuestionnaireInterval.setText("You will asked questions every "
          + snapshotLengthToString(campaignSpecification.getInt("snapshot_length")));

      final LinearLayout lv = (LinearLayout) parent.findViewById(R.id.fragment_campaign_specification_questions_listing);

      final List<String> questions = new ArrayList<>();
      int questionCounter = 0;
      for (Question question : campaign.getQuestionnaire().getQuestions()) {
        questionCounter++;
        questions.add(question.getQuestion());
        if (questionCounter > 2) {
          break;
        }
      }

      if (questions.isEmpty()) {
        parent.findViewById(R.id.questions_headline).setVisibility(View.GONE);
      }

      final ArrayAdapter<String> arrayAdapter =
          new ArrayAdapter<>(parent.getContext(), R.layout.fragment_campaign_specification_question_item, questions);
      final LinearLayout.LayoutParams layoutParams =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

      for (int counter = 0; counter < arrayAdapter.getCount(); counter++) {
        lv.addView(arrayAdapter.getView(counter, null, parent), layoutParams);
      }

    } catch (JSONException exception) {
      exception.printStackTrace();
    }
  }

  private String snapshotLengthToString(final int timestamp) {
    final int msPerMinute = (60 * 1000);
    final int msPerHour = (60 * msPerMinute);
    final int hours = timestamp / msPerHour;
    final int restOfHours = timestamp % msPerHour;
    final int minutes = (restOfHours / msPerMinute) == 0 && hours == 0 ? 1 : (restOfHours / msPerMinute);

    String str = "";

    if (hours > 0) {
      str += hours;
      str += "h";
      str += " ";
    }

    if (minutes > 0) {
      str += minutes;
      str += "m";
    }

    return str;
  }

  private void updateCampaignMeasurementsCategory(final View parent,
                                                  final int categoryResource,
                                                  final int indicatorResource,
                                                  final String headline,
                                                  final List<SensorType> sensors) {

    final Context context = parent.getContext();

    final LinearLayout categoryView = (LinearLayout) parent.findViewById(categoryResource);

    // If no description is provided, do not display the view
    if (sensors == null || sensors.size() == 0) {
      categoryView.setVisibility(View.GONE);
      return;
    }

    final ImageView indicatorImageView = (ImageView) categoryView.findViewById(R.id.category_indicator);
    indicatorImageView.setImageDrawable(context.getResources().getDrawable(indicatorResource));

    final TextView headlineTextView = (TextView) categoryView.findViewById(R.id.category_headline);
    headlineTextView.setText(headline);

    final TextView descriptionTextView = (TextView) categoryView.findViewById(R.id.category_description);
    String sensorString = "";
    for (int i = 0; i < sensors.size(); i++) {
      SensorType type = sensors.get(i);
      sensorString += type.toString();
      if (i != sensors.size() - 1) {
        sensorString += ", ";
      }
    }
    descriptionTextView.setText(sensorString);
  }
}
