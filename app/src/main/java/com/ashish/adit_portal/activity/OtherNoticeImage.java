package com.ashish.adit_portal.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;


public class OtherNoticeImage extends Activity {

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_image);
        Intent intent = getIntent();
        final String imagename = intent.getStringExtra("image_name");
        final ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageview);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        pDialog.setMessage("Downloading...");
        showDialog();
        if (AppController.isInternetAvailable(OtherNoticeImage.this)) {
            ImageRequest request = new ImageRequest(AppConfig.URL_IMAGE + imagename,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            hideDialog();
                            mImageView.setImageBitmap(bitmap);

                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            hideDialog();
                            Toast.makeText(OtherNoticeImage.this, "Something is wrong there might be problem with your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
            AppController.getInstance().addToRequestQueue(request);
        }else{
            Toast.makeText(this, "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
     }
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
