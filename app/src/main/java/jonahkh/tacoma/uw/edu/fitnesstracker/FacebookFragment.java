package jonahkh.tacoma.uw.edu.fitnesstracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookFragment extends Fragment {


    public FacebookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        List<String> permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("public_profile");
        loginButton.setReadPermissions(permissions);
        loginButton.setFragment(this);

        // If using in a fragment

        // Other app specific specialization

//        // Callback registration
        CallbackManager callback = CallbackManager.Factory.create();
        Log.e("TAG", "here");
        loginButton.registerCallback(callback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("TAG", "Success");
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
        return inflater.inflate(R.layout.fragment_facebook, container, false);

    }

}
