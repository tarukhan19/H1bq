package com.dbvertex.company.h1bq.UserAuth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity   implements ConnectivityReceiver.ConnectivityReceiverListener{

    ImageView edit_img,picImg;
    TextView titleTV;
    LinearLayout back_LL,countryLL,zipcodeLL,fnameLL,lnameLL;
    Toolbar toolbar_main;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    TextView userTV,contryTV;
    EditText fnameTV,lnameTV,zipTV;
    TextView emailTV;
    ListView countryLV;
    Intent intent;
    Button update;
    byte[] profilePicbyte= null;


    ArrayAdapter<String> countryarrayadapter;
    ArrayList<String> countryNameArray,countryIdArray,suggestedusername;
    String country_name="", country_code,firstName,lastName,username,email,zipcode,password="",confpassword="",message="",signuptype,gender,confemail;

    boolean isValidate;


    boolean isConnected;
    AlertDialog.Builder countrybuilder;
    AlertDialog countrydialog;
    ListView ccountryLV;

   // private TextInputLayout firtnameTIL,lastnameTIL,zipcodeTIL,countryTIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        isConnected = ConnectivityReceiver.isConnected();
        intent=getIntent();
        update=findViewById(R.id.update);
        requestQueue = Volley.newRequestQueue(Profile.this);
        dialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        toolbar_main = findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        lnameTV=findViewById(R.id.lnameTV);
        picImg=findViewById(R.id.picImg);
        zipcodeLL=findViewById(R.id.zipcodeLL);
        fnameLL=findViewById(R.id.fnameLL);
        lnameLL=findViewById(R.id.lnameLL);

        titleTV.setText("My Profile");

        edit_img.setVisibility(View.GONE);
        back_LL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this, HomeActivity.class);
                intent.putExtra("from","inside");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0,0);

            }
        });

        sessionManager = new SessionManager(getApplicationContext());


        countryLL=findViewById(R.id.countryLL);

        fnameTV=findViewById(R.id.fnameTV);
        userTV=findViewById(R.id.userTV);
        emailTV=findViewById(R.id.emailTV);
        contryTV=findViewById(R.id.contryTV);
        zipTV=findViewById(R.id.zipTV);


        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("0"))
        {
            fnameTV.setKeyListener(null);
            lnameTV.setKeyListener(null);
            zipTV.setKeyListener(null);
            fnameLL.setBackgroundResource(R.drawable.gray_back);
            lnameLL.setBackgroundResource(R.drawable.gray_back);
            zipcodeLL.setBackgroundResource(R.drawable.gray_back);
            countryLL.setBackgroundResource(R.drawable.gray_back);

        }



        countrybuilder = new AlertDialog.Builder(Profile.this);
        countryLV = new ListView(this);
        countryNameArray=new ArrayList<>();
        countryIdArray=new ArrayList<>();
        countryarrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_countryspinner,R.id.text, countryNameArray);

        countryLV.setAdapter(countryarrayadapter);

        countrybuilder.setView(ccountryLV);
        countrydialog = countrybuilder.create();


        countryLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                contryTV.setText(txt.getText().toString());
                country_name = txt.getText().toString();
                country_code = countryIdArray.get(position).toString();
                //Toast.makeText(AddProductActivity.this, prodTypestring, Toast.LENGTH_LONG).show();
                countrydialog.dismiss();

            }
        });

        countryLL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1")) {
                    countrydialog.setView(countryLV);
                    countrydialog.show();
                }
            }
        });

