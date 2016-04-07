package dk.aau.sw808f16.datacollection.campaign;

import android.test.ApplicationTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.snapshot.Snapshot;
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

    realm.beginTransaction();
    realm.copyToRealm(campaign);
    realm.commitTransaction();

    final Campaign loadedCampaign = realm.where(Campaign.class).findFirst();

    assertEquals(campaign, loadedCampaign);

    realm.close();
  }

  public void testCanBecomeJson() throws JSONException {
    final int identifier = 2;

    final Campaign campaign = new Campaign(identifier);

    final JSONObject campaignJsonObject = campaign.toJsonObject();
    final String campaignJsonObjectString = campaignJsonObject.toString();

    assertNotNull(campaignJsonObjectString);

    assertTrue(campaignJsonObjectString.contains("snapshots"));
  }

}