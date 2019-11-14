package com.dbvertex.company.h1bq.UserAuth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.HideKeyboard;
import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.Network.MyApplication;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.ResetPassword;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Otp extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    EditText Et1,Et2,Et3,Et4,Et5,Et6;
    Button verifyLL;
    TextView resend;
    Intent intent;
    SessionManager sessionManager;
    boolean isConnected;
    LinearLayout back_LL;
    String regId;

    String otp1,otp2,otp3,otp4,otp5,otp6,email,from;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        sessionManager=  new SessionManager(getApplicationContext());
        back_LL=findViewById(R.id.back_LL);
        isConnected = ConnectivityReceiver.isConnected();

        Et1=findViewById(R.id.Et1);
        Et2=findViewById(R.id.Et2);
        Et3=findViewById(R.id.Et3);
        Et4=findViewById(R.id.Et4);
        Et5=findViewById(R.id.Et5);
        Et6=findViewById(R.id.Et6);
        verifyLL=findViewById(R.id.verifyLL);
        resend=findViewById(R.id.resend);
        requestQueue = Volley.newRequestQueue(Otp.this);
        dialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        intent=getIntent();
        email=intent.getStringExtra("email");
        from=intent.getStringExtra("from");

        if (from.equalsIgnoreCase("signup"))
        {
            regId=intent.getStringExtra("deviceid");
        }

        //Log.e("from",from);

        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Otp.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        Et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Et1.getText().toString().length() == 1) {
                    Et2.requestFocus();
                }

                if (Et1.getText().toString().length() > 1) {
                    String str = Et1.getText().toString();
                    //  //Log.e("OPT1>>", str);
                    // //Log.e("OPT1>>", str.substring(1, 2));
                    Et1.setText(str.substring(1, 2));
                }
            }
        });

        Et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Et2.getText().toString().length() == 0) {
                    Et1.requestFocus();
                }

                if (Et2.getText().toString().length() == 1) {
                    Et3.requestFocus();
                }

                if (Et2.getText().toString().length() > 1) {
                    String str = Et2.getText().toString();
                    //  //Log.e("OPT2>>", str);
                    //  //Log.e("OPT2>>", str.substring(1, 2));
                    Et2.setText(str.substring(1, 2));
                }
            }
        });

        Et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Et3.getText().toString().length() == 0) {
                    Et2.requestFocus();
                }

                if (Et3.getText().toString().length() == 1) {
                    Et4.requestFocus();
                }

                if (Et3.getText().toString().length() > 1) {
                    String str = Et3.getText().toString();
                    //  //Log.e("OPT3>>", str);
                    // //Log.e("OPT3>>", str.substring(1, 2));
                    Et3.setText(str.substring(1, 2));
                }
            }
        });

        Et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Et4.getText().toString().length() == 0) {
                    Et3.requestFocus();
                }

                if (Et4.getText().toString().length() == 1) {
                    Et5.requestFocus();
                }

                if (Et4.getText().toString().length() > 1) {
                    String str = Et4.getText().toString();
                    //  //Log.e("OPT3>>", str);
                    // //Log.e("OPT3>>", str.substring(1, 2));
                    Et4.setText(str.substring(1, 2));
                }
            }
        });


        Et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Et5.getText().toString().length() == 0) {
                    Et4.requestFocus();
                }

                if (Et5.getText().toString().length() == 1) {
                    Et6.requestFocus();
                }

                if (Et5.getText().toString().length() > 1) {
                    String str = Et5.getText().toString();
                    //  //Log.e("OPT3>>", str);
                    // //Log.e("OPT3>>", str.substring(1, 2));
                    Et5.setText(str.substring(1, 2));
                }
            }
        });

        Et6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Et6.getText().toString().length() == 0) {
                    Et5.requestFocus();
                }

                if (Et6.getText().toString().length() > 1) {
                    String str = Et6.getText().toString();
                    //  //Log.e("OPT4>>", str);
                    // //Log.e("OPT4>>", str.substring(1, 2));
                    Et6.setText(str.substring(1, 2));
                }
            }
        });


        if (from.equalsIgnoreCase("login"))
        {

            if (!isConnected)
            {
                showSnack(isConnected);
            }
            else
            {
                String msg="OTP sent successfully";

                resendOTP("1",msg,"dilaognotopen");
            }
        }