//        fnameTV.addTextChangedListener(new TextWatc(fnameTV));
//        lnameTV.addTextChangedListener(new TextWatc(lnameTV));
//        zipTV.addTextChangedListener(new TextWatc(zipTV));

        if (isConnected)
        {
            loadProfile();
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1")) {
                    HideKeyboard.hideKeyboard(Profile.this);
                    submit();
                }
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

        if (!validateZip()) {
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

               UpdateTask task = new UpdateTask();
                task.execute();
            }

        }
    }

    class UpdateTask extends AsyncTask<String, Void, String>
    {
        ;

        @Override
        protected void onPreExecute() {


            dialog.setMessage("Loading..");
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Endpoints comm = new Endpoints();
            try {
                JSONObject params = new JSONObject();
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("first_name",firstName );
                params.put("last_name",lastName );
                params.put("email", sessionManager.getLoginSession().get(SessionManager.KEY_EMAIL));
                params.put("countray", country_name);
                params.put("zipcode", zipcode);

                //Log.e("params", params+"");
                String result = comm.forSignUp(Endpoints.UPDATE_MYPROFILE, params, profilePicbyte);
                //Log.e("SignUpTask", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            //  return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.e("SignUpTask", s);
            dialog.cancel();
            try {
                //{"Status":200,"Message":"Success","Profile":"","First_name":"Hina","Last_name":"Khan","Email":"tarukhan19@gmail.com"}
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("Status");
                    String message = obj.getString("Message");

                    if (status==200 && message.equalsIgnoreCase("Success"))
                    {
                        String name= obj.getString("First_name")+" "+obj.getString("Last_name");
                        sessionManager.setLoginSession(sessionManager.getLoginSession().get(SessionManager.KEY_USERID),sessionManager.getLoginSession().get(SessionManager.KEY_LOGINTYPE),
                                sessionManager.getLoginSession().get(SessionManager.KEY_EMAIL),name,
                                sessionManager.getLoginSession().get(SessionManager.KEY_DEVICEID));
                        openDialog();
                    }

                }
            } catch (Exception ex) {
            }
        }
    }

    private void openDialog()
    {
        final Dialog dialog = new Dialog(Profile.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok=dialog.findViewById(R.id.ok);
        TextView msg=dialog.findViewById(R.id.msg);
        msg.setText("Profile Successfully Updated!");


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                loadProfile();
            }
        });
    }
    private void loadProfile()
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LOAD_MYPROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {

////                           {"Status":"0","Message":"failed","Data":"OTP Not Verify"}
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            JSONObject data = obj.getJSONObject("Data");
                            if (status==200 && message.equalsIgnoreCase("Success"))
                            {
                                JSONObject jsonObject=obj.getJSONObject("Data");
                                fnameTV.setText(jsonObject.getString("first_name"));
                                lnameTV.setText(jsonObject.getString("last_name"));
                                userTV.setText(jsonObject.getString("username"));
                                emailTV.setText(jsonObject.getString("Email"));
                                country_name=jsonObject.getString("Country");
                                contryTV.setText(jsonObject.getString("Country"));
                                zipTV.setText(jsonObject.getString("Cuntray_Zipe_Code"));

                            }
//                            else if (status==0 && message.equalsIgnoreCase("failed"))
//                            {
//                                if (data.equalsIgnoreCase("OTP Not Verify"))
//                                {
//                                    openOtpVerifyDialog();
//                                }
//
//                                if (data.equalsIgnoreCase("Invalide Password"))
//                                {
//                                    Toast.makeText(Login.this, "Invalid Password", Toast.LENGTH_SHORT).show();
//                                }
//
//                                if (data.equalsIgnoreCase("Invalide Email Id"))
//                                {
//                                    Toast.makeText(Login.this, "Invalid Email Id", Toast.LENGTH_SHORT).show();
//
//                                }
//                            }

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

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
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
//                case R.id.fnameTV:
//                    validateFname();
//                    break;
//
//                case R.id.lnameTV:
//                    validateLname();
//                    break;
//
//
//                case R.id.zipTV:
//                    validateZip();
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
    private boolean validateFname()
    {
        firstName=fnameTV.getText().toString();
        String fnameValidateMSG = UIValidation.nameValidate(firstName, true,"First Name is required");
        if (!fnameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(fnameValidateMSG);
            fnameTV.requestFocus();
            return false;
        }
        else
        {
            isValidate=true;
        }
        return true;
    }
    private boolean validateLname()
    {
        lastName=lnameTV.getText().toString();
        String lnameValidateMSG = UIValidation.nameValidate(lastName, true,"Last Name is required");
        if (!lnameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(lnameValidateMSG);
            lnameTV.requestFocus();
            return false;
        }
        else
        {
        }
        return true;
    }

    private boolean validateZip()
    {
        zipcode=zipTV.getText().toString();

        String zipValidateMSG = UIValidation.zipValidate(zipcode, true);
        if (!zipValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(zipValidateMSG);
            zipTV.requestFocus();
            return false;
        }

        else
        {
        }
        return true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Profile.this, HomeActivity.class);
        intent.putExtra("from","inside");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);

    }

    @Override
    protected void onStart() {
        //Log.e("onStart",isConnected+"");
        if (!isConnected)
        {
            showSnack(isConnected);
        }

        super.onStart();
    }


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
        Log.e("onNetworkConnectionconnprofile",isConnected+"");

        showSnack(isConnected);

    }
    private void openValidateDialog(String msg)
    {
        final Dialog dialog = new Dialog(Profile.this, R.style.CustomDialog);
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
