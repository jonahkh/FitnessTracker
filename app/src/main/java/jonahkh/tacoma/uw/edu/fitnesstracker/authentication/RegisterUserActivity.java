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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
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
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.AddPictureTask;

/**
 * Activity used to register a User. In order to register, a user must enter in their first and last
 * name, email, and a password. The email must not already be registered and the user must enter
 * their password twice to confirm they are correct.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class RegisterUserActivity extends AppCompatActivity {

    /** The Directory to store the pic for this app. */
    private final String DIRECTORY = "FitnessTracker";

    /** Tag used for debugging. */
    private final String TAG = "Register Activity";

    /** Permission for the Camera. */
    private static final int MY_PERMISSIONS_CAMERA = 1;

    /** Message for the permission of the camera. */
    private static final String CAMERA_PERMISSION_MESSAGE =
            "Camera permission is needed to add profile picture using your camera.";

    /**
     *  Boolean value used for lunching AdditionalInformationFragment after entering
     *  user first information.
     */
    private boolean redo = true;

    /** URL used to add the user information to database. */
    private final static String USER_ADDITIONAL_INFO_ADD_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/addUserAdditionalInfo.php?";

    /** URL used to delete the user information from database. */
    private final static String ADD_IMAGE_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/addPicture.php?";

    /** Only one image can be taken. */
    private static final int REQUEST_IMAGE_CAPTURE = 1;

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

    /** Whether we are capturing an image or grabbing it from file. */
    private boolean mImageCapture;

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
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
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
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    //TODO this is empty
                    // permission was granted, yay! Do the
                    // add a picture task needed.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, CAMERA_PERMISSION_MESSAGE, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /** function that invokes an intent to capture a photo.
     * @param imageCapture*/
    protected void dispatchTakePictureIntent(boolean imageCapture) {
        mImageCapture = imageCapture;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
                final SharedPreferences sharedPreferences = getSharedPreferences(
                        getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(getString(R.string.profile_pic_file_name),
                        mCurrentPhotoPath).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(imageFile));
                galleryAddPic();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void dispatchPictureIntent(boolean imageCapture) {
        mImageCapture = imageCapture;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(!mImageCapture) {
                Uri selectedImageUri = data.getData();
                mCurrentPhotoPath = getPath(selectedImageUri);
                final SharedPreferences sharedPreferences = getSharedPreferences(
                        getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(getString(R.string.profile_pic_file_name),
                        mCurrentPhotoPath).apply();

            }
            setImageView();
        }
    }

    private String getPath(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void setImageView() {
        ImageView profilePic = (ImageView) findViewById(R.id.add_pic);
        Log.i("Image Location", mCurrentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        final long currTime = System.currentTimeMillis();
        final long end = currTime + 5000;
        // will run a loop for either 5 seconds waiting for the image
        // or until the image is found. If the 5 seconds run out
        // and the image is still not found the profile pic will be
        // empty.
        while (System.currentTimeMillis() < end && bitmap == null) {
            bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        }

        if (profilePic != null && bitmap != null) {
            profilePic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            profilePic.setImageBitmap(bitmap);
            final SharedPreferences sharedPreferences = getSharedPreferences(
                    getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            final AddPictureTask addPictureTask = new AddPictureTask(getApplicationContext());
            final String userEmail = sharedPreferences.getString(getString(R.string.current_email),
                    "Email does not exist");
            String url = ADD_IMAGE_URL + "&email=" + userEmail + "&photoDirectoryLocation=" +
                    mCurrentPhotoPath;
            Log.i(TAG, url);
            addPictureTask.execute(url);
        }
    }

    /**
     * Creates the image file.
     *
     * @return The image file.
     * @throws IOException Exception to be handled by calling method.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DCIM);
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString() + File.separator + DIRECTORY;
        File storageDir = new File(path);
        if(!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, mCurrentPhotoPath);
        return image;
    }

    /**
     *  Adds photo to the Media Provider's database, making it available
     *  in the Android Gallery application and to other apps.
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


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