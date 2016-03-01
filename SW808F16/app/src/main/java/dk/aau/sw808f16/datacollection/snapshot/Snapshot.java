package dk.aau.sw808f16.datacollection.snapshot;

import dk.aau.sw808f16.datacollection.label.Label;

public class Snapshot<D> {

  private Label label;
  private D data;

  public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public D getData() {
    return data;
  }

  public void setData(D data) {
    this.data = data;
  }
}
