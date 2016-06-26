package dk.aau.sw808f16.wekatest.campaign;

import android.test.ApplicationTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw808f16.wekatest.DataCollectionApplication;
import dk.aau.sw808f16.wekatest.SensorType;
import dk.aau.sw808f16.wekatest.questionaire.models.Question;
import dk.aau.sw808f16.wekatest.questionaire.models.Questionnaire;
import dk.aau.sw808f16.wekatest.snapshot.Snapshot;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class CampaignTest extends ApplicationTestCase<DataCollectionApplication> {

  public CampaignTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Campaign();
    new Campaign(2);
  }

  public void testExtendsRealmObject() {
    assertTrue(Campaign.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Campaign.class));
  }

  public void testAddSnapshot() {
    final Campaign campaign = new Campaign(1);
    final Snapshot snapshot = new Snapshot();

    campaign.addSnapshot(snapshot);

    assertTrue(campaign.getSnapshots().contains(snapshot));
  }

  public void testSetIdentifier() {
    final int expectedIdentifier = 10;
    final Campaign campaign = new Campaign(expectedIdentifier - 1);

    campaign.setIdentifier(expectedIdentifier);

    assertEquals(expectedIdentifier, campaign.getIdentifier());
  }

  public void testEqualsNull() {
    final Campaign campaign = new Campaign(2);

    assertNotSame(campaign, null);
  }

  public void testEqualsSameValues() {
    final Campaign campaign1 = new Campaign(1);
    final Campaign campaign2 = new Campaign(1);

    assertEquals(campaign1, campaign2);
  }

  public void testEqualsSameValuesWithSnapshots() {
    final Campaign campaign1 = new Campaign(1);
    final Campaign campaign2 = new Campaign(1);

    final Snapshot snapshot = new Snapshot();

    campaign1.addSnapshot(snapshot);
    campaign2.addSnapshot(snapshot);

    assertEquals(campaign1, campaign2);
  }

  public void testEqualsDifferentValues() {
    final Campaign campaign1 = new Campaign(1);
    final Campaign campaign2 = new Campaign(2);

    assertNotSame(campaign1, campaign2);
  }

  public void testEqualsSameReference() {
    final Campaign campaign1 = new Campaign(1);
    final Campaign campaign2 = campaign1;

    assertEquals(campaign1, campaign2);
  }

  public void testSaveToRealm() {
    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_campaign.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Campaign campaign = new Campaign(1);
    campaign.setPrivate(true);
    campaign.setName("name");
    campaign.setDescription("description");
    campaign.setSnapshotLength(10);
    campaign.setSampleDuration(10);
    campaign.setSampleFrequency(10);
    campaign.setMeasurementFrequency(10);
    campaign.setCampaignLength(10);
    campaign.setQuestionnairePlacement(QuestionnairePlacement.END);
    campaign.setSensors(new ArrayList<SensorType>() {
      {
        add(SensorType.AMBIENT_LIGHT);
        add(SensorType.ACCELEROMETER);
      }
    });

    List<Question> questions = new ArrayList<>();
    questions.add(new Question("Er du okay?"));
    questions.add(new Question("Er du okay, Annie?"));

    campaign.setQuestionnaire(new Questionnaire(questions));

    realm.beginTransaction();
    realm.copyToRealm(campaign);
    realm.commitTransaction();

    final Campaign loadedCampaign = realm.where(Campaign.class).findFirst();

    assertEquals(campaign, loadedCampaign);

    realm.close();
    Realm.deleteRealm(realmConfiguration);
  }

  public void testCanBecomeJson() throws JSONException {
    final int identifier = 2;

    final Campaign campaign = new Campaign(identifier);

    final JSONObject campaignJsonObject = campaign.toJsonObject();
    final String campaignJsonObjectString = campaignJsonObject.toString();

    assertNotNull(campaignJsonObjectString);

    assertTrue(campaignJsonObjectString.contains("snapshots"));
  }

  public void testSetName() {
    Campaign campaign = new Campaign();

    String expected = "hej";
    campaign.setName(expected);

    assertEquals(expected, campaign.getName());
  }

  public void testSetDescription() {
    Campaign campaign = new Campaign();

    String expected = "hej";
    campaign.setDescription(expected);

    assertEquals(expected, campaign.getDescription());
  }

  public void testSetIsPrivate() {
    Campaign campaign = new Campaign();

    campaign.setPrivate(true);

    assertTrue(campaign.isPrivate());
  }

  public void testSetSnapshotLength() {
    Campaign campaign = new Campaign();

    int expected = 1;
    campaign.setSnapshotLength(expected);

    assertEquals(expected, campaign.getSnapshotLength());
  }

  public void testSetSampleDuration() {
    Campaign campaign = new Campaign();

    int expected = 1;
    campaign.setSampleDuration(expected);

    assertEquals(expected, campaign.getSampleDuration());
  }

  public void testSetSampleFrequency() {
    Campaign campaign = new Campaign();

    int expected = 1;
    campaign.setSampleFrequency(expected);

    assertEquals(expected, campaign.getSampleFrequency());
  }

  public void testSetMeasurementFrequency() {
    Campaign campaign = new Campaign();

    int expected = 1;
    campaign.setMeasurementFrequency(expected);

    assertEquals(expected, campaign.getMeasurementFrequency());
  }

  public void testSetSensors() {
    Campaign campaign = new Campaign();

    ArrayList<SensorType> expected = new ArrayList<>();
    expected.add(SensorType.ACCELEROMETER);
    expected.add(SensorType.AMBIENT_LIGHT);

    campaign.setSensors(expected);

    assertEquals(expected, campaign.getSensors());
  }

  public void testSetQuestionnaire() {
    Campaign campaign = new Campaign();

    ArrayList<Question> questions = new ArrayList<Question>() {
      {
        add(new Question("Er du god?"));
        add(new Question("Er du dårlig?"));
      }
    };
    Questionnaire expected = new Questionnaire(questions);


    campaign.setQuestionnaire(expected);

    assertEquals(expected, campaign.getQuestionnaire());
  }

  public void testSetGetCampaignLength() {
    Campaign campaign = new Campaign();

    int expected = 1;
    campaign.setCampaignLength(expected);

    assertEquals(expected, campaign.getCampaignLength());
  }

  public void testSetGetQuestionnairePlacement() {
    Campaign campaign = new Campaign();

    QuestionnairePlacement expected = QuestionnairePlacement.END;
    campaign.setQuestionnairePlacement(expected);

    assertEquals(expected, campaign.getQuestionnairePlacement());
  }
}