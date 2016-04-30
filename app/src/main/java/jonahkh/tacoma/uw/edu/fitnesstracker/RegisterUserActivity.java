package jonahkh.tacoma.uw.edu.fitnesstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Hector on 4/26/2016.
 */
public class RegisterUserActivity extends AppCompatActivity implements RegisterUserFragment.UserAddListener {

    public final String TAG = "REGISTER ACTIVIY";

    /** Users First name. */
    private String mUserFirstName = "";

    /** Users Last name. */
    private String mUserLastName = "";

    /** Users email. */
    private String mUserEmail = "";

    private String mUserPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // launch the the registerUserfragment
        RegisterUserFragment userAddFragment = new RegisterUserFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_register_user_xml, userAddFragment)
                .addToBackStack(null)
                .commit();

        // TODO lunch the new fragment for additional infor and close userAddFragment after next click;
//        -- From fragment to activty:
//
//        ((YourActivityClassName)getActivity()).yourPublicMethod();
//
//        -- From activity to fragment:
//
//        FragmentManager fm = getSupportFragmentManager();
//
//        //if you added fragment via layout xml
//        YourFragmentClass fragment = (YourFragmentClass)fm.findFragmentById(R.id.your_fragment_id);
//        fragment.yourPublicMethod();
//        If you added fragment via code and used a tag string when you added your fragment, use findFragmentByTag instead:
//
//        YourFragmentClass fragment = (YourFragmentClass)fm.findFragmentByTag("yourTag");
    }

    @Override
    public void addUser(String url) {
        // TODO to finish this
    }

    /**
     * Method to set the information of the user.
     * @param theFirstName
     */
    public void setUserInformation(final String theFirstName, final String theLastName,
                                   final String theEmail, final String thePass) {
        mUserFirstName = theFirstName;
        mUserLastName = theLastName;
        mUserEmail = theEmail;
        mUserPassword = thePass;
    }

    protected void getUserAdditionalInfo() {
        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();
        RegisterUserAdditional_Info userOtherInfo = new RegisterUserAdditional_Info();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_register_user_xml, userOtherInfo)
                .addToBackStack(null)
                .commit();
    }
}