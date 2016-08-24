/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jonahkh.tacoma.uw.edu.fitnesstracker.Data.FitnessAppDB;
import jonahkh.tacoma.uw.edu.fitnesstracker.Data.RSSService;
import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.authentication.AddPictureFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;

/**
 * Fragment used to Display the personal information of the user. This information is displayed on
 * the dashboard. The user has the option to edit their weight, activity level, and number of days
 * they workout per week.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class DashboardDisplayFragment extends Fragment {

//    /** First name field of database. */
//    public static final String FIRST_NAME = "firstName";
//
//    /** Last name field of database. */
//    public static final String LAST_NAME = "lastName";
//
//    /** Photo field of database. */
//    public static final String PROFILE_PHOTO = "profilePhoto";
//
//    /** Birthday field of database. */
//    public static final String BIRTHDAY = "birthDay";

    /** Weight field of database. */
    private static final String WEIGHT = "weigth"; // Misspelled in the database :)

//    /** Height feet field of database. */
//    public static final String HEIGHT_FT = "heightFt";
//
//    /** Height inches field of database. */
//    public static final String HEIGHT_IN = "heightIn";
//
//    /** Gender field of database. */
//    public static final String GENDER = "gender";

    /** Activity level field of database. */
    private static final String ACTIVITY_LEVEL = "activityLevel";

    /** Days to workout field of database. */
    private static final String DAYS_TO_WORKOUT = "daysToWorkout";

    /** Workout number field of database. */
    public static final String WORKOUT_NUMBER = "workoutNumber";

    /** Workout name field of database. */
    private static final String WORKOUT_NAME = "workoutName";

    /** Date of workout completed field of database. */
    private static final String DATE_COMPLETED = "dateCompleted";

    /** URL used get user additional information from database. */
    private static final String USER_INFO
            = "http://cssgate.insttech.washington.edu/~_450atm2/additionalInfo.php?";

    /** URL used get users last logged workout information from database. */
    public static final String USER_LAST_LOGGED_WORKOUT
            = "http://cssgate.insttech.washington.edu/~_450atm2/getLastUserWorkout.php?";

    /** Message for the permission of the camera. */
    private static final String CAMERA_PERMISSION_MESSAGE =
            "Permissions are needed to add profile pictures.";

    /** Tag used for debugging. */
    private static final String TAG = "Dashboard Display";

    /** Users email. */
    private String mUserEmail;
//    For later implementation
//    private String mUserFirstName;
//    private String mUserLastName;
//    private byte[] mUserProfilePhoto;
//    private String mUserBirhtDay;

    /** Users weight. */
    private int mUserWeight;

