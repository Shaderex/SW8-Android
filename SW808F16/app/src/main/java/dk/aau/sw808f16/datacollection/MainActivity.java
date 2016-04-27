package dk.aau.sw808f16.datacollection;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.HeartRateConsentListener;

import dk.aau.sw808f16.datacollection.fragment.CampaignSpecificationFragment;
import dk.aau.sw808f16.datacollection.fragment.PrivateCampaignFragment;
import dk.aau.sw808f16.datacollection.fragment.PublicCampaignFragment;
import dk.aau.sw808f16.datacollection.fragment.StartFragment;

public class MainActivity extends Activity implements HeartRateConsentListener {

  public enum DrawerMenuItems {

    CURRENT_CAMPAIGN("Current campaign") {
      @Override
      void open(final MainActivity activity) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        final long currentlyActiveCampaignId = preferences.getLong(activity.getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), -1);

        if (currentlyActiveCampaignId != -1) {

          final Fragment fragment = CampaignSpecificationFragment.newInstance(currentlyActiveCampaignId);
          activity.setContent(fragment);

        } else {
          Toast.makeText(activity, "You are not subscribed to any campaign", Toast.LENGTH_LONG).show();
        }
      }
    },
    PUBLIC_CAMPAIGNS("Public Campaigns") {
      @Override
      void open(final MainActivity activity) {

        final Fragment fragment = PublicCampaignFragment.newInstance();
        activity.setContent(fragment);
      }
    },
    PRIVATE_CAMPAIGNS("Private Campaigns") {
      @Override
      void open(final MainActivity activity) {

        final Fragment fragment = PrivateCampaignFragment.newInstance();
        activity.setContent(fragment);
      }
    };

    public final String name;

    DrawerMenuItems(final String string) {
      this.name = string;
    }

    abstract void open(final MainActivity activity);

    @Override
    public String toString() {
      return name;
    }
  }

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

    final Thread getConsent = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          getConnectedBandClient();

          if (bandClient != null && bandClient.getSensorManager().getCurrentHeartRateConsent() != UserConsent.GRANTED) {
            // user has not consented, request consent
            // the calling class is an Activity and implements
            // HeartRateConsentListener
            bandClient.getSensorManager().requestHeartRateConsent(MainActivity.this, MainActivity.this);
          }

          bandClient = null;
        } catch (InterruptedException | BandException exception) {
          exception.printStackTrace();
        }
      }
    });

    getConsent.start();

    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawerToggle = new ActionBarDrawerToggle(
        this,                  /* host Activity */
        drawerLayout,         /* DrawerLayout object */
        R.string.open,  /* "open drawer" description */
        R.string.close  /* "close drawer" description */
    ) {

      @Override
      public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
      }

      /** Called when a drawer has settled in a completely closed state. */
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
      }

      /** Called when a drawer has settled in a completely open state. */
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
      }
    };

    // Set the drawer toggle as the DrawerListener
    drawerLayout.setDrawerListener(drawerToggle);
    drawerLayout.setScrimColor(Color.TRANSPARENT);

    final ListView listView = (ListView) drawerLayout.findViewById(R.id.left_drawer);
    listView.setAdapter(new DrawerButtonsAdapter());

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        drawerLayout.closeDrawers();
        ((DrawerMenuItems) (parent.getAdapter()).getItem(position)).open(MainActivity.this);

      }
    });

    if (getActionBar() != null) {
      getActionBar().setDisplayHomeAsUpEnabled(true);
      getActionBar().setHomeButtonEnabled(true);
    }
  }

  public class DrawerButtonsAdapter extends BaseAdapter {

    private final DrawerMenuItems[] items = DrawerMenuItems.values();

    @Override
    public int getCount() {
      return items.length;
    }

    @Override
    public Object getItem(final int position) {
      return items[position];
    }

    @Override
    public long getItemId(final int position) {
      return position;
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

    class ViewHolder {
      TextView menuNameTextView;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

      final DrawerMenuItems item = items[position];

      final ViewHolder holder;

      if (convertView == null) {

        convertView = getLayoutInflater().inflate(R.layout.drawer_menu_item, null);

        holder = new ViewHolder();
        holder.menuNameTextView = (TextView) convertView.findViewById(R.id.menu_item_text_view);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      holder.menuNameTextView.setText(item.name);

      return convertView;
    }
  }

  @Override
  protected void onPostCreate(final Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(final Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    // Pass the event to ActionBarDrawerToggle, if it returns
    // true, then it has handled the app icon touch event
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    super.onCreateOptionsMenu(menu);

    return true;
  }

  protected void setContent(final Fragment fragment) {
    final FragmentManager fm = getFragmentManager();

    fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fm.popBackStackImmediate();

    fm.beginTransaction().replace(R.id.content_frame_layout, fragment).addToBackStack(null).commit();

    drawerLayout.closeDrawers();
  }

  @Override
  public void userAccepted(boolean accepted) {
    // handle user's heart rate consent decision
  }

  private BandClient bandClient;

  protected boolean getConnectedBandClient() throws InterruptedException, BandException {
    if (bandClient == null) {
      BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
      if (devices.length == 0) {
        Log.d("Band2", "Band isn't paired with your phone (from " + this.getClass().getName() + ").");
        return false;
      }
      bandClient = BandClientManager.getInstance().create(this, devices[0]);
    } else if (ConnectionState.CONNECTED == bandClient.getConnectionState()) {
      return true;
    }

    Log.d("Band2", "Band is connecting...  (from " + this.getClass().getName() + ")");
    final ConnectionState result = bandClient.connect().await();

    Log.d("Band2", "Band connection status: " + result + " (from " + this.getClass().getName() + ")");

    return ConnectionState.CONNECTED == result;
  }
}
