package com.dbvertex.company.h1bq;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbvertex.company.h1bq.Util.Endpoints;

public class TandCWebview extends AppCompatActivity {
    Endpoints endpoints;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tand_cwebview);

        pd = new ProgressDialog(this);
        pd.setMessage("Page Loading .....");
        pd.setCancelable(false);
        endpoints=new Endpoints();

//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
//        TextView mTitle = (TextView) toolbar.findViewById(R.id.titleTV);
//        LinearLayout backImage =  toolbar.findViewById(R.id.back_LL);
//        ImageView edit_img =  toolbar.findViewById(R.id.edit_img);
//        mTitle.setText("Terms & Conditions");
//        edit_img.setVisibility(View.GONE);
//        backImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        WebView webView = (WebView) findViewById(R.id.webView);

        webView.setWebViewClient(new MyClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("www.google.com");
        webView.setHorizontalScrollBarEnabled(false);
    }

    class MyClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            pd.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            pd.cancel();
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
        {
            view.loadUrl("www.google.com");
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
}
