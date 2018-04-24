/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.domain.ExerciseRequest;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.domain.StartWorkoutRequest;
import jonahkh.tacoma.uw.edu.fitnesstracker.services.JsonUtilities;
import jonahkh.tacoma.uw.edu.fitnesstracker.services.RestClient;

/**
 * This class represents the dialog that appears when the user is attempting to add a set. The user
 * can enter the weight used and repetitions performed. If the user performed a body-weight
 * exercise, they can enter '0' to indicate that no weight was used.
 */
public class AddSetFragment extends DialogFragment {

    private static final String BASE_ENDPOINT = "localhost:5000/v1/fitnesstracker/";

    /** The url to start a new workout. */
    private static final String WORKOUT_URL
            = "localhost:5000/v1/fitnesstracker/weightWorkout.php?cmd=start_workout";

    /** Url to add a new set. */
    private static final String ADD_EXERCISE_URL
            = "localhost:5000/v1/fitnesstracker/weightWorkout.php?cmd=addSet";

    /** The current weight workout. */
    private WeightWorkout mCurrentWorkout;

    /** The current email. */
    private String mCurrentEmail;

    /** The current workout number. */
    private int mWorkoutNum;

    /** The current exercise name. */
    private String mCurrentExercise;

    /** The current set number. */
    private int mSetNumber = 1;

    /** Initialize a new AddSet Fragment. Empty by default. */
    public AddSetFragment() {
        // Required empty public constructor
    }

    /**
     * Set the current exercise name to the passed value.
     *
     * @param name the name of the current exercise
     */
    public void setExerciseName(final String name) {
        mCurrentExercise = name.replace(' ', '_');
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DashboardActivity dashboard = (DashboardActivity) getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = dashboard.getLayoutInflater();
        final GetWorkoutNumber task = new GetWorkoutNumber();
        @SuppressLint("InflateParams") final View v = inflater.inflate(R.layout.add_set_dialog, null);
        final EditText weight = (EditText) v.findViewById(R.id.enter_weight);
        final EditText reps = (EditText) v.findViewById(R.id.enter_reps);
        final Button addSet = (Button) v.findViewById(R.id.add_set);
        mCurrentWorkout = dashboard.getCurrentWorkout();
        mCurrentEmail = dashboard.getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");
        final String url = BASE_ENDPOINT + "getlastworkout/&email="  + mCurrentEmail;
        Log.e("addSet", url);
        task.execute(url);
        builder.setView(v);
        builder.setTitle("Enter Set Information");
        final Dialog dialog = builder.create();
        addSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidData(weight, reps)) {
                    SendWorkoutDataTask newTask = new SendWorkoutDataTask(buildRequest());
                    // If it's the first set, add a new Recorded Workout in database
                    if (mSetNumber == 1) {
                        newTask.execute(BASE_ENDPOINT + "startWorkout");
                    }
//                    String param = ADD_EXERCISE_URL + "&set=" + mSetNumber + "&wNum="
//                            + mWorkoutNum + "&name=" + mCurrentExercise + "&reps="
//                            + reps.getText().toString() + "&email=" + mCurrentEmail + "&weight="
//                            +  weight.getText().toString();
                    // Send set data to the server
                    SendWorkoutDataTask setTask = new SendWorkoutDataTask(new ExerciseRequest(mSetNumber,
                            mWorkoutNum,
                            mCurrentExercise,
                            reps.getText().toString(),
                            mCurrentEmail,
                            weight.getText().toString())
                    );
                    setTask.execute(BASE_ENDPOINT + "addexercise");
                    Toast.makeText(getActivity().getApplicationContext(), "Set Successfully added!",
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    mSetNumber++;
                }
            }
        });
        Button cancel = (Button) v.findViewById(R.id.cancel_set);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(getActivity().getApplicationContext(), "Set not added!", Toast.LENGTH_LONG).show();
            }
        });
        return dialog;
    }

    /**
     * Build the url to add the workout to the server.
     *
     * @return the url to add the workout to the server
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    private StartWorkoutRequest buildRequest() {

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

//        url.append(year + "-" + month + "-" + day);
        final StartWorkoutRequest startWorkoutRequest = new StartWorkoutRequest(mWorkoutNum,
                mCurrentWorkout.getWorkoutName(),
                year + "-" + month + "-" + day,
                mCurrentEmail,
                "weight");

        return startWorkoutRequest;
    }

    /**
     * Checks that the user has entered valid data.
     *
     * @param weight the weight edit text
     * @param reps the reps edit text
     * @return true if the data was valid, false otherwise
     */
    private boolean checkValidData(EditText weight, EditText reps) {
        boolean check = true;
        if (weight.getText().toString().equals("")
                || Integer.parseInt(weight.getText().toString()) < 0) {
            weight.setError("Invalid data! Must be positive or zero!");
            check = false;
        }
        if (reps.getText().toString().equals("")
                || Integer.parseInt(reps.getText().toString()) < 0) {
            reps.setError("Invalid data! Must be positive or zero!");
            check = false;
        }
        return check;
    }

    /** Private class to get the information about the last logged workout from user. */
    private class SendWorkoutDataTask extends AsyncTask<String, Void, String> {
        private String requesetJson;

        SendWorkoutDataTask(StartWorkoutRequest startWorkoutRequest) {
            this.requesetJson = JsonUtilities.toJson(startWorkoutRequest);
        }

        SendWorkoutDataTask(ExerciseRequest exerciseRequest) {
            this.requesetJson = JsonUtilities.toJson(exerciseRequest);
        }
        @Override
        protected String doInBackground(String... urls) {
            return RestClient.runRequest("POST", JsonUtilities.toJson(requesetJson), urls);
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.contains("Error")) {
                Log.e("JSON Error", result);
            }
        }
    }

    /** Private class to get the information about the last logged workout from user. */
    private class GetWorkoutNumber extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return RestClient.runRequest("GET", null, urls);
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            int num;
            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    num = obj.getInt(DashboardDisplayFragment.WORKOUT_NUMBER);
                    if (num > mWorkoutNum) {
                        mWorkoutNum = num;
                    }
                }
            } catch (JSONException e) {
                Log.e("Dashboard", Arrays.toString(e.getStackTrace()));
            }
        }
    }

}
