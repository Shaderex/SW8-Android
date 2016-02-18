package dk.aau.sw808f16.datacollection;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ExampleUITest {

    private MainActivity mainActivity;

    @Before
    public void setup() {
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void exampleGUITest() throws Exception {
        TextView textView = (TextView) mainActivity.findViewById(R.id.hello_world);

        assertEquals(textView.getText(), "Hello World!");
    }


}
