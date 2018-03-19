package com.ashish.adit_portal.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashish.adit_portal.R;
import com.ashish.adit_portal.helper.ImageStorage;

public class NoticeImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_image);

        Intent intent=getIntent();
        String imagename=intent.getStringExtra("image_name");
        final ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageview);
       try {
            Bitmap b = ImageStorage.getImage(imagename);
            mImageView.setImageBitmap(b);

        }
        catch (Exception e){
            Toast.makeText(NoticeImage.this,e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
