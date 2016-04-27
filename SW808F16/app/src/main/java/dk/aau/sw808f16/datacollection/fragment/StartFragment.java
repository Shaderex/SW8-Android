package dk.aau.sw808f16.datacollection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import dk.aau.sw808f16.datacollection.MainActivity;
import dk.aau.sw808f16.datacollection.R;

public class StartFragment extends Fragment {

  public static StartFragment newInstance() {
    return new StartFragment();
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_start, container, false);

    view.findViewById(R.id.browse_campaign_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MainActivity.DrawerMenuItems.PUBLIC_CAMPAIGNS.open((MainActivity) view.getContext());
      }
    });

    return view;
  }

}
