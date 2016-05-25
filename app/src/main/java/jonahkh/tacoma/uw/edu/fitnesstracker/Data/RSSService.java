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
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RSSService extends IntentService {
    public static final String TAG = "RSSService";
    private static final int POLL_INTERVAL = 10000; //5 seconds

    /** The interval that notifications are sent. Default is every 3 days. */
    private long mPollInterval = AlarmManager.INTERVAL_DAY * 3;

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
        mBuilder.setContentText("Last workout was on: " +
                DashboardActivity.parseDate(dateSinceLastWorkout) + ", you committed to "
                + daysToWorkout + "days of working out!");
        // Creates an Intent for the Activity
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

    private int getDaysSinceLastWorkout(int days, String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        int daysSinceLastWorkout = currentDate.format(calendar.getTime()).compareTo(date);
        Log.e(TAG, "Days since last workout: " + daysSinceLastWorkout);
        Log.e(TAG, "Days committed to workouts" + days);
        Log.e(TAG, "" + currentDate.format(calendar.getTime()).compareTo(date));
        return 1;
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, RSSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                    , 10
                    , POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

    }
}
