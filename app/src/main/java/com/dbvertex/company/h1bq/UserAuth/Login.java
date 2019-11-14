package com.dbvertex.company.h1bq.UserAuth;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.dbvertex.company.h1bq.Fcm.Config;
import com.dbvertex.company.h1bq.HideKeyboard;
import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.Network.MyApplication;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.UIValidation;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener
{
    ProgressDialog dialog;
    RequestQueue requestQueue;
    EditText emailET, passET;
    Button signInBT;
    TextView forgot_passTV, login_plusTV, sign_upTV;
    LinearLayout googleLoginLL;
    private SignInButton google_login_button;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    String email, password, name, firstname, lastname, img_url, gender;
    private static final String TAG = "GoogleSignIn";
    boolean isConnected;
    SessionManager sessionManager;
    public static final int PERMISSION_REQUEST = 100;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        mayRequestPermissions();
        requestQueue = Volley.newRequestQueue(Login.this);
        dialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);
        googleLoginLL = findViewById(R.id.googleLoginLL);
        sign_upTV = findViewById(R.id.sign_upTV);
        forgot_passTV = findViewById(R.id.forgot_passTV);
        login_plusTV = findViewById(R.id.login_plusTV);
        signInBT = findViewById(R.id.signIn);
        google_login_button = findViewById(R.id.google_login_button);
        sessionManager = new SessionManager(getApplicationContext());

        signInBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideKeyboard(Login.this);
                submit();
            }
        });


        forgot_passTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, ForgotPassword.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        sign_upTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignUp.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                i.putExtra("logintype", Constants.MANAUAL_LOGIN_TYPE);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        googleLoginLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.googleLoginLL) {
                    if (!isConnected) {
                        showSnack(isConnected);
                    } else {
                        google_login_button.performClick();
                        signIn();
                    }

                }
            }


        });

    }

    public void submit() {


        if (!validateEmail()) {
            return;
        }

        if (!validatePass()) {
            return;
        } else

        {
            if (!isConnected) {
                showSnack(isConnected);
            } else {
                login();
            }

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();

        isConnected = ConnectivityReceiver.isConnected();
        //Log.e("onStart",isConnected+"");
        if (!isConnected) {
            showSnack(isConnected);
        }
        super.onStart();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

            // G+
            if (googleApiClient.hasConnectedApi(Plus.API)) {
                Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                if (person != null) {

                } else {
                    //Log.e(TAG, "Error!");
                }
            } else {
                //Log.e(TAG, "Google+ not connected");
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //Log.e(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            firstname = acct.getGivenName();
            lastname = acct.getFamilyName();
            email = acct.getEmail();
            chechUserExistence();


        } else {
        }
    }

    private void chechUserExistence() {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CHECK_SOCIALLOGIN_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {

                            JSONObject obj = new JSONObject(response);

                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");

                            if (status == 0 && message.equalsIgnoreCase("failed"))
                            {
                                String data = obj.getString("Data");
                                if (data.equalsIgnoreCase("Email Not Exists"))
                                {

                                    Intent i = new Intent(Login.this, SignUp.class);
                                    i.putExtra("logintype", Constants.GOOGLE_LOGIN_TYPE);
                                    i.putExtra("fname", firstname);
                                    i.putExtra("lname", lastname);
                                    i.putExtra("email", email);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    overridePendingTransition(0, 0);
                                } else {

                                    openDialog(data);
                                }

                            } else if (status == 200 && message.equalsIgnoreCase("success")) {
                                String User_id = obj.getString("User_id");
                                String badgecount=obj.getString("badge_count");
                                sessionManager.setNotificationCount(Integer.parseInt(badgecount));

                                sessionManager.setLoginSession(User_id, Constants.GOOGLE_LOGIN_TYPE,
                                        email, firstname + " " + lastname,regId);
                                sessionManager.setNotify("true");
                                Intent i = new Intent(Login.this, HomeActivity.class);
                                i.putExtra("from", "outside");

                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                overridePendingTransition(0, 0);
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
                params.put("device_id", displayFirebaseRegId());
                params.put("devicetype", "android"); //1=for android 2=ios
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private boolean validatePass() {
        password = passET.getText().toString();
        String passValidateMSG = UIValidation.passwordValidate(password, true, "login");
        if (!passValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            openValidateDialog(passValidateMSG);
            passET.requestFocus();
            return false;
        } else {
            //  passTIL.setErrorEnabled(false);

        }
        return true;
    }


    private boolean validateEmail() {
        email = emailET.getText().toString();
        String emailValidateMSG = UIValidation.emailValidate(email, true);
        if (!emailValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            // emailTIL.setError(emailValidateMSG);
            openValidateDialog(emailValidateMSG);
            emailET.requestFocus();
            return false;
        } else {
            // emailTIL.setErrorEnabled(false);

        }
        return true;
    }

    private void openValidateDialog(String msg) {
        final Dialog dialog = new Dialog(Login.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msgTV = dialog.findViewById(R.id.msg);
        ImageView image = dialog.findViewById(R.id.image);

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

    private void login()
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("LOGINresponse", response);
                        try {
//

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("success")) {

                                JSONObject jsonObject = obj.getJSONObject("Data");
                                String User_id = jsonObject.getString("userid");
                                String f_name = jsonObject.getString("First Name");
                                String l_name = jsonObject.getString("Last Name");
                                String emailid = jsonObject.getString("email");
                                //String username=jsonObject.getString("username");

                                String badgecount=jsonObject.getString("badge_count");
                                sessionManager.setNotificationCount(Integer.parseInt(badgecount));

                                sessionManager.setLoginSession(User_id, Constants.MANAUAL_LOGIN_TYPE, emailid, f_name + " " + l_name,regId);
                                sessionManager.setNotify("true");
                                Intent i = new Intent(Login.this, HomeActivity.class);
                                i.putExtra("from", "outside");
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                overridePendingTransition(0, 0);
                            } else if (status == 0 && message.equalsIgnoreCase("failed"))
                            {
                                String data = obj.getString("Data");

                                if (data.equalsIgnoreCase("Your OTP is not verified.")) {
                                    //openOtpVerifyDialog(email);
                                    Intent intent = new Intent(Login.this, Otp.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("from", "login");

                                    //Log.e("email",email);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    startActivity(intent);
                                    overridePendingTransition(0, 0);

                                } else {
                                    openDialog(data);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("exception",e.getLocalizedMessage());
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
                params.put("password", password);
                params.put("device_id", displayFirebaseRegId());
                params.put("devicetype", "android"); //1=for android 2=ios
                //Log.e("params", params.toString());
                return params;

            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private String displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        try {
            Log.e("regId", "" + regId);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return regId;
    }


    private void openDialog(String data) {
        final Dialog dialog = new Dialog(Login.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msg = dialog.findViewById(R.id.msg);
        ImageView image = dialog.findViewById(R.id.image);

        msg.setText(data);
        image.setImageResource(R.drawable.red_cross);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
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
        this.isConnected = isConnected;
        showSnack(isConnected);


    }

    private boolean mayRequestPermissions()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) + checkSelfPermission(READ_EXTERNAL_STORAGE) +
                checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.permission_rationale);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage("Please confirm access to files & folders");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                                    WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA,
                            },
                            PERMISSION_REQUEST);
                }
            });
            builder.show();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA},
                    PERMISSION_REQUEST);
        }
        return false;
    }


}
