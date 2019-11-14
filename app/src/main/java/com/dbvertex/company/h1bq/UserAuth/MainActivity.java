package com.dbvertex.company.h1bq.UserAuth;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.session.SessionManager;


public class MainActivity extends AppCompatActivity
{
    private static int SPLASH_TIME_OUT =1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        final SessionManager session = new SessionManager(getApplicationContext());
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if (session.isLoggedIn()) {
                    Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("from","outside");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                    finish();
                } else

                    startActivity(new Intent(MainActivity.this, Login.class));
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                finish();
            }
        }, 1000);


    //    printHashKey();


    }

//    public void printHashKey() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.e("printHashKey() Hash Key: " ,hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("exc", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("ecptn", "printHashKey()", e);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();

// Clear all notification
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

}
