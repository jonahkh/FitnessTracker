/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * This Fragment is used to display the list of exercises for a workout.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class SpecificWorkoutFragment extends Fragment {

    /** Initialize a new SpecificWorkoutFragment. */
    public SpecificWorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_workout, container, false);
    }

}
