package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

public class SnapshotTest {

  @Test
  public void testConstructor() {
    new Snapshot();
  }

  @Test
  public void testGetSetLabel() {
    final Label expectedLabel = new Label();
    final Snapshot snapshot = new Snapshot();

    snapshot.setLabel(expectedLabel);

    Assert.assertEquals(expectedLabel, snapshot.getLabel());
  }
}
