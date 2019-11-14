package com.dbvertex.company.h1bq.UserAuth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
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
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.UIValidation;
import com.dbvertex.company.h1bq.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ImageView edit_img;
    TextView titleTV;
    LinearLayout back_LL;
    Toolbar toolbar_main;
    EditText old_passET,new_passET,c_passET;
    String oldpassword,confpassword,password;
    Button update;
    boolean isConnected;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    LinearLayout confPassLL,newpassLL,oldpassLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        requestQueue = Volley.newRequestQueue(ChangePassword.this);
        dialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        toolbar_main = findViewById(R.id.toolbar_main);
        sessionManager=  new SessionManager(getApplicationContext());

        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
//        newconfTIL = findViewById(R.id.newconfTIL);
//        newpassTIL = findViewById(R.id.newpassTIL);
//        oldpassTIL = findViewById(R.id.oldpassTIL);
        old_passET= findViewById(R.id.old_passET);
        new_passET= findViewById(R.id.new_passET);
        c_passET= findViewById(R.id.c_passET);
        confPassLL= findViewById(R.id.confPassLL);
        newpassLL= findViewById(R.id.newpassLL);
        oldpassLL= findViewById(R.id.oldpassLL);
        titleTV.setText("Change Password");
        edit_img.setVisibility(View.GONE);
        update= findViewById(R.id.update);


        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
//        new_passET.addTextChangedListener(new TextWatc(new_passET));
//        c_passET.addTextChangedListener(new TextWatc(c_passET));
//        old_passET.addTextChangedListener(new TextWatc(old_passET));

        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("0"))
        {
            old_passET.setKeyListener(null);
            new_passET.setKeyListener(null);
            c_passET.setKeyListener(null);
            oldpassLL.setBackgroundResource(R.drawable.gray_back);
            newpassLL.setBackgroundResource(R.drawable.gray_back);
            confPassLL.setBackgroundResource(R.drawable.gray_back);

        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1")) {
                    HideKeyboard.hideKeyboard(ChangePassword.this);

                    submit();
                }
            }
        });


        back_LL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChangePassword.this, HomeActivity.class);
                intent.putExtra("from","inside");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0,0);

            }
        });

    }

    public void submit()
    {

        if (!validateoldPass()) {
            return;
        }
            if (!validatePass()) {
                return;
            }

            if (!validateConPass()) {
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
                changePass();
            }

        }
    }

    private void changePass()
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CHANGE_PASS,
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
                                String data=obj.getString("Data");
                                if(data.equalsIgnoreCase("Password Change Successfully"))
                                {
                                opanDialog();
                                }

                            }
                            else if (status == 0 && message.equalsIgnoreCase("failed"))
                            {
                                String data=obj.getString("Data");
                                if (data.equalsIgnoreCase("Old password does not match"))
                                {
                                    openValidateDialog("Old password does not match");
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
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("old_password", oldpassword);
                params.put("new_password", password);
                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private void openValidateDialog(String msg)
    {
        final Dialog dialog = new Dialog(ChangePassword.this, R.style.CustomDialog);
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

    private void opanDialog()
    {


            final Dialog dialog = new Dialog(ChangePassword.this, R.style.CustomDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_passchange);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
            LinearLayout ok=dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    sessionManager.logoutUser();
                    Intent intent=new Intent(ChangePassword.this,Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0,0);

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
//                case R.id.new_passET:
//                    password=new_passET.getText().toString();
//                    validatePass();
//                    break;
//                case R.id.old_passET:
//                    validateoldPass();
//                    break;
//                case R.id.c_passET:
//                    validateConPass();
//                    break;
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

    private boolean validateoldPass() {
        oldpassword=old_passET.getText().toString();
        String passValidateMSG = UIValidation.passwordValidate(oldpassword, true,"changeoldpass");
        if (!passValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(passValidateMSG);

            //oldpassTIL.setError(passValidateMSG);
            old_passET.requestFocus();
            return false;
        }
        else
        {
           // oldpassTIL.setErrorEnabled(false);

        }
        return true;
    }

    private boolean validateConPass()
    {
        confpassword=c_passET.getText().toString();
        password=new_passET.getText().toString();
        String conpassValidateMSG = UIValidation.confirmPasswordValidate(confpassword,password, true,"New Password does not match");
        if (!conpassValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(conpassValidateMSG);

            //   newconfTIL.setError(conpassValidateMSG);
            c_passET.requestFocus();
            return false;
        }
        else
        {
          //  newconfTIL.setErrorEnabled(false);
        }
        return  true;

    }

    private boolean validatePass()
    {
        password=new_passET.getText().toString();
        String passValidateMSG = UIValidation.passwordValidate(password, true,"changenewpass");
        if (!passValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(passValidateMSG);

            // newpassTIL.setError(passValidateMSG);
            new_passET.requestFocus();
            return false;
        }
        else
        {
           // newpassTIL.setErrorEnabled(false);

        }
        return true;
    }
    @Override
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        //Log.e("onStart",isConnected+"");
        if (!isConnected)
        {
            showSnack(isConnected);
        }

        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(final boolean isConnected) {
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
        Intent intent=new Intent(ChangePassword.this,HomeActivity.class);
        intent.putExtra("from","inside");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
