package com.ashish.adit_portal.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Ashish on 9/12/2016.
 */
public class ImageStorage {


    static String saveToSdCard(Bitmap bitmap, String filename, Context context) throws Exception {

        String stored = "failed";
        if (isExternalStorageWritable()) {
            File sdcard = Environment.getExternalStorageDirectory();

            File folder = new File(sdcard.getAbsoluteFile(), "Adit portal");
            if(!folder.exists()){
                folder.mkdirs();
            }
            File file = new File(folder.getAbsoluteFile(), filename);
            if (file.exists())
                return stored;
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                scanMedia(context,sdcard.getAbsolutePath() + "/Adit portal/"+filename);
            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));//scanFile(context,"/Adit portal/"+filename);
                stored = "success";
            return stored;
        }
        return null;
    }

    public static Bitmap getImage(String imagename) {

        Bitmap mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = BitmapFactory.decodeFile(myDir.getPath() + "/Adit portal/"+imagename);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return mediaImage;
    }
    public static boolean checkifImageExists(String imagename)
    {
        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/Adit portal/"+imagename);
        return myFile.exists();
    }
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    private static void scanMedia(Context context,String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
    }
}
