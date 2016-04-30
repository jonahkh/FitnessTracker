package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class stores information about weight workouts.
 */
public class WeightWorkout implements Serializable{

    /** Name of the workout in the weight workout table. */
    public static final String NAME = "workoutName";

    /** Name of the exercise in the weight workout table. */
    public static final String EXERCISE = "exercise";

    public static final String WORKOUT_SELECTED = "workout_selected";

    /** Maps exercises for this workout to the sets performed. */
    private Map<Exercise, List<WorkoutSet>> mExercises;
    /** The name of the current workout. */
    private String mWorkoutName;

    public WeightWorkout(String workoutName) {
        mWorkoutName = workoutName;
    }

    /**
     * Add a set to an exercise for this workout.
     *
     * @param exercise the exercise that a set was completed for
     * @param set the information pertinent to the completed set
     */
    public void addExercise(Exercise exercise, WorkoutSet set) {
        if (mExercises.containsKey(exercise)) {
            mExercises.get(exercise).add(set);
        } else {
            mExercises.put(exercise, Arrays.asList(set));
        }
    }

    /**
     * Returns this workout name.
     *
     * @return this workout name
     */
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
                    WeightWorkout weightWorkout = new WeightWorkout(obj.getString(WeightWorkout.EXERCISE));
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
