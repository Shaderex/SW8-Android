package dk.aau.sw808f16.datacollection.snapshot;

import android.test.ApplicationTestCase;

import dk.aau.sw808f16.datacollection.DataCollectionApplication;
import dk.aau.sw808f16.datacollection.label.Label;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class LabelTest extends ApplicationTestCase<DataCollectionApplication> {

  public LabelTest() {
    super(DataCollectionApplication.class);
  }

  public void testConstructor() {
    new Label();
    new Label("label");
  }

  public void testExtendsRealmObject() {
    assertTrue(Label.class.getName() + " does not extend " + RealmObject.class.getName(),
        RealmObject.class.isAssignableFrom(Label.class));
  }

  public void testEqualsNull() {
    final Label label = new Label("label");

    assertNotSame(label, null);
  }

  public void testEqualsSameValues() {
    final Label label1 = new Label("label");
    final Label label2 = new Label("label");

    assertEquals(label1, label2);
  }

  public void testEqualsDifferentValues() {
    final Label label1 = new Label("label");
    final Label label2 = new Label("lebal");

    assertNotSame(label1, label2);
  }

  public void testEqualsSameReference() {
    final Label label1 = new Label("label");
    final Label label2 = label1;

    assertEquals(label1, label2);
  }

  public void testSaveToRealm() {

    final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).name("test_label.realm").build();
    final Realm realm = Realm.getInstance(realmConfiguration);

    final Label label = new Label("label");

    realm.beginTransaction();
    realm.copyToRealm(label);
    realm.commitTransaction();

    final Label loadedLabel = realm.where(Label.class).findFirst();

    assertEquals(label, loadedLabel);

    realm.close();
  }


}
