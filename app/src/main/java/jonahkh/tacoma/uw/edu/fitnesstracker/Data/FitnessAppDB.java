/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.model.CardioWorkout;

/**
 * This app's internal database.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class FitnessAppDB {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "FitnessApp.db";
    private static final String CARDIO_WORKOUT_TABLE = "CardioWorkout";

    private final FitnessAppDBHelper mCardioWorkoutDBHelper;
    private final SQLiteDatabase mSQLiteDatabase;

    /** Construtor of this Fitness App Database. */
    public FitnessAppDB(Context context) {
        mCardioWorkoutDBHelper = new FitnessAppDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCardioWorkoutDBHelper.getWritableDatabase();
    }


    /**
     * Inserts the cardio workout into the local sqlite table.
     * Returns true if successful, false otherwise.
     *
     * @param workoutNumber The cardio workout Number.
     * @param dateCompleted The date the cardio workout was entered.
     * @param duration The duration of the cardio workout.
     * @param workoutName The name of the cardio workout.
     * @param email This users email.
     * @param distance The distance the cardio workout
     * @return Either true or false.
     */
    public boolean insertCardioWorkout(int workoutNumber, String dateCompleted,
                                       int duration, String workoutName,
                                       String email, double distance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("workoutNumber", workoutNumber);
        contentValues.put("dateCompleted", dateCompleted);
        contentValues.put("duration", duration);
        contentValues.put("workoutName", workoutName);
        contentValues.put("email", email);
        contentValues.put("distance", distance);
        long rowId = mSQLiteDatabase.insert(CARDIO_WORKOUT_TABLE, null, contentValues);
        return rowId != -1;
    }

    /** Method to close the database so that the file resource is closed. */
    public void closeDB() {
        mSQLiteDatabase.close();
    }


    /**
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public List<CardioWorkout> getCourses() {

        String[] columns = {
                "workoutNumber", "dateCompleted", "duration", "workoutName", "email",
                "distance"
        };

        Cursor c = mSQLiteDatabase.query(
                CARDIO_WORKOUT_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<CardioWorkout> list = new ArrayList<>();
        for (int i=0; i<c.getCount(); i++) {
            int workoutNumber = c.getInt(0);
            String dateCompleted = c.getString(1);
            int duration = c.getInt(2);
            String workoutName = c.getString(3);
//            String email = c.getString(4);
            double distance = c.getDouble(5);
            CardioWorkout cardioWorkout = new CardioWorkout(workoutNumber,dateCompleted,duration,
                    workoutName, distance);
            list.add(cardioWorkout);
            c.moveToNext();
        }

        return list;
    }

    /**
     * Delete all the data from the CARDIO_WORKOUT_TABLE
     */
    public void deleteCardioWorkouts() {
        mSQLiteDatabase.delete(CARDIO_WORKOUT_TABLE, null, null);
    }

    /** Fitness App SqLiteOpenHelper class. */
    class FitnessAppDBHelper extends SQLiteOpenHelper {

        /** String command to create CardioWorkout table. */
        private static final String CREATE_COURSE_SQL =
                "CREATE TABLE IF NOT EXISTS CardioWorkout "
                        + "(workoutNumber INTEGER, dateCompleted TEXT, duration INTEGER,"
                        +"workoutName TEXT, email TEXT, distance DOUBLE, "
                        +"PRIMARY KEY (workoutNumber, email))";

        /** String command to drop CardioWorkout Table. */
        private static final String DROP_COURSE_SQL =
                "DROP TABLE IF EXISTS CardioWorkout";

        /** Constructor */
        public FitnessAppDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_COURSE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_COURSE_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
