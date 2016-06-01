package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.ViewOriginalImageFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Picture;

import java.util.List;


public class MyPictureRecyclerViewAdapter extends RecyclerView.Adapter<MyPictureRecyclerViewAdapter.ViewHolder> {

    /** Tag used for debugging. */
    private static final String TAG = "Picture does not exist:";

    private final List<Picture> mValues;

    private final FragmentActivity mActivity;

    private static Picture mCurrentPicture;

    public MyPictureRecyclerViewAdapter(List<Picture> items,
                                        FragmentActivity activity) {
        mValues = items;
        mActivity = activity;
    }

    public static void setBigImageView(ImageView bigImageView) {
        bigImageView.setImageBitmap(mCurrentPicture.getmOriginalImage());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_picture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
//        Bitmap image = getImageBitmap(mValues.get(position).getPhotoDirectoryLocation(), position);
        Bitmap image = mValues.get(position).getImage();
        if(image != null) {
            holder.mContentView.setImageBitmap(image);
            holder.mContentView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewOriginalImageFragment orgImgFrag = new ViewOriginalImageFragment();
                    mCurrentPicture = mValues.get(position);
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, orgImgFrag)
                            .addToBackStack(null)
                            .commit();
                    Log.e("CLick", "clikc");
                }
            });
        } else {
            holder.mContentView.setBackgroundResource(android.R.color.transparent);
            Log.e(TAG, mValues.get(position).getPhotoDirectoryLocation());
        }
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