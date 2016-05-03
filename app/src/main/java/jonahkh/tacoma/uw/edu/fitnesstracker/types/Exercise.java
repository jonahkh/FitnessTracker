package jonahkh.tacoma.uw.edu.fitnesstracker.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonah on 4/28/2016.
 */
public class Exercise implements Serializable{

    /** Name of the exercise in the exercise table. */
    public static final String NAME = "exerciseName";

    public static final String EXERCISE_SELECTED = "exercise_selected";

    /** The name of this exercise. */
    private String mName;

    private List<WorkoutSet> mSets;


    public Exercise(String name) {
        mName = name;
        mSets = new ArrayList<WorkoutSet>();
    }

    public String getExerciseName() {
        return mName;
    }

    public void addSet(WorkoutSet set) {
        mSets.add(set);
    }

    public List<WorkoutSet> getSets() {
        return mSets;
    }

    @Override
    public String toString() {
        return mName + ": " + mSets.toString();
    }
}
