package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * Created by jonah on 5/1/2016.
 */
public class MyExerciseExpandableListAdapter extends BaseExpandableListAdapter {
    private Map<Exercise, List<WorkoutSet>> mExerciseSets;
    private Activity mContext;
    private List<Exercise> mExercises;

    public MyExerciseExpandableListAdapter(Activity context, List<Exercise> exercises,
                                           Map<Exercise, List<WorkoutSet>> exerciseSets) {
        mExercises = exercises;
        mContext = context;
        mExerciseSets = exerciseSets;
    }
    @Override
    public int getGroupCount() {
        return mExercises.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mExerciseSets.get(mExercises.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mExercises.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mExerciseSets.get(mExercises.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {        String exerciseName = ((Exercise) getGroup(groupPosition)).getExerciseName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_specific_workout, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.exericse);
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
        setView.setText(set.getSetNumber());
        weightView.setText(set.getWeight());
        repsView.setText(set.getReps());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
