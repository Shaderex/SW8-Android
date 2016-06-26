package dk.aau.sw808f16.wekatest;

/*
 * Copyright (C) 2008 The Android open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.test.ActivityInstrumentationTestCase;
import android.test.ActivityTestCase;
import android.test.mock.MockApplication;
import android.view.Window;

import java.lang.reflect.Field;

/**
 * This class provides isolated testing of a single activity.  The activity under test will
 * be created with minimal connection to the system infrastructure, and you can inject mocked or
 * wrappered versions of many of Activity's dependencies.  Most of the work is handled
 * automatically here by {@link #setUp} and {@link #tearDown}.
 * <p/>
 * <p>If you prefer a functional test, see {@link android.test.ActivityInstrumentationTestCase}.
 * <p/>
 * <p>It must be noted that, as a true unit test, your Activity will not be running in the
 * normal system and will not participate in the normal interactions with other Activities.
 * The following methods should not be called in this configuration - most of them will throw
 * exceptions:
 * <ul>
 * <li>{@link android.app.Activity#createPendingResult(int, Intent, int)}</li>
 * <li>{@link android.app.Activity#startActivityIfNeeded(Intent, int)}</li>
 * <li>{@link android.app.Activity#startActivityFromChild(Activity, Intent, int)}</li>
 * <li>{@link android.app.Activity#startNextMatchingActivity(Intent)}</li>
 * <li>{@link android.app.Activity#getCallingActivity()}</li>
 * <li>{@link android.app.Activity#getCallingPackage()}</li>
 * <li>{@link android.app.Activity#createPendingResult(int, Intent, int)}</li>
 * <li>{@link android.app.Activity#getTaskId()}</li>
 * <li>{@link android.app.Activity#isTaskRoot()}</li>
 * <li>{@link android.app.Activity#moveTaskToBack(boolean)}</li>
 * </ul>
 * <p/>
 * <p>The following methods may be called but will not do anything.  For test purposes, you can use
 * the methods {@link #getStartedActivityIntent()} and {@link #getStartedActivityRequest()} to
 * inspect the parameters that they were called with.
 * <ul>
 * <li>{@link android.app.Activity#startActivity(Intent)}</li>
 * <li>{@link android.app.Activity#startActivityForResult(Intent, int)}</li>
 * </ul>
 * <p/>
 * <p>The following methods may be called but will not do anything.  For test purposes, you can use
 * the methods {@link #isFinishCalled()} and {@link #getFinishedActivityRequest()} to inspect the
 * parameters that they were called with.
 * <ul>
 * <li>{@link android.app.Activity#finish()}</li>
 * <li>{@link android.app.Activity#finishFromChild(Activity child)}</li>
 * <li>{@link android.app.Activity#finishActivity(int requestCode)}</li>
 * </ul>
 */
