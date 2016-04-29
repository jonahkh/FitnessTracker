package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jonah on 4/29/2016.
 */
public class PreDefinedWorkout implements Serializable {
    private static String NAME = "name";
    private static String TYPE = "type";
    private String mName;
    private String mType;

    public PreDefinedWorkout(String name, String type) {
        mName = name;
        mType = type;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public static String parseWeightWorkoutJSON(String weightWorkoutJSON, List<PreDefinedWorkout> workoutList) {
        String reason = null;
        Log.i("TAG", weightWorkoutJSON);
        if (weightWorkoutJSON != null) {
            try {
                JSONArray arr = new JSONArray(weightWorkoutJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    PreDefinedWorkout workout = new PreDefinedWorkout(obj.getString(PreDefinedWorkout.NAME), obj.getString(PreDefinedWorkout.TYPE));
                    workoutList.add(workout);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }
}
