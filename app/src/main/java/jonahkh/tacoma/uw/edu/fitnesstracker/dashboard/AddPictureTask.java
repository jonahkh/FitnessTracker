/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */

package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;

/**
 * Class to save information about the location of pictures taking by user.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class AddPictureTask extends AsyncTask<String, Void, String> {

    /** The application context. */
    private final Context mApplicationContext;

    /** Tag used for debugging. */
    private final String TAG = "AddPictureTask";


    /**
     * Constructor to pass in a Context of the Class using this class.
     *
     * @param applicationContext The Application Context using this class.
     */
    public AddPictureTask(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    protected String doInBackground(String... urls) {
        return null; // TODO implement
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = (String) jsonObject.get("result");
            if (!status.equals("success")) {
                (Toast.makeText(mApplicationContext,
                        R.string.addPictureTask_error, Toast.LENGTH_LONG)).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Something wrong with the data" + e.getMessage());
        }

    }

}
