package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.MyViewLoggedWorkoutsRecyclerViewAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.WeightWorkout;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardDisplay extends Fragment {

    public static final String FIRST_NAME = "firstName";

    public static final String LAST_NAME = "lastName";

    public static final String PROFILE_PHOTO = "profilePhoto";

    public static final String BIRTHDAY = "birthDay";

    public static final String WEIGHT = "weigth";

    public static final String HEIGHT_FT = "heightFt";

    public static final String HEIGHT_IN = "heightIn";

    public static final String GENDER = "gender";

    public static final String ACTIVITY_LEVEL = "activityLevel";

    public static final String DAYS_TO_WORKOUT = "daysToWorkout";

    public static final String WORKOUT_NUMBER = "workoutNumber";

    public static final String WORKOUT_NAME = "workoutName";

    public static final String DATE_COMPLETED = "dateCompleted";

    private static final String USER_INFO
            = "http://cssgate.insttech.washington.edu/~_450atm2/additionalInfo.php?";

    private static final String USER_LAST_LOGGED_WORKOUT
            = "http://cssgate.insttech.washington.edu/~_450atm2/getLastUserWorkout.php?";

    private static final String TAG = "Dash Board Display";

    private String mUserEmail;
    private String mUserFirstName;
    private String mUserLastName;
    private byte[] mUserProfilePhoto;
    private String mUserBirhtDay;
    private int mUserWeight;
    private int mUserHeightFt;
    private int mUserHeightIn;
    private String mUserGender;
    private String mUserActivityLevel;
    private int mUserDaysToWorkout;

    private View myView;
    private int mWorkoutNum;
    private String mWorkoutName;
    private String mDateCompleted;


    public DashBoardDisplay() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_dash_board_display, container, false);
        setFieldsPersonalInformation();
        setUserLastLoggedWorkout();

        Button viewLogBut = (Button)myView.findViewById(R.id.dasbB_viewLog_bt);
        viewLogBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ViewExercisesFragment exercises = new ViewExercisesFragment();
                exercises.setWorkout(new WeightWorkout(mWorkoutName, mWorkoutNum, mDateCompleted));
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, exercises)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return myView;
    }

    private void setUserLastLoggedWorkoutView() {
        TextView name = (TextView)myView.findViewById(R.id.dashB_workoutName);
        name.setText(" " + mWorkoutName);

        TextView date = (TextView)myView.findViewById(R.id.dashB_workoutDate);
        date.setText(" " + mDateCompleted);

        TextView number = (TextView)myView.findViewById(R.id.dashB_workoutNumber);
        number.setText(" " + mWorkoutNum);
    }

    private void setUserLastLoggedWorkout() {
        String url = USER_LAST_LOGGED_WORKOUT + "email=" + mUserEmail;
        Log.i(TAG, url);
        UserLastLoggedWorkoutTask task = new UserLastLoggedWorkoutTask();
        task.execute(new String[]{url});
    }

    private void setFieldsPersonalInformation() {
        mUserEmail = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");
        String url = USER_INFO + "email=" + mUserEmail;
        Log.i(TAG, url);
        DownloadUserInfoTask task = new DownloadUserInfoTask();
        task.execute(new String[]{url});
    }

    private void setPersonalDataView() {
        TextView weight = (TextView) myView.findViewById(R.id.dashB_weightV);
        weight.setText("" + mUserWeight);

        TextView activity = (TextView) myView.findViewById(R.id.dashB_activityLevelV);
        activity.setText("" + mUserActivityLevel);

        TextView daysWorkingOut = (TextView) myView.findViewById(R.id.dashB_daysWorkingOutV);
        daysWorkingOut.setText("" + mUserDaysToWorkout);
    }

    /** Private class to download user personal information */
    private class DownloadUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {super.onPreExecute();}

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
                    response = "Unable to download user additional information, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.

            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    mUserFirstName = obj.getString(FIRST_NAME);
                    mUserLastName = obj.getString(LAST_NAME);
                    Object temp = obj.get(PROFILE_PHOTO);
                    if(temp.equals(null)) {
                        mUserProfilePhoto = null;
                    } else {
                        mUserProfilePhoto = (byte[]) temp;
                    }
                    mUserBirhtDay = obj.getString(BIRTHDAY);
                    mUserWeight = obj.getInt(WEIGHT);
                    mUserHeightFt = obj.getInt(HEIGHT_FT);
                    mUserHeightIn = obj.getInt(HEIGHT_IN);
                    mUserGender = obj.getString(GENDER);
                    mUserActivityLevel = obj.getString(ACTIVITY_LEVEL);
                    mUserDaysToWorkout = obj.getInt(DAYS_TO_WORKOUT);
                    setPersonalDataView();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse data, Reason: " + e.getMessage());
            }
        }
    }

    private class UserLastLoggedWorkoutTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {super.onPreExecute();}

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
                    response = "Unable to download user last logged workout, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.

            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    mWorkoutNum = obj.getInt(WORKOUT_NUMBER);
                    mWorkoutName = obj.getString(WORKOUT_NAME);
                    mDateCompleted = obj.getString(DATE_COMPLETED);
                    setUserLastLoggedWorkoutView();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse data, Reason: " + e.getMessage());
            }
        }
    }
}
