/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import jonahkh.tacoma.uw.edu.fitnesstracker.Data.FitnessAppDB;
import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.authentication.LoginActivity;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;

/**
 * This class represents the Dashboard for the FitnessTracker application. It will display
 * information including the last completed workout, the user's before and after pictures, and will
 * allow the user to view and/or edit their statistics (weight, height, activity level, etc.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PreDefinedWorkoutFragment.PreDefinedWorkoutListener,
                    WeightWorkoutListFragment.OnListFragmentInteractionListener,
        ViewLoggedWorkoutsListFragment.LoggedWeightWorkoutsInteractListener {

    /** Only one image can be taken. */
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /** The Directory to store the pic for this app. */
    private final String DIRECTORY = "FitnessTracker";

    /** Permission for the Camera. */
    private static final int MY_PERMISSIONS_CAMERA = 1;

    /** URL used to delete the user information from database. */
    private final static String ADD_IMAGE_URL
            = "localhost:5000/v1/fitnesstracker/addPicture.php?";

    /** Message for the permission of the camera. */
    private static final String CAMERA_PERMISSION_MESSAGE =
            "Camera permission is needed to add profile picture using your camera.";

    /** Stores the current workout that is being used by the active Fragment. */
    private WeightWorkout mCurrentWorkout;

    /** The Fragment for the dashboard home page. */
    private DashboardDisplayFragment mDashView = null;

    /** Shared preferences for this activity. */
    private SharedPreferences mSharedPreferences;

    /** The dialog for adding a set. */
    private AddSetFragment mDialog;

    /** Starts a custom workout when pressed. */
    private FloatingActionButton mFab;

    /** Adds a cardio workout. */
    private FloatingActionButton mFabCardioWorkout;
    /** The dialog for starting a custom workout. */
    private Dialog mStartCustomWorkoutDialog;

    /** The mDrawer for this Activity. */
    private DrawerLayout mDrawer;

    /** The navigation view for the drawer. */
    private NavigationView mNavView;

    /** The path of the current photo. */
    private String mCurrentPhotoPath;

    /** Whether we are capturing an image or grabbing it from file. */
    private boolean mImageCapture;

    /** Local database. */
    protected FitnessAppDB mProfilePicDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFabCardioWorkout = (FloatingActionButton) findViewById(R.id.fab_cardio_workout);
        assert mFabCardioWorkout != null;
        mFabCardioWorkout.hide();
        assert mFab != null;
        initializeCustomWorkoutDialog();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = new AddSetFragment();
                mStartCustomWorkoutDialog.show();
            }
        });


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert mDrawer != null;
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavView = (NavigationView) findViewById(R.id.nav_view);
        assert mNavView != null;
        mNavView.setNavigationItemSelectedListener(this);

        // Display of personal data
        if (mDashView == null) {
            mDashView = new DashboardDisplayFragment();
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mDashView)
                .addToBackStack(null)
                .commit();
        }
        mSharedPreferences  = getSharedPreferences(getString(R.string.CURRENT_WORKOUT)
                , Context.MODE_PRIVATE);
        // request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        final SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // add a picture task needed.
