/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.ViewOriginalImageFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Picture;

import java.util.List;

/**
 * The View adapter for the recycler view used to show a list of previous pictures.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class MyPictureRecyclerViewAdapter extends RecyclerView.Adapter<MyPictureRecyclerViewAdapter.ViewHolder> {

    /** Tag used for debugging. */
    private static final String TAG = "MyPicRecyViewAdapter:";

    /** List of Picture for this user. */
    private final List<Picture> mValues;

    /** The activity calling this Recycler Adapter. */
    private final FragmentActivity mActivity;

    /** The current Picture (Profile pic). */
    private static Picture mCurrentPicture;

    /**
     * The constructor to instantiate this class.
     *
     * @param items The list of pictures.
     * @param activity The activity calling this Recycler Adapter.
     */
    public MyPictureRecyclerViewAdapter(List<Picture> items,
                                        FragmentActivity activity) {
        mValues = items;
        mActivity = activity;
    }

    /**
     * Method to set the image of an ImageView.
     *
     * @param bigImageView The Image view to set its image.
     */
    public static void setBigImageView(ImageView bigImageView) {
        bigImageView.setImageBitmap(mCurrentPicture.getmOriginalImage());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_picture, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        final Bitmap image = mValues.get(position).getImage();
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
                }
            });
            holder.mContentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.mContentView.setLongClickable(true);
//                    holder.mContentView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(image)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    ShareDialog.show(mActivity, content);
                    return true;
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

    /** The view holder containing required info to make the adapter work. */
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