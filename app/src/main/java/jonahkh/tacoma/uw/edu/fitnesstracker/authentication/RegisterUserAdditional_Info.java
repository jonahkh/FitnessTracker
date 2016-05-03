package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * A simple {@link Fragment} subclass.
 */
public class RegisterUserAdditional_Info extends Fragment {

    private final static String USER_DELETE_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/deleteUser.php?";

    public final String TAG = "Reg Additional Info";

    public final int INVALID = -1;

    private byte[] mPhoto = null;

    private int mDateDOB;

    private int mMonthDOB;

    private int mYearDOB;

    private int mWeight;

    private int mHeightFt;

    private int mHeightIn;

    private char mGender;

    private String mActivityLevel;

    private int mDaysToWorkout;

    private SharedPreferences mSharedPreferences;

    public RegisterUserAdditional_Info() {
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
                if(mDateDOB != INVALID && mMonthDOB != INVALID && mYearDOB != INVALID
                        && mWeight != INVALID && mHeightFt != INVALID && mHeightIn != INVALID){
                    ((RegisterUserActivity)getActivity()).setUserAdditionInfo(mPhoto, mDateDOB, mMonthDOB,
                            mYearDOB, mWeight, mHeightFt, mHeightIn, mGender, mActivityLevel, mDaysToWorkout);

                    String url = ((RegisterUserActivity)getActivity()).buildAddUserAddtionaIfoURL();
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
                task.execute(new String[]{url});

                // Load the login activity.
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return myView;
    }



    //                    boolean addedUserAddtionalInfo = ((RegisterUserActivity)getActivity()).mSuccesful;
//                    if(addedUserAddtionalInfo) {
    // Go to dashboard
//                        (Toast.makeText(getActivity().getApplicationContext(),
//                                R.string.registration_sucessful, Toast.LENGTH_SHORT)).show();
//                        Intent intent = new Intent(getActivity().getApplicationContext(), DashboardActivity.class);
//                        startActivity(intent);
//                        getActivity().getSupportFragmentManager().popBackStackImmediate();
//                    }

    private int getDaysToWorkout(View myView) {
        Spinner activitySpinner = (Spinner) myView.findViewById(R.id.daysToWorkout_spinner);
        String numDays = activitySpinner.getSelectedItem().toString();
        return Integer.parseInt(numDays);
    }

    private String getActivityLevel(View myView) {
        Spinner activitySpinner = (Spinner) myView.findViewById(R.id.activtiyLv_spinner);
        String activityLv = activitySpinner.getSelectedItem().toString();
        return activityLv;
    }


    private char getGender(View myView) {
        Spinner genderSpinner = (Spinner) myView.findViewById(R.id.gender_spinner);
        String gender = genderSpinner.getSelectedItem().toString();
        return gender.charAt(0);
    }

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

    public int getMonthSelected(View v) {
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

    /** AsyncTask class called CourseAddAsyncTask that will allow us to call the service for
     * deleting a user. */
    private class DeleteUserTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to delete user, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {

                    // Store user email and record that they are logged in
                    mSharedPreferences  = getActivity().getSharedPreferences(
                            getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                    mSharedPreferences.edit().putString(getString(R.string.current_email), "")
                            .commit();
                    mSharedPreferences.edit().putBoolean(getString(R.string.logged_in), false)
                            .commit();
                } else {
                    Log.e(TAG, "Failed to delete user, Reason: " + jsonObject.get("error"));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Something wrong with the email" + e.getMessage());
            }
        }
    }
}
