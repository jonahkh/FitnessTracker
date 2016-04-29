package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import java.io.Serializable;

/**
 * Created by Jonah on 4/28/2016.
 */
public class Exercise implements Serializable{
    private String mName;

    public Exercise(String name) {
        mName = name;
    }

    public String getExerciseName() {
        return mName;
    }


}
