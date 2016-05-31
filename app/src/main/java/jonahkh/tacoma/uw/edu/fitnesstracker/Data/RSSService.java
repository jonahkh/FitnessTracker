/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.Data;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.authentication.LoginActivity;


/**
 * A service to send notifications to the user. The user has the option to enable or disable
 * notifications on the dashboard. If notifications are enabled, they will receive notifications
 * if the current date minus the date of the user's last workout is greater than the number of days
 * they committed to working out (i.e. if they said three then they committed to working out every
 * three days.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class RSSService extends IntentService {
    /** Tag for this class. */
    private static final String TAG = "RSSService";

    /** Frequency notifications are sent. */
    private static final long POLL_INTERVAL = AlarmManager.INTERVAL_HOUR * 3; // every 3 hours


    /** The interval that notifications are sent. Default is every 3 days. */
    private static long mPollInterval = 3;

    /**
     * Initialize a new RSSService. Empty by default.
     */
    public RSSService() {
        super("RSSService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.bottom_bar)
                        .setContentTitle("FitnessTracker");
        SharedPreferences preferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        String dateSinceLastWorkout = preferences.getString(getString(R.string.last_workout),
                "no last workout");
        int daysToWorkout = preferences.getInt(getString(R.string.days_working_out),
                -1);
        getDaysSinceLastWorkout(daysToWorkout, dateSinceLastWorkout);
//        mBuilder.setContentText("Last workout was on: " +
//                DashboardActivity.parseDate(dateSinceLastWorkout) + ", you committed to "
//                + daysToWorkout + "days of working out!");
        // Creates an Intent for the Activity
        mBuilder.setContentText("Last workout was " + daysToWorkout + " days ago time to workout!");
        if (mPollInterval < 1) {
            Intent notifyIntent =
                    new Intent(this, LoginActivity.class);
            // Sets the Activity to start in a new, empty task
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Creates the PendingIntent
            PendingIntent notifyPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            notifyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            // Puts the PendingIntent into the notification builder
            mBuilder.setContentIntent(notifyPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    /**
     * Determine the number of days since the last workout and set the polling interval to the
     * difference between the number of days the user committed to working out minus the number of
     * days it's been since the user's last workout.
     *
     * @param days the number of days since the last workout
     * @param date the date of the last workout
     */
    private void getDaysSinceLastWorkout(int days, String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        int daysSinceLastWorkout = currentDate.format(calendar.getTime()).compareTo(date);
        Log.e(TAG, "Days since last workout: " + daysSinceLastWorkout);
        Log.e(TAG, "Days committed to workouts" + days);
        Log.e(TAG, "" + currentDate.format(calendar.getTime()).compareTo(date));
        mPollInterval = days - currentDate.format(calendar.getTime()).compareTo(date);
        Log.e(TAG, "mPollInterval = " + mPollInterval);
    }

    /**
     * Set the service alarm.
     *
     * @param context the context of the application
     * @param isOn determines whether to set the service alarm to on
     */
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, RSSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        if (isOn && mPollInterval < 1) {
            Log.e(TAG, "here");
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                    , 10
                    , POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
