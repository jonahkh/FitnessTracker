/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Integer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;


/**
 * Fragment used to enter users additional information when registering.
 * A simple {@link Fragment} subclass.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class RegisterUserAdditionalInfoFragment extends Fragment {

    /** URL used to delete the user information from database. */
    private final static String USER_DELETE_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/deleteUser.php?";

    /** Tag used for debugging. */
    private final String TAG = "Reg Additional Info";

    /** Field used to check that all the required information is entered. */
    private final int INVALID = -1;

    /** Users photo, null for now for this stage of project.*/
    private final byte[] mPhoto = null;

    /** Users Date from his/hers DOB.*/
    private int mDateDOB;

    /** Users Month from his/hers DOB.*/
    private int mMonthDOB;

    /** Users year from his/hers DOB.*/
    private int mYearDOB;

    /** Users weight. */
    private int mWeight;

    /** Users Height in Feet. */
    private int mHeightFt;

    /** Users Height in Inches. */
    private int mHeightIn;

    /** Users gender. */
    private char mGender;

    /** Users activity level. */
    private String mActivityLevel;

    /** Number of days the user worksout. */
    private int mDaysToWorkout;
    private ImageView mImageView;

    /** Required empty public constructor */
    public RegisterUserAdditionalInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_register_user_additional__info,
                container, false);

        Button register = (Button)myView.findViewById(R.id.registerUser_bt);
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDateDOB = getDateSelected(myView);
                mMonthDOB = getMonthSelected(myView);
                mYearDOB = getYearEntered(myView);
                mWeight = getWeightEntered(myView);
                mHeightFt = getHeightFt(myView);
                mHeightIn = getHeightIn(myView);
                mGender = getGender(myView);
                mActivityLevel = getActivityLevel(myView);
                mDaysToWorkout = getDaysToWorkout(myView);

                // Check that the entered information is valid.
                if(mDateDOB != INVALID && mMonthDOB != INVALID && mYearDOB != INVALID
                        && mWeight != INVALID && mHeightFt != INVALID && mHeightIn != INVALID){

                    ((RegisterUserActivity)getActivity()).setUserAdditionInfo(mPhoto, mDateDOB,
                            mMonthDOB, mYearDOB, mWeight, mHeightFt, mHeightIn, mGender,
                            mActivityLevel, mDaysToWorkout);

                    String url = ((RegisterUserActivity)getActivity()).buildAddUserAdditionaIfoURL();
                    ((RegisterUserActivity)getActivity()).addUserData(url);
                }
            }
        });

        Button cancelBut = (Button) myView.findViewById(R.id.registerUser_cancelBt);
        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // delete email and password from user
                String email = ((RegisterUserActivity)getActivity()).getUserEmail();
                String url = USER_DELETE_URL + "email=" + email;
                DeleteUserTask task = new DeleteUserTask();
                Log.i(TAG, url);
                task.execute(url);
            }
        });

        mImageView = (ImageView) myView.findViewById(R.id.add_pic);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPictureFragment fragment = new AddPictureFragment();
                fragment.show(getActivity().getSupportFragmentManager(), "launch");
            }
        });

        return myView;
    }

    /**
     * Gets the number of days the user entered to workout.
     *
     * @param myView The view.
     * @return The number of days the users workout.
     */
    private int getDaysToWorkout(View myView) {
        Spinner activitySpinner = (Spinner) myView.findViewById(R.id.daysToWorkout_spinner);
        String numDays = activitySpinner.getSelectedItem().toString();
        return Integer.parseInt(numDays);
    }

    /**
     * Gets the activity level selected by user.
     *
     * @param myView The view.
     * @return The activity level selected by user.
     */
    private String getActivityLevel(View myView) {
        return ((Spinner) myView.findViewById(R.id.activtiyLv_spinner))
                .getSelectedItem().toString();
    }

    /**
     * Gets the users gender entered.
     *
     * @param myView The view.
     * @return The users gender entered.
     */
    private char getGender(View myView) {
        Spinner genderSpinner = (Spinner) myView.findViewById(R.id.gender_spinner);
        String gender = genderSpinner.getSelectedItem().toString();
        return gender.charAt(0);
    }

    /**
     * Gets the users height in Feet.
     *
     * @param myView The view.
     * @return The users height in Feet.
     */
    private int getHeightIn(View myView) {
        TextView heightIn = (TextView) myView.findViewById(R.id.registerUser_heightIn);
        if(TextUtils.isEmpty(heightIn.getEditableText())){
            heightIn.setError(getString(R.string.error_field_required));
            heightIn.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(heightIn.getEditableText().toString());
        Log.i(TAG, "Height Entered (in): " + temp);
        return temp;
    }

    /**
     * Gets the users height in Feet.
     *
     * @param myView The view.
     * @return The users height in Feet.
     */
    private int getHeightFt(View myView) {
        TextView heightFt = (TextView) myView.findViewById(R.id.registerUser_heightFt);
        if(TextUtils.isEmpty(heightFt.getEditableText())){
            heightFt.setError(getString(R.string.error_field_required));
            heightFt.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(heightFt.getEditableText().toString());
        Log.i(TAG, "Height Entered (Ft): " + temp);
        return temp;
    }

    /**
     * Gets the users weight entered.
     *
     * @param myView The view.
     * @return The users weight entered.
     */
    private int getWeightEntered(View myView) {
        TextView weightV = (TextView) myView.findViewById(R.id.registerUser_weight);
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
     * Gets the users Year from his/hers DOB.
     *
     * @param myView The view.
     * @return The users Year from his/hers DOB.
     */
    private int getYearEntered(View myView) {
        TextView yearV = (TextView) myView.findViewById(R.id.year_entered);
        if(TextUtils.isEmpty(yearV.getEditableText())){
            yearV.setError(getString(R.string.error_field_required));
            yearV.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(yearV.getEditableText().toString());
        Log.i(TAG, "DOB Year Entered: " + temp);
        return temp;
    }

    /**
     * Gets the users Date from his/hers DOB.
     *
     * @param v The view.
     * @return The users Date from his/hers DOB.
     */
    private int getDateSelected(View v) {
        Spinner daySpinner= ((Spinner) v.findViewById(R.id.day_spinner));
        String day = daySpinner.getSelectedItem().toString();
        if(day.equals("Date")) {
            (Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_day_required, Toast.LENGTH_SHORT)).show();
            daySpinner.requestFocus();
            return INVALID;
        }
        int daySelec = Integer.parseInt(day);
        Log.i(TAG, "Day Selected: " + daySelec);
        return daySelec;
    }

    /**
     * Gets the users Month from his/hers DOB.
     *
     * @param v The view.
     * @return The users Month from his/hers DOB.
     */
    private int getMonthSelected(View v) {
        Spinner monthSpinner = (Spinner) v.findViewById(R.id.month_spinner);
        String tempMonth = monthSpinner.getSelectedItem().toString();
        if(tempMonth.equals("Month")){
            (Toast.makeText(getActivity().getApplicationContext(),
                    R.string.error_month_required, Toast.LENGTH_SHORT)).show();
            monthSpinner.requestFocus();
            return INVALID;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(tempMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int monthSelec = cal.get(Calendar.MONTH) + 1;
        Log.i(TAG, "Month Selected (int): " + monthSelec);
        return monthSelec;
    }

    /** AsyncTask class called DeleteUserTask that will allow us to call the service for
     * deleting a user. */
    private class DeleteUserTask extends AsyncTask<String, Void, String> {


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

                    // Store user email and record that they are logged in
                    SharedPreferences sharedPreferences  = getActivity().getSharedPreferences(
                            getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString(getString(R.string.current_email), "")
                            .apply();
                    sharedPreferences.edit().putBoolean(getString(R.string.logged_in), false)
                            .apply();
                    // Load the login activity.
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Log.e(TAG, "Failed to delete user, Reason: " + jsonObject.get("error"));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Something wrong with the email" + e.getMessage());
            }
        }
    }
}
