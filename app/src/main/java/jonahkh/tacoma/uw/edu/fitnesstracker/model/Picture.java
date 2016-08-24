/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;

/**
 * Picture class to store different size of bitmaps and picture location.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class Picture implements Serializable {

    /** The maximum image dimension when displaying the small image size of the bitmap. */
    public static final int MAX_IMAGE_SIZE = 250;

    /** URL for the PHP to delelte a picture directory. */
    private static final String DELETE_PICTURE_URL =
            "http://cssgate.insttech.washington.edu/~_450atm2/deletePicture.php?";

    /** The photo directory location. */
    public static final String PHOTO_DIR_LOC = "photoDirectoryLocation";

    /** The reduce bitmap image/picture of this class. */
    private Bitmap mImage;

    /** The user's profile picture directory. */
    private String mPhotoDirectoryLocation;

//    /** The original bitmap image of this picture class. */
//    private Bitmap mOriginalImage;

    /**
     * Constructor for this class.
     *
     * @param photoDirectoryLocation The directory of the where the image/picture
     *                               is stored at.
     */
    public Picture(String photoDirectoryLocation) {
        mPhotoDirectoryLocation = photoDirectoryLocation;
        setImage(photoDirectoryLocation);
    }

    public String getPhotoDirectoryLocation() {
        return mPhotoDirectoryLocation;
    }

//    public void setPhotoDirectoryLocation(String mPhotoDirectoryLocation) {
//        this.mPhotoDirectoryLocation = mPhotoDirectoryLocation;
//    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param courseJSON The JSON string object representation.
     * @return reason or null if successful.
     */
    public static String parseCourseJSON(String courseJSON, List<Picture> courseList, String userEmail) {
        String reason = null;
        ArrayList<String> imagesFilestoDelete = new ArrayList<>();
        if (courseJSON != null) {
            try {
                JSONArray arr = new JSONArray(courseJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    final String dir = obj.getString(PHOTO_DIR_LOC);
                    Picture picture = new Picture(dir);
                    if(picture.getImage() != null) {
                        courseList.add(picture);
                    } else {
                        final String url = DELETE_PICTURE_URL + "email=" +
                                userEmail + "&photoDirectoryLocation=" +
                                picture.getPhotoDirectoryLocation();
                        Log.i("JPG to Delete: ", url);
                        imagesFilestoDelete.add(url);
                    }
                }
                String[] urls = new String[imagesFilestoDelete.size()];
                imagesFilestoDelete.toArray(urls);
                DeletePicturesTask task = new DeletePicturesTask();
                task.execute(urls);
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

    public Bitmap getImage() {
        return mImage;
    }

    /* Methid to set the fields of this picture class. */
    public void setImage(String photoDirectoryLocation) {
        Bitmap image = BitmapFactory.decodeFile(photoDirectoryLocation);
        if(image == null){
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
//        setmOriginalImage(image);
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = MAX_IMAGE_SIZE;
            height = (int) (width / bitmapRatio);
        } else {
            height = MAX_IMAGE_SIZE;
            width = (int) (height * bitmapRatio);
        }
        mImage = Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Bitmap getmOriginalImage() {
        if(mPhotoDirectoryLocation != null) {
            return BitmapFactory.decodeFile(mPhotoDirectoryLocation);
        }
        return null;
    }

//    public void setmOriginalImage(Bitmap image) {
//        mOriginalImage = image;
//    }

    /**
     * Async task class to delete directories of images that are not found on the device
     * but are stored in the database.
     */
    private static class DeletePicturesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Log.e("DeletePicTask Problem:", result);
            }
        }
    }
}
