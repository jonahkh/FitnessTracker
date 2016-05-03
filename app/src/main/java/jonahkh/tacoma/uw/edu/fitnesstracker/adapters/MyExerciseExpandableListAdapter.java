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

    /** The activity that holds this adapter. */
    private Activity mContext;

    /** The list of exercises for the selected workout. */
    private List<Exercise> mExercises;

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
        String exerciseName = ((Exercise) getGroup(groupPosition)).getExerciseName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_specific_workout, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.this_exercise);
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
        TextView setView = (TextView) convertView.findViewById(R.id.set_number);
        TextView weightView = (TextView) convertView.findViewById(R.id.weight);
        TextView repsView = (TextView) convertView.findViewById(R.id.reps_performed);
        setView.setText("Set " + Integer.toString(set.getSetNumber()));
        weightView.setText("Weight: " + Integer.toString(set.getWeight()));
        repsView.setText("Reps: " + Integer.toString(set.getReps()));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
