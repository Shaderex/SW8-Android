package dk.aau.sw808f16.wekatest;

import android.content.ComponentName;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

  public MainActivityTest() {
    super(MainActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
    setActivityContext(context);

    final ComponentName componentName = new ComponentName("dk.aau.sw808f16.wekatest", "MainActivity");
    final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    startActivity(mainActivityIntent, null, null);
  }

  public void testHasNavigationButtons() {

    final ListView listView = (ListView) getActivity().findViewById(R.id.left_drawer);

    final MainActivity.DrawerMenuItems items[] = MainActivity.DrawerMenuItems.values();

    for(int childCounter = 0; childCounter < listView.getChildCount(); childCounter++)
    {
      final TextView textView = (TextView) listView.getChildAt(childCounter).findViewById(R.id.menu_item_text_view);
      assertTrue("Item names in listview should match DrawerMenuItems enum",  items[childCounter].name.equals(textView.getText().toString()));
    }
  }
}
