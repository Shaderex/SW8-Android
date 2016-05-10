package dk.aau.sw808f16.datacollection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

import dk.aau.sw808f16.datacollection.backgroundservice.BackgroundSensorService;
import dk.aau.sw808f16.datacollection.fragment.CampaignSpecificationFragment;
import dk.aau.sw808f16.datacollection.fragment.PrivateCampaignFragment;
import dk.aau.sw808f16.datacollection.fragment.PublicCampaignFragment;
import dk.aau.sw808f16.datacollection.fragment.StartFragment;
import dk.aau.sw808f16.datacollection.webutil.CampaignRegistrator;

public class MainActivity extends ActionBarActivity implements HeartRateConsentListener, CampaignRegistrator {

  public enum DrawerMenuItems {

    CURRENT_CAMPAIGN(R.drawable.ic_assignment_turned_in_black_24dp, "Current campaign") {
      @Override
      public void open(final MainActivity activity) {

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
    PUBLIC_CAMPAIGNS(R.drawable.ic_public_black_24dp, "Public campaigns") {
      @Override
      public void open(final MainActivity activity) {

        final Fragment fragment = PublicCampaignFragment.newInstance();
        activity.setContent(fragment);
      }
    },
    PRIVATE_CAMPAIGNS(R.drawable.ic_enhanced_encryption_black_24dp, "Private campaigns") {
      @Override
      public void open(final MainActivity activity) {

        final Fragment fragment = PrivateCampaignFragment.newInstance();
        activity.setContent(fragment);
      }
    };

    public final String name;
    public final int iconRessource;

    DrawerMenuItems(final int iconRessource, final String string) {
      this.name = string;
      this.iconRessource = iconRessource;
    }

    public abstract void open(final MainActivity activity);

    @Override
    public String toString() {
      return name;
    }
  }

  private static final String START_FRAGMENT_KEY = "START_FRAGMENT_KEY";

  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle drawerToggle;
  private boolean isBoundToResponder = false;
  private Messenger serviceMessenger = null;


  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    final FragmentManager fragmentManager = getSupportFragmentManager();

    fragmentManager.beginTransaction()
        .replace(R.id.content_frame_layout, StartFragment.newInstance(), START_FRAGMENT_KEY).commit();

    final Thread getConsent = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          getConnectedBandClient();

          Log.d("BAND", "bandClient:" + (bandClient == null ? "NULL" : bandClient.toString()));

          if (bandClient != null && bandClient.getSensorManager().getCurrentHeartRateConsent() != UserConsent.GRANTED) {
            // user has not consented, request consent
            // the calling class is an Activity and implements
            // HeartRateConsentListener
            Log.d("BAND", "ASK FOR CONSENT");
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

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
    }

    bindToResponder();
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
      ImageView menuIcon;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

      final DrawerMenuItems item = items[position];

      final ViewHolder holder;

      if (convertView == null) {

        convertView = getLayoutInflater().inflate(R.layout.drawer_menu_item, null);

        holder = new ViewHolder();
        holder.menuNameTextView = (TextView) convertView.findViewById(R.id.menu_item_text_view);
        holder.menuIcon = (ImageView) convertView.findViewById(R.id.drawer_item_icon);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      holder.menuNameTextView.setText(item.name);
      holder.menuIcon.setImageResource(item.iconRessource);

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

    final FragmentManager fm = getSupportFragmentManager(); // getFragmentManager();

    fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fm.popBackStackImmediate();

    fm.beginTransaction().replace(R.id.content_frame_layout, fragment).addToBackStack(null).commit();

    drawerLayout.closeDrawers();
  }

  @Override
  public void userAccepted(boolean accepted) {
    // handle user's heart rate consent decision
  }

  private void bindToResponder() {
    final Intent serviceIntent = new Intent(this, BackgroundSensorService.class);
    bindService(serviceIntent, serviceConnection, Context.BIND_ABOVE_CLIENT);
  }

  private ServiceConnection serviceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(final ComponentName className, final IBinder binder) {

      // We've bound to LocalService, cast the IBinder and get LocalService instance
      serviceMessenger = new Messenger(binder);
      isBoundToResponder = true;
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
      isBoundToResponder = false;
      serviceMessenger = null;
    }
  };

  public void registerCampaign(final long campaignId) {

    final Messenger replyMessenger = new Messenger(new Handler() {

      @Override
      public void handleMessage(final Message msg) {

        switch (msg.what) {

          case BackgroundSensorService.SERVICE_ACK_BUSY: {

            new Thread(new Runnable() {
              @Override
              public void run() {

                try {
                  Thread.sleep(2000);
                } catch (InterruptedException exception) {
                  exception.printStackTrace();
                }
                registerCampaign(campaignId);
              }
            }).start();
            break;
          }
          case BackgroundSensorService.SERVICE_ACK_OK: {
            final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor.putLong(MainActivity.this.getString(R.string.CURRENTLY_CHECKED_CAMPAIGN_ID_KEY), campaignId);
            editor.commit();

            getFragmentManager().executePendingTransactions();

            final ListView listView = (ListView) findViewById(R.id.campaigns_list_view);
            if (listView != null && listView.getAdapter() != null) {
              ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            }

            break;
          }
        }

      }
    });

    final Message msg = Message.obtain(null, BackgroundSensorService.NOTIFY_NEW_CAMPAIGN, 0, 0);
    Bundle data = new Bundle();
    data.putLong(BackgroundSensorService.NOTIFY_QUESTIONNAIRE_COMPLETED_CAMPAIGN_ID, campaignId);
    msg.setData(data);
    msg.replyTo = replyMessenger;

    try {
      serviceMessenger.send(msg);
    } catch (RemoteException exception) {
      exception.printStackTrace();
    }

    return;
  }

  private BandClient bandClient;

  protected boolean getConnectedBandClient() throws InterruptedException, BandException {
    if (bandClient == null) {
      final BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
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

  @Override
  protected void onStop() {
    super.onStop();

    // Unbind from the service
    if (isBoundToResponder && serviceConnection != null) {
      unbindService(serviceConnection);
      isBoundToResponder = false;
    }
  }
}
