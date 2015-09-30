package com.example.barameek.myuploaddemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barameek.myuploaddemo.util.CameraIntentHelperActivity;
import com.example.barameek.myuploaddemo.util.UploadImageUtils;

public class MainActivity extends CameraIntentHelperActivity {

    public Handler mUIHandler = new Handler();
    private String selectedImagePath;
    private ImageView selectImageInCameraBtn;
    private ImageView selectImageInGalleryBtn;
    private TextView imageFileName;
    private ImageView imageView;
    private Bitmap mPhotoBitMap;
    public String mUploadedFileName;
    public int maxPixel = 500;

    private Intent inboundIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindWidget();
        setWidgetListener();
        // share from other app
        handleShareIntent();
    }

    private void handleShareIntent() {
        try {
            inboundIntent = getIntent();
            Uri receivedUri=(Uri) inboundIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            imageView.setImageURI(receivedUri);
        } catch (Exception e) {

        }
    }

    private void setWidgetListener() {

        // Select image from camera
        selectImageInCameraBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // if maxPixel=-1 then do not compress
                startCameraIntent(maxPixel);
            }
        });

        // Select image from gallery
        selectImageInGalleryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // if maxPixel=-1 then do not compress
                startGalleryIntent(maxPixel);
            }
        });
    }

    @Override
    protected void onPhotoUriFound(Uri _photoUri, Bitmap _photoBitMap, String _filePath) {
        super.onPhotoUriFound(_photoUri, _photoBitMap, _filePath);

        mPhotoBitMap = _photoBitMap;
        imageFileName.setText(_filePath);
        imageView.setImageBitmap(mPhotoBitMap);
        new UploadTask().execute("http://172.16.1.194/tutorials/upload.php");
    }

    private void bindWidget() {
        selectImageInCameraBtn = (ImageView) findViewById(R.id.selectImageInCameraBtn);
        selectImageInGalleryBtn = (ImageView) findViewById(R.id.selectImageInGalleryBtn);
        imageFileName = (TextView) findViewById(R.id.imagename);
        imageView = (ImageView) findViewById(R.id.imageview);

    }

    public class UploadTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // called before doInBackground - UI thread

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        // AsyncTask<String,Void,String>
        // First param : String => params
        // Second param : Void => use for onProgressUpdate, most of the case should be int.
        // Third param : String => return value, use for OnPostExecute
        protected String doInBackground(String... params) {
            // Called after onPreExecute - Custom Thread
            final String url=params[0];
            final String randomName=UploadImageUtils.getRandomFileName();
            final String result=UploadImageUtils.uploadFile(randomName,url,mPhotoBitMap);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // called after doInBackground - UI thread
            if (s!=null && s.equals("OK")) {
                Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
