package com.dbvertex.company.h1bq;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostDetail extends AppCompatActivity {
    Toolbar toolbar_main;
    ImageView edit_img;
    TextView titleTV;
    LinearLayout back_LL,bucketLL;
    Button post,cancel;
    boolean isValidate;
    Intent intent;
    String postId,from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        toolbar_main = findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        titleTV.setText("Post Detail");
        edit_img.setVisibility(View.GONE);
        intent=getIntent();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, new PostDetailFragment()).commit();
        postId=intent.getStringExtra("postid");
        from=intent.getStringExtra("from");


        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("home"))
                {
                    finish();
                }

                else if (from.equalsIgnoreCase("favorite"))
                {
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equalsIgnoreCase("home"))
        {
            finish();
        }

        else if (from.equalsIgnoreCase("favorite"))
        {
            finish();
        }
    }
}
