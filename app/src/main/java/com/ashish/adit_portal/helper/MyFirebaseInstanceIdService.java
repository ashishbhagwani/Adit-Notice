package com.ashish.adit_portal.helper;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Ashish on 12/3/2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private SQLiteHandler db;
    public void onTokenRefresh(){
        db=new SQLiteHandler(getApplicationContext());
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        try {
            String enrollno = db.getUserDetails().get("Enrollment_number");
            db.addToken(enrollno,refreshedToken);
            TokenSend.SendToken(refreshedToken,enrollno,MyFirebaseInstanceIdService.this);
        } catch (SQLiteHandler.EmptyDatabaseException e) {
           Log.e("Empty Cursor",e.toString());
        }

    }
}