//        else
//        {
//
//            if (!isConnected) {
//                showSnack(isConnected);
//            }
//        }



        verifyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp1=Et1.getText().toString();
                otp2=Et2.getText().toString();
                otp3=Et3.getText().toString();
                otp4=Et4.getText().toString();
                otp5=Et5.getText().toString();
                otp6=Et6.getText().toString();

                if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty())
                {
                    openValidateDialog("Please enter a valid OTP");
                }
                else
                {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else
                    {
                        HideKeyboard.hideKeyboard(Otp.this);

                        if (from.equalsIgnoreCase("login") || from.equalsIgnoreCase("signup"))
                        {
                            verifyOTP(otp1+otp2+otp3+otp4+otp5+otp6);

                        }
                        else
                        {
                            verifyPasswordOTP(otp1+otp2+otp3+otp4+otp5+otp6);

                        }

                    }
                }

            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (from.equalsIgnoreCase("login"))
                {

                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else
                    {
                        String msg="OTP has been sent successfully";

                        resendOTP("1",msg,"dilaogopen");
                    }
                }


                  if (from.equalsIgnoreCase("signup"))
                {

                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else
                    {
                        String msg="OTP has been sent successfully";

                        resendOTP("1",msg, "dilaogopen");
                    }
                }


                if (from.equalsIgnoreCase("forgotpass"))
                {

                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else
                    {
                        String msg="OTP has been sent successfully";
                        resendOTP("2",msg, "dilaogopen");
                    }
                }




            }
        });


    }

    private void verifyPasswordOTP(final String s)
    {

        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.VERIFY_OTP_RESET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("VERIFY_OTP_RESET", response);
                        try {
                            //{"Status":200,"Message":"Success","Data":{"userid":"2","profile":"","first_name":"taru","last_name":"khan","email":"tarukhan19@gmail.com"}}

//                            {"Status":"0","Massege":"Failure","Data":"Invalid Otp"}

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                JSONObject data=obj.getJSONObject("Data");

                                String f_name=data.getString("first_name");
                                String l_name=data.getString("last_name");
                                String emailid=data.getString("email");


                                sessionManager.setLoginSession(data.getString("userid"), Constants.MANAUAL_LOGIN_TYPE,emailid,f_name+" "+l_name,regId);
                                    sessionManager.setNotify("true");
//                                if (from.equalsIgnoreCase("login") || from.equalsIgnoreCase("signup"))
//                                {
//                                    openDialog();
//                                }
//                                else
//                                {
                                    Intent intent=new Intent(Otp.this,ResetPassword.class);
                                    startActivity(intent);
                             //   }




                            }
                            else if (status==0 && message.equalsIgnoreCase("Fuiler"))
                            {
                                String data=obj.getString("Data");
                                if (data.equalsIgnoreCase("Invalid Otp"))
                                {
                                    openValidateDialog("Invalid OTP");

                                    // Toast.makeText(Otp.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password_otp", s);
                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    public void resendOTP(final String otptype, final String msg, final String dilaogopen)
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.OTP_RESEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                //Log.e("dilaogopen",dilaogopen);
                                if (dilaogopen.equalsIgnoreCase("dilaogopen"))
                                {
                                    openDial(msg);
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("otp_reset_type", otptype);

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }



    private void openDial(String msg)
    {
        final Dialog dialog = new Dialog(Otp.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_mesage);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok=dialog.findViewById(R.id.ok);
        TextView msgTV=dialog.findViewById(R.id.msg);
        msgTV.setText(msg);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void verifyOTP(final String s)
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.OTP_VERIFY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
 //{"Status":200,"Message":"Success","Data":{"userid":"2","profile":"","first_name":"taru","last_name":"khan","email":"tarukhan19@gmail.com"}}

//                            {"Status":"0","Massege":"Failure","Data":"Invalid Otp"}

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                JSONObject data=obj.getJSONObject("Data");
                                String f_name=data.getString("first_name");
                                String l_name=data.getString("last_name");
                                String emailid=data.getString("email");

                                sessionManager.setLoginSession(data.getString("userid"), Constants.MANAUAL_LOGIN_TYPE,emailid,f_name+" "+l_name,regId);
                                sessionManager.setNotify("true");

//                                if (from.equalsIgnoreCase("login") || from.equalsIgnoreCase("signup"))
//                                {
//
//                                }
//                                else
//                                {
//                                    Intent intent=new Intent(Otp.this,ResetPassword.class);
//                                    startActivity(intent);
//                                }
                                openDialog();



                            }
                            else if (status==0 && message.equalsIgnoreCase("Fuiler"))
                            {
                                String data=obj.getString("Data");
                                if (data.equalsIgnoreCase("Invalid Otp"))
                                {
                                    openValidateDialog(data);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("otp", s);
                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private void openDialog()
    {
        final Dialog dialog = new Dialog(Otp.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok=dialog.findViewById(R.id.ok);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Otp.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from","outside");

                startActivity(intent);
                overridePendingTransition(0,0);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        }
    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Internet Connected";
            color = Color.WHITE;

        } else {
            message = "No Internet connection available.";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Otp.this,Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }


    private void openValidateDialog(String msg)
    {
        final Dialog dialog = new Dialog(Otp.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok=dialog.findViewById(R.id.ok);
        TextView msgTV=dialog.findViewById(R.id.msg);
        ImageView image=dialog.findViewById(R.id.image);

        msgTV.setText(msg);
        image.setImageResource(R.drawable.red_cross);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                dialog.cancel();

            }
        });
    }
}
