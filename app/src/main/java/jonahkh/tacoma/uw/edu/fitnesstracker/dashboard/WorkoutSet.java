package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

/**
 * Created by Jonah on 4/28/2016.
 */
public class WorkoutSet {
    private String mExerciseName;
    private int mReps;
    private int mSetNumber;
    private int mWeight;

    public WorkoutSet(String exerciseName, int reps, int setNumber, int weight) {
        mExerciseName = exerciseName;
        mReps = reps;
        mSetNumber = setNumber;
        mWeight = weight;
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public int getReps() {
        return mReps;
    }

    public int getSetNumber() {
        return mSetNumber;
    }

    public int getWeight() {
        return mWeight;
    }
}
