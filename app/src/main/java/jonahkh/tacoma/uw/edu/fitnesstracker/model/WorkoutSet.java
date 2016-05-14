/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */

package jonahkh.tacoma.uw.edu.fitnesstracker.model;

/**
 * Represents a workout set. Sets are created when the user clicks on an exercise during a workout.
 * They enter the weight used and how many repetitions they performed and this class generates a
 * set number for that set based on how many sets have been performed for that exercise.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class WorkoutSet {

    /** Identifies the database name for the set number. */
    public static final String SET_NUMBER = "setNumber";

    /** Identifies the database name for the repetitions. */
    public static final String REPETITIONS = "repetitions";

    /** Identifies the database name for the weight. */
    public static final String WEIGHT = "weight";

    /** The current exercise name. */
    private final String mExerciseName;

    /** The reps performed. */
    private final int mReps;

    /** The set number. */
    private final int mSetNumber;

    /** The weight used for this set. */
    private final int mWeight;

    /**
     * Initialize a new WorkoutSet.
     *
     * @param exerciseName the name of the exercise
     * @param reps the reps performed
     * @param setNumber the number of the set for this exercise
     * @param weight the weight used
     */
    public WorkoutSet(String exerciseName, int reps, int setNumber, int weight) {
        if (exerciseName == null || exerciseName.length() < 1 || reps < 0
                || setNumber < 1 || weight < 0) {
            throw new IllegalArgumentException();
        }
        mExerciseName = exerciseName;
        mReps = reps;
        mSetNumber = setNumber;
        mWeight = weight;
    }

    /**
     * Return the exercise name for this set.
     *
     * @return the exercise name
     */
    public String getExerciseName() {
        return mExerciseName;
    }

    /**
     * Return the repetitions for this set.
     *
     * @return the repetitions performed for this set
     */
    public int getReps() {
        return mReps;
    }

    /**
     * Return the set number for this set.
     *
     * @return the set number for this set
     */
    public int getSetNumber() {
        return mSetNumber;
    }

    /**
     * Return the weight used for this set.
     *
     * @return the weight used for this set
     */
    public int getWeight() {
        return mWeight;
    }

    @Override
    public String toString() {
        return "Reps: " + mReps + ", Weight: " + mWeight + ", Set Number: " + mSetNumber;
    }
}
