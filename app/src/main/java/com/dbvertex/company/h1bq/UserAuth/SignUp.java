package com.dbvertex.company.h1bq.UserAuth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.dbvertex.company.h1bq.PrivacyPolicy;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.TermsConditon;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.UIValidation;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity   implements ConnectivityReceiver.ConnectivityReceiverListener{

    Button signUp;
    TextView signinTV;
    TextView countryTV,tc_TV,suggestedUsername,privacy_TV,cookie_TV;
    LinearLayout back_LL;
    String regId;
    LinearLayout countryLL,maleLL,femaleLL,passwordLL,confpasswordLL,signinLL,confemailLL;
    private boolean agreeStatus = false;
    SessionManager mSessionManager;
    int status=1;
    Intent intent;
    ImageView maleactive,maleunactive,femaleactive,femaleunactive,check;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    ImageView tc_img;

    ArrayAdapter<String> countryarrayadapter;
    ArrayList<String> countryNameArray,countryIdArray,suggestedusername;
    String country_name="", country_code,firstName,lastName,username,email,zipcode,password="",confpassword=""
            ,message="",signuptype,gender,confemail;
    EditText first_nameET,last_nameET,usernameET,emailET,zipcodeET,passET,conf_passET,confemailET;
    boolean isValidate;
    boolean isConnected;
    ListView countryLV;
    AlertDialog.Builder countrybuilder;
    AlertDialog countrydialog;
    ListView ccountryLV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        isConnected = ConnectivityReceiver.isConnected();
        passwordLL= findViewById(R.id.passwordLL);
        confpasswordLL= findViewById(R.id.confpasswordLL);
        signUp = findViewById(R.id.signUp);
        signinTV = findViewById(R.id.signinTV);
        countryTV=findViewById(R.id.countryTV);
        countryLL=findViewById(R.id.countryLL);
        maleLL=findViewById(R.id.maleLL);
        signinLL=findViewById(R.id.signinLL);
        femaleLL =findViewById(R.id.femaleLL);
        check=findViewById(R.id.check);

        displayFirebaseRegId();
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        confemailLL=findViewById(R.id.confemailLL);
        mSessionManager = new SessionManager(getApplicationContext());
        tc_TV=findViewById(R.id.tc_TV);
        tc_img=findViewById(R.id.tc_img);
        maleactive=findViewById(R.id.maleactive);
        maleunactive=findViewById(R.id.maleunactive);
        femaleactive=findViewById(R.id.femaleactive);
        femaleunactive=findViewById(R.id.femaleunactive);
        intent=getIntent();
        back_LL=findViewById(R.id.back_LL);
        privacy_TV=findViewById(R.id.privacy_TV);
        cookie_TV=findViewById(R.id.cookie_TV);
        first_nameET=findViewById(R.id.first_nameET);
        last_nameET=findViewById(R.id.last_nameET);
        usernameET=findViewById(R.id.usernameET);
        emailET=findViewById(R.id.emailET);
        zipcodeET=findViewById(R.id.zipcodeET);
        passET=findViewById(R.id.passET);
        conf_passET=findViewById(R.id.Conf_passET);
        confemailET=findViewById(R.id.confemailET);

        suggestedUsername=findViewById(R.id.suggestedUsername);
        suggestedusername=new ArrayList<>();
        requestQueue = Volley.newRequestQueue(SignUp.this);
        dialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        signuptype=intent.getStringExtra("logintype");

        countrybuilder = new AlertDialog.Builder(SignUp.this);
        countryLV = new ListView(this);
        countryNameArray=new ArrayList<>();
        countryIdArray=new ArrayList<>();
        countryarrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_countryspinner,R.id.text, countryNameArray);

        countryLV.setAdapter(countryarrayadapter);

        countrybuilder.setView(ccountryLV);
        countrydialog = countrybuilder.create();
        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });


        if (signuptype.equalsIgnoreCase(Constants.GOOGLE_LOGIN_TYPE))
        {
            first_nameET.setText(intent.getStringExtra("fname"));
            last_nameET.setText(intent.getStringExtra("lname"));
            emailET.setText(intent.getStringExtra("email"));
            first_nameET.setEnabled(false);
            last_nameET.setEnabled(false);
            emailET.setEnabled(false);
            passwordLL.setVisibility(View.GONE);
            //passTIL.setVisibility(View.GONE);
            signinLL.setVisibility(View.INVISIBLE);
            confpasswordLL.setVisibility(View.GONE);
            confemailLL.setVisibility(View.GONE);

        }


        maleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (maleactive.getVisibility()==View.VISIBLE)
                {
                    maleactive.setVisibility(View.GONE);
                    maleunactive.setVisibility(View.VISIBLE);
                    femaleactive.setVisibility(View.VISIBLE);
                    femaleunactive.setVisibility(View.GONE);
                }

                else if (maleunactive.getVisibility()==View.VISIBLE)
                {
                    maleactive.setVisibility(View.VISIBLE);
                    maleunactive.setVisibility(View.GONE);
                    femaleactive.setVisibility(View.GONE);
                    femaleunactive.setVisibility(View.VISIBLE);
                }



            }
        });

        femaleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (femaleactive.getVisibility()==View.VISIBLE)
                {
                    femaleactive.setVisibility(View.GONE);
                    femaleunactive.setVisibility(View.VISIBLE);
                    maleactive.setVisibility(View.VISIBLE);
                    maleunactive.setVisibility(View.GONE);
                }
                else  if (femaleunactive.getVisibility()==View.VISIBLE)
                {
                    femaleactive.setVisibility(View.VISIBLE);
                    femaleunactive.setVisibility(View.GONE);
                    maleactive.setVisibility(View.GONE);
                    maleunactive.setVisibility(View.VISIBLE);
                }
            }
        });




        tc_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (agreeStatus) {
                    tc_img.setImageResource(R.drawable.tandc_empty);
                  //  tc_TV.setTextColor(Color.BLACK);
                    agreeStatus = false;
                } else {
                    tc_img.setImageResource(R.drawable.termandcondtion);
                //    tc_TV.setTextColor(Color.GRAY);
                    agreeStatus = true;
                }


            }
        });

        tc_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String urlString = Endpoints.TERMANDCONDITION;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed and open Kindle Browser
