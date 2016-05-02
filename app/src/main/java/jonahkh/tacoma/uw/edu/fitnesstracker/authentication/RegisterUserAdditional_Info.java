package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Integer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUserAdditional_Info extends Fragment {

    public final String TAG = "Reg Additional Info";

    public final int INVALID = -1;

    private byte[] mPhoto = null;

    private int mDateDOB;

    private int mMonthDOB;

    private int mYearDOB;

    private int mWeight;

    private int mHeightFt;

    private int mHeightIn;

    public RegisterUserAdditional_Info() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_register_user_additional__info, container, false);
        Button register = (Button)myView.findViewById(R.id.registerUser_bt);
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDateDOB = getDateSelected(myView);
                mMonthDOB = getMonthSelected(myView);
                mYearDOB = getYearEntered(myView);
                mWeight = getWeightEntered(myView);
                mHeightFt = getHeightFt(myView);
                mHeightIn = getHeightIn(myView);
                if(mDateDOB != INVALID && mMonthDOB != INVALID && mYearDOB != INVALID
                        && mWeight != INVALID && mHeightFt != INVALID && mHeightIn != INVALID){
                    ((RegisterUserActivity)getActivity()).setUserAdditionInfo(mPhoto, mDateDOB, mMonthDOB,
                            mYearDOB, mWeight, mHeightFt, mHeightIn);


                    String url = ((RegisterUserActivity)getActivity()).buildAddUserAddtionaIfoURL();
                    ((RegisterUserActivity)getActivity()).addUserData(url);
//                    boolean addedUserAddtionalInfo = ((RegisterUserActivity)getActivity()).mSuccesful;
//                    if(addedUserAddtionalInfo) {
                        // Go to dashboard
//                        (Toast.makeText(getActivity().getApplicationContext(),
//                                R.string.registration_sucessful, Toast.LENGTH_SHORT)).show();
//                        Intent intent = new Intent(getActivity().getApplicationContext(), DashboardActivity.class);
//                        startActivity(intent);
//                        getActivity().getSupportFragmentManager().popBackStackImmediate();
//                    }
                }
            }
        });

        return myView;
    }

    private int getHeightIn(View myView) {
        TextView heightIn = (TextView) myView.findViewById(R.id.registerUser_heightIn);
        if(TextUtils.isEmpty(heightIn.getEditableText())){
            heightIn.setError(getString(R.string.error_field_required));
            heightIn.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(heightIn.getEditableText().toString());
        Log.i(TAG, "Height Entered (in): " + temp);
        return temp;
    }

    private int getHeightFt(View myView) {
        TextView heightFt = (TextView) myView.findViewById(R.id.registerUser_heightFt);
        if(TextUtils.isEmpty(heightFt.getEditableText())){
            heightFt.setError(getString(R.string.error_field_required));
            heightFt.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(heightFt.getEditableText().toString());
        Log.i(TAG, "Height Entered (Ft): " + temp);
        return temp;
    }

    private int getWeightEntered(View myView) {
        TextView weightV = (TextView) myView.findViewById(R.id.registerUser_weight);
        if(TextUtils.isEmpty(weightV.getEditableText())){
            weightV.setError(getString(R.string.error_field_required));
            weightV.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(weightV.getEditableText().toString());
        Log.i(TAG, "Weight Entered: " + temp);
        return temp;
    }

    private int getYearEntered(View myView) {
        TextView yearV = (TextView) myView.findViewById(R.id.year_entered);
        if(TextUtils.isEmpty(yearV.getEditableText())){
            yearV.setError(getString(R.string.error_field_required));
            yearV.requestFocus();
            return INVALID;
        }
        int temp = Integer.parseInt(yearV.getEditableText().toString());
        Log.i(TAG, "DOB Year Entered: " + temp);
        return temp;
    }

    private int getDateSelected(View v) {
        Spinner daySpinner= ((Spinner) v.findViewById(R.id.day_spinner));
        String day = daySpinner.getSelectedItem().toString();
        if(day.equals("Date")) {
            (Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_day_required, Toast.LENGTH_SHORT)).show();
            daySpinner.requestFocus();
            return INVALID;
        }
        int daySelec = Integer.parseInt(day);
        Log.i(TAG, "Day Selected: " + daySelec);
        return daySelec;
    }

    public int getMonthSelected(View v) {
        Spinner monthSpinner = (Spinner) v.findViewById(R.id.month_spinner);
        String tempMonth = monthSpinner.getSelectedItem().toString();
        if(tempMonth.equals("Month")){
            (Toast.makeText(getActivity().getApplicationContext(),
                    R.string.error_month_required, Toast.LENGTH_SHORT)).show();
            monthSpinner.requestFocus();
            return INVALID;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(tempMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int monthSelec = cal.get(Calendar.MONTH) + 1;
        Log.i(TAG, "Month Selected (int): " + monthSelec);
        return monthSelec;
    }


}
