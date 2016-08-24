/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * This class stores information about weight workouts. Weight workouts store each exercise that
 * was performed for that workout and how many sets for each workout were completed.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class WeightWorkout implements Serializable {

    /** Error message for parsing JSON. */
    private static final String ERROR = "Network Connection Error";

    /** Name of the workout in the weight workout table. */
    private static final String NAME = "workoutName";

    /** Name of the exercise in the weight workout table. */
    private static final String EXERCISE = "exercise";

    /** Name of the exercise in the exercise table. */
    private static final String LOGGED = "exerciseName";

    /** Name of the workout number for this workout. */
    private static final String NUMBER = "workoutNumber";

    /** Identifier for this Serializable object. */
    public static final String WORKOUT_SELECTED = "workout_selected";

    /** Name of the date in the weight workout table. */
    private static final String DATE = "dateCompleted";

    /** The name of the current workout. */
    private final String mWorkoutName;

    /** The current workout number. */
    private int mWorkoutNumber;

    /** The date this workout was completed. */
    private String mDate;

    /**
     * Initialize a new Weight Workout. the name must be non null and at least one character.
     *
     * @param workoutName the workout name for this workout
     */
    public WeightWorkout(String workoutName) {
        this(workoutName, 1, "");
    }

    /**
     * Initializes a new Weight Workout given a name, number, and date.
     *
     * @param workoutName the name of this workout
     * @param workoutNumber the number identifying this workout
     * @param date the date this workout was completed
     */
    public WeightWorkout(String workoutName, int workoutNumber, String date) {
        if (workoutName == null || date == null || workoutName.length() < 1
                || workoutNumber < 1) {
            throw new IllegalArgumentException();
        }
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

    /**
     * Parses JSON and converts to a new Weight workout that is displayed for the Workout Templates
     * menu option. Populates the passed list with all created Weight Workouts.
     *
     * @param weightWorkoutJSON the JSON being parsed
     * @param weightWorkoutList the WeightWorkout list being populated
     * @return null if the parsing was successful, otherwise returns an error message
     */
    public static String parsePreDefinedWeightWorkoutJSON(String weightWorkoutJSON,
                                                          List<WeightWorkout> weightWorkoutList) {
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
                reason =  ERROR;
            }

        }
        return reason;
    }

    /**
     * Parses JSON and converts to a new Exercise. Populates the passed list with all created
     * Exercises.
     *
     * @param weightWorkoutJSON The JSON being parsed
     * @param exerciseList the exercise list being populated
     * @param isLogged true if a logged workout is being redone
     * @return null if the parsing was successful, otherwise returns an error message
     */
    public static String parseWeightWorkoutListExerciseJSON(String weightWorkoutJSON,
                                                            List<Exercise> exerciseList, final boolean isLogged) {
        String reason = null;
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Exercise exercise;
                    if (isLogged) {
                        exercise = new Exercise(obj.getString(LOGGED));
                    } else {
                        exercise = new Exercise(obj.getString(EXERCISE));
                    }
                    exerciseList.add(exercise);
                }
            } catch (JSONException e) {
                reason =  ERROR;
            }

        }
        return reason;
    }

    /**
     * Parse the passed input and convert into a new weight workout.
     *
     * @param weightWorkoutJSON the input being parsed (pulled from database)
     * @param exerciseList the list being populated based on the input being parsed
     * @return null if no issues, otherwise return the error that occurred
     */
    public static String parseExercisesJSON(String weightWorkoutJSON, List<Exercise> exerciseList) {
        String reason = null;
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
                    // If the list already contains the exercise, just add a new set
                    if (check > -1) {
                        exerciseList.get(check).addSet(set);
                    } else {
                        Exercise exercise = new Exercise(exerciseName);
                        exercise.addSet(set);
                        exerciseList.add(exercise);
                    }
                }
            } catch (JSONException e) {
                reason =  ERROR;
            }
        }
        return reason;
    }

    /**
     * Checks if the passed list already contains the exercise.
     *
     * @param name the name of the exercise being compared
     * @param exercises the list of exercises for this workout
     * @return the index of the passed exercise if found or -1
     */
    private static int checkContainsExercise(String name, List<Exercise> exercises) {
        for (int i = 0; i < exercises.size(); i++) {
            if (name.equals(exercises.get(i).getExerciseName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Parse the passed JSON input and converts into a new WeightWorkout. Populates the passed list
     * with these created Weight Workouts.
     *
     * @param weightWorkoutJSON the data being parsed
     * @param weightWorkoutList the list being populated
     * @return null if the parsing was successful, otherwise return the error message
     */
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
                reason =  ERROR;
            }
        }
        return reason;
    }

    @Override
    public String toString() {
        return mWorkoutName + ", " + mWorkoutNumber;
    }

}
