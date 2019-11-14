package com.dbvertex.company.h1bq.Fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.ProductDetailActivity;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final int BADGE_ICON_SMALL = 1;

    int MESSAGE_NOTIFICATION_ID = 0;
    Intent intent;
    String message, pushType, postid;
    private SharedPreferences sp;
    SessionManager sessionManager;
    String countS = "0";
    int count = 0, i = 0;
    NotificationCompat.Builder mBuilder;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "getData:push " + remoteMessage.getData());
        pushType = remoteMessage.getData().get("key");
        message = remoteMessage.getData().get("body").replace("\"", "");
        sessionManager = new SessionManager(getApplicationContext());
        if (pushType.equalsIgnoreCase("Like")) {
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));
            postid = remoteMessage.getData().get("post_id");

            try {
                HomeActivity.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }

        } else if (pushType.equalsIgnoreCase("Comment")) {
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));
            postid = remoteMessage.getData().get("post_id");

            try {
                HomeActivity.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }


        } else if (pushType.equalsIgnoreCase("Bookmark")) {
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));
            postid = remoteMessage.getData().get("post_id");

            try {
                HomeActivity.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }

        } else if (pushType.equalsIgnoreCase("Trending")) {


            // {content-available=1, notification_title=Trending Post, badge_count=20, post_id=123, user_id=56,key=Trending, body=this post as trending section Fr , post_type=1}
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));
            postid = remoteMessage.getData().get("post_id");

            try {
                HomeActivity.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }
        } else if (pushType.equalsIgnoreCase("Notification"))
        {
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));
            HomeActivity.getInstance().runThread(Integer.parseInt(countS));

        } else if (pushType.equalsIgnoreCase("Inactive")) {
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));
            HomeActivity.getInstance().runThread(Integer.parseInt(countS));
        } else if (pushType.equalsIgnoreCase("Active")) {
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));
            HomeActivity.getInstance().runThread(Integer.parseInt(countS));

        }


        sp = getSharedPreferences("photo", MODE_PRIVATE);

        if (sessionManager.getnotify().get(SessionManager.KEYNOTIFY).equalsIgnoreCase("true")) {
            sendNotification();
        } else {
            cancelNotification();
        }
// {content-available=1, notification_title=Notification, badge_count=1, key=Notification, body=Your Account Inactivated From H1bq, badge=1}

    }


    @SuppressLint("WrongConstant")
    public void sendNotification() {

        String id = "my_channel_01";
        Context context = getBaseContext();
        if (pushType.equalsIgnoreCase("Comment")) {
            intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("from", "notification");
            intent.putExtra("post_id", postid);
        } else if (pushType.equalsIgnoreCase("Like")) {
            intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("from", "notification");
            intent.putExtra("post_id", postid);
        } else if (pushType.equalsIgnoreCase("Bookmark")) {
            intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("from", "notification");
            intent.putExtra("post_id", postid);
        } else if (pushType.equalsIgnoreCase("Trending")) {
            intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("from", "notification");
            intent.putExtra("post_id", postid);
        }
        else if (pushType.equalsIgnoreCase("Notification")) {

            intent = new Intent(context, HomeActivity.class);
            intent.putExtra("from", "outside");
        } else if (pushType.equalsIgnoreCase("Inactive")) {

            intent = new Intent(context, HomeActivity.class);
            intent.putExtra("from", "outside");
            try {
                new HomeActivity.CheckUserExistence().checkuser();

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (pushType.equalsIgnoreCase("Active")) {

            intent = new Intent(context, HomeActivity.class);
            intent.putExtra("from", "outside");
            try {
                new HomeActivity.CheckUserExistence().checkuser();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        MESSAGE_NOTIFICATION_ID = (int) (System.currentTimeMillis() & 0xfffffff);

        PendingIntent pIntent = PendingIntent.getActivity(context, MESSAGE_NOTIFICATION_ID, intent, MESSAGE_NOTIFICATION_ID);

        mBuilder = new NotificationCompat.Builder(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }


        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("H1BQ")
                .setContentText(message)
                .setContentIntent(pIntent)
                .setBadgeIconType(BADGE_ICON_SMALL)
                .setNumber(Integer.parseInt(countS))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setChannelId(id);


        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
                mChannel.setShowBadge(true);

            }
        }
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }


    public void cancelNotification() {
        Context context = getBaseContext();
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }


    public void clearNotification() {


    }
}