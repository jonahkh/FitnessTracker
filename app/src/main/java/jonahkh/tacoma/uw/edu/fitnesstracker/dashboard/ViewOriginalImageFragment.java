package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.MyPictureRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewOriginalImageFragment extends Fragment {

    /** The Image view. */
    private ImageView mImageView;

    private View mView;

    public ViewOriginalImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_view_original_image, container, false);
        mImageView = (ImageView) mView.findViewById(R.id.original_img_dis);
        MyPictureRecyclerViewAdapter.setBigImageView(mImageView);
        return mView;
    }
}
