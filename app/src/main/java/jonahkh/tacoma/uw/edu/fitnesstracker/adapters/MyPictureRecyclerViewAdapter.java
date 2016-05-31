package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.PictureListFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Picture;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link jonahkh.tacoma.uw.edu.fitnesstracker.model.Picture} and makes a call to the
 * specified {@link PictureListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPictureRecyclerViewAdapter extends RecyclerView.Adapter<MyPictureRecyclerViewAdapter.ViewHolder> {

    /** Tag used for debugging. */
    private static final String TAG = "Picture does not exist:";

    private final List<Picture> mValues;

    private final PictureListFragment.OnListFragmentInteractionListener mListener;

    public MyPictureRecyclerViewAdapter(List<Picture> items, PictureListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_picture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        Bitmap image = getImageBitmap(mValues.get(position).getPhotoDirectoryLocation(), position);
        Bitmap image = mValues.get(position).getImage();
        if(image != null) {
            holder.mContentView.setImageBitmap(image);
            holder.mContentView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            holder.mContentView.setBackgroundResource(android.R.color.transparent);
            Log.e(TAG, mValues.get(position).getPhotoDirectoryLocation());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mContentView;
        public Picture mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (ImageView) view.findViewById(R.id.imageView);
        }

    }
}