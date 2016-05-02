package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import java.io.Serializable;

/**
 * Created by Jonah on 4/28/2016.
 */
public class Exercise implements Serializable{

    /** Name of the exercise in the exercise table. */
    public static final String NAME = "exerciseName";

    public static final String EXERCISE_SELECTED = "exercise_selected";

    /** The name of this exercise. */
    private String mName;

    public Exercise(String name) {
        mName = name;
    }

    public String getExerciseName() {
        return mName;
    }


}
