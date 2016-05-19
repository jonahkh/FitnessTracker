/*
 * TCSS 450 FitnessTracker
 * Jonah Howard
 * Hector Diaz
 */

package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;
import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * A login screen that offers login via email/password. Also allows you to register for the app.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /** Url to attempt user login. */
    private static final String USER_URL = "http://cssgate.insttech.washington.edu/~_450atm2/login.php?";

    /** Url to generate a code to verify user when resetting password. */
    private static final String GET_CODE_URL = "http://cssgate.insttech.washington.edu/~_450atm2/resetpassword.php?";

    /* Keep track of the login task to ensure we can cancel it if requested. */
    private UserLoginTask mAuthTask = null;

    /** Text field for the user's email. */
    private AutoCompleteTextView mEmailView;

    /** Text field for the user's password. */
    private EditText mPasswordView;

    /** View that displays the progress when the app is attempting to verify credentials. */
    private View mProgressView;

    /** View representing the login form. */
    private View mLoginFormView;

    /** Determines if the user would like to remain logged in. */
    private CheckBox mCheckBox;

    /** The login button. */
    private LoginButton mLoginButton;

    /** The Facebook login button. */
    private FacebookActivity mFacebook;

    /** The shared preferences. */
    private SharedPreferences mSharedPreferences;

    /** Callback manager for Facebook login. */
    private CallbackManager mCallback;

    /** Determines if it's okay to log in with facebook. */
    private boolean mLoginFacebook = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        checkLoggedIn();
//        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mCheckBox = (CheckBox) findViewById(R.id.stay_logged_in);
        final TextView text = (TextView) findViewById(R.id.text);
        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBox.isChecked()) {
                    mCheckBox.setChecked(false);
                } else {
                    mCheckBox.setChecked(true);
                }
            }
        });
        final TextView forgotText = (TextView) findViewById(R.id.forgot_password);
        forgotText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // the register button
        final Button regBut = (Button) findViewById(R.id.register_account_button);
        assert regBut != null;
        regBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hide the register button
                //regBut.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                startActivity(intent);
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        assert mEmailSignInButton != null;
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mCallback = CallbackManager.Factory.create();
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissions = new ArrayList<>();
                // Set up permissions
                permissions.add("email");
                permissions.add("public_profile");
                mLoginButton.setReadPermissions(permissions);

                // Check that profile is logged in

                mLoginButton.registerCallback(mCallback, new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        requestData();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("TAG", "Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("TAG", "ERROR");
                    }
                });
            }
        });


    }

    /** Retrieves the email for the facebook profile currently logged in. */
    private void requestData() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                final JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        String email = json.getString("email");
                        FacebookLoginTask task = new FacebookLoginTask();
                        String url = USER_URL + "email=" + email + "&fbook=true";
                        task.execute(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void resetPassword() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.fragment_forgot_password_email, null);
        final EditText emailText = (EditText) v.findViewById(R.id.email);
        builder.setPositiveButton("Send Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VerifyEmailTask task = new VerifyEmailTask();
                task.execute(GET_CODE_URL + "cmd=getcode&email=" + emailText.getText().toString());
                Log.e("LoginActivity", GET_CODE_URL + "cmd=getcode&email=" + emailText.getText().toString());
                task.getStatus();
                while (task.getCode() == null);
                Log.e("LoginActivity", "code registered");
                verifyCode(task);
            }
        });
        builder.setView(v);
        builder.setTitle("Enter your email:");
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void verifyCode(final VerifyEmailTask task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.fragment_enter_code, null);
        final EditText text = (EditText) v.findViewById(R.id.code);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("LoginActivity", "here first");
//                if (task.getCode().equals(text.getText().toString().toUpperCase())) {
//                    Log.e("LoginActivity", task.getCode());
//                }
            }
        });
        builder.setView(v);
        builder.setTitle("An email has been sent with a verification code, please enter that code below");
        Dialog dialog = builder.create();
        dialog.show();


    }
    /**
     * Check whether the user is already logged in. If so, go straight to the dashboard.
     */
    private void checkLoggedIn() {
        if (mSharedPreferences.getBoolean(getString(R.string.logged_in), false)) {
            Intent i = new Intent(this, DashboardActivity.class);
            startActivity(i);
            finish();
        } else {
            // Double check that there's no Facebook account still logged in
            if (LoginManager.getInstance() != null) {
                LoginManager.getInstance().logOut();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallback.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempts to sign in
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Email profile query.
     */
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    /**
     * Login the user with the given email.
     *
     * @param email the email for the user
     */
    private void login(final String email) {
        // Store user email and record that they are logged in
        mSharedPreferences  = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(getString(R.string.current_email), email).apply();
        if (mCheckBox.isChecked()) {
            mSharedPreferences.edit().putBoolean(getString(R.string.logged_in), true).apply();
        }
        finish();
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
    }

    /**
     * Adds the list of emails to the adapter for the email view.
     *
     * @param emailAddressCollection list of emails
     */
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    public class VerifyEmailTask extends AsyncTask<String, Void, String> {
        private String mCode;

//        protected VerifyEmailTask(final )
        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    if (obj.getString("result").equals("success")) {
                        mCode = obj.getString("code");
                        Log.e("LoginActivity", mCode);
                    } else {
                        mCode = "error";
                        Log.e("LoginActivity", mCode);
                    }
                }
            } catch (JSONException e) {
                Log.e("LoginActivity", e.getStackTrace().toString());
            }
        }

        protected void setCode(final String code) {
            mCode = code;
        }
        protected String getCode() {
            return mCode;
        }
    }

    /**
     * Asynchronous task to attempt to log in a user using Facebook.
     */
    public class FacebookLoginTask extends AsyncTask<String, Void, String> {

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
                    if (obj.getString("result").equals("success")) {
                        login(obj.getString("email"));
                    } else {
                        Intent intent = new Intent(getApplicationContext(),
                                RegisterUserActivity.class);
                        Bundle bundle = new Bundle();
                        Profile profile = Profile.getCurrentProfile();
                        bundle.putString("email", obj.getString("email"));
                        bundle.putString("first", profile.getFirstName());
                        bundle.putString("last", profile.getLastName());
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                }
            } catch (JSONException e) {
                Log.e("Dashboard", e.getStackTrace().toString());
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        /** Tag to reference for console output. */
        public static final String TAG = "THIS_ACT";

        /** The current email address being evaluated. */
        private final String mEmail;

        /** The current password being evaluated. */
        private final String mPassword;

        /** Determines if there is a current network connection. Default value is true. */
        private boolean networkAccess = true;

        /**
         * Initialize a new UserLoginTask.
         *
         * @param email the email to be verified
         * @param password the password to be verified
         */
        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            StringBuilder url = new StringBuilder(USER_URL);
            try {
                // Build url
                url.append("&email=");
                url.append(URLEncoder.encode(mEmail, "UTF-8"));
                url.append("&pwd=");
                url.append(URLEncoder.encode(mPassword, "UTF-8"));
                // Establish network access
                URL urlObject = new URL(USER_URL + url.toString());
                HttpURLConnection urlConnection =  (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = buffer.readLine();
                if (s.contains("success")) {    // Evaluate if email and password exist
                    networkAccess = true;
                    return true;
                } else {
                    return false;
                }

            } catch (IOException e) {
                Log.e(TAG, "Network Error!");
                networkAccess = false;
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                login(mEmail);
            } else if (!networkAccess) {
                Toast.makeText(getApplicationContext(), "Cannot connect to network!", Toast.LENGTH_LONG).show();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

