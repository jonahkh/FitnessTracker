/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.types;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * This class stores information about weight workouts. Weight workouts store each exercise that
 * was performed for that workout and how many sets for each workout were completed.
 */
public class WeightWorkout implements Serializable {

    /** Name of the workout in the weight workout table. */
    public static final String NAME = "workoutName";

    /** Name of the exercise in the weight workout table. */
    public static final String EXERCISE = "exercise";

    /** Name of the workout number for this workout. */
    public static final String NUMBER = "workoutNumber";

    /** Identifier for this Serializable object. */
    public static final String WORKOUT_SELECTED = "workout_selected";

    /** Name of the date in the weight workout table. */
    public static final String DATE = "dateCompleted";

    /** The name of the current workout. */
    private String mWorkoutName;

    /** The current workout number. */
    private int mWorkoutNumber;

    /** The date this workout was completed. */
    private String mDate;

    /**
     * Initialize a new Weight Workout.
     *
     * @param workoutName the workout name for this workout
     */
    public WeightWorkout(String workoutName) {
        mWorkoutName = workoutName;
    }

    /**
     * Initializes a new Weight Workout given a name, number, and date.
     *
     * @param workoutName the name of this workout
     * @param workoutNumber the number identifying this workout
     * @param date the date this workout was completed
     */
    public WeightWorkout(String workoutName, int workoutNumber, String date) {
        mWorkoutName = workoutName;
        mWorkoutNumber = workoutNumber;
        mDate = date;
    }

    /**
     * Returns this workout name.
     *
     * @return this workout name
     */
    public String getWorkoutName() {
        return mWorkoutName;
    }

    /**
     * Get the workout number for this weight workout.
     *
     * @return the workout number for this weight workout
     */
    public int getWorkoutNumber() {
        return mWorkoutNumber;
    }

    /**
     * Return the date this workout was completed.
     *
     * @return the date this workout was completed
     */
    public String getDate() {
        return mDate;
    }

    public static String parsePreDefinedWeightWorkoutJSON(String weightWorkoutJSON, List<WeightWorkout> weightWorkoutList) {
        String reason = null;
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    WeightWorkout weightWorkout = new WeightWorkout(obj.getString(NAME));
                    weightWorkoutList.add(weightWorkout);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

    public static String parseWeightWorkoutlistExerciseJSON(String weightWorkoutJSON,
                                                            List<Exercise> exerciseList) {
        String reason = null;
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
//                    WeightWorkout weightWorkout = new WeightWorkout(obj.getString(NAME));
                    Exercise exercise = new Exercise(obj.getString(EXERCISE));
                    exerciseList.add(exercise);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

//    public void setSharedPreferneces(Activity activity) {
//        mSharedPreferences = activity.getSharedPreferences(activity.getString(R.string.WORKOUT_INFO), Context.MODE_PRIVATE);
//    }

    /**
     * Parse the passed input and convert into a new weight workout.
     *
     * @param weightWorkoutJSON the input being parsed (pulled from database)
     * @param exerciseList the list being populated based on the input being parsed
     * @return null if no issues, otherwise return the error that occurred
     */
    public static String parseExercisesJSON(String weightWorkoutJSON, List<Exercise> exerciseList) {
        String reason = null;
        Log.e("WEEIGHT", weightWorkoutJSON);
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String exerciseName = obj.getString(Exercise.NAME);
                    WorkoutSet set = new WorkoutSet(exerciseName,
                            obj.getInt(WorkoutSet.REPETITIONS),
                            obj.getInt(WorkoutSet.SET_NUMBER),
                            obj.getInt(WorkoutSet.WEIGHT));
                    int check =  checkContainsExercise(exerciseName, exerciseList);
                    if (check > -1) {
                        exerciseList.get(check).addSet(set);
                    } else {
                        Exercise exercise = new Exercise(exerciseName);
                        exercise.addSet(set);
                        exerciseList.add(exercise);
                    }
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    private static int checkContainsExercise(String name, List<Exercise> exercises) {
        for (int i = 0; i < exercises.size(); i++) {
            if (name.equals(exercises.get(i).getExerciseName())) {
                return i;
            }
        }
        return -1;
    }

    public static String parseWeightWorkoutJSON(String weightWorkoutJSON, List<WeightWorkout> weightWorkoutList) {
        String reason = null;
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    WeightWorkout workout = new WeightWorkout(obj.getString(NAME),
                            obj.getInt(NUMBER), obj.getString(DATE));
                    weightWorkoutList.add(workout);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    @Override
    public String toString() {
        return mWorkoutName + ", " + mWorkoutNumber;
    }

}
