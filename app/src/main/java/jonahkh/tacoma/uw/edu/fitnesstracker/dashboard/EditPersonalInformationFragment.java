package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPersonalInformationFragment extends Fragment {

    private static final String USER_INFO
            = "http://cssgate.insttech.washington.edu/~_450atm2/updateUserData.php?";

    public static final String TAG = "Edit Personal Info";

    private final int INVALID = -1;

    private int mWeight;

    private String mActivityLevel;

    private int mDaysToWorkout;

    private String mUserEmail;

    public EditPersonalInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_edit_personal_information, container, false);

        mUserEmail = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");

        // cancel updating info
        Button cancel = (Button)myView.findViewById(R.id.epi_cancelBt);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        Button save = (Button)myView.findViewById(R.id.epi_save);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mWeight = getWeightEntered(myView);
                mActivityLevel = getActivityLevel(myView);
                mDaysToWorkout = getDaysToWorkout(myView);

                if(mWeight != INVALID){
                    String url = null;
                    try {
                        url = getUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, url);
                    UpdateUserDataTask task = new UpdateUserDataTask();
                    task.execute(new String[]{url});
                }
            }
        });

        return myView;
    }

    private int getWeightEntered(View myView) {
        TextView weightV = (TextView) myView.findViewById(R.id.epi_weight);
        if(TextUtils.isEmpty(weightV.getEditableText())){
            weightV.setError(getString(R.string.error_field_required));
            weightV.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(weightV.getEditableText().toString());
        Log.i(TAG, "Weight Entered: " + temp);
        return temp;
    }

    private String getActivityLevel(View myView) {
        return ((Spinner) myView.findViewById(R.id.epi_activtiyLv_spinner))
                .getSelectedItem().toString();
    }

    private int getDaysToWorkout(View myView) {
        Spinner activitySpinner = (Spinner) myView.findViewById(R.id.epi_daysToWorkout_spinner);
        String numDays = activitySpinner.getSelectedItem().toString();
        return Integer.parseInt(numDays);
    }

    private String getUrl() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(USER_INFO);
        sb.append("email=");
        sb.append(mUserEmail);

        int weight = mWeight;
        sb.append("&weight=");
        sb.append(weight);

        int daysToWorkout = mDaysToWorkout;
        sb.append("&daysToWorkout=");
        sb.append(daysToWorkout);

        String activityLevel = mActivityLevel;
        sb.append("&activityLevel=");
        sb.append(URLEncoder.encode(activityLevel, "UTF-8"));

        return sb.toString();
    }

    /** AsyncTask class called CourseAddAsyncTask that will allow us to call the service for adding
     * user information. */
    private class UpdateUserDataTask extends AsyncTask<String, Void, String> {

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
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to update user data, Reason: "
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
                    Toast.makeText(getActivity().getApplicationContext(), "Information Updated!"
                            , Toast.LENGTH_SHORT)
                            .show();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Log.e(TAG, "Failed to update Error: " + jsonObject.get("error"));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem updating user info, something wrong: " + e.getMessage());
            }
        }
    }
}
