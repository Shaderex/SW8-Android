package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.DataSetObserver;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.R;

public class PublicCampaignFragmentTest extends ActivityUnitTestCase<MainActivity> {
  public PublicCampaignFragmentTest() {
    super(MainActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);

    setActivityContext(context);

    final ComponentName componentName = new ComponentName("dk.aau.sw808f16.datacollection", "MainActivity");

    final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    startActivity(mainActivityIntent, null, null);
  }

  public void testAddPublicCampaignFragmentToActivity() {

    final String KEY = "KEY";

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PublicCampaignFragment.newInstance(), KEY).commit();
    fragmentManager.executePendingTransactions();

    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);

    Assert.assertEquals(fragment.getClass(), PublicCampaignFragment.class);
  }

  public void testPublicCampaignFragmentHasListView() {

    final String KEY = "KEY";

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PublicCampaignFragment.newInstance(), KEY).commit();
    fragmentManager.executePendingTransactions();

    final Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame_layout);
    final ListView listView = (ListView) fragment.getView().findViewById(R.id.campaigns_list_view);
    assertNotNull(listView);
  }

  public void testPublicCampaignFragmentIsListViewPopulatable() {

    final String KEY = "KEY";

    final FragmentManager fragmentManager = getActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(R.id.content_frame_layout, PublicCampaignFragment.newInstance(), KEY).commit();
    fragmentManager.executePendingTransactions();

    final PublicCampaignFragment fragment = (PublicCampaignFragment) fragmentManager.findFragmentById(R.id.content_frame_layout);
    final ListView listView = (ListView) fragment.getView().findViewById(R.id.campaigns_list_view);

    listView.setAdapter(new ListAdapter() {

      final List<JSONObject> campaings = new ArrayList<>(Arrays.asList(new JSONObject(), new JSONObject()));

      @Override
      public boolean areAllItemsEnabled() {
        return true;
      }

      @Override
      public boolean isEnabled(int position) {
        return true;
      }

      @Override
      public void registerDataSetObserver(DataSetObserver observer) {

      }

      @Override
      public void unregisterDataSetObserver(DataSetObserver observer) {

      }

      @Override
      public int getCount() {
        return campaings.size();
      }

      @Override
      public Object getItem(int position) {
        return campaings.get(position);
      }

      @Override
      public long getItemId(int position) {
        try {
          return campaings.get(position).getInt("id");
        } catch (JSONException e) {
          e.printStackTrace();
        }

        return -1;
      }

      @Override
      public boolean hasStableIds() {
        return false;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {

        final LinearLayout layout = new LinearLayout(parent.getContext());
        final TextView textView = new TextView(parent.getContext());
        textView.setText("test");
        layout.addView(textView);

        return layout;
      }

      @Override
      public int getItemViewType(int position) {
        return 1;
      }

      @Override
      public int getViewTypeCount() {
        return 1;
      }

      @Override
      public boolean isEmpty() {
        return campaings.isEmpty();
      }
    });


  }

}