//                    sharedPreferences.edit().putBoolean(getString(R.string.permission_granted),
//                            true).apply();
                } else {
//                    sharedPreferences.edit().putBoolean(getString(R.string.permission_granted),
//                            false).apply();
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

//    public void setLocalDatabase() {
//        mProfilePicDB = new FitnessAppDB(getApplicationContext());
//    }

    /**
     * Locks or unlocks the drawer depending on the passed mode. The drawer is locked when the user
     * is performing a workout.
     *
     * @param mode determines whether to lock or unlock the drawer
     */
    protected void lockDrawer(final int mode) {
        mDrawer.setDrawerLockMode(mode);
    }


    /**
     * Initialize the dialog that appears when the user clicks the floating action button. It
     * prompts the user to enter the name of the workout they are starting. This field is required.
     */
    private void initializeCustomWorkoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.start_custom_workout_dialog, null);
        final EditText text = (EditText) view.findViewById(R.id.workout_name);
        final Button start = (Button) view.findViewById(R.id.start);
        final Button cancel = (Button) view.findViewById(R.id.cancel);
        builder.setTitle("Enter the name of your workout");
        builder.setView(view);
        mStartCustomWorkoutDialog = builder.create();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = text.getText().toString().trim();
                if (contents.length() > 1) {
                    mCurrentWorkout = new WeightWorkout(contents);
                    mFab.hide();
                    mStartCustomWorkoutDialog.dismiss();
                    text.setText("");
                    WeightWorkoutListFragment fragment = new WeightWorkoutListFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    text.setError("Workout Name Required!");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartCustomWorkoutDialog.dismiss();
                text.setText("");
            }
        });
    }

    /**
     * Returns if the user's device is connected to the network.
     *
     * @param message the message to display if network is not connected
     * @return true if the user's device is connected to the network
     */
    public boolean isNetworkConnected(String message) {
        return isNetworkConnected(message, true);
    }

    /**
     * Parses a date string to the form of DD-MM-YYYY.
     *
     * @param date the date being parsed
     * @return the result of the parsed date
     */
    public static String parseDate(String date) {
        String month = "";
        String year = "";
        String day = "";
        int i = 0;
        while (date.charAt(i) != '-') {
            year += date.charAt(0);
            date = date.substring(1);
        }
        date = date.substring(1);
        while (date.charAt(0) != '-') {
            month += date.charAt(0);
            date = date.substring(1);
        }

        date = date.substring(1);
        while (date.length() > 0) {
            day += date.charAt(0);
            date = date.substring(1);
        }
        return day + '-' + month + '-' + year;
    }

    /**
     * Sets the currently selected navigation item to the passed id.
     *
     * @param id the id of the navigation item being set
     */
    void setNavigationItem(int id) {
        mNavView.setCheckedItem(id);
    }

    /**
     * Returns if the user's device is connected to the network.
     *
     * @param message the message to display if network is not connected
     * @return true if the user's device is connected to the network
     */
    public boolean isNetworkConnected(String message, boolean showToast) {
        // Check for network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if ((networkInfo == null || !networkInfo.isConnected())) {
            if(showToast) {
                Toast.makeText(this,
                        "No network connection available. Cannot display " + message,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Called when the user is on the View Logged Workouts page and starts a new workout by holding
     * one of the list items down. Starts a new weight workout by using the same exercises as the
     * selected workout.
     *
     * @param workout the workout being repeated
     */
    void redoLoggedWorkout(final WeightWorkout workout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start " + workout.getWorkoutName() + " workout?");
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
//                ((DashboardActivity) getActivity()).setCurrentWorkout(workout);
                mDialog = new AddSetFragment();
                mCurrentWorkout = workout;
                WeightWorkoutListFragment weightWorkout = new WeightWorkoutListFragment();
                weightWorkout.setWorkout(workout);
                Bundle bundle = new Bundle();
                bundle.putBoolean("is_custom", true);
                bundle.putInt("workout_number", workout.getWorkoutNumber());
                weightWorkout.setArguments(bundle);
//                weightWorkout.setName(mCurrentWorkout.getWorkoutName());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, weightWorkout)
                        .addToBackStack(null)
                        .commit();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });
        mSharedPreferences.edit().putInt(getString(R.string.current_set), 0);
        Dialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onListFragmentInteraction(Exercise exercise) {
//        mDialog = new AddSetFragment();
        mDialog.setExerciseName(exercise.getExerciseName());
        mDialog.show(getSupportFragmentManager(), "AddSetFragment");
    }

    @Override
    public void onPreDefinedWorkoutInteraction(final WeightWorkout workout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start " + workout.getWorkoutName() + " workout?");
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mCurrentWorkout = workout;
                mDialog = new AddSetFragment();
                WeightWorkoutListFragment weightWorkout = new WeightWorkoutListFragment();
                weightWorkout.setName(mCurrentWorkout.getWorkoutName());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, weightWorkout)
                        .addToBackStack(null)
                        .commit();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });
        mSharedPreferences.edit().putInt(getString(R.string.current_set), 0);
        Dialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("current_workout", mCurrentWorkout);
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        if (savedInstanceState != null && savedInstanceState.getSerializable("current_workout") != null) {
//            mCurrentWorkout = (WeightWorkout) savedInstanceState.getSerializable("current_workout");
//        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void LoggedWeightWorkoutInteraction(WeightWorkout workout) {
        mCurrentWorkout = workout;
        final ViewExercisesFragment exercises = new ViewExercisesFragment();
        exercises.setWorkout(workout);
        Bundle args = new Bundle();
        args.putSerializable(WeightWorkout.WORKOUT_SELECTED, exercises);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, exercises)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert mDrawer != null;
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Return the current WeightWorkout.
     *
     * @return the current weight workout
     */
    public WeightWorkout getCurrentWorkout() {
        return mCurrentWorkout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPreferences preferences
                    = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                    , Context.MODE_PRIVATE);
            preferences.edit().putBoolean(getString(R.string.logged_in), false).apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        manager.getFragments().size();
        Fragment fragment;
        switch (id)  {
            case (R.id.nav_predefined_workouts) :
                mFab.hide();
                mFabCardioWorkout.hide();
                fragment = new PreDefinedWorkoutFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case (R.id.view_logged_workouts) :
                mFab.hide();
                mFabCardioWorkout.hide();
                fragment = new ViewLoggedWorkoutsListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case (R.id.nav_home) :
                mFab.show();
                mFabCardioWorkout.hide();
                if(mDashView != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, mDashView)
                            .addToBackStack(null)
                            .commit();
                } else {
                    mDashView = new DashboardDisplayFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, mDashView)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case (R.id.view_logged_cardio_workouts) :
                mFab.hide();
                mFabCardioWorkout.show();
                fragment =
                        new ViewLoggedCardioExerciseListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Shows the Floating action button. */
    public void showFab() {
        mFab.show();
    }

    /** function that invokes an intent to capture a photo. */
    public void dispatchTakePictureIntent(boolean imageCapture) {
        mImageCapture = imageCapture;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {

                imageFile = createImageFile();
//                final SharedPreferences sharedPreferences = getSharedPreferences(
//                        getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
//                sharedPreferences.edit().putString(getString(R.string.profile_pic_file_name),
//                        mCurrentPhotoPath).apply();
                final SharedPreferences sharedPreferences = getSharedPreferences(
                        getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                String userEmail = sharedPreferences.getString(getString(R.string.current_email),
                        "Email does not exist");
                if(mProfilePicDB == null) {
                    mProfilePicDB = new FitnessAppDB(this);
                }
                boolean successful = mProfilePicDB.setProfilePicture(userEmail, mCurrentPhotoPath);
                if(!successful) {
                    Log.e("DashBoardActivity", "Profile pic could not be send to local database");
                }
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

    /** Method that invokes an intent to upload pircture from gallery of images.  */
    public void dispatchGalleryIntent() {
        mImageCapture = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Creates the image file.
     *
     * @return The image file.
     * @throws IOException Exception to be handled by calling method.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DCIM);
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString() + File.separator + DIRECTORY;
        File storageDir = new File(path);
        if(!storageDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("Dashboard Activity", mCurrentPhotoPath);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(!mImageCapture) {
                Uri selectedImageUri = data.getData();
                mCurrentPhotoPath = getPath(selectedImageUri);
//                final SharedPreferences sharedPreferences = getSharedPreferences(
//                        getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
//                sharedPreferences.edit().putString(getString(R.string.profile_pic_file_name),
//                        mCurrentPhotoPath).apply();
                final SharedPreferences sharedPreferences = getSharedPreferences(
                        getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
//                sharedPreferences.edit().putString(getString(R.string.profile_pic_file_name),
//                        mCurrentPhotoPath).apply();
                String userEmail = sharedPreferences.getString(getString(R.string.current_email),
                        "Email does not exist");
                if(mProfilePicDB == null) {
                    mProfilePicDB = new FitnessAppDB(this);
                }
                boolean successful = mProfilePicDB.setProfilePicture(userEmail, mCurrentPhotoPath);
                if(!successful) {
                    Log.e("DashBoardActivity", "Profile pic could not be send to local database");
                }
            }
            setImageView();
        }
    }

    /**
     * Helper method for the onActivityResult method. to getPth of picture being used.
     *
     * @param uri The partial location of the image been use.
     * @return The location of the the picture being used.
     */
    private String getPath(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /** Helper method for onActivityResult method to set the profile picture of user. */
    private void setImageView() {
        ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        final long currTime = System.currentTimeMillis();
        final long end = currTime + 5000;
        // will run a loop for either 5 seconds waiting for the image
        // If the 5 seconds run out and the image is still not found
        // the profile pic will be empty.
        while(System.currentTimeMillis() < end && bitmap == null){
            bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        }
        if(profilePic != null && bitmap != null) {
            profilePic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            profilePic.setImageBitmap(bitmap);
            final SharedPreferences sharedPreferences = getSharedPreferences(
                    getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            final AddPictureTask addPictureTask = new AddPictureTask(getApplicationContext());
            final String userEmail = sharedPreferences.getString(getString(R.string.current_email),
                    "Email does not exist");
            String url = ADD_IMAGE_URL + "email=" + userEmail + "&photoDirectoryLocation=" +
                    mCurrentPhotoPath;
            Log.i("PIC URL: ", url);
            addPictureTask.execute(url);
        }
    }

    /**
     * Sets reference to local database.
     * @param localDataBase The local data base.
     * @param currentPhotoPath The current path of the profile picture.
     * @param userEmail The email of the user.
     */
    public void setLocalDataBase(FitnessAppDB localDataBase, String currentPhotoPath,
                                 String userEmail) {
        mProfilePicDB = localDataBase;
        if(mProfilePicDB == null) {
            mProfilePicDB = new FitnessAppDB(this);
        }
        mProfilePicDB.setProfilePicture(userEmail, currentPhotoPath);

    }
}
