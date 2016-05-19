package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardDisplayFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordEmailDialog extends DialogFragment {
    private Dialog mDialog;

    public ForgotPasswordEmailDialog() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DashboardActivity dashboard = (DashboardActivity) getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = dashboard.getLayoutInflater();
        final View v = inflater.inflate(R.layout.fragment_forgot_password_email, null);
        final EditText emailText = (EditText) dashboard.findViewById(R.id.email);
//        mCurrentEmail;
//        final String url = DashboardDisplayFragment.USER_LAST_LOGGED_WORKOUT + "&email="  + mCurrentEmail;
//        Log.e("addSet", url);
//        task.execute(url);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(v);
        builder.setTitle("Enter Set Information");
        final Dialog dialog = builder.create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(getActivity().getApplicationContext(), "Set not added!", Toast.LENGTH_LONG).show();
            }
        });
        return dialog;
    }
}
