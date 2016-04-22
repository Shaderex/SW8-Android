package dk.aau.sw808f16.datacollection;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import dk.aau.sw808f16.datacollection.fragment.StartFragment;

public class MainActivity extends Activity {

  private static final String START_FRAGMENT_KEY = "START_FRAGMENT_KEY";

  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle drawerToggle;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final FragmentManager fragmentManager = getFragmentManager();

    fragmentManager.beginTransaction()
        .replace(R.id.content_frame_layout, StartFragment.newInstance(), START_FRAGMENT_KEY).commit();

    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawerToggle = new ActionBarDrawerToggle(
        this,                  /* host Activity */
        drawerLayout,         /* DrawerLayout object */
        R.drawable.ic_list_white_48dp,  /* nav drawer icon to replace 'Up' caret */
        R.string.open,  /* "open drawer" description */
        R.string.close  /* "close drawer" description */
    ) {

      /** Called when a drawer has settled in a completely closed state. */
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        //getActionBar().setTitle("pet");
      }

      /** Called when a drawer has settled in a completely open state. */
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        //getActionBar().setTitle("haps");
      }
    };

    // Set the drawer toggle as the DrawerListener
    drawerLayout.setDrawerListener(drawerToggle);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Pass the event to ActionBarDrawerToggle, if it returns
    // true, then it has handled the app icon touch event
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    // Handle your other action bar items...

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);


    return true;
  }
}
