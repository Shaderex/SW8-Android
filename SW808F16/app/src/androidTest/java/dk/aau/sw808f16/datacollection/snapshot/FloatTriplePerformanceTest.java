package dk.aau.sw808f16.datacollection.snapshot;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class FloatTriplePerformanceTest extends ApplicationTestCase<Application> {

  public FloatTriplePerformanceTest() {
    super(Application.class);
  }

  public void testData() {
    final int floatIterations = 500000;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    final ArrayList<float[]> floatArrays = new ArrayList<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    final ArrayList<FloatTriple> floatTriples = new ArrayList<>();

    final Random rng = new Random();

    long time0 = System.currentTimeMillis();

    for (int i = 0; i < floatIterations; i++) {
      final float[] newFloats = new float[] {rng.nextFloat(), rng.nextFloat(), rng.nextFloat()};
      floatArrays.add(newFloats);
      @SuppressWarnings("UnusedAssignment")
      final float result = newFloats[0] * newFloats[1] * newFloats[2];
    }

    long time1 = System.currentTimeMillis();

    Log.d("FloatPerformance", "Float[] time: " + (time1 - time0) + " ms");

    long time2 = System.currentTimeMillis();

    for (int i = 0; i < floatIterations; i++) {
      final FloatTriple floatTriple = new FloatTriple(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
      floatTriples.add(floatTriple);
      @SuppressWarnings("UnusedAssignment")
      final float result = floatTriple.getFirstValue() * floatTriple.getSecondValue() * floatTriple.getThirdValue();
    }

    long time3 = System.currentTimeMillis();

    Log.d("FloatPerformance", "FloatTriple time: " + (time3 - time2) + " ms");
  }

}