//            intent.setPackage("com.amazon.cloud9");
//            startActivity(intent);
                }

            }
        });


        privacy_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = Endpoints.PRIVACYPOLICY;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed and open Kindle Browser
//            intent.setPackage("com.amazon.cloud9");
//            startActivity(intent);
                }

            }
        });

        cookie_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String urlString = Endpoints.TERMANDCONDITION;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed and open Kindle Browser
//            intent.setPackage("com.amazon.cloud9");
//            startActivity(intent);
                }
            }
        });

        countryLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                countryTV.setText(txt.getText().toString());
                country_name = txt.getText().toString();
                country_code = countryIdArray.get(position).toString();
                //Toast.makeText(AddProductActivity.this, prodTypestring, Toast.LENGTH_LONG).show();
                countrydialog.dismiss();

            }
        });

        countryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard.hideKeyboard(SignUp.this);
                countrydialog.setView(countryLV);
                countrydialog.show();
            }
        });

        signinTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this,Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                overridePendingTransition(0,0);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideKeyboard(SignUp.this);
                submit();
            }
        });

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("country");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String name = jo_inside.getString("name");
                String code = jo_inside.getString("code");
                countryNameArray.add(name);
                countryIdArray.add(code);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        first_nameET.addTextChangedListener(new TextWatc(first_nameET));
//        last_nameET.addTextChangedListener(new TextWatc(last_nameET));
           usernameET.addTextChangedListener(new TextWatc(usernameET));
