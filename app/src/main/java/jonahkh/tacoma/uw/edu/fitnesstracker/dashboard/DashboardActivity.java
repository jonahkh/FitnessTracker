/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;
// TODO finish project
/*
 * Settings page (Use Case 7)
 * Camera for login and on dashboard
 * Forgot password
 * Share workout to Facebook
 * Message in logged workouts saying no workouts completed yet
 * Robotium Test
 */
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    /** Stores the current workout that is being used by the active Fragment. */
    private WeightWorkout mCurrentWorkout;

    /** The SavedInstanceState. Used to restore data when the app is rotated. */
//    private Bundle mSavedInstanceState;

    /** The Fragment for the dashboard home page. */
    private DashboardDisplayFragment mDashView = null;

    /** Shared preferences for this activity. */
    private SharedPreferences mSharedPreferences;

    /** The dialog for adding a set. */
    private AddSetFragment mDialog;

    /** Starts a custom workout when pressed. */
    private FloatingActionButton mFab;

    /** The dialog for starting a custom workout. */
    private Dialog mStartCustomWorkoutDialog;

    /** The mDrawer for this Activity. */
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
//        mDialog = new AddSetFragment();
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        assert mFab != null;
        final WeightWorkoutListFragment.OnListFragmentInteractionListener listener = this;
        final Activity activity = this;
        initializeCustomWorkoutDialog();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = new AddSetFragment();
                mStartCustomWorkoutDialog.show();
            }
        });


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        lockDrawer(DrawerLayout.LOCK_MODE_UNLOCKED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert mDrawer != null;
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
//        mSavedInstanceState = savedInstanceState;

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
    }

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
        final View view = getLayoutInflater().inflate(R.layout.start_custom_workout_dialog, null);
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
        // Check for network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this,
                    "No network connection available. Cannot display " + message,
                    Toast.LENGTH_SHORT) .show();
            return false;
        }
        return true;
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
    protected void redoLoggedWorkout(final WeightWorkout workout) {
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

    protected void setCurrentWorkout(WeightWorkout workout) {
        mCurrentWorkout = workout;
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

    /**
     * Retrieves the current workout if the app was restarted unexpectedly (if the user changes the
     * screen orientation).
     */
    public void retrieveCurrentWorkout() {
//        onRestoreInstanceState(mSavedInstanceState);
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
        //TODO make it so when you go to another mDrawer item from a workout the ondestroy method of that fragment is called
        FragmentManager manager = getSupportFragmentManager();
        manager.getFragments().size();
        if (id == R.id.nav_predefined_workouts) {
            mFab.hide();
            PreDefinedWorkoutFragment fragment = new PreDefinedWorkoutFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.view_logged_workouts) {
            mFab.hide();
            ViewLoggedWorkoutsListFragment fragment = new ViewLoggedWorkoutsListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if(id == R.id.nav_home) {
            mFab.show();
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
        } else if(id == R.id.view_logged_cardio_workouts){
            mFab.show();
            ViewLoggedCardioExersiceListFragment fragment =
                    new ViewLoggedCardioExersiceListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
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
    /**
     * Helper method for all AsyncTask inner classes. Connects to the web service and extracts
     * data according to the url.
     *
     * @param urls the urls being passed to the web service
     * @return an empty String if the connection was successful, otherwise returns the error
     *          message received
     */
    public static String doInBackgroundHelper(String...urls) {
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
                response = "Network connection unavailable, please try again later";
            }
            finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
        return response;
    }



}
