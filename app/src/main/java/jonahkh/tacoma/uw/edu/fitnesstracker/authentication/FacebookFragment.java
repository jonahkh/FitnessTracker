package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;


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
        CallbackManager callback = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);

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
        List<String> permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("public_profile");
        permissions.add("user_friends");
        loginButton.setReadPermissions(permissions);
        loginButton.setFragment(this);
        Log.e("FBOOK", "");
        if (FacebookSdk.getClientToken() == null) {
            Log.e("FBOOK", "null");
        }
//        Generate key hash
        // If using in a fragment
    Log.e("TAG", LoginManager.getInstance().getLoginBehavior().toString());
        // Other app specific specialization
//        Log.e("FBOOK--", AccessToken.getCurrentAccessToken().getUserId().toString());
//        Log.e("TAG", AccessToken.getCurrentAccessToken().toString());
//        // Callback registration

        return inflater.inflate(R.layout.fragment_facebook, container, false);

    }



}
