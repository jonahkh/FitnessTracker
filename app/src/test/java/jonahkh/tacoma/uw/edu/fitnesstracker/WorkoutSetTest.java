/*
 * Jonah Howard
 * Hector diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker;

import android.util.Log;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import jonahkh.tacoma.uw.edu.fitnesstracker.model.WorkoutSet;

/**
 * This class tests the WorkoutSet class.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class WorkoutSetTest extends TestCase {
    /** The workout set used for testing. */
    private WorkoutSet mSet;

    @Before
    public void setUp() {
        mSet = new WorkoutSet("Exercise", 10, 1, 10);
    }

    @Test
    public void testConstructor() {
        assertNotNull(mSet);
    }

    @Test
    public void testNullNameConstructor() {
        try {
            WorkoutSet set = new WorkoutSet(null, 1, 1, 1);
            fail("Workout Set can have null name");
        } catch (IllegalArgumentException e) {
            Log.e("WorkoutSetTest", e.toString());
        }
    }

    @Test
    public void testInvalidNameLengthConstructor() {
        try {
            WorkoutSet set = new WorkoutSet("", 1, 1, 1);
            fail("Workout set can have empty name");
        } catch (IllegalArgumentException e) {
            Log.e("WorkoutSetTest", e.toString());
        }
    }

    @Test
    public void testInvalidRepCountConstructor() {
        try {
            WorkoutSet set = new WorkoutSet("Exercise", -1, 1, 1);
            fail("Workout set can have negative reps count");
        } catch (IllegalArgumentException e) {
            Log.e("ExerciseTest", e.toString());
        }
    }

    @Test
    public void testInvalidSetNumberConstructor() {
        try {
            WorkoutSet set = new WorkoutSet("Exercise", 1, 0, 1);
            fail("Workout set can have a set number less than 1");
        } catch (IllegalArgumentException e) {
            Log.e("WorkoutSetTest", e.toString());
        }
    }

    @Test
    public void testNegativeWeightConstructor() {
        try {
            WorkoutSet set = new WorkoutSet("Exercise", 1, 1, -1);
            fail("Workout set can have negative weight");
        } catch (IllegalArgumentException e) {
            Log.e("WorkoutSetTest", e.toString());
        }
    }

    @Test
    public void testGetExerciseName() {
        assertEquals("Exercise", mSet.getExerciseName());
    }

    @Test
    public void testGetReps() {
        assertEquals(10, mSet.getReps());
    }

    @Test
    public void testGetSetNumber() {
        assertEquals(1, mSet.getSetNumber());
    }

    @Test
    public void testGetWeight() {
        assertEquals(10, mSet.getWeight());
    }
}