//        emailET.addTextChangedListener(new TextWatc(emailET));
//        zipcodeET.addTextChangedListener(new TextWatc(zipcodeET));
//        passET.addTextChangedListener(new TextWatc(passET));
//        conf_passET.addTextChangedListener(new TextWatc(conf_passET));
//        confemailET.addTextChangedListener(new TextWatc(confemailET));
//        countryTV.addTextChangedListener(new TextWatc(countryTV));

        zipcodeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (country_name.equalsIgnoreCase("Canada") || country_name.equalsIgnoreCase("UK"))
                {
                    zipcodeET.setInputType(InputType.TYPE_CLASS_TEXT);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput((zipcodeET), InputMethodManager.SHOW_IMPLICIT);                }

                else
                {
                    zipcodeET.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput((zipcodeET), InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });



        if (isConnected)
        {
            loadUsername();
        }


    }

    private void checkUsername(final CharSequence s)
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.USERNAME_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("response", response);
                        try {
                            // {"Status":0,"Message":"failed","Data":"Minimum 6 Characters"}
                            //{"Status":200,"Message":"Success","Data":"True"}
                            JSONObject obj = new JSONObject(response);
                             status = obj.getInt("Status");
                             message = obj.getString("Message");

                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                check.setImageResource(R.drawable.termandcondtion);
                            }
                            else if (status ==0 && message.equalsIgnoreCase("failed"))
                            {
                                String data=obj.getString("Data");

                                check.setImageResource(R.drawable.red_cross);
                            }
                            else
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Username",s+"");
                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("Countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void submit()
    {

        if (!validateFname()) {
            return;
        }
        if (!validateLname()) {
            return;
        }
        if (!validateUsername()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }


    if (signuptype.equalsIgnoreCase(Constants.MANAUAL_LOGIN_TYPE))
    {
        if (!validateConfEmail()) {
            return;
        }
        if (!validatePass()) {
            return;
        }

        if (!validateConPass()) {
            return;
        }

    }

        if (!validateCountry())
        {
            return;
        }
        if (!validateZip()) {
            return;
        }
        if (!agreeStatus) {
            openValidateDialog("Please accept Terms & Conditions, Privacy Policy and Cookies Policy.");
            //tc_TV.setTextColor(Color.RED);
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
                if (maleactive.getVisibility()==View.VISIBLE)
                {
                    gender="M";
                }
                else
                {
                    gender="F";
                }
                signupReq(gender);
            }

        }
    }
    private class TextWatc implements TextWatcher
    {
        private View view;
        public TextWatc( View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId())
            {
//                case R.id.first_nameET:
//                    validateFname();
//                    break;
//
//                case R.id.last_nameET:
//                    validateLname();
//                    break;
                case R.id.usernameET:
                   // validateUsername();
                    checkUsername(charSequence);
                    break;
//                case R.id.emailET:
//                    validateEmail();
//                    break;
//                case R.id.passET:
//
//                        validatePass();
//
//                    break;
//                case R.id.Conf_passET:
//
//                        validateConPass();
//
//                    break;
//                case R.id.confemailET:
//                    validateConfEmail();
//                    break;
//
//                case R.id.zipcodeET:
//                    validateZip();
//                    break;
//
//                case R.id.contryTV:
//                    validateCountry();
//                    break;

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    }

    private boolean validateCountry()
    {

        if (country_name.isEmpty())
        {
            //countryTIL.setError("Select Country");
            openValidateDialog("Select Country");
            countryTV.requestFocus();
            return false;
        }
        else
        {
           // countryTIL.setErrorEnabled(false);
        }
        return  true;

    }

    private boolean validateConfEmail()
    {
        email=emailET.getText().toString();
        confemail=confemailET.getText().toString();

        String conemailValidateMSG = UIValidation.confirmEmailValidate(confemail,email, true);


        if (!conemailValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
           /// confemailTIL.setError(conemailValidateMSG);
            openValidateDialog(conemailValidateMSG);

            confemailET.requestFocus();
            return false;
        }
        else
        {
           // confemailTIL.setErrorEnabled(false);
        }
        return  true;
    }

    private boolean validateConPass()
    {
        confpassword=conf_passET.getText().toString();
        password=passET.getText().toString();
        String conpassValidateMSG = UIValidation.confirmPasswordValidate(confpassword,password, true,"Password does not match");
        if (!conpassValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(conpassValidateMSG);

            // confTIL.setError(conpassValidateMSG);
            conf_passET.requestFocus();
            return false;
        }
        else
        {
           // confTIL.setErrorEnabled(false);
        }
        return  true;

    }

    private boolean validatePass()
    {
        password=passET.getText().toString();
        String passValidateMSG = UIValidation.passwordValidate(password, true,"Signup");
        if (!passValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(passValidateMSG);
            passET.requestFocus();
            return false;
        }
        else
        {
        }
        return true;
    }


    private boolean validateEmail()
    {
        email=emailET.getText().toString();
        String emailValidateMSG = UIValidation.emailValidate(email, true);
        if (!emailValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(emailValidateMSG);

            //emailTIL.setError(emailValidateMSG);
            emailET.requestFocus();
            return false;
        }
        else
        {
           // emailTIL.setErrorEnabled(false);

        }
return true;
    }


    private boolean validateFname()
    {
        firstName=first_nameET.getText().toString();
        String fnameValidateMSG = UIValidation.nameValidate(firstName, true,"First Name is required");
        if (!fnameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(fnameValidateMSG);

            // firtnameTIL.setError(fnameValidateMSG);
            first_nameET.requestFocus();
            return false;
        }
        else
        {
           // firtnameTIL.setErrorEnabled(false);
            isValidate=true;
        }
        return true;
    }

    private boolean validateUsername()
    {
       username=usernameET.getText().toString();
       String usernameValidateMSG = UIValidation.nameValidate(username, true,"Username is required");
        if (!usernameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(usernameValidateMSG);

            // usernameTIL.setError(usernameValidateMSG);
            usernameET.requestFocus();
            return false;
        }

        else if (username.length()<=5  || username.length()>8)
        {
            openValidateDialog("Please enter minimum 6 and maximum 8 characters!");

            // usernameTIL.setError(usernameValidateMSG);
            usernameET.requestFocus();
            return false;
        }

        else if (status ==0 && message.equalsIgnoreCase("failed"))
        {
            openValidateDialog("Username already exists");

            check.setVisibility(View.VISIBLE);
            check.setImageResource(R.drawable.red_cross);
           // usernameTIL.setError("Username taken");
            return false;
        }
//        else
//        {
//           // usernameTIL.setErrorEnabled(false);
//            check.setVisibility(View.VISIBLE);
//
//        }
        return true;
    }

    private boolean validateLname()
    {
        lastName=last_nameET.getText().toString();
        String lnameValidateMSG = UIValidation.nameValidate(lastName, true,"Last Name is required");
        if (!lnameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
           // lastnameTIL.setError(lnameValidateMSG);
            openValidateDialog(lnameValidateMSG);

            last_nameET.requestFocus();
            return false;
        }
        else
        {
           // lastnameTIL.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateZip()
    {
        zipcode=zipcodeET.getText().toString();

        String zipValidateMSG = UIValidation.zipValidate(zipcode, true);
        if (!zipValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(zipValidateMSG);
            zipcodeET.requestFocus();
            return false;
        }

        else
        {
        }
        return true;

    }

    private void loadUsername()
    {
        dialog.setMessage("Loading..");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.USERNAME_SUGGETION, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    dialog.dismiss();
                    suggestedusername.clear();
                    //Log.e("response",response);
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status=obj.getInt("Status");
                    String message=obj.getString("Message");
                    if (status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONArray Data = obj.getJSONArray("Data");

                        for (int i = 0; i < Data.length(); i++)
                        {

                            String  jsonObject=Data.getString(i);
                            if (Data.getString(0).equalsIgnoreCase("empty"))
                            {suggestedUsername.setText("No Suggetion");}
                            else {
                                String support = jsonObject.replace("[]", "");
                                suggestedusername.add(support);

                                String text = suggestedusername.toString().replace("[", "").replace("]", "");

                                suggestedUsername.setText(text);
                            }

                        }
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    private void signupReq(final String g)
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.SIGNUP,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                if (signuptype.equalsIgnoreCase(Constants.MANAUAL_LOGIN_TYPE))
                                {

                                    Intent intent=new Intent(SignUp.this,Otp.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("deviceid",regId);

                                    intent.putExtra("from","signup");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(0,0);

                                }

                                else
                                {
                                    String userid= obj.getString("User_id");
                                    mSessionManager.setLoginSession(userid,Constants.GOOGLE_LOGIN_TYPE,email,firstName+" "+lastName,regId);
                                    mSessionManager.setNotify("true");
                                    openDialog();
                                }


                            }

                            else
                                openValidateDialog(obj.getString("Data"));
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
                params.put("first_name",firstName);
                params.put("last_name", lastName);
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("country", country_name);
                params.put("zipcode", zipcode);
                params.put("sigup_type", signuptype);  //1=normal signup, 2=google signup
                params.put("deviceid", displayFirebaseRegId());
                params.put("devicetype", "android"); //1=for android 2=ios
                params.put("gender", g); //1=for android 2=ios

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private String displayFirebaseRegId()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        try {
            Log.e("regId", "" + regId);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return regId;
    }

    private void openDialog()
    {
        final Dialog dialog = new Dialog(SignUp.this, R.style.CustomDialog);
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
                Intent intent=new Intent(SignUp.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from","outside");

                startActivity(intent);
                overridePendingTransition(0,0);
                dialog.dismiss();
            }
        });
    }



    @Override
    public void onStart()
    {

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
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected) {
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }

                    }
                });

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
        Intent intent=new Intent(SignUp.this,Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
    }



    private void openValidateDialog(String msg)
    {
        final Dialog dialog = new Dialog(SignUp.this, R.style.CustomDialog);
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
