/*
 * TCSS 450 FitnessTracker
 * Jonah Howard
 * Hector Diaz
 */

package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * Fragment used to edit the personal information of the user.
 * A simple {@link Fragment} subclass.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class EditPersonalInformationFragment extends Fragment {

    /** URL used get update user additional information from database. */
    private static final String USER_INFO
            = "http://cssgate.insttech.washington.edu/~_450atm2/updateUserData.php?";

    /** Tag used for debugging. */
    private static final String TAG = "Edit Personal Info";

    /** Field used to check that all the required information is entered. */
    private final int INVALID = -1;

    /** Users weight. */
    private int mWeight;

    /** Users activity level. */
    private String mActivityLevel;

    /** Number of days the user works out. */
    private int mDaysToWorkout;

    /** Users email. */
    private String mUserEmail;

    /** Required empty public constructor */
    public EditPersonalInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_edit_personal_information, container, false);

        mUserEmail = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");

        // cancel updating info
        Button cancel = (Button)myView.findViewById(R.id.epi_cancelBt);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        Button save = (Button)myView.findViewById(R.id.epi_save);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mWeight = getWeightEntered(myView);
                mActivityLevel = getActivityLevel(myView);
                mDaysToWorkout = getDaysToWorkout(myView);

                if(mWeight != INVALID){
                    String url = null;
                    try {
                        url = getUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, url);
                    UpdateUserDataTask task = new UpdateUserDataTask();
                    task.execute(url);
                }
            }
        });

        return myView;
    }

    /**
     * Gets the users weight entered.
     *
     * @param myView The view.
     * @return The users weight entered.
     */
    private int getWeightEntered(View myView) {
        TextView weightV = (TextView) myView.findViewById(R.id.epi_weight);
        if(TextUtils.isEmpty(weightV.getEditableText())){
            weightV.setError(getString(R.string.error_field_required));
            weightV.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(weightV.getEditableText().toString());
        Log.i(TAG, "Weight Entered: " + temp);
        return temp;
    }

    /**
     * Gets the activity level selected by user.
     *
     * @param myView The view.
     * @return The activity level selected by user.
     */
    private String getActivityLevel(View myView) {
        return ((Spinner) myView.findViewById(R.id.epi_activtiyLv_spinner))
                .getSelectedItem().toString();
    }

    /**
     * Gets the number of days the user entered to workout.
     *
     * @param myView The view.
     * @return The number of days the users workout.
     */
    private int getDaysToWorkout(View myView) {
        Spinner activitySpinner = (Spinner) myView.findViewById(R.id.epi_daysToWorkout_spinner);
        String numDays = activitySpinner.getSelectedItem().toString();
        return Integer.parseInt(numDays);
    }

    /**
     * Gets the url needed to launch the UpdateUserDataTask.
     *
     * @return The URL needed to launch the UpdateUserDataTask.
     *
     * @throws UnsupportedEncodingException Incase the activity level cannot be converted to string.
     */
    @NonNull
    private String getUrl() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(USER_INFO);
        sb.append("email=");
        sb.append(mUserEmail);

        sb.append("&weight=");
        sb.append(mWeight);

        sb.append("&daysToWorkout=");
        sb.append(mDaysToWorkout);

        sb.append("&activityLevel=");
        sb.append(URLEncoder.encode(mActivityLevel, "UTF-8"));

        return sb.toString();
    }

    /** AsyncTask class called UpdateUserDataTask that will allow us to call the update
     * user information. */
    private class UpdateUserDataTask extends AsyncTask<String, Void, String> {

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
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Information Updated!"
                            , Toast.LENGTH_SHORT)
                            .show();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Log.e(TAG, "Failed to update Error: " + jsonObject.get("error"));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem updating user info, something wrong: " + e.getMessage());
            }
        }
    }
}
