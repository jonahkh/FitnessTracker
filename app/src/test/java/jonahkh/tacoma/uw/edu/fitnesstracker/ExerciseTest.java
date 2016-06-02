/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */

package jonahkh.tacoma.uw.edu.fitnesstracker;

import android.util.Log;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WorkoutSet;

/**
 * This class test the Exercise class.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class ExerciseTest {
    /** Exercise used for testing. */
    private Exercise mExercise;

    @Before
    public void setUp() {
        mExercise = new Exercise("Test Exercise");
    }

    @Test
    public void testConstructor() {
        Exercise exercise = new Exercise("Test Exercise");
        Assert.assertNotNull(exercise);
    }

    @Test
    public void testNullConstructor() {
        try {
            Exercise exercise = new Exercise(null);
            Assert.fail("Exercise name can be set to null");
        } catch (IllegalArgumentException e) {
            Log.e("ExerciseTest", e.toString());
        }
    }

    @Test
    public void TestEmptyConstructor() {
        try {
            Exercise exercise = new Exercise("");
            Assert.fail("Exercise name can be set to less than one character");
        } catch (IllegalArgumentException e) {
            Log.e("ExerciseTest", e.toString());
        }
    }

    @Test
    public void testGetExerciseName() {
        Assert.assertEquals("Test Exercise", mExercise.getExerciseName());
    }

    @Test
    public void testGetSets() {
        WorkoutSet set = new WorkoutSet(mExercise.getExerciseName(), 10, 1, 100);
        mExercise.addSet(set);
        List<WorkoutSet> list = mExercise.getSets();
        WorkoutSet set2 = list.get(0);
        // Test all fields the same
        Assert.assertEquals(set.getExerciseName(), set2.getExerciseName());
        Assert.assertEquals(set.getReps(), set2.getReps());
        Assert.assertEquals(set.getSetNumber(), set2.getSetNumber());
        Assert.assertEquals(set.getWeight(), set2.getWeight());
        // Test that the sets are the same objects
        Assert.assertEquals(list.get(0), set);
    }

    @Test
    public void testNullAddSet() {
        try {
            mExercise.addSet(null);
            Assert.fail("Exercise can add a null set");
        } catch (IllegalArgumentException e) {
            Log.e("ExerciseTest", e.toString());
        }
    }
}
