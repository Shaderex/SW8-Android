package dk.aau.sw808f16.datacollection.label;

import io.realm.RealmObject;

public class Label extends RealmObject {
  private String label;

  public Label() {
  }

  public Label(final String label) {
    setLabel(label);
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public boolean equals(final Object object) {
    return super.equals(object) || (object instanceof Label && ((Label) object).label.equals(this.label));
  }
}
