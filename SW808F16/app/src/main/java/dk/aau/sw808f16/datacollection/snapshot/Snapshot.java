package dk.aau.sw808f16.datacollection.snapshot;

import dk.aau.sw808f16.datacollection.label.Label;

public class Snapshot<DataType> {

  private Label label;
  private DataType data;

  public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public DataType getData() {
    return data;
  }

  public void setData(DataType data) {
    this.data = data;
  }
}
