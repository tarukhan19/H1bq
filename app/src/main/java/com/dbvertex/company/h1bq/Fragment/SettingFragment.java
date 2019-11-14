package com.dbvertex.company.h1bq.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.FavouriteActivity;
import com.dbvertex.company.h1bq.R;


import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbvertex.company.h1bq.PrivacyPolicy;
import com.dbvertex.company.h1bq.TermsConditon;
import com.dbvertex.company.h1bq.UserAuth.ChangePassword;
import com.dbvertex.company.h1bq.UserAuth.ContactsUs;
import com.dbvertex.company.h1bq.UserAuth.Login;
import com.dbvertex.company.h1bq.UserAuth.Profile;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {
    private LinearLayout profileLL, favLL, referLL, contactLL, termsLL, privacyLL, changepassLL, logoutLL;
    private CardView changepassCARD;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private SessionManager sessionManager;
    private ImageView edit_img;
    private TextView titleTV;
    private LinearLayout back_LL;
    private Toolbar toolbar_main;
    private SwitchCompat switchCompat;
    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }


        sessionManager = new SessionManager(getActivity().getApplicationContext());

        switchCompat = (SwitchCompat) view.findViewById(R.id.switchButton);
        toolbar_main = view.findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        changepassCARD = view.findViewById(R.id.changepassCARD);
        titleTV.setText("Settings");
        edit_img.setVisibility(View.GONE);
        back_LL.setVisibility(View.GONE);


        profileLL = view.findViewById(R.id.profileLL);
        favLL = view.findViewById(R.id.favLL);
        referLL = view.findViewById(R.id.referLL);
        contactLL = view.findViewById(R.id.contactLL);
        termsLL = view.findViewById(R.id.termsLL);
        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        privacyLL = view.findViewById(R.id.privacyLL);
        changepassLL = view.findViewById(R.id.changepassLL);
        logoutLL = view.findViewById(R.id.logoutLL);
        if (sessionManager.getLoginSession().get(SessionManager.KEY_LOGINTYPE).equalsIgnoreCase(Constants.MANAUAL_LOGIN_TYPE)) {
            changepassCARD.setVisibility(View.VISIBLE);
        } else {
            changepassCARD.setVisibility(View.GONE);

        }

        referLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "H1BQ");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.dbvertex.company.h1bq \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        profileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);

            }
        });

        contactLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ContactsUs.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        termsLL.setOnClickListener(new View.OnClickListener() {
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

        privacyLL.setOnClickListener(new View.OnClickListener() {
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

        changepassLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChangePassword.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        favLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FavouriteActivity.class);
               // i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        logoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogOutDialog();
            }
        });
        if (sessionManager.getnotify().get(SessionManager.KEYNOTIFY).equalsIgnoreCase("true"))
        {
           switchCompat.setChecked(true);
        }
        else
        {
            switchCompat.setChecked(false);
        }


        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sessionManager.setNotify("true");
                    Log.e("notify",sessionManager.getnotify().get(SessionManager.KEYNOTIFY));

                } else {
                    sessionManager.setNotify("false");
                    Log.e("notify",sessionManager.getnotify().get(SessionManager.KEYNOTIFY));

                }
            }
        });
    }

    private void showLogOutDialog()
    {

        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_logout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout ok = (LinearLayout) dialog.findViewById(R.id.ok);
        LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                logOut();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void logOut()
    {
        progressDialog.setMessage("loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.LOGOUT, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response",response);
                    progressDialog.dismiss();
                    JSONObject obj = new JSONObject(response);
// {"Status":200,"Message":"success"}
                    if (obj.getString("Status").equalsIgnoreCase("200")) {
                        Intent in7 = new Intent(getActivity(), Login.class);
                        in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in7);
                        sessionManager.logoutUser();

                        getActivity().overridePendingTransition(0, 0);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }


        ){  @Override
        protected Map<String, String> getParams() {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
            headers.put("device_id", sessionManager.getLoginSession().get(SessionManager.KEY_DEVICEID));
            headers.put("device_type","android");

            Log.e("params",headers.toString());



            return headers;
        }};



        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


}
