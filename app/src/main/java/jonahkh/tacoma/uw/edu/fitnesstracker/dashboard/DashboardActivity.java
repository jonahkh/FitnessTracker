/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

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
    private Bundle mSavedInstanceState;

    /** The Fragment for the dashboard home page. */
    private DashboardDisplayFragment mDashView = null;

    /** Shared preferences for this activity. */
    private SharedPreferences mSharedPreferences;

    /** The current email logged in. */
    private String mCurrentEmail;

    /** The current workout number. */
    private int mWorkoutNum;

    /** The current set number for a weight workout. */
    private int mSetNum = 1;

    /** True if the current workout being performed has not logged any exercises. */
    private boolean mFirstExercise = true;

    /** The dialog for adding a set. */
    private AddSetFragment mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        mSavedInstanceState = savedInstanceState;

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

    public boolean getFirstAddedExercise() {
        return mFirstExercise;
    }

    public void setFirstAddedExercise(final boolean bool) {
        mFirstExercise = bool;
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


    @Override
    public void onListFragmentInteraction(Exercise exercise) {
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
                WeightWorkoutListFragment weightWorkout = new WeightWorkoutListFragment();
                weightWorkout.setName(mCurrentWorkout.getWorkoutName());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, weightWorkout)
                        .addToBackStack(null)
                        .commit();
                mDialog = new AddSetFragment();
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
        if (savedInstanceState != null && savedInstanceState.getSerializable("current_workout") != null) {
            mCurrentWorkout = (WeightWorkout) savedInstanceState.getSerializable("current_workout");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Retrieves the current workout if the app was restarted unexpectedly (if the user changes the
     * screen orientation).
     */
    public void retrieveCurrentWorkout() {
        onRestoreInstanceState(mSavedInstanceState);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        if (id == R.id.nav_predefined_workouts) {
            PreDefinedWorkoutFragment fragment = new PreDefinedWorkoutFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.view_logged_workouts) {
            ViewLoggedWorkoutsListFragment fragment = new ViewLoggedWorkoutsListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if(id == R.id.nav_home) {
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
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
