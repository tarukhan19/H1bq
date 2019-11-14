package com.dbvertex.company.h1bq.UserAuth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactsUs extends AppCompatActivity   implements ConnectivityReceiver.ConnectivityReceiverListener{

    ImageView edit_img;
    TextView titleTV,fnameET,emailET;
    LinearLayout back_LL,regardingLL;
    Toolbar toolbar_main;
    Spinner regarding,countryId;
    EditText numberET,descripationET;
    Button submit;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    Intent intent;
    boolean isConnected;
    Typeface font;
    private ArrayList<String> regardingList, countryidlist;
    ArrayAdapter regardingarrayadapter,countryarrayadapter;

    String countryidS,regardingS,nameS,emailS,mobilenoS,descriptionS;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_us);
        sessionManager=  new SessionManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(ContactsUs.this);
        dialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        toolbar_main = findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        titleTV.setText("Contact Us");
       // viewProgressBar = (ProgressBar) findViewById(R.id.viewProgressBar);
        edit_img.setVisibility(View.GONE);
//        descriptionTIL=findViewById(R.id.descriptionTIL);
//        regardTIL=findViewById(R.id.regardTIL);
//        mobileTIL=findViewById(R.id.mobileTIL);
        regarding= findViewById(R.id.regarding);
        countryId= findViewById(R.id.countryId);
        fnameET= findViewById(R.id.fnameET);
        emailET= findViewById(R.id.emailET);
        numberET= findViewById(R.id.numberET);
        descripationET= findViewById(R.id.descripationET);
        regardingLL= findViewById(R.id.regardingLL);
        submit= findViewById(R.id.submit);
        regardingList = new ArrayList<>();
        countryidlist = new ArrayList<>();
        isConnected = ConnectivityReceiver.isConnected();

        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        intent=getIntent();
        back_LL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ContactsUs.this, HomeActivity.class);
                intent.putExtra("from","inside");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        regardingList.add("Regarding");
        regardingList.add("Account");
        regardingList.add("Marketing");
        regardingList.add("Report bug");

        countryidlist.add("+1");
        countryidlist.add("+61");
        countryidlist.add("+56");
        countryidlist.add("+86");
        countryidlist.add("+91");
        countryidlist.add("+52");
        countryidlist.add("+63");
        countryidlist.add("+65");
        countryidlist.add("+44");

        //regardingarrayadapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,regardingList);

        // Initializing an ArrayAdapter
        regardingarrayadapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item,regardingList){

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setVisibility(View.GONE);

                }
                else {
                    tv.setTextColor(Color.BLACK);
                    tv.setVisibility(View.VISIBLE);
                    tv.setTypeface(font);

                }
                return view;
            }
        };

        regardingarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        regarding.setAdapter(regardingarrayadapter);


        countryarrayadapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,countryidlist);
        countryarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        countryId.setAdapter(countryarrayadapter);
        emailET.setText(sessionManager.getLoginSession().get(SessionManager.KEY_EMAIL));
        fnameET.setText(sessionManager.getLoginSession().get(SessionManager.KEY_NAME));


        regarding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                regardingS = parent.getItemAtPosition(position).toString();

                if (position==0)
                {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#8D8D8D"));
                    ((TextView) parent.getChildAt(0)).setTypeface(font);
                }
                else
                {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) parent.getChildAt(0)).setTypeface(font);

                }

                // Showing selected spinner item
//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        countryId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryidS = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTypeface(font);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

                // Showing selected spinner item
               // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HideKeyboard.hideKeyboard(ContactsUs.this);
                submitData();

            }
        });
//        numberET.addTextChangedListener(new TextWatc(numberET));
//        descripationET.addTextChangedListener(new TextWatc(descripationET));

    }




    public void submitData()
    {

        if (!validateMobile()) {
            return;
        }
        if (!validateRegard()) {
            return;
        }
        if (!validateDesc()) {
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
                contactUs();
            }

        }
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
//                case R.id.numberET:
//                    validateMobile();
//                    break;
//
//
//                case R.id.regarding:
//                    validateRegard();
//                    break;
//                case R.id.descripationET:
//                    validateDesc();
//                    break;
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

    private boolean validateMobile() {
        mobilenoS=numberET.getText().toString();
        String mobValidateMSG = UIValidation.mobileValidate(mobilenoS, true);
        if (!mobValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(mobValidateMSG);
            numberET.requestFocus();
            return false;
        }
        else
        {
        }
        return true;
    }
    private boolean validateDesc() {
        descriptionS=descripationET.getText().toString();
        String descValidateMSG = UIValidation.addressValidate(descriptionS, true,"Description is required");
        if (!descValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS))
        {
            openValidateDialog(descValidateMSG);
            descripationET.requestFocus();
            return false;
        }
        else
        {
        }
        return true;
    }

    private boolean validateRegard() {
        if (regardingS.equalsIgnoreCase("Regarding"))
        {
            openValidateDialog("Select regarding issue");
            regarding.requestFocus();
            return false;
        }
        else
        {
        }
        return true;
    }


    private void contactUs()
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();
        //  viewProgressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CONTACT_US,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     //   viewProgressBar.setVisibility(View.GONE);
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                String data=obj.getString("Data");
                                if(data.equalsIgnoreCase("Data Successfully Submit"))
                                {
                                    dialog.dismiss();
                                    openDialog();
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
                       // viewProgressBar.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("full_name", sessionManager.getLoginSession().get(SessionManager.KEY_NAME));
                params.put("email", sessionManager.getLoginSession().get(SessionManager.KEY_EMAIL));
                params.put("regarding", regardingS);
                params.put("descripation", descriptionS);
                params.put("mobile",countryidS+mobilenoS);

                Log.e("params", params.toString());
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
        final Dialog dialog = new Dialog(ContactsUs.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_contactus);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        LinearLayout ok=dialog.findViewById(R.id.ok);
        TextView msgTV=dialog.findViewById(R.id.msgTV);
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(intent);
                overridePendingTransition(0,0);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ContactsUs.this, HomeActivity.class);
        intent.putExtra("from","inside");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);

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

    private void openValidateDialog(String msg)
    {
        final Dialog dialog = new Dialog(ContactsUs.this, R.style.CustomDialog);
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
