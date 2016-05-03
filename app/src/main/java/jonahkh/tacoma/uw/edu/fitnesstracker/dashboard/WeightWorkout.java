/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

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


    /** Maps exercises for this workout to the sets performed. */
    private Map<Exercise, List<WorkoutSet>> mExercises;

    /** The name of the current workout. */
    private String mWorkoutName;

    private int mWorkoutNumber;

    private String mDate;

    /** Shared preferences for this Fragment. */
    private SharedPreferences mSharedPreferences;


    /**
     * Initialize a new Weight Workout.
     *
     * @param workoutName the workout name for this workout
     */
    public WeightWorkout(String workoutName) {
        mWorkoutName = workoutName;
        mExercises = new TreeMap<Exercise, List<WorkoutSet>>();
    }

    public WeightWorkout(String workoutName, int workoutNumber, String date) {
        mWorkoutName = workoutName;
        mWorkoutNumber = workoutNumber;
        mDate = date;
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

    /**
     * Set the workout number to the passed value.
     *
     * @param workoutNumber the workout number for this workout
     */
    public void setWorkoutNumber(int workoutNumber) {
        mWorkoutNumber = workoutNumber;
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

    /**
     * Set the date for this workout to the passed value.
     *
     * @param mDate the date this workout was completed
     */
    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public static String parsePreDefinedWorkoutJSON(String weightWorkoutJSON, List<WeightWorkout> weightWorkoutList) {
        String reason = null;
        Log.e("MYTAG", weightWorkoutJSON);
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
        Log.e("EXERCISE_TAG", weightWorkoutJSON);
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
//                    String workoutName = obj.getString(NAME);
//                    Exercise exercise = new Exercise(obj.getString(Exercise.NAME));
//                    WorkoutSet set = new WorkoutSet(exercise.getExerciseName(), obj.getInt(WorkoutSet.REPETITIONS),
//                            obj.getInt(WorkoutSet.SET_NUMBER), obj.getInt(WorkoutSet.WEIGHT));
//                    int check = checkContains(workoutName, weightWorkoutList);
//                    if (check > -1) {
//                        weightWorkoutList.get(check).addExercise(exercise, set);
//                        weightWorkoutList.get(check).setWorkoutNumber(obj.getInt(NUMBER));
//                    } else {
//                        WeightWorkout weightWorkout = new WeightWorkout(obj.getString(NAME));
//                        weightWorkout.addExercise(exercise, set);
//                        weightWorkoutList.add(weightWorkout);
//                    }
                    String exerciseName = obj.getString(Exercise.NAME);
                    WorkoutSet set = new WorkoutSet(exerciseName,
                            obj.getInt(WorkoutSet.REPETITIONS),
                            obj.getInt(WorkoutSet.SET_NUMBER),
                            obj.getInt(WorkoutSet.WEIGHT));
                    int check =  checkContainsExercise(exerciseName, exerciseList);
                    Log.e("CHECKIS", check + "");
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
        Log.e("EXERCISE_TAG", weightWorkoutJSON);
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    WeightWorkout workout = new WeightWorkout(obj.getString(NAME),
                            obj.getInt(NUMBER), obj.getString(DATE).toString());
                    weightWorkoutList.add(workout);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    /**
     * Check if the passed list already contains the workout based on the passed workout name.
     *
     * @param workoutName the workout name being searched for
     * @param workoutList the list of workouts being searched
     * @return the index in the list if found, -1 otherwise
     */
    private static int checkContains(String workoutName, List<WeightWorkout> workoutList) {
        for (int i = 0; i < workoutList.size(); i++) {
            if (workoutName.equals(workoutList.get(i).getWorkoutName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Return all of the exercises and their corresponding sets for this weight workout.
     *
     * @return all of the exercises and their corresponding sets for this weight workout.
     */
    public Map<Exercise, List<WorkoutSet>> getExercises() {
        return mExercises;
    }

    @Override
    public String toString() {
        return mWorkoutName + ", " + mWorkoutNumber + ", " + mExercises.toString();
    }

}
