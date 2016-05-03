/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an exercise. It stores the exercise name and a list of all of the sets
 * the user has completed for this exercise.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class Exercise implements Serializable{

    /** Name of the exercise in the exercise table. */
    public static final String NAME = "exerciseName";

    /** The name of this exercise. */
    private final String mName;

    /** A list of all sets completed for this exercise. */
    private final List<WorkoutSet> mSets;

    /**
     * Initialize a new Exercise.
     *
     * @param name the name of this exercise
     */
    public Exercise(String name) {
        mName = name;
        mSets = new ArrayList<>();
    }

    /**
     * Return the current exercise name.
     *
     * @return the current exercise name
     */
    public String getExerciseName() {
        return mName;
    }

    /**
     * Add the passed set to the list of sets for this workout.
     *
     * @param set the set being added
     */
    public void addSet(WorkoutSet set) {
        mSets.add(set);
    }

    /**
     * Return the list of sets completed for this exercise.
     *
     * @return the list of sets completed for this exercise
     */
    public List<WorkoutSet> getSets() {
        return mSets;
    }

    @Override
    public String toString() {
        return mName + ": " + mSets.toString();
    }
}
