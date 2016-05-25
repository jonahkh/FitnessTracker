/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * A receiver to start the RSSService if the phone is rebooted. The service will start automatically
 * on restart.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class BootUpReceiver extends BroadcastReceiver {
    /** A tag to identify messages in Logcat. */
    private static final String TAG = "BootUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        SharedPreferences preferences
                = context.getSharedPreferences(context.getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        if (preferences.getBoolean(context.getString(R.string.ON), false)) {
            Log.d(TAG, "starting service");
            RSSService.setServiceAlarm(context, true);
        } else {
            Log.d(TAG, "stopping service");
            RSSService.setServiceAlarm(context, false);
        }
    }
}
