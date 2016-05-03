package dk.aau.sw808f16.datacollection.backgroundservice;

import dk.aau.sw808f16.datacollection.campaign.Campaign;

public interface ConfigurationResponder {
  boolean notifyNewCampaign(Campaign campaign);
}
