/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.net.URLEncoder;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;


/**
 * Fragment used to enter users information when registering.
 * A simple {@link Fragment} subclass.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class RegisterUserFragment extends Fragment {

    /** Tag used for debugging. */
    private final String TAG = "Register User Fragment";

    /** URL used to add the user information to the database. */
    private final static String USER_ADD_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/addUser.php?";

    /** Users First name view. */
    private EditText mFirstName;

    /** Users Last name view. */
    private EditText mLastName;

    /** Users email view. */
    private EditText mEmail;

    /** User password view. */
    private EditText mPassword;

    /** User confirm password view. */
    private EditText mConfirmPassword;

    /** Required empty public constructor */
    public RegisterUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register_user, container, false);
        mFirstName = (EditText) v.findViewById(R.id.add_user_first_name);
        mLastName = (EditText) v.findViewById(R.id.add_user_last_name);
        mEmail = (EditText) v.findViewById(R.id.add_user_email);
        mPassword = (EditText) v.findViewById(R.id.add_user_password);
        mConfirmPassword = (EditText) v.findViewById(R.id.add_user_confirm_password);
        savedInstanceState = getArguments();

        if (savedInstanceState != null && savedInstanceState.getBoolean("fbook")) {
            // Fill and disable name and email fields if registering through Facebook
            mLastName.setEnabled(false);
            mEmail.setEnabled(false);
            mFirstName.setEnabled(false);
            mFirstName.setText(savedInstanceState.getString("first"));
            mLastName.setText(savedInstanceState.getString("last"));
            mEmail.setText(savedInstanceState.getString("email"));
        }

        Button addUserButton = (Button) v.findViewById(R.id.register_next_button);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean fieldNotNull = checkRequirements();
                boolean passwordsMatch = false;
                if(fieldNotNull) {
                   passwordsMatch = checkPasswordMatching();
                }

                if(passwordsMatch) {//password does match
                    ((RegisterUserActivity)getActivity()).setUserInformation(
                            mFirstName.getText().toString(),
                            mLastName.getText().toString(),
                            mEmail.getText().toString(),
                            mPassword.getText().toString());
                    String addUserURL = buildAddUserURL();
                    ((RegisterUserActivity)getActivity()).addUserData(addUserURL);
                }
            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        Bundle b = getArguments();
        Profile profile = Profile.getCurrentProfile();
        if (b != null && profile != null &&  b.getBoolean("fbook")) {
            LoginManager mgr = LoginManager.getInstance();
            mgr.logOut();
        }
        super.onDestroy();
    }

    /**
     * Checks whether the password and confirm passwords fields match.
     *
     * @return Whether the password and confirm passwords fields match.
     */
    private boolean checkPasswordMatching() {
        if(!mPassword.getEditableText().toString().
                equals(mConfirmPassword.getEditableText().toString())){
            Log.i(TAG, "The passwords do not match");
            mConfirmPassword.setError(getString(R.string.password_mis_match));
            mConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Checks whether all required fields have data.
     *
     * @return Whether all required fields have data.
     */
    private boolean checkRequirements() {
        boolean notEmpty = true;
        // checking that fields are not empty
        if (TextUtils.isEmpty(mFirstName.getEditableText())) {
            notEmpty = false;
            mFirstName.setError(getString(R.string.error_field_required));
            mFirstName.requestFocus();
        }
        if (TextUtils.isEmpty(mLastName.getEditableText())) {
            notEmpty = false;
            mLastName.setError(getString(R.string.error_field_required));
            mLastName.requestFocus();
        }
        if (TextUtils.isEmpty(mEmail.getEditableText())) {
            notEmpty = false;
            mEmail.setError(getString(R.string.error_field_required));
            mEmail.requestFocus();
        }
        if (TextUtils.isEmpty(mPassword.getEditableText())) {
            notEmpty = false;
            mPassword.setError(getString(R.string.error_field_required));
            mPassword.requestFocus();
        }
        if (TextUtils.isEmpty(mConfirmPassword.getEditableText())) {
            notEmpty = false;
            mConfirmPassword.setError(getString(R.string.error_field_required));
            mConfirmPassword.requestFocus();
        }
        return notEmpty;
    }

    /**
     * Method that will build the url for calling the AsyncTask.
     *
     * @return rl for calling the AsyncTask.
     */
    @NonNull
    private String buildAddUserURL() {

        StringBuilder sb = new StringBuilder(USER_ADD_URL);

        try {
            String email = mEmail.getText().toString();
            sb.append("email=");
            sb.append(email);


            String pwd = mPassword.getText().toString();
            sb.append("&pwd=");
            sb.append(URLEncoder.encode(pwd, "UTF-8"));

            Log.i(TAG, sb.toString());
        }
        catch(Exception e) {
            Toast.makeText( getContext(), "Network connectivity issue", Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }
}
