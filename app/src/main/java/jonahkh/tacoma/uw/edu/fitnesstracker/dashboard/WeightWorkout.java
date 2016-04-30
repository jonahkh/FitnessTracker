package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class stores information about weight workouts.
 */
public class WeightWorkout implements Serializable{

    public static final String NAME = "workoutName";
    /** Maps exercises for this workout to the sets performed. */
    private Map<Exercise, List<WorkoutSet>> mExercises;
    private String mWorkoutName;

    public WeightWorkout(String workoutName) {
        mWorkoutName = workoutName;
    }

    public void addExercise(Exercise exercise, WorkoutSet set) {
        if (mExercises.containsKey(exercise)) {
            mExercises.get(exercise).add(set);
        } else {
            mExercises.put(exercise, Arrays.asList(set));
        }
    }

    public String getWorkoutName() {
        return mWorkoutName;
    }

    public static String parseWeightWorkoutJSON(String weightWorkoutJSON, List<WeightWorkout> weightWorkoutList) {
        String reason = null;
        Log.i("TAG", weightWorkoutJSON);
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Log.e("NEW_TAG", arr.getJSONObject(i).toString());
                    WeightWorkout weightWorkout = new WeightWorkout(obj.getString(WeightWorkout.NAME));
                    weightWorkoutList.add(weightWorkout);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

    public String getmWorkoutName() {
        return mWorkoutName;
    }

    public Map<Exercise, List<WorkoutSet>> getExercises() {
        return mExercises;
    }

}