public abstract class ActivityUnitTestCase<T extends Activity>
    extends ActivityTestCase {

  private Class<T> activityClass;

  private Context activityContext;
  private Application application;
  private MockParent mockParent;

  private boolean attached = false;
  private boolean created = false;

  public ActivityUnitTestCase(Class<T> activityClass) {
    this.activityClass = activityClass;
  }

  @Override
  public T getActivity() {
    return (T) super.getActivity();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    // default value for target context, as a default
    activityContext = getInstrumentation().getTargetContext();
  }

  /**
   * Start the activity under test, in the same way as if it was started by
   * {@link android.content.Context#startActivity Context.startActivity()}, providing the
   * arguments it supplied.  When you use this method to start the activity, it will automatically
   * be stopped by {@link #tearDown}.
   * <p/>
   * <p>This method will call onCreate(), but if you wish to further exercise Activity life
   * cycle methods, you must call them yourself from your test case.
   * <p/>
   * <p><i>Do not call from your setUp() method.  You must call this method from each of your
   * test methods.</i>
   *
   * @param intent                       The Intent as if supplied to {@link android.content.Context#startActivity}.
   * @param savedInstanceState           The instance state, if you are simulating this part of the life
   *                                     cycle.  Typically null.
   * @param lastNonConfigurationInstance This Object will be available to the
   *                                     Activity if it calls {@link android.app.Activity#getLastNonConfigurationInstance()}.
   *                                     Typically null.
   * @return Returns the Activity that was created
   */
  protected T startActivity(Intent intent, Bundle savedInstanceState,
                            Object lastNonConfigurationInstance) {
    assertFalse("Activity already created", created);

    if (!attached) {
      assertNotNull(activityClass);
      setActivity(null);
      T newActivity = null;
      try {
        if (application == null) {
          setApplication(new MockApplication());
        }
        ComponentName cn = new ComponentName(activityClass.getPackage().getName(),
            activityClass.getName());
        intent.setComponent(cn);
        ActivityInfo info = new ActivityInfo();
        CharSequence title = activityClass.getName();
        mockParent = new MockParent();
        String id = null;

        IBinder token = null;
        newActivity = (T) getInstrumentation().newActivity(activityClass, activityContext,
            token, application, intent, info, title, mockParent, id,
            lastNonConfigurationInstance);
      } catch (Exception exception) {
        assertNotNull(newActivity);
      }

      assertNotNull(newActivity);
      setActivity(newActivity);

      attached = true;
    }

    T result = getActivity();
    if (result != null) {
      getInstrumentation().callActivityOnCreate(getActivity(), savedInstanceState);
      created = true;
    }
    return result;
  }

  @Override
  protected void tearDown() throws Exception {

    setActivity(null);

    // Scrub out members - protects against memory leaks in the case where someone
    // creates a non-static inner class (thus referencing the test case) and gives it to
    // someone else to hold onto
    scrubClass(ActivityInstrumentationTestCase.class);

    super.tearDown();
  }

  /**
   * Set the application for use during the test.  You must call this function before calling
   * {@link #startActivity}.  If your test does not call this method,
   *
   * @param application The Application object that will be injected into the Activity under test.
   */
  public void setApplication(Application application) {
    this.application = application;
  }

  /**
   * If you wish to inject a Mock, Isolated, or otherwise altered context, you can do so
   * here.  You must call this function before calling {@link #startActivity}.  If you wish to
   * obtain a real Context, as a building block, use getInstrumentation().getTargetContext().
   */
  public void setActivityContext(Context activityContext) {
    this.activityContext = activityContext;
  }

  /**
   * This method will return the value if your Activity under test calls
   * {@link android.app.Activity#setRequestedOrientation}.
   */
  public int getRequestedOrientation() {
    if (mockParent != null) {
      return mockParent.requestedOrientation;
    }
    return 0;
  }

  /**
   * This method will return the launch intent if your Activity under test calls
   * {@link android.app.Activity#startActivity(Intent)} or
   * {@link android.app.Activity#startActivityForResult(Intent, int)}.
   *
   * @return The Intent provided in the start call, or null if no start call was made.
   */
  public Intent getStartedActivityIntent() {
    if (mockParent != null) {
      return mockParent.startedActivityIntent;
    }
    return null;
  }

  /**
   * This method will return the launch request code if your Activity under test calls
   * {@link android.app.Activity#startActivityForResult(Intent, int)}.
   *
   * @return The request code provided in the start call, or -1 if no start call was made.
   */
  public int getStartedActivityRequest() {
    if (mockParent != null) {
      return mockParent.startedActivityRequest;
    }
    return 0;
  }

  /**
   * This method will notify you if the Activity under test called
   * {@link android.app.Activity#finish()},
   * {@link android.app.Activity#finishFromChild(Activity)}, or
   * {@link android.app.Activity#finishActivity(int)}.
   *
   * @return Returns true if one of the listed finish methods was called.
   */
  public boolean isFinishCalled() {
    if (mockParent != null) {
      return mockParent.finished;
    }
    return false;
  }

  public Intent getFinishedActivityIntent() {
    if (mockParent != null) {
      return mockParent.finishedActivityIntent;
    }
    return null;
  }

  /**
   * This method will return the request code if the Activity under test called
   * {@link android.app.Activity#finishActivity(int)}.
   *
   * @return The request code provided in the start call, or -1 if no finish call was made.
   */
  public int getFinishedActivityRequest() {
    if (mockParent != null) {
      return mockParent.finishedActivityRequest;
    }
    return 0;
  }

  /**
   * This mock Activity represents the "parent" activity.  By injecting this, we allow the user
   * to call a few more Activity methods, including:
   * <ul>
   * <li>{@link android.app.Activity#getRequestedOrientation()}</li>
   * <li>{@link android.app.Activity#setRequestedOrientation(int)}</li>
   * <li>{@link android.app.Activity#finish()}</li>
   * <li>{@link android.app.Activity#finishActivity(int requestCode)}</li>
   * <li>{@link android.app.Activity#finishFromChild(Activity child)}</li>
   * </ul>
   * <p/>
   * TODO: Make this overrideable, and the unit test can look for calls to other methods
   */
  @SuppressLint("Registered")
  private static class MockParent extends Activity {

    public int requestedOrientation = 0;
    public Intent startedActivityIntent = null;
    public int startedActivityRequest = -1;
    public boolean finished = false;
    public Intent finishedActivityIntent = null;
    public int finishedActivityRequest = -1;

    /**
     * Implementing in the parent allows the user to call this function on the tested activity.
     */
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
      this.requestedOrientation = requestedOrientation;
    }

    /**
     * Implementing in the parent allows the user to call this function on the tested activity.
     */
    @SuppressWarnings("WrongConstant")
    @Override
    public int getRequestedOrientation() {
      return requestedOrientation;
    }

    /**
     * By returning null here, we inhibit the creation of any "container" for the window.
     */
    @Override
    public Window getWindow() {
      return null;
    }

    /**
     * By defining this in the parent, we allow the tested activity to call
     * <ul>
     * <li>{@link android.app.Activity#startActivity(Intent)}</li>
     * <li>{@link android.app.Activity#startActivityForResult(Intent, int)}</li>
     * </ul>
     */
    @Override
    public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
      startedActivityIntent = intent;
      startedActivityRequest = requestCode;
    }

    /**
     * By defining this in the parent, we allow the tested activity to call
     * <ul>
     * <li>{@link android.app.Activity#finish()}</li>
     * <li>{@link android.app.Activity#finishFromChild(Activity child)}</li>
     * </ul>
     */
    @Override
    public void finishFromChild(Activity child) {
      finished = true;
    }

    /**
     * By defining this in the parent, we allow the tested activity to call
     * <ul>
     * <li>{@link android.app.Activity#finishActivity(int requestCode)}</li>
     * </ul>
     */
    @Override
    public void finishActivityFromChild(Activity child, int requestCode) {
      finished = true;
      finishedActivityRequest = requestCode;
      finishedActivityIntent = getResultData(child);
    }

    private Intent getResultData(final Activity activity) {
      Field field = null;
      try {
        field = Activity.class.getDeclaredField("mResultData");
        field.setAccessible(true);
        final Intent value = (Intent) field.get(activity);
        return value;
      } catch (NoSuchFieldException | IllegalAccessException exception) {
        exception.printStackTrace();
      }
      return null;
    }
  }
}
