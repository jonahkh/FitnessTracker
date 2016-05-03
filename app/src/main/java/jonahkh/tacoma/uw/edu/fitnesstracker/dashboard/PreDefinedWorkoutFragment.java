/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of PreDefined Workouts.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PreDefinedWorkoutFragment extends Fragment {
    /** The url to fetch data from the mysql server. */
    private static final String WORKOUT_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/workouts.php?cmd=predefinedworkouts";

    /** The listener for this Fragment. */
    private OnListFragmentInteractionListener mListener;

    /** The list of predefined workouts. */
    private List<PreDefinedWorkout> mWorkoutList;

    /** The adapter for this Fragment. */
    private BaseAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PreDefinedWorkoutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predefined_workouts_list, container, false);
        // Check for network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadPreDefinedWorkoutsTask task = new DownloadPreDefinedWorkoutsTask();
            task.execute(new String[]{WORKOUT_URL});
        } else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Cannot display workouts",
                    Toast.LENGTH_SHORT) .show();
        }
        mAdapter = new PreDefinedWorkoutAdapter(getActivity(), mWorkoutList, mListener);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExerciseListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(PreDefinedWorkout workout);
    }

    /**
     * This class handles the interactions with the web service for this Fragment.
     */
    private class DownloadPreDefinedWorkoutsTask extends AsyncTask<String, Void, String> {
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
                    response = "Unable to download the list of predefinedworkouts, Reason: "
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
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            mWorkoutList = new ArrayList<>();
            result = PreDefinedWorkout.parseWeightWorkoutJSON(result, mWorkoutList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of workouts.
            if (!mWorkoutList.isEmpty()) {
                mAdapter = new PreDefinedWorkoutAdapter(getActivity(), mWorkoutList, mListener);
                ListView view = (ListView) getActivity().findViewById(R.id.predefined_wo_list);
                view.setAdapter(mAdapter);

            }

        }

    }
}
