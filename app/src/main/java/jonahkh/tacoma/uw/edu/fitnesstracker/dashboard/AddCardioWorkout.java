/*
 * TCSS 450 FitnessTracker
 * Jonah Howard
 * Hector Diaz
 */

package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Calendar;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * Fragment used to add a cardio exercise.
 * A simple {@link Fragment} subclass.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class AddCardioWorkout extends Fragment {

    /** URL used get update user additional information from database. */
    private static final String ADD_CARDIO_EXERCISE
            = "http://cssgate.insttech.washington.edu/~_450atm2/AddCardioExercise.php?";
    private static final double INVALID_DOUBLE = -1.0;

    /** Field used to check that all the required information is entered. */
    private final int INVALID = -1;

    /** Tag used for debugging. */
    public static final String TAG = "New Cardio Exercise";

    /** Floating action button.*/
    private FloatingActionButton mFab;

    /** Required empty public constructor */
    public AddCardioWorkout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_cardio_exercise, container, false);
        final String userEmail = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");
        final int cardioExerciseNum = getActivity().getSharedPreferences(getString(R.string.DASHBOARD_PREFS),
                Context.MODE_PRIVATE).getInt(getString(R.string.next_cardio_exercise_num),
                0);
        final String dateofCardioExercise = getDateofExercise();
        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab_cardio_workout);
        Button addCardioWorkout = (Button)view.findViewById(R.id.cardio_activity_save);
        addCardioWorkout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String cardioAtivityName = getCardioActivityName();
                int activityDuration = getCardioDuration();
                double activityDistance = getCardioDistance();
                // Check that the entered information is valid.
                if(cardioAtivityName.length() > 0 && activityDuration != INVALID &&
                        activityDistance != INVALID_DOUBLE) {
                    String url = buildAddUserAdditionaIfoURL(cardioExerciseNum,
                            dateofCardioExercise, activityDuration,
                            cardioAtivityName, userEmail, activityDistance);
                    AddUserCadioWorkout task = new AddUserCadioWorkout();
                    task.execute(url);
                }
            }
        });

        Button cancel = (Button)view.findViewById(R.id.cardio_activity_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                mFab.show();
            }
        });

        return view;
    }

    /** Method that will build the url for calling the AsyncTask.
     *
     * @param cardioExerciseNum The this cardio exercise.
     * @param dateofCardioExercise The date of the exercise is entered.
     * @param activityDuration The time the activity was performed for.
     * @param cardioAtivityName The name of the cardio exercise.
     * @param userEmail The email of the user.
     * @param activityDistance The distance of the activity if applicable.
     */
    public String buildAddUserAdditionaIfoURL(int cardioExerciseNum,
                                              String dateofCardioExercise,
                                              int activityDuration,
                                              String cardioAtivityName,
                                              String userEmail,
                                              double activityDistance) {

        StringBuilder sb = new StringBuilder(ADD_CARDIO_EXERCISE);
        // //  workoutNumber | dateCompleted | duration | workoutName | email          | distance
        try {
            sb.append("workoutNumber=");
            sb.append(cardioExerciseNum);


            sb.append("&dateCompleted=");
            sb.append(URLEncoder.encode(dateofCardioExercise, "UTF-8"));

            sb.append("&duration=");
            sb.append(activityDuration);

            sb.append("&workoutName=");
            sb.append(URLEncoder.encode(cardioAtivityName, "UTF-8"));


            sb.append("&email=");
            sb.append(URLEncoder.encode(userEmail, "UTF-8"));

            sb.append("&distance=");
            sb.append(activityDistance);
            Log.i(TAG, sb.toString());
        }
        catch(Exception e) {
            Log.e(TAG, "Something wrong with the url" + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Method to get the distance of the cardio activity.
     * @return Returns 0.0 if no distance is entered else the distance entered.
     */
    private double getCardioDistance() {
        EditText activityDistance =(EditText)getActivity().
                findViewById(R.id.cardio_activity_distance);
        if(TextUtils.isEmpty(activityDistance.getEditableText())){
            return 0.0;
        }
        double temp = INVALID_DOUBLE;
        try {
            temp = Double.parseDouble(activityDistance.getEditableText().toString());
        } catch(NumberFormatException e) {
            activityDistance.setError(getString(R.string.invalid_input));
            activityDistance.requestFocus();
        }
        Log.d(TAG, "Cardio workout Distance: " + temp);
        return temp;
    }

    /**
     * Method to get the duration of the cardio activity.
     *
     * @return The duration of cardio exercise.
     */
    private int getCardioDuration() {
        EditText activityDuration = (EditText)getActivity().
                findViewById(R.id.cardio_activity_duration);
        if(TextUtils.isEmpty(activityDuration.getEditableText())){
            activityDuration.setError(getString(R.string.error_field_required));
            activityDuration.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(activityDuration.getEditableText().toString());
        if(temp < 0) {
            activityDuration.setError(getString(R.string.invalid_input));
            activityDuration.requestFocus();
            return INVALID;
        }
        Log.d(TAG, "Cardio workout Duration: " + temp);
        return temp;
    }

    /**
     * Method to get the name of the cardio activity.
     *
     * @return The name of cardio exercise.
     */
    private String getCardioActivityName() {
        EditText activityName = (EditText)getActivity().
                findViewById(R.id.cardio_activity_name);
        if(TextUtils.isEmpty(activityName.getEditableText())){
            activityName.setError(getString(R.string.error_field_required));
            activityName.requestFocus();
            return "";
        }
        String temp = activityName.getEditableText().toString();
        Log.d(TAG, "Cardio workout Name: " + temp);
        return temp;
    }

    /**
     * Method to get today's date. The date of the exercise.
     *
     * @return Todays date.
     */
    public String getDateofExercise() {
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        return year + "-" + month + "-" + day;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mFab.show();
    }

    /** Private class add new user cardio workout. */
    private class AddUserCadioWorkout extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {super.onPreExecute();}

        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result the result of this task
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.cardio_workout_succ
                            , Toast.LENGTH_LONG);
                    mFab.show();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("Error Referece: ")
                            , Toast.LENGTH_LONG)
                            .show();

                }
            } catch (JSONException e) {
                Log.e(TAG, "Something wrong with the data" + e.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
