package jonahkh.tacoma.uw.edu.fitnesstracker.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;

/**
 * Created by Hector on 5/31/2016.
 */
public class Picture implements Serializable {

    private String mPhotoDirectoryLocation;

    public static final int MAX_IMAGE_SIZE = 300;

    private static final String DELETE_PICTURE_URL =
            "http://cssgate.insttech.washington.edu/~_450atm2/deletePicture.php?";

    public static final String PHOTO_DIR_LOC = "photoDirectoryLocation";

    private Bitmap mImage;

    public Picture(String photoDirectoryLocation) {
        mPhotoDirectoryLocation = photoDirectoryLocation;
        setImage(photoDirectoryLocation);
    }

    public String getPhotoDirectoryLocation() {
        return mPhotoDirectoryLocation;
    }

    public void setPhotoDirectoryLocation(String mPhotoDirectoryLocation) {
        this.mPhotoDirectoryLocation = mPhotoDirectoryLocation;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param courseJSON
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

    public void setImage(String photoDirectoryLocation) {
        Bitmap image = BitmapFactory.decodeFile(photoDirectoryLocation);
        if(image == null){
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();

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


    private static class DeletePicturesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Log.e("DeletePicTask Poblem:", result);
                return;
            }
        }
    }
}
