package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.Data.FitnessAppDB;
import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.ViewLoggedCardioExerciseAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.CardioWorkout;
import jonahkh.tacoma.uw.edu.fitnesstracker.services.RestClient;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class ViewLoggedCardioExerciseListFragment extends Fragment {

    private static final String CARDIO_EXERCISE_URL =
            "localhost:5000/v1/fitnesstracker/downloadcardioexercises";

    /** so that we can access it in the thread to load the data. */
    private RecyclerView mRecyclerView;

    /** Tag used for debugging. */
    private final String TAG = "Cardio Workout Fragment list";

    private final int mColumnCount = 1;

    /** The list of completed exercises for this user. */
    private List<CardioWorkout> mWorkoutList;

    /** The adapter for this Fragment. */
    private ViewLoggedCardioExerciseAdapter mAdapter;

    /** Shared Preferences */
    private SharedPreferences mSharedPreferences;

    /** Starts a new cardio exercise when pressed. */
    private FloatingActionButton mFab;

    /** The internal database for this app. */
    private FitnessAppDB cardioWorkoutDB;

    /** The email of the user. */
    private String mUserEmail;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViewLoggedCardioExerciseListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cardio_exercise_list, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.DASHBOARD_PREFS),
                Context.MODE_PRIVATE);
        final boolean networkAlive = ((DashboardActivity) getActivity()).isNetworkConnected(getString(
                R.string.CardioExercises), false);
        mUserEmail = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");
        String param = "email=" + mUserEmail;
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            //noinspection ConstantConditions
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

        }
        // check network
        if (networkAlive) {
            DownloadCardioExercisesTask task = new DownloadCardioExercisesTask();
//            Log.i(TAG, CARDIO_EXERCISE_URL + param);
            task.execute(CARDIO_EXERCISE_URL + "/" + mUserEmail);
        } else {
            if(cardioWorkoutDB == null){
                cardioWorkoutDB = new FitnessAppDB(getActivity());
            }
            if(mWorkoutList == null) {
                mWorkoutList = cardioWorkoutDB.getCardioWorkouts();
            }
            Toast.makeText(view.getContext(), R.string.local_data_message,
                    Toast.LENGTH_SHORT).show();
            mRecyclerView.setAdapter(new ViewLoggedCardioExerciseAdapter(getActivity(),
                    mWorkoutList));
        }
        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab_cardio_workout);
        mFab.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkAlive) {
                    mFab.hide();
                    AddCardioWorkout fragment = new AddCardioWorkout();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.adding_cardio_workout_noNet, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        ((DashboardActivity) getActivity()).setNavigationItem(R.id.view_logged_cardio_workouts);
        super.onResume();
    }

    /**
     * Handles web service interaction for the ViewLoggedWorkoutsListFragment class.
     */
    private class DownloadCardioExercisesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return RestClient.runRequest("GET", null, urls);
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            mWorkoutList = new ArrayList<>();
//            Log.i("TEST", result.toString());
            result = CardioWorkout.parseCardioExercisesJSON(result, mWorkoutList);
            // Something wrong with the JSON returned.
            if (result != null) {
                mSharedPreferences.edit().putInt(getString(R.string.next_cardio_exercise_num),
                        1).apply();
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!mWorkoutList.isEmpty()) {
                if(cardioWorkoutDB == null){
                    cardioWorkoutDB = new FitnessAppDB(getActivity());
                }
                // Delete old data so that you can refresh the local
                // database with the network data.
                cardioWorkoutDB.deleteCardioWorkouts();

                // Also, add to the local database
                for (int i=0; i<mWorkoutList.size(); i++) {
                    CardioWorkout workout = mWorkoutList.get(i);
                    cardioWorkoutDB.insertCardioWorkout(workout.getWorkoutNumber(),
                            workout.getDate(), workout.getDuration(),
                            workout.getActivityName(), mUserEmail, workout.getDistance());
                }
                mRecyclerView.setAdapter(new ViewLoggedCardioExerciseAdapter(getActivity(), mWorkoutList));
                mSharedPreferences.edit().putInt(getString(R.string.next_cardio_exercise_num),
                        mWorkoutList.size() + 1).apply();
            } else {
                mSharedPreferences.edit().putInt(getString(R.string.next_cardio_exercise_num),
                        1).apply();
                Toast.makeText(getActivity().getApplicationContext(),
                        R.string.no_cardioExe_to_display, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
