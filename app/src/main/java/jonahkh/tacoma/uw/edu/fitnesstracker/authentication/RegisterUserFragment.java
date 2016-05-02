package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUserFragment extends Fragment {

    public final String TAG = "Register User Fragment";

    private final static String USER_ADD_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/addUser.php?";

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;

    public RegisterUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register_user, container, false);
        mFirstName = (EditText) v.findViewById(R.id.add_user_first_name);
        mLastName = (EditText) v.findViewById(R.id.add_user_last_name);
        mEmail = (EditText) v.findViewById(R.id.add_user_email);
        mPassword = (EditText) v.findViewById(R.id.add_user_password);
        mConfirmPassword = (EditText) v.findViewById(R.id.add_user_confirm_password);

        Button addUserButton = (Button) v.findViewById(R.id.register_next_button);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = buildCourseURL(v);
//                mListener.addUser(url);

                boolean fieldNotNull = checkRequirements();
                boolean passwordsMatch = false;
                if(fieldNotNull) {
                   passwordsMatch = checkPasswordMatching();
                }

                // successful registeration
                if(passwordsMatch) {//passwordsMatch
                    ((RegisterUserActivity)getActivity()).setUserInformation(
                            mFirstName.getText().toString(),
                            mLastName.getText().toString(),
                            mEmail.getText().toString(),
                            mPassword.getText().toString());
                    String addUserURL = buildAddUserURL();
                    ((RegisterUserActivity)getActivity()).addUserData(addUserURL);
//                    boolean addedUser = ((RegisterUserActivity)getActivity()).mSuccesful;
//                    Log.i(TAG, "Continue: " + addedUser);
//                    if(addedUser) {
//                        ((RegisterUserActivity)getActivity()).getUserAdditionalInfo();
//                    } else {
//                        mEmail.requestFocus();
//                    }
                }
            }
        });

        return v;
    }

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

    /** method that will build the url for calling the AsyncTask.  */
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
            Toast.makeText( getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }
}
