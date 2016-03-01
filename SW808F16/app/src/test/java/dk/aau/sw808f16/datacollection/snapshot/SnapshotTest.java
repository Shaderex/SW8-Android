package dk.aau.sw808f16.datacollection.snapshot;

import junit.framework.Assert;

import org.junit.Test;

import dk.aau.sw808f16.datacollection.label.Label;

public class SnapshotTest {

  @Test
  public void testConstructor() {
    new Snapshot<>();
  }

  @Test
  public void testGetSetLabel() {
    final Label expectedLabel = new Label();
    final Snapshot<Object> snapshot = new Snapshot<>();

    snapshot.setLabel(expectedLabel);

    Assert.assertEquals(expectedLabel, snapshot.getLabel());
  }

  @Test
  public void testSetDataCase1() {
    final Snapshot<Integer> snapshot = new Snapshot<>();
    final Integer expectedData = 42;

    snapshot.setData(expectedData);

    Assert.assertEquals(expectedData, snapshot.getData());
  }

  @Test
  public void testSetDataCase2() {
    final Snapshot<String> snapshot = new Snapshot<>();
    final String expectedData = "hej";

    snapshot.setData(expectedData);

    Assert.assertEquals(expectedData, snapshot.getData());
  }

  @Test
  public void testSetDataCase3() {
    final Snapshot<Float[]> snapshot = new Snapshot<>();
    final Float[] expectedData = new Float[]{ -1f, 0f, 1f, 2f, 3f, 3.14f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY };

    snapshot.setData(expectedData);

    Assert.assertEquals(expectedData, snapshot.getData());
  }

}
