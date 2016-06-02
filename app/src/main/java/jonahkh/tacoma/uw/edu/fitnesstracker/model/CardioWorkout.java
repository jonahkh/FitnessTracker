/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents an exercise. It stores the exercise name and a list of all of the sets
 * the user has completed for this exercise.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class CardioWorkout implements Serializable{

    /** Error do display when there is no data. */
    private static final String ERROR = "No Logged Workouts to Display!";

    /** Name of the workout number for this workout. */
    private static final String NUMBER = "workoutNumber";

    /** Name of the date in the weight workout table. */
    private static final String DATE = "dateCompleted";

    /** The duration of the exercise. */
    private static final String DURATION = "duration";

    /** Name of the exercise in the exercise table. */
    private static final String NAME = "workoutName";

    /** The distance of the exercise if applicable. */
    private static final String DISTANCE = "distance";

    /** The current exercise number. */
    private int mWorkoutNumber;

    /** The date this exercise was completed. */
    private String mDate;

    /** The duration of the exercise. */
    private int mDuration;

    /** The name of the exercise. */
    private String mActivityName;

    /** The distance of the exercise. */
    private double mDistance;

    /**
     * Initialize a new Exercise. the name must be non-null and at least one character.
     *
     * @param name the name of this exercise
     */
    public CardioWorkout(final String name) {
        this(name, 1, "", 0, 0.0);
    }

    /**
     * Initializes an exercise activity given a name, workout number, date, duration,
     * and distance.
     *
     * @param name The name of the exercise.
     * @param workoutNumber The current exercise number.
     * @param date The date this exercise was completed.
     * @param duration The duration of the exercise.
     * @param distance The distance of the exercise.
     */
    private CardioWorkout(String name, int workoutNumber, String date, int duration,
                          double distance) {
        if (name == null || name.length() < 1 || workoutNumber <0 ||
                date == null || duration < 0 || distance < 0) {
            throw new IllegalArgumentException();
        }
        mActivityName = name;
        if(workoutNumber == 0) {
            mWorkoutNumber = 1;
        } else {
            mWorkoutNumber = workoutNumber;
        }
        mDate = date;
        mDuration = duration;
        mDistance = distance;
    }

    /**
     * Initializes an exercise activity given a name, workout number, date, duration,
     * and distance.
     *
     * @param workoutNumber The current exercise number.
     * @param dateCompleted he date this exercise was completed.
     * @param duration The duration of the exercise.
     * @param workoutName The name of the exercise.
     * @param distance The distance of the exercise.
     */
    public CardioWorkout(int workoutNumber, String dateCompleted, int duration,
                         String workoutName, double distance) {
        if (workoutName == null || workoutName.length() < 1 || workoutNumber < 0 ||
                dateCompleted == null || duration < 0 || distance < 0) {
            throw new IllegalArgumentException();
        }
        mActivityName = workoutName;
        if(workoutNumber == 0) {
            mWorkoutNumber = 1;
        } else {
            mWorkoutNumber = workoutNumber;
        }
        mDate = dateCompleted;
        mDuration = duration;
        mDistance = distance;
    }

    /**
     * Parse the passed input and convert into a new weight workout.
     *
     * @param exerciseActivityJSON the input being parsed (pulled from database)
     * @param exerciseList the list being populated based on the input being parsed
     * @return null if no issues, otherwise return the error that occurred
     */
    public static String parseCardioExercisesJSON(String exerciseActivityJSON, List<CardioWorkout> exerciseList) {
        if(exerciseActivityJSON.length() < 1) {
            return ERROR;
        }
        String reason = null;
        try {
            JSONArray arr = new JSONArray(exerciseActivityJSON);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int workoutNumber = obj.getInt(NUMBER);
                String dateCompleted = obj.getString(DATE);
                int duration = obj.getInt(DURATION);
                String exerciseName = obj.getString(NAME);
                double distance = obj.getDouble(DISTANCE);
//                    Log.i("TEST", "" + check);
                Log.i("Tat", obj.toString());
                CardioWorkout exercise = new CardioWorkout(exerciseName, workoutNumber,
                        dateCompleted, duration, distance);
                exerciseList.add(exercise);
            }
        } catch (JSONException e) {
            reason =  e.getMessage();
        }
        return reason;
    }

    /**
     * Checks if the passed list already contains the exercise.
     *
     * @param name The name of the exercise.
     * @param exercises The list of exercises.
     * @return Either negative one or the index of where the exercise is at in the list.
     */
    private static int checkContainsExercise(String name, List<CardioWorkout> exercises) {
        for (int i = 0; i < exercises.size(); i++) {
            if (name.equals(exercises.get(i).getActivityName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Methdo to get the workoutNumber.
     *
     * @return The workout Number of this cardio workout.
     */
    public int getWorkoutNumber() {
        return mWorkoutNumber;
    }

//    public void setWorkoutNumber(int mWorkoutNumber) {
//        this.mWorkoutNumber = mWorkoutNumber;
//    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public int getDuration() {
        return mDuration;
    }

//    public void setDuration(int mDuration) {
//        this.mDuration = mDuration;
//    }

    public String getActivityName() {
        return mActivityName;
    }

//    public void setActivityName(String mActivityName) {
//        this.mActivityName = mActivityName;
//    }

    public double getDistance() {
        return mDistance;
    }

//    public void setDistance(double mDistance) {
//        this.mDistance = mDistance;
//    }

    @Override
    public String toString() {
        return mActivityName + ", " + mWorkoutNumber;
    }
}
