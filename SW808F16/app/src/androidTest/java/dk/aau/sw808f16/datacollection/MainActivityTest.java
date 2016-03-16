package dk.aau.sw808f16.datacollection;

import android.content.ComponentName;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.CountDownLatch;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

  public MainActivityTest() {
    super(MainActivity.class);
  }

  private MainActivity mainActivity;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    final ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
    setActivityContext(context);

    final ComponentName componentName = new ComponentName("dk.aau.sw808f16.datacollection", "MainActivity");
    final Intent mainActivityIntent = Intent.makeMainActivity(componentName);
    mainActivity = startActivity(mainActivityIntent, null, null);  //launchActivityWithIntent(, MainActivity.class,);

    return;
  }

  public void testStartQuestionnaireButtonExists() {

    final View view = mainActivity.findViewById(R.id.start_questionnaire_button);

    assertNotNull(view);
  }

  public void testStartQuestionnaireWhenButtonIsPressed() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final Button startQuestionnaireButton = (Button) mainActivity.findViewById(R.id.start_questionnaire_button);

    mainActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        startQuestionnaireButton.performClick();
        latch.countDown();
      }
    });

    latch.await();

    Intent startedIntent = this.getStartedActivityIntent();
    assertEquals(startedIntent.getComponent().getClassName(), QuestionnaireActivity.class.getName());
  }

}
