/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;
import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * Activity used to register a User. In order to register, a user must enter in their first and last
 * name, email, and a password. The email must not already be registered and the user must enter
 * their password twice to confirm they are correct.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class RegisterUserActivity extends AppCompatActivity {

    /** Tag used for debugging. */
    private final String TAG = "Register Activity";

    /** Permission for the Camera. */
    private static final int MY_PERMISSIONS_CAMERA = 0;

    /** Message for the permission of the camera. */
    public static final String CAMERA_PERMISSION_MESSAGE =
            "Camera permission is needed to add profile picture using your camera.";

    /**
     *  Boolean value used for lunching AdditionalInformationFragment after entering
     *  user first information.
     */
    private boolean redo = true;

    /** URL used to add the user information to database. */
    private final static String USER_ADDITIONAL_INFO_ADD_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/addUserAdditionalInfo.php?";

    /** Only one image can be taken. */
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    /** Users First name. */
    private String mUserFirstName = "";

    /** Users Last name. */
    private String mUserLastName = "";

    /** Users email. */
    private String mUserEmail = "";

    /** User password. */
    private String mUserPassword = "";

    /** Users photo, null for now for this stage of project.*/
    private byte[] mPhoto;

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

    /** Number of days the user works out. */
    private int mDaysToWorkout;

    /** The path of the current photo. */
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null) {
            mUserEmail = savedInstanceState.getString("email");
            mUserFirstName = savedInstanceState.getString("first");
            mUserLastName = savedInstanceState.getString("last");
            savedInstanceState.putBoolean("fbook", true);
        } else {
            Log.e("BUNDLE", "NULL");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_CAMERA);
        }

        // launch the the registerUserfragment
        RegisterUserFragment userAddFragment = new RegisterUserFragment();
        userAddFragment.setArguments(savedInstanceState);
//        userAddFragment.set
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_register_user_xml, userAddFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // add a picture task needed.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, CAMERA_PERMISSION_MESSAGE, Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }






    /** function that invokes an intent to capture a photo. */
    protected void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageBt = (ImageView) findViewById(R.id.add_pic);
            if(imageBt != null) {
                imageBt.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageBt.setImageBitmap(imageBitmap);
                Log.i(TAG, "H: " + imageBt.getHeight() + "W: "
                        + imageBt.getWidth() + "SCALE: " + imageBt.getScaleType());
//                Log.i(TAG, "W: " + imageBitmap.getWidth() + " H: " + imageBitmap.getHeight());
//                setPic(imageBt, imageBitmap);
            }

        }
    }

//    private void setPic(ImageView mImageView, Bitmap imageBitmap) {
//        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);
//    }












    /**
     * Method used to register the User to User Database Table.
     *
     * @param url Url used to lunch the AddUserTask class.
     */
    public void addUserData(String url) {
        AddUserTask task = new AddUserTask();
        task.execute(url);
    }

    /**
     * Method to set the information of the user.
     *
     * @param theFirstName the first name of the user
     * @param theLastName the last name of the user
     * @param theEmail the email of the user
     * @param thePass the password for the user
     */
    public void setUserInformation(final String theFirstName, final String theLastName,
                                   final String theEmail, final String thePass) {
        mUserFirstName = theFirstName;
        mUserLastName = theLastName;
        mUserEmail = theEmail;
        mUserPassword = thePass;
    }

    /**
     * Method used to set the Users additional information.
     *
     * @param photo Users photo. In this version it's null.
     * @param dateDOB Users Date from his/hers DOB.
     * @param monthDOB Users Month from his/hers DOB.
     * @param yearDOB Users Year from his/hers DOB.
     * @param weight Users weight.
     * @param heightFt Users Height in Feet.
     * @param heightIn Users Height in Inches.
     * @param gender Users Gender.
     * @param activityLevel Users activity level.
     * @param daysToWorkout Number of days the users workouts.
     */
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

    /** Method that will build the url for calling the AsyncTask.  */
    public String buildAddUserAdditionaIfoURL() {

        StringBuilder sb = new StringBuilder(USER_ADDITIONAL_INFO_ADD_URL);

        try {
            sb.append("email=");
            sb.append(mUserEmail);


            sb.append("&firstName=");
            sb.append(URLEncoder.encode(mUserFirstName, "UTF-8"));

            sb.append("&lastName=");
            sb.append(URLEncoder.encode(mUserLastName, "UTF-8"));

            sb.append("&profilePhoto=");
            sb.append(Arrays.toString(mPhoto));

            String birthDay = "" + mYearDOB + "-" + mMonthDOB + "-" + mDateDOB;
            sb.append("&birthDay=");
            sb.append(URLEncoder.encode(birthDay, "UTF-8"));

            sb.append("&weight=");
            sb.append(mWeight);

            sb.append("&heightFt=");
            sb.append(mHeightFt);

            sb.append("&heightIn=");
            sb.append(mHeightIn);

            sb.append("&gender=");
            sb.append(URLEncoder.encode(String.valueOf(mGender), "UTF-8"));

            sb.append("&activityLevel=");
            sb.append(URLEncoder.encode(mActivityLevel, "UTF-8"));

            sb.append("&daysToWorkout=");
            sb.append(mDaysToWorkout);
            Log.i(TAG, sb.toString());
        }
        catch(Exception e) {
            Log.e(TAG, "Something wrong with the url" + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Gets the users email.
     *
     * @return Returns the users email.
     */
    public String getUserEmail() {
        return mUserEmail;
    }


    /** AsyncTask class called AddUserTask that will allow us to call the service for adding
     * user information. */
    private class AddUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // TODO simplify, only need to send url, don't need to receive data
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
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
         * @param result the result of this task
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    getSupportFragmentManager().popBackStackImmediate();
                    if(redo) {
                        // Launch the RegisterUserAdditionalInfoFragment to get the additional information.
                        RegisterUserAdditionalInfoFragment userOtherInfo = new RegisterUserAdditionalInfoFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.activity_register_user_xml, userOtherInfo)
                                .addToBackStack(null)
                                .commit();
                        redo = !redo;
                    } else {
                        (Toast.makeText(getApplicationContext(),
                                R.string.registration_successful, Toast.LENGTH_SHORT)).show();
                        // Store user email and record that they are logged in
                        SharedPreferences sharedPreferences  = getSharedPreferences
                                (getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString(getString(R.string.current_email),
                                mUserEmail).apply();
                        sharedPreferences.edit().putBoolean(getString(R.string.logged_in), true)
                                .apply();
                        // Registration complete go to dashboard.
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