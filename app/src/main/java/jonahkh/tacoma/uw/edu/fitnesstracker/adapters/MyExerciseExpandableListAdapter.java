/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.WorkoutSet;

/**
 * An adapter for the view exercises list. The main list items will display the name of the
 * exercises for the current workout and then expanding an exercise list item will display all of
 * the sets for that exercise.
 */
public class MyExerciseExpandableListAdapter extends BaseExpandableListAdapter {

    /** Set number identifier for this set. */
    private static final String SET = "Set ";

    /** Weight identifier for this set. */
    private static final String WEIGHT = "Weight: ";

    /** Repetition identifier for this set. */
    public static final String REPS = "Reps: ";

    /** The activity that holds this adapter. */
    private final Activity mContext;

    /** The list of exercises for the selected workout. */
    private final List<Exercise> mExercises;

    /**
     * Initialize a new MyExerciseExpandableListAdapter.
     *
     * @param context the Activity that holds this adapter
     * @param exercises the list of exercises for the current workout
     */
    public MyExerciseExpandableListAdapter(Activity context, List<Exercise> exercises) {
        mExercises = exercises;
        mContext = context;
    }
    @Override
    public int getGroupCount() {
        return mExercises.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mExercises.get(groupPosition).getSets().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mExercises.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mExercises.get(groupPosition).getSets().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String exerciseName = ((Exercise) getGroup(groupPosition)).getExerciseName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_specific_workout, null);
        }
        final TextView item = (TextView) convertView.findViewById(R.id.this_exercise);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(exerciseName);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final WorkoutSet set = ((WorkoutSet) getChild(groupPosition, childPosition));
        LayoutInflater inflater = mContext.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_exercise, null);
        }
        final TextView setView = (TextView) convertView.findViewById(R.id.set_number);
        final TextView weightView = (TextView) convertView.findViewById(R.id.weight);
        final TextView repsView = (TextView) convertView.findViewById(R.id.reps_performed);
        final String setText = SET + Integer.toString(set.getSetNumber());
        final String weightText = WEIGHT + Integer.toString(set.getWeight());
        final String repsText = "Reps: " + Integer.toString(set.getReps());

        setView.setText(setText);
        weightView.setText(weightText);
        repsView.setText(repsText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
