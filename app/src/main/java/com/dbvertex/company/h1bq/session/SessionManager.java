package com.dbvertex.company.h1bq.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;
    // Shared pref file name
    private static final String PREF_NAME = "PhotopeolpePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_WORKING_HOURS_ADDED = "is_working_hours_added";
    // Notification Count
    public static final String NOTIFICATION_COUNT = "notification_count";

    public static final String KEY_VERIFY_EMAIL = "verify_email";
    public static final String KEY_FORGET_EMAIL = "forget_email";
    //login
    public static final String KEY_EMAIL = "emp_email";
    public static final String KEY_USER_STATUS = "user_status";
    public static final String KEY_FREE_JOBSTATUS = "jobstatus";

    public static final String KEY_NAME = "first_name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_DEVICEID= "mobile";
    public static final String KEY_DESIGNATION = "designation";
    public static final String KEY_IMAGE = "profile_image";
    public static final String KEY_COVERPIC = "cover_image";
    public static final String KEY_PROFESSION = "profession";
    public static final String KEY_USERID= "user_id";
    public static final String KEYNOTIFY="notify" ;
    public static final String KEY_LOGINTYPE= "logintype";


    public static final String KEY_STARTDATE= "startdate";
    public static final String KEY_ENDDATE= "enddate";
    public static final String KEY_FREEE_TYPE= "freelancertype";
    public static final String KEY_LATITUDE = "ip_latitude";
    public static final String KEY_LONGITUDE = "ip_longitude";
    public static final String KEY_LOCATION = "location";

    public static final String isnotificationarrive ="isnotificationarrive";


    // Constructor
    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public HashMap<String, String> getNotification() {
        HashMap<String, String> user = new HashMap<>();
        user.put(isnotificationarrive, pref.getString(isnotificationarrive, ""));
        return user;    }

    public void setNotification(String isnotificationarrive) {
        editor.putString(isnotificationarrive, isnotificationarrive);
        editor.commit();
    }

    public void setLoginSession(String id,String logintype,String email,String name,String deviceid)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERID, id);
        editor.putString(KEY_LOGINTYPE, logintype);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_DEVICEID,deviceid);
        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getLoginSession() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
        user.put(KEY_LOGINTYPE, pref.getString(KEY_LOGINTYPE, ""));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));
        user.put(KEY_DEVICEID,pref.getString(KEY_DEVICEID,""));

        return user;
    }


//    public void setLoginSession(String email, String id, String name, String user_type, String mobile,  String image) {
//        editor.putBoolean(IS_LOGIN, true);
//        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_USERID, id);
//        editor.putString(KEY_NAME, name);
//        editor.putString(KEY_USER_TYPE, user_type);
//        editor.putString(KEY_MOBILE, mobile);
//        editor.putString(KEY_IMAGE, image);
//
//        // commit changes
//        editor.commit();
//    }
//
//    // Get stored session data
//    public HashMap<String, String> getLoginSession() {
//        HashMap<String, String> user = new HashMap<>();
//        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
//        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
//        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));
//        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, ""));
//        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, ""));
//        user.put(KEY_DESIGNATION, pref.getString(KEY_DESIGNATION, ""));
//        user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, ""));
//        return user;
//    }

    // Clear session details
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }


    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public void setFirstTimeLaunch(boolean b) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, b);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean checkWorkingHours() {
        return pref.getBoolean(IS_WORKING_HOURS_ADDED, false);
    }



    public int getNotificationCount() {
        return pref.getInt(NOTIFICATION_COUNT, 0);
    }

    public void setNotificationCount(int count) {
        editor.putInt(NOTIFICATION_COUNT, count);
        editor.commit();
    }

    public void setNotify(String ischeck)
    {
        editor.putString(KEYNOTIFY,ischeck);
        editor.commit();
    }

    public HashMap<String,String> getnotify()
    {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEYNOTIFY, pref.getString(KEYNOTIFY, ""));
        return user;
    }

    public void setUserStatus(String status)
    {
        editor.putString(KEY_USER_STATUS,status);
        editor.commit();
    }

    public HashMap<String,String> getUserStatus()
    {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_USER_STATUS, pref.getString(KEY_USER_STATUS, ""));
        return user;
    }


    public void setImageSession(String picurl)
    {
        editor.putString(KEY_IMAGE, picurl);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getImageSession() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, ""));
        return user;
    }




    public void setCoverPicSession(String picurl1)
    {
        editor.putString(KEY_COVERPIC, picurl1);
        editor.commit();
    }

    public HashMap<String, String> getCoverPicSession() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_COVERPIC, pref.getString(KEY_COVERPIC, ""));
        return user;
    }

    public void setuserid(String userId)
    {
        editor.putString(KEY_USERID, userId);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getuserId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
        return user;
    }








}