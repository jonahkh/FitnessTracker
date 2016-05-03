package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;
import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * Created by Hector on 4/26/2016.
 */
public class RegisterUserActivity extends AppCompatActivity {

    public final String TAG = "REGISTER ACTIVIY";

    protected boolean redo = true;

    private final static String USER_ADDITIONAL_INFO_ADD_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/addUserAdditionalInfo.php?";

    /** Users First name. */
    private String mUserFirstName = "";

    /** Users Last name. */
    private String mUserLastName = "";

    /** Users email. */
    private String mUserEmail = "";

    private String mUserPassword = "";

    private byte[] mPhoto;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // launch the the registerUserfragment
        RegisterUserFragment userAddFragment = new RegisterUserFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_register_user_xml, userAddFragment)
                .addToBackStack(null)
                .commit();
    }


    public void addUserData(String url) {
        AddUserTask task = new AddUserTask();
        task.execute(new String[]{url.toString()});
    }

    /**
     * Method to set the information of the user.
     * @param theFirstName
     */
    public void setUserInformation(final String theFirstName, final String theLastName,
                                   final String theEmail, final String thePass) {
        mUserFirstName = theFirstName;
        mUserLastName = theLastName;
        mUserEmail = theEmail;
        mUserPassword = thePass;
    }

    protected void getUserAdditionalInfo() {
        // Takes you back to the previous fragment by popping the current fragment out.
//        getSupportFragmentManager().popBackStackImmediate();
//
//        RegisterUserAdditional_Info userOtherInfo = new RegisterUserAdditional_Info();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.activity_register_user_xml, userOtherInfo)
//                .addToBackStack(null)
//                .commit();
    }

    public void setUserAdditionInfo(byte[] photo, int dateDOB, int monthDOB, int yearDOB,
                                    int weight, int heightFt, int heightIn, char gender,
                                    String activityLevel, int daysToWorkout) {
        mPhoto = photo;
        mDateDOB = dateDOB;
        mMonthDOB = monthDOB;
        mYearDOB = yearDOB;
        mWeight = weight;
        mHeightFt = heightFt;
        mHeightIn = heightIn;
        mGender = gender;
        mActivityLevel = activityLevel;
        mDaysToWorkout = daysToWorkout;
        String messageDebug = "User Registering Info: "
                + "\nName: " + mUserFirstName + " " + mUserLastName
                + "\nemail: " + mUserEmail + "\nPassword: " + mUserPassword
                + "\nDOB: " + mMonthDOB + "/" + mDateDOB + "/" + mYearDOB
                + "\nWeight: " + mWeight + "\nHeight: " + mHeightFt + "'" + mHeightIn + "\""
                +"\nGender: " + mGender + "\nActivity Level: " + mActivityLevel
                + "\nDays to workout: " + mDaysToWorkout;
        Log.i(TAG, messageDebug);
    }

    /** method that will build the url for calling the AsyncTask.  */
    protected String buildAddUserAddtionaIfoURL() {

        StringBuilder sb = new StringBuilder(USER_ADDITIONAL_INFO_ADD_URL);

        try {
            String email = mUserEmail;
            sb.append("email=");
            sb.append(email);


            String firstName = mUserFirstName;
            sb.append("&firstName=");
            sb.append(URLEncoder.encode(firstName, "UTF-8"));

            String lastName = mUserLastName;
            sb.append("&lastName=");
            sb.append(URLEncoder.encode(lastName, "UTF-8"));

            byte[] profilePhoto = mPhoto;
            sb.append("&profilePhoto=");
            sb.append(profilePhoto); // TODO might cause a problem;

            String birthDay = "" + mYearDOB + "-" + mMonthDOB + "-" + mDateDOB;
            sb.append("&birthDay=");
            sb.append(URLEncoder.encode(birthDay, "UTF-8"));

            int weight = mWeight;
            sb.append("&weight=");
            sb.append(weight);

            int heightFt = mHeightFt;
            sb.append("&heightFt=");
            sb.append(heightFt);

            int heightIn = mHeightIn;
            sb.append("&heightIn=");
            sb.append(heightIn);

            char gender = mGender;
            sb.append("&gender=");
            sb.append(URLEncoder.encode(String.valueOf(gender), "UTF-8"));

            String activityLevel = mActivityLevel;
            sb.append("&activityLevel=");
            sb.append(URLEncoder.encode(activityLevel, "UTF-8"));

            int daysToWorkout = mDaysToWorkout;
            sb.append("&daysToWorkout=");
            sb.append(daysToWorkout);

//            $firstName = isset($_GET['firstName']) ? $_GET['firstName'] : '';
//            $lastName = isset($_GET['lastName']) ? $_GET['lastName'] : '';
//            $profilePhoto = isset($_GET['profilePhoto']) ? $_GET['profilePhoto'] : '';
//            $birthDay = isset($_GET['birthDay']) ? $_GET['birthDay'] : '';
//            $weight = isset($_GET['weight']) ? $_GET['weight'] : '';
//            $heightFt = isset($_GET['heightFt']) ? $_GET['heightFt'] : '';
//            $heightIn = isset($_GET['heightIn']) ? $_GET['heightIn'] : '';

            Log.i(TAG, sb.toString());
        }
        catch(Exception e) {
            Log.e(TAG, "Something wrong with the url" + e.getMessage());
        }
        return sb.toString();
    }


    public String getUserEmail() {
        return mUserEmail;
    }


    /** AsyncTask class called CourseAddAsyncTask that will allow us to call the service for adding
     * user information. */
    private class AddUserTask extends AsyncTask<String, Void, String> {


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
                    response = "Unable to add user, Reason: "
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
//                    Toast.makeText(getApplicationContext(), "User successfully added!"
//                            , Toast.LENGTH_LONG)
//                            .show();
                    getSupportFragmentManager().popBackStackImmediate();
                    if(redo) {
                        RegisterUserAdditional_Info userOtherInfo = new RegisterUserAdditional_Info();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.activity_register_user_xml, userOtherInfo)
                                .addToBackStack(null)
                                .commit();
                        redo = !redo;
                    } else {
                        (Toast.makeText(getApplicationContext(),
                                R.string.registration_sucessful, Toast.LENGTH_SHORT)).show();
                        // Store user email and record that they are logged in
                        mSharedPreferences  = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                                , Context.MODE_PRIVATE);
                        mSharedPreferences.edit().putString(getString(R.string.current_email), mUserEmail)
                                .commit();
                        mSharedPreferences.edit().putBoolean(getString(R.string.logged_in), true).commit();

                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        getSupportFragmentManager().popBackStackImmediate();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();

                }
            } catch (JSONException e) {
                Log.e(TAG, "Something wrong with the data" + e.getMessage());
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}


// TODO lunch the new fragment for additional infor and close userAddFragment after next click;
//        -- From fragment to activty:
//
//        ((YourActivityClassName)getActivity()).yourPublicMethod();
//
//        -- From activity to fragment:
//
//        FragmentManager fm = getSupportFragmentManager();
//
//        //if you added fragment via layout xml
//        YourFragmentClass fragment = (YourFragmentClass)fm.findFragmentById(R.id.your_fragment_id);
//        fragment.yourPublicMethod();
//        If you added fragment via code and used a tag string when you added your fragment, use findFragmentByTag instead:
//
//        YourFragmentClass fragment = (YourFragmentClass)fm.findFragmentByTag("yourTag");