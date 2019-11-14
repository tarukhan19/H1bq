package com.dbvertex.company.h1bq.Util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Endpoints {

   // https://www.h1bq.com/webservice/Post/bookmark_list
    private static final String TAG = Endpoints.class.getSimpleName();
    public static final String TERMANDCONDITION = "https://www.h1bq.com/welcome/terms_condition";
    public static final String PRIVACYPOLICY = "https://www.h1bq.com/welcome/privacy_policy";
    public static final String BASE_URL ="https://www.h1bq.com/webservice/";
    public static final String USERNAME_SUGGETION =BASE_URL+"User/get_username";
    public static final String USERNAME_CHECK =BASE_URL+"User/check_username";
    public static final String SIGNUP =BASE_URL+"User/sigup_api";
    public static final String LOGIN =BASE_URL+"User/login_api";
    public static final String OTP_VERIFY =BASE_URL+"User/verify_opt";
    public static final String OTP_RESEND =BASE_URL+"User/opt_reset";
    public static final String CHANGE_PASS =BASE_URL+"User/password_change";
    public static final String RESET_PASS =BASE_URL+"User/forget_password_change";
    public static final String VERIFY_OTP_RESET =BASE_URL+"User/password_opt_verify";
    public static final String CHECK_SOCIALLOGIN_USER =BASE_URL+"User/Check_social_email";
    public static final String LOAD_MYPROFILE =BASE_URL+"User/my_profile";
    public static final String CONTACT_US =BASE_URL+"Countact_us/add_contact";
    public static final String UPDATE_MYPROFILE =BASE_URL+"User/update_profile";
    public static final String CREATE_POSTPOLL =BASE_URL+"Post/add_post";
    public static final String POSTPOLL_LIST =BASE_URL+"Post/post_list";
    public static final String POSTPOLL_LIKE =BASE_URL+"Post/like_post";
    public static final String POSTPOLL_BOOKMARK =BASE_URL+"Post/Add_bookmark";
    public static final String FAV_LIST =BASE_URL+"Post/bookmark_list";
    public static final String DELETE_POST =BASE_URL+"Post/delete_post";
    public static final String UPDATE_POST =BASE_URL+"Post/edit_post";
    public static final String POST_DETAIL =BASE_URL+"Post/poll_detail";
    public static final String REPORT_ABUSE =BASE_URL+"Post/report_abous";
    public static final String SUBMIT_ANS =BASE_URL+"post/answe_submit";
    public static final String ADD_COMMENT =BASE_URL+"Comment/add_comment";
    public static final String LOAD_COMMENT =BASE_URL+"Comment/load_comment";
    public static final String LIKE_COMMENT =BASE_URL+"Comment/like_comment";
    public static final String SEARCH =BASE_URL+"Post/search_post";
    public static final String SEARCH_LIST =BASE_URL+"Post/search_keyword";
    public static final String LOAD_SINGLE_COMMENT =BASE_URL+"Comment/load_nested_comment";
    public static final String CHECK_USER=BASE_URL+"User/check_user_id";
    public static final String LOAD_NOTIFICATIONS=BASE_URL+"User/notification_list";
    public static final String LOADadmin_NOTIFICATIONS=BASE_URL+"User/admin_notification_list";


    public  static final String CLEAR_NOTIFICATIONS=BASE_URL+"User/notification_clear";
    public  static final String LOGOUT=BASE_URL+"User/logout";


    public  static final String Load_Ads=BASE_URL+"App/advertisement_list";

    public String forComment(String signUp, JSONObject params, byte profilepic[]) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            URL url = new URL(signUp);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"profilepic.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }

            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;
    }



    public String forSignUp(String signUp, JSONObject params, byte profilepic[]) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            URL url = new URL(signUp);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"profile_file\"; filename=\"profilepic.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }

            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;
    }

    private String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }



    public String forPost(String urlString, JSONObject params, ArrayList<String> ansList,byte profilepic[] ) throws Exception
    {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        ////Log.e("videolist1",videoList+"");
        ////Log.e("photoArray1",photoArray+"");

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            System.setProperty("http.keepAlive", "false");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }

            for (String arr : ansList)
            {
                //Log.e("ansList",ansList+"");

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"anser[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }



            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("REg Error ", e.getMessage());
        }
        return result;
    }


    public String forUpdatePost(String urlString, JSONObject params, byte profilepic[] ) throws Exception
    {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        ////Log.e("videolist1",videoList+"");
        ////Log.e("photoArray1",photoArray+"");

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            System.setProperty("http.keepAlive", "false");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }




            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            ////Log.e("REg Error ", e.getMessage());
        }
        return result;
    }

    public String forAnsPost(String submitAns, JSONObject ob, ArrayList<String> ansIdlist)
    {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        try {
            URL url = new URL(submitAns);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            for (String arr : ansIdlist)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"anser_id[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }


            // Upload POST Data
            Iterator<String> keys = ob.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = ob.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            ////Log.e("REg Error ", e.getMessage());
        }
        return result;

    }
}
