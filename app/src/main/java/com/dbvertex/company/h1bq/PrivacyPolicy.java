package com.dbvertex.company.h1bq;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.dbvertex.company.h1bq.Util.Endpoints;

public class PrivacyPolicy extends AppCompatActivity {

//    ImageView edit_img;
//    TextView titleTV;
//    LinearLayout back_LL;
//    Toolbar toolbar_main;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

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



//        pd = new ProgressDialog(this);
//        pd.setMessage("Page Loading .....");
//        pd.setCancelable(false);
//
//
//        WebView webView = (WebView) findViewById(R.id.webView);
//
//        webView.setWebViewClient(new MyClient());
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl(Endpoints.PRIVACYPOLICY);
//        webView.setHorizontalScrollBarEnabled(false);
    }

//
//    class MyClient extends WebViewClient {
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            pd.show();
//            super.onPageStarted(view, url, favicon);
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            pd.cancel();
//            super.onPageFinished(view, url);
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            view.loadUrl(Endpoints.PRIVACYPOLICY);
//            return super.shouldOverrideUrlLoading(view, request);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//       finish();
//    }
}
