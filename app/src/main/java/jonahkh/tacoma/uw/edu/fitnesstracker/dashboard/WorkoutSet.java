package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

/**
 * Created by Jonah on 4/28/2016.
 */
public class WorkoutSet {
    private String mExerciseName;
    private int mReps;
    private int mSetNumber;

    public WorkoutSet(String exerciseName, int reps, int setNumber) {
        mExerciseName = exerciseName;
        mReps = reps;
        mSetNumber = setNumber;
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public int getReps() {
        return mReps;
    }

    public int getmSetNumber() {
        return mSetNumber;
    }
}