//    private int mUserHeightFt;
//    private int mUserHeightIn;
//    private String mUserGender;


    /** Users activity level. */
    private String mUserActivityLevel;

    /** Number of days the user works out. */
    private int mUserDaysToWorkout;

    /** The current View. */
    private View mView;

    /**
     * Users last logged workout number. Default value is negative one, value gets switched
     * if data available.
     */
    private int mWorkoutNum = -1;

    /**
     * Users last logged workout name. Default value is "None to Display", value gets switched
     *  if data available.
     */
    private String mWorkoutName = "None to Display";

    /**
     * Users last logged workout date. Default value is "N/A", value gets switched
     *  if data available.
     */
    private String mDateCompleted = "N/A";

    /** The shared preferences for this application. */
    private SharedPreferences mSharedPreferences;

    private WeightWorkoutListFragment.OnListFragmentInteractionListener mListener;

    /** The File name of the profile picture. */
    private String mImageFileName;

    /** The profile picture View. */
    private ImageView mProfilePic;

    /** Required empty public constructor */
    public DashboardDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_dash_board_display, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(getActivity().getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        setFieldsPersonalInformation();
        setUserLastLoggedWorkout();
        ((DashboardActivity) getActivity()).showFab();
        Button viewLogBut = (Button) mView.findViewById(R.id.dasbB_viewLog_bt);
        viewLogBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mWorkoutNum != -1) {
                    ViewExercisesFragment exercises = new ViewExercisesFragment();
                    exercises.setWorkout(new WeightWorkout(mWorkoutName, mWorkoutNum, mDateCompleted));
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, exercises)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Logged Workout to Display"
                            , Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        Button editBut = (Button)mView.findViewById(R.id.dashB_editPersonalBt);
        editBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View c) {
                EditPersonalInformationFragment edit = new EditPersonalInformationFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, edit)
                        .addToBackStack(null)
                        .commit();
            }
        });
        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                getActivity().finish();
                return true;
            }
            return false;
            }
        });
        Switch notificationsSwitch = (Switch) mView.findViewById(R.id.toggle_notifications);
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Log.i(TAG, "checked");
                mSharedPreferences.edit().putBoolean(getActivity().getString(R.string.ON), true).apply();
                getActivity().startService(new Intent(getActivity(), RSSService.class));
                RSSService.setServiceAlarm(getActivity(), true);
            } else {
                Log.i(TAG, "unchecked");
                RSSService.setServiceAlarm(getActivity(), false);
                mSharedPreferences.edit().putBoolean(getActivity()
                        .getString(R.string.ON), false).apply();
            }
            }
        });
        if (mSharedPreferences.getBoolean(getActivity().getString(R.string.ON), false)) {
            notificationsSwitch.setChecked(true);
        } else {
            notificationsSwitch.setChecked(false);
        }

        mProfilePic = (ImageView) mView.findViewById(R.id.profile_pic);
        setImage(mProfilePic);
        mProfilePic.setOnClickListener(new View.OnClickListener() {
//            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
//                    getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
//            boolean permissions = sharedPreferences.getBoolean(getString(R.string.permission_granted),
//                    false);
            @Override
            public void onClick(View v) {
//                if(permissions) {
                AddPictureFragment fragment = new AddPictureFragment();
                fragment.show(getActivity().getSupportFragmentManager(), "launch");
//                } else {
//                    Toast.makeText(getActivity(), CAMERA_PERMISSION_MESSAGE, Toast.LENGTH_SHORT)
//                            .show();
//                }

            }
        });

        TextView prevPics = (TextView) mView.findViewById(R.id.view_pre_pics);
        prevPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureListFragment pictureListFragment = new PictureListFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, pictureListFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return mView;
    }


    /**
     * Sets the profile picture of the user.
     *
     * @param profilePic The profile picture view.
     */
    private void setImage(ImageView profilePic) {
        final SharedPreferences sharedPreferences = getActivity().
                getSharedPreferences(getActivity().getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString(getString(R.string.current_email),
                "Email does not exist");
        FitnessAppDB dataBase = ((DashboardActivity)getActivity()).mProfilePicDB;
        if(dataBase == null) {
            ((DashboardActivity)getActivity()).mProfilePicDB = new FitnessAppDB(getActivity());
            dataBase = ((DashboardActivity)getActivity()).mProfilePicDB;
        }
        if(dataBase == null) {
            Log.e(TAG, "Could not create local database");
            return;
        }
        mImageFileName = dataBase.getProfilePictureDirectory(userEmail);
        if(mImageFileName.equals("")){ return;}
        Bitmap bitmap = BitmapFactory.decodeFile(mImageFileName);
        if(bitmap != null) {
            profilePic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            profilePic.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton fab = (FloatingActionButton)
                getActivity().findViewById(R.id.fab_cardio_workout);
        fab.hide();
        ((DashboardActivity) getActivity()).setNavigationItem(R.id.nav_home);
        if (mImageFileName == null) {
            FitnessAppDB dataBase = ((DashboardActivity) getActivity()).mProfilePicDB;
            if (dataBase == null) {
                ((DashboardActivity) getActivity()).mProfilePicDB = new FitnessAppDB(getActivity());
                dataBase = ((DashboardActivity) getActivity()).mProfilePicDB;
            }
            if (dataBase == null) {
                Log.e(TAG, "Could not create local database");
                return;
            }
            mImageFileName = dataBase.getProfilePictureDirectory(mUserEmail);
            if (mImageFileName.equals("")) {
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(mImageFileName);
            if(bitmap != null && mProfilePic != null) {
                mProfilePic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mProfilePic.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeightWorkoutListFragment.OnListFragmentInteractionListener) {
            mListener = (WeightWorkoutListFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoggedWeightWorkoutsInteractListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /** Sets the view of the users last logged workout. */
    private void setUserLastLoggedWorkoutView() {
        TextView name = (TextView) mView.findViewById(R.id.dashB_workoutName);
        name.setText(" " + mWorkoutName); // not concatenation, is a space to separate data

        TextView date = (TextView)mView.findViewById(R.id.dashB_workoutDate);
        date.setText(" " + mDateCompleted); // not concatenation, is a space to separate data

        TextView number = (TextView)mView.findViewById(R.id.dashB_workoutNumber);
        number.setText(" " + mWorkoutNum); // not concatenation, is a space to separate data
        mSharedPreferences.edit().putString(getString(R.string.last_workout),
                mDateCompleted).apply();
    }

    /**
     * Method to launch the UserLastLoggedWorkoutTask and get the last logged workout out of
     * the database. Also, it sets its fields when UserLastLoggedWorkoutTask is called.
     */
    private void setUserLastLoggedWorkout() {
        String url = USER_LAST_LOGGED_WORKOUT + "email=" + mUserEmail;
        Log.i(TAG, url);
        UserLastLoggedWorkoutTask task = new UserLastLoggedWorkoutTask();
        task.execute(url);
    }

    /**
     * Method to launch the DownloadUserInfoTask and get the additional information of the user
     * from the database.
     */
    private void setFieldsPersonalInformation() {
        mUserEmail = mSharedPreferences.getString(getString(R.string.current_email),
                "Email does not exist");
        String url = USER_INFO + "email=" + mUserEmail;
        Log.i(TAG, url);
        DownloadUserInfoTask task = new DownloadUserInfoTask();
        task.execute(url);
    }

    /** Sets the personal information View. */
    private void setPersonalDataView() {
        TextView weight = (TextView) mView.findViewById(R.id.dashB_weightV);
        weight.setText("" + mUserWeight); // Concatenating to make it a string.

        TextView activity = (TextView) mView.findViewById(R.id.dashB_activityLevelV);
        activity.setText(mUserActivityLevel);

        TextView daysWorkingOut = (TextView) mView.findViewById(R.id.dashB_daysWorkingOutV);
        daysWorkingOut.setText("" + mUserDaysToWorkout); // Concatenating to make it a string.
        mSharedPreferences.edit().putInt(getString(R.string.days_working_out),
                mUserDaysToWorkout).apply();
    }

    /** Private class to download user personal information */
    private class DownloadUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
//                    mUserFirstName = obj.getString(FIRST_NAME);
//                    mUserLastName = obj.getString(LAST_NAME);
//                    Object temp = obj.get(PROFILE_PHOTO);
//                    if(temp.equals(null)) {
//                        mUserProfilePhoto = null;
//                    } else {
//                        mUserProfilePhoto = (byte[]) temp;
//                    }
//                    mUserBirhtDay = obj.getString(BIRTHDAY);
                    mUserWeight = obj.getInt(WEIGHT);
//                    mUserHeightFt = obj.getInt(HEIGHT_FT);
//                    mUserHeightIn = obj.getInt(HEIGHT_IN);
//                    mUserGender = obj.getString(GENDER);
                    mUserActivityLevel = obj.getString(ACTIVITY_LEVEL);
                    mUserDaysToWorkout = obj.getInt(DAYS_TO_WORKOUT);
                    setPersonalDataView();
                }
            } catch (JSONException e) {
                Log.e(TAG, "1: Unable to parse data, Reason: " + e.getMessage());
            }
        }
    }

    /** Private class to get the information about the last logged workout from user. */
    private class UserLastLoggedWorkoutTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if(result.equals("")) {
                setUserLastLoggedWorkoutView();
                return;
            }
            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    mWorkoutNum = obj.getInt(WORKOUT_NUMBER);
                    mWorkoutName = obj.getString(WORKOUT_NAME);
                    mDateCompleted = obj.getString(DATE_COMPLETED);
                    setUserLastLoggedWorkoutView();
                }
            } catch (JSONException e) {
                Log.e(TAG, "2: Unable to parse data, Reason: " + e.getMessage());
            }
        }
    }
}
