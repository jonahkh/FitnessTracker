/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */

package jonahkh.tacoma.uw.edu.fitnesstracker.authentication;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.ImageButton;
import android.widget.Toast;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * Fragment used to add a profile image.
 * A simple {@link Fragment} subclass.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class AddPictureFragment extends DialogFragment {



    /** Error message displayed if no camera permission was granted. */
    private static final String CAMERA_PERMISSION_ERROR =
            "Camera persmission was not granted, can not take picture.";

    /** The image button. */
    private ImageButton mImageView;

    /** Default empty constructor. */
    public AddPictureFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        mImageView = (ImageButton) getActivity().findViewById(R.id.add_pic);
//        assert mImageView != null;
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.profile_photo);
        // positive button
        builder.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // add from camera
                    ((RegisterUserActivity)getActivity()).dispatchTakePictureIntent();
                } else {
                    Toast.makeText(getContext(), CAMERA_PERMISSION_ERROR, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        // negative button
        builder.setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // add from gallery
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
