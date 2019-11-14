package com.dbvertex.company.h1bq;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbvertex.company.h1bq.Util.Endpoints;

public class TermsConditon extends AppCompatActivity {

    ////ImageView edit_img;
   // TextView titleTV;
   // LinearLayout back_LL;
   // Toolbar toolbar_main;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditon);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
//        toolbar_main = findViewById(R.id.toolbar_main);
//        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
//        back_LL = toolbar_main.findViewById(R.id.back_LL);
//        edit_img = toolbar_main.findViewById(R.id.edit_img);
//        titleTV.setText("Terms & Conditions");
//        edit_img.setVisibility(View.GONE);

//        back_LL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               finish();
//
//            }
//        });

//        pd = new ProgressDialog(this);
//        pd.setMessage("Page Loading .....");
//        pd.setCancelable(false);
//
//
//        WebView webView = (WebView) findViewById(R.id.webView);
//
//        webView.setWebViewClient(new MyClient());
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl(Endpoints.TERMANDCONDITION);
//        webView.setHorizontalScrollBarEnabled(false);


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



}
