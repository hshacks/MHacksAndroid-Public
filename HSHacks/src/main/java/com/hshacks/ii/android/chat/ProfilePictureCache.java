package com.hshacks.ii.android.chat;

import android.graphics.*;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.hshacks.ii.android.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfilePictureCache {
    //Just let me know if this is silly, I'm wingin it
    Map<String, Bitmap> profilePictures;
    Map<ImageView, String> imageViewsWaiting;

    ArrayList<DownloadAndMaskImageTask> tasks;

    public ProfilePictureCache() {
        profilePictures = new HashMap<String, Bitmap>();
        imageViewsWaiting = new HashMap<ImageView, String>();

        tasks = new ArrayList<DownloadAndMaskImageTask>();
    }

    public void loadImageURLIntoImageView(String imageUrl, ImageView imageView) {
        if (profilePictures.containsKey(imageUrl)) {
            if(imageViewsWaiting.containsKey(imageView)) {
                imageViewsWaiting.remove(imageView);
            }

            imageView.setImageBitmap(profilePictures.get(imageUrl));
        } else {
            imageView.setImageResource(R.drawable.default_profile);
            imageViewsWaiting.put(imageView, imageUrl);

            if(!isImageAlreadyBeingLoaded(imageUrl)) {
                DownloadAndMaskImageTask newTask = new DownloadAndMaskImageTask(imageUrl);
                tasks.add(newTask);
                newTask.execute();
            }
        }
    }

    private boolean isImageAlreadyBeingLoaded(String imageUrl) {
        for(DownloadAndMaskImageTask task : tasks) {
            if(task.getImageUrl().equals(imageUrl)) {
                return true;
            }
        }

        return false;
    }

    private class DownloadAndMaskImageTask extends AsyncTask<Void, Void, Bitmap> {
        private String imageUrl;

        public DownloadAndMaskImageTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        protected Bitmap doInBackground(Void... voids) {
            Bitmap loadedBitmap = null;
            Bitmap circularBitmap = null;

            try {
                InputStream in = new java.net.URL(imageUrl).openStream();
                loadedBitmap = getResizedBitmap(BitmapFactory.decodeStream(in), 256, 256);

                BitmapShader shader = new BitmapShader(loadedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);

                circularBitmap = Bitmap.createBitmap(loadedBitmap.getWidth(), loadedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(circularBitmap);
                canvas.drawCircle(loadedBitmap.getWidth() / 2, loadedBitmap.getHeight() / 2, loadedBitmap.getWidth() / 2, paint);
            } catch (Exception e) {
                e.printStackTrace();
            }

           // loadedBitmap.recycle();
           // loadedBitmap = null;
            return circularBitmap;
        }

        public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
            return resizedBitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            ArrayList<ImageView> imageViewsWaitingForThisBitmap = new ArrayList<ImageView>();
            for(Map.Entry entry : imageViewsWaiting.entrySet()) {
                if(entry.getValue().equals(imageUrl)) {
                    imageViewsWaitingForThisBitmap.add((ImageView) entry.getKey());
                }
            }

            if (bitmap != null) {
                //Set all of the waiting ImageViews
                for(ImageView imageView : imageViewsWaitingForThisBitmap) {
                    imageView.setImageBitmap(bitmap);
                    imageViewsWaiting.remove(imageView);
                }

                //Add it to the cache
                profilePictures.put(imageUrl, bitmap);
            } else {
                //Loading failed, remove the waiting ImageViews
                for(ImageView imageView : imageViewsWaitingForThisBitmap) {
                    imageViewsWaiting.remove(imageView);
                }
            } 
            tasks.remove(this);
        }
    }
}
