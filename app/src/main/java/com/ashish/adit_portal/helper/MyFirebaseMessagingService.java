package com.ashish.adit_portal.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ashish.adit_portal.R;
import com.ashish.adit_portal.activity.Navigation_Main;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Ashish on 12/3/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        int count = 0;
        SessionManager session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            Log.e("working", remoteMessage.getData().get("Notification_type"));
            if (remoteMessage.getData().get("Notification_type").equals("Notice")) {
                String Title = remoteMessage.getData().get("Notice");
                Log.e("titleofimage", Title);
                String img_path = remoteMessage.getData().get("Image Location");
                Log.e("imagepath", img_path);
                db.addNotice(Title, img_path);
                Intent i = new Intent("NOTICE_INSERTED");
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
                lbm.sendBroadcast(i);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_notice)
                        .setContentTitle("New Notice")
                        .setContentText(Title);
                Intent resultIntent = new Intent(this, Navigation_Main.class);
                //Intent extraResultIntent=new Intent(this, LoginActivity.class);
                // Because clicking the notification opens a new ("special") activity, there's
                // no need to create an artificial back stack.
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                Notification note = mBuilder.build();
                note.flags |= Notification.FLAG_AUTO_CANCEL;
                note.defaults |= Notification.DEFAULT_VIBRATE;
                note.defaults |= Notification.DEFAULT_SOUND;

                // Sets an ID for the notification
                int mNotificationId = 1;
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, note);
            } else if(remoteMessage.getData().get("Notification_type").equals("Logout")){
                session.setLogin(false);
                db.deleteUsers();
            }else if(remoteMessage.getData().get("Notification_type").equals("form")) {
                String server = remoteMessage.getData().get("server_id");
                String Type = remoteMessage.getData().get("type");
                String Code = remoteMessage.getData().get("subject_code");
                String Faculty = remoteMessage.getData().get("faculty");
                String Subject = remoteMessage.getData().get("subject");
                Log.e("messaging service", server + Type + Code + Faculty + Subject);
                db.addForm(server, Code, Subject, Faculty, Type);
                Intent i = new Intent("FORM_INSERTED");
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
                lbm.sendBroadcast(i);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_other_notice)
                        .setContentTitle("New Feedback Form")
                        .setContentText(Subject);
                Intent resultIntent = new Intent(this, Navigation_Main.class);
                resultIntent.putExtra("fragment", "feedback");
                // Because clicking the notification opens a new ("special") activity, there's
                // no need to create an artificial back stack.
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                Notification note = mBuilder.build();
                note.flags |= Notification.FLAG_AUTO_CANCEL;
                note.defaults |= Notification.DEFAULT_VIBRATE;
                note.defaults |= Notification.DEFAULT_SOUND;

                // Sets an ID for the notification
                int mNotificationId = 2;
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, note);
            }
            else if(remoteMessage.getData().get("Notification_type").equals("livemsg")){
                String title="Announcement By "+remoteMessage.getData().get("title");
                String content=remoteMessage.getData().get("content");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_other_notice)
                        .setContentTitle(title)
                        .setContentText(content);
                Intent resultIntent = new Intent(this, Navigation_Main.class);
                resultIntent.putExtra("fragment", "livemsg");
                resultIntent.putExtra("content",content);
                resultIntent.putExtra("title",title);
                // Because clicking the notification opens a new ("special") activity, there's
                // no need to create an artificial back stack.
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                Notification note = mBuilder.build();
                note.flags |= Notification.FLAG_AUTO_CANCEL;
                note.defaults |= Notification.DEFAULT_VIBRATE;
                note.defaults |= Notification.DEFAULT_SOUND;

                // Sets an ID for the notification
                int mNotificationId = 2;
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, note);
            }
        }
    }
}
