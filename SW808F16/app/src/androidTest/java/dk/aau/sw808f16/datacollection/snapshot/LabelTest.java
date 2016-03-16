package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.label.Label;

public class LabelTest extends ApplicationTestCase<DataCollectionApplication> {

  public LabelTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Label();
  }

}
