package dk.aau.sw808f16.datacollection;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;

import dk.aau.sw808f16.datacollection.fragment.StartFragment;

public class MainActivity extends Activity {

  private static final String START_FRAGMENT_KEY = "START_FRAGMENT_KEY";

  @Override
  protected void onCreate(final Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final FragmentManager fragmentManager = getFragmentManager();

    fragmentManager.beginTransaction()
        .add(R.id.content_frame_layout, StartFragment.newInstance(), START_FRAGMENT_KEY).addToBackStack(START_FRAGMENT_KEY).commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);


    return true;
  }
}
