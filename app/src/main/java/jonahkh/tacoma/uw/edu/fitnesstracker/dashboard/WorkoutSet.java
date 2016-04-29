package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

/**
 * Created by Jonah on 4/28/2016.
 */
public class WorkoutSet {
    private String mExerciseName;
    private int mReps;

    public WorkoutSet(String exerciseName, int reps) {
        mExerciseName = exerciseName;
        mReps = reps;
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public int getReps() {
        return mReps;
    }
}
