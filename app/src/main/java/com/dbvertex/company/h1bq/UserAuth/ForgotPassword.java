package com.dbvertex.company.h1bq.UserAuth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.Network.MyApplication;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.UIValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    boolean isConnected;
    EditText emailET;
   // private TextInputLayout emailTIL;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    String email;
    LinearLayout back_LL;
    Button submitBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        back_LL=findViewById(R.id.back_LL);
        emailET = findViewById(R.id.emailET);
        submitBT=findViewById(R.id.submitBT);
        requestQueue = Volley.newRequestQueue(ForgotPassword.this);
        dialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
      //  emailTIL=(TextInputLayout)findViewById(R.id.emailTIL);
//        emailET.addTextChangedListener(new TextWatc(emailET));


        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgotPassword.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        submitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideKeyboard(ForgotPassword.this);
                submit();
            }
        });



    }
    public void submit()
    {


        if (!validateEmail())
        {
            return;
        }

        else

        {
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else
                {
                    String msg="OTP sent successfully";
                    resendOTP("2",msg);
                }

        }
    }
    public void resendOTP(final String otptype, final String msg)
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
                                Intent intent=new Intent(ForgotPassword.this,Otp.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("from","forgotpass");
                                intent.putExtra("email",email);
                                startActivity(intent);
                                overridePendingTransition(0,0);
                                dialog.dismiss();

                            }
                            else if (status==0 && message.equalsIgnoreCase("Success"))
                            {

                                String data=obj.getString("Data");
                                openValidateDialog(data);

                            }
                            else if (status==0 && message.equalsIgnoreCase("failed"))
                            {
                                String data=obj.getString("Data");
                                openValidateDialog(data);
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
    private void openDialog(String msg)
    {
        final Dialog dialog = new Dialog(ForgotPassword.this, R.style.CustomDialog);
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
                Intent intent=new Intent(ForgotPassword.this,Otp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from","forgotpass");
                intent.putExtra("email",email);
                startActivity(intent);
                overridePendingTransition(0,0);
                dialog.dismiss();
            }
        });

    }

//    private class TextWatc implements TextWatcher
//    {
//        private View view;
//        public TextWatc( View view) {
//            this.view = view;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            switch (view.getId())
//            {
//
//                case R.id.emailET:
//                    validateEmail();
//                    break;
//
//
//            }
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//
//
//        }
//    }

    private boolean validateEmail()
    {
        email=emailET.getText().toString();
        String emailValidateMSG = UIValidation.emailValidate(email, true);
        if (!emailValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
           // emailTIL.setError(emailValidateMSG);
            openValidateDialog(emailValidateMSG);
            emailET.requestFocus();
            return false;
        }
        else
        {
           // emailTIL.setErrorEnabled(false);

        }
        return true;
    }

    private void openValidateDialog(String msg)
    {
        final Dialog dialog = new Dialog(ForgotPassword.this, R.style.CustomDialog);
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


    @Override
    public void onStart() {
        super.onStart();
        isConnected = ConnectivityReceiver.isConnected();

            if (!isConnected) {
                showSnack(isConnected);
            }

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
        Intent intent=new Intent(ForgotPassword.this,Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
