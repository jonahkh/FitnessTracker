package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.MyPictureRecyclerViewAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 * interface.
 */
public class PictureListFragment extends Fragment {
    private static final String ADD_PICTURE_URL =
            "http://cssgate.insttech.washington.edu/~_450atm2/getAfterImages.php?";

    // TODO: Customize parameters
    private int mColumnCount = 3;

    /**
     * so that we can access it in the thread to load the data.
     */
    private RecyclerView mRecyclerView;

    private List<Picture> mCourseList;

    /** The Users email. */
    private String mUserEmail;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PictureListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

        }
        // check if network exist
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadPicturesTask task = new DownloadPicturesTask();
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                    getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            mUserEmail = sharedPreferences.getString(getString(R.string.current_email),
                    "Email does not exist");
            String url = ADD_PICTURE_URL + "email=" + mUserEmail;
            Log.i("Pictire List Frag", url);
            task.execute(url);
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class DownloadPicturesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            mCourseList = new ArrayList<Picture>();
            result = Picture.parseCourseJSON(result, mCourseList, mUserEmail);
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.no_prev_pic_message,
                        Toast.LENGTH_LONG)
                        .show();
                return;
            } else {
                mRecyclerView.setAdapter(new MyPictureRecyclerViewAdapter(mCourseList, getActivity()));
            }
        }
    }
}
