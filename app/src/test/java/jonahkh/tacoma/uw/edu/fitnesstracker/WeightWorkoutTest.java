/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */

package jonahkh.tacoma.uw.edu.fitnesstracker;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;

import static org.junit.Assert.*;

/**
 * This class test the WeightWorkout class.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class WeightWorkoutTest {
    private WeightWorkout mWeightWorkout;

    @Before
    public void setUp() {
        mWeightWorkout = new WeightWorkout("Workout", 1, "2016-05-14");
    }
    @Test
    public void testNameConstructor() {
        WeightWorkout workout = new WeightWorkout("Workout");
        assertNotNull(workout);
    }

    @Test
    public void testNullName() {
        try {
            WeightWorkout workout = new WeightWorkout(null);
            fail("workout name can be set to null");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testShortName() {
        try {
            WeightWorkout workout = new WeightWorkout("");
            fail("workout name can be empty");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testInvalidWorkoutNumber() {
        try {
            WeightWorkout workout = new WeightWorkout("workout", 0, "");
            fail("workout number can be set to a number less than 1");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testNullDate() {
        try {
            WeightWorkout workout = new WeightWorkout("workout", 1, null);
            fail("date can be set to null");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testGetWorkoutName() {
        assertEquals("Workout", mWeightWorkout.getWorkoutName());
    }

    @Test
    public void testGetWorkoutNumber() {
        assertEquals(1, mWeightWorkout.getWorkoutNumber());
    }

    @Test
    public void testGetDate() {
        assertEquals("2016-05-14", mWeightWorkout.getDate());
    }

    @Test
    public void testParsePreDefinedWeightWorkoutJSON() {
        String JSON = "[{\"workoutName\":\"Legs\",\"type\":\"weight\"},{\"workoutName\":\"Arms\"," +
                "\"type\":\"weight\"},{\"workoutName\":\"Chest\",\"type\":\"weight\"}," +
                "{\"workoutName\":\"Back\",\"type\":\"weight\"},{\"workoutName\":\"Abs\"," +
                "\"type\":\"weight\"}]";
        String message = WeightWorkout.parsePreDefinedWeightWorkoutJSON(JSON,
                new ArrayList<WeightWorkout>());
        assertTrue("JSON With Valid String", message == null);

    }

    @Test
    public void testParseWeightWorkoutListExerciseJSON() {
        String JSON = "[{\"workoutName\":\"Chest\",\"exercise\":\"bench press\"}," +
                "{\"workoutName\":\"Chest\",\"exercise\":\"decline pushup\"},{\"workoutName\":" +
                "\"Chest\",\"exercise\":\"incline fly\"},{\"workoutName\":\"Chest\",\"exercise\":" +
                "\"incline press\"}]";
        String message = WeightWorkout.parseWeightWorkoutListExerciseJSON(JSON,
                new ArrayList<Exercise>(), false);
        assertTrue("JSON With Valid String", message == null);
    }

    @Test
    public void testParseExerciseJSON() {
        String JSON = "[{\"setNumber\":\"1\",\"workoutNumber\":\"1\",\"exerciseName\":" +
                "\"Curls\",\"repetitions\":\"10\",\"email\":\"jonahkh@uw.edu\",\"weight\":" +
                "\"30\"},{\"setNumber\":\"2\",\"workoutNumber\":\"1\",\"exerciseName\":" +
                "\"Tricep Dips\",\"repetitions\":\"10\",\"email\":\"jonahkh@uw.edu\",\"weight\":" +
                "\"30\"}]";
        String message = WeightWorkout.parseExercisesJSON(JSON, new ArrayList<Exercise>());
        assertTrue("JSON With Valid String", message == null);
    }

    @Test
    public void testParseWeightWorkoutJSON() {
        String JSON = "[{\"workoutNumber\":\"1\",\"workoutName\":\"Arms\",\"email\":" +
                "\"jonahkh@uw.edu\",\"dateCompleted\":\"2016-05-01\",\"type\":\"weight\"}," +
                "{\"workoutNumber\":\"2\",\"workoutName\":\"Legs\",\"email\":\"jonahkh@uw.edu\"," +
                "\"dateCompleted\":\"2016-05-01\",\"type\":\"weight\"},{\"workoutNumber\":\"3\"," +
                "\"workoutName\":\"Chest\",\"email\":\"jonahkh@uw.edu\",\"dateCompleted\":" +
                "\"2016-05-03\",\"type\":\"weight\"},{\"workoutNumber\":\"4\",\"workoutName\":" +
                "\"Legs\",\"email\":\"jonahkh@uw.edu\",\"dateCompleted\":\"2016-05-12\",\"type\":" +
                "\"weight\"},{\"workoutNumber\":\"5\",\"workoutName\":\"Chest\",\"email\":" +
                "\"jonahkh@uw.edu\",\"dateCompleted\":\"2016-05-12\",\"type\":\"weight\"}]";
        String message = WeightWorkout.parseWeightWorkoutJSON(JSON, new ArrayList<WeightWorkout>());
        assertTrue("JSON With Valid String", message == null);

    }
}
