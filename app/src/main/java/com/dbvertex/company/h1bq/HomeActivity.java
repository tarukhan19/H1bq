package com.dbvertex.company.h1bq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import androidx.room.Room;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dbvertex.company.h1bq.Fragment.AdminNotificationFragment;
import com.dbvertex.company.h1bq.Fragment.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.dbvertex.company.h1bq.Adapter.HomeAdapter;
import com.dbvertex.company.h1bq.Adapter.MainCommentAdapter;
import com.dbvertex.company.h1bq.Adapter.SearchAdapter;
import com.dbvertex.company.h1bq.Adapter.SearchMainCommentAdp;
import com.dbvertex.company.h1bq.Fragment.HIBFragment;
import com.dbvertex.company.h1bq.Fragment.I131Fragment;
import com.dbvertex.company.h1bq.Fragment.I140Fragment;
import com.dbvertex.company.h1bq.Fragment.I485Fragment;
import com.dbvertex.company.h1bq.Fragment.I765Fragment;
import com.dbvertex.company.h1bq.Fragment.MiscFragment;
import com.dbvertex.company.h1bq.Fragment.PermFragment;
import com.dbvertex.company.h1bq.Fragment.SearchFragment;
import com.dbvertex.company.h1bq.Fragment.SettingFragment;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.Network.MyApplication;
import com.dbvertex.company.h1bq.UserAuth.Login;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public  class  HomeActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    Intent intent;
    String from;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    AppBarLayout appbarLL;
    Fragment fragment;
    boolean doubleBackToExitPressedOnce = false;
    boolean shouldLoadHomeFragOnBackPress = true;
    public static int navItemIndex = 0;
    MediaPlayer tabClick;
    FloatingActionButton floatingActionButton;
    Bundle myBundle;
    BottomNavigationView navigation;
    Bitmap scaledBitmap = null;
    ImageLoadingUtils utils;
    ImageView pic;
    SearchAdapter searchAdapter;
    SearchMainCommentAdp searchMainCommentAdp;
    static SessionManager sessionManager;
    Dialog adDialog;
    ArrayList<String> imagelist, textlist;
    final Handler handler = new Handler();
    private boolean runnable;
    String aditem, aditemtext;
    boolean isConnected;
    View badge;
    HomeAdapter adapter;
    MainCommentAdapter commentAdapter;
    String fromAdp;
    Dialog dialog;
    static RequestQueue requestQueue;
    TextView text;
    int bucket_id = 0;
    public static MyAppDatabase myAppDatabase;
    //   RelativeLayout rl;
    Uri imageUri;
    byte[] profilePicbyte = null;
    public static final int PERMISSION_REQUEST = 100;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    private Random randomGenerator;
    BottomNavigationItemView itemView;
    Menu m1;
    MenuItem item;
    static HomeActivity homeActivity;
    private AdView mAdView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("NewApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HIBFragment();
                    loadFragment(fragment);
                    appbarLL.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);

                    if (isConnected) {

                        mAdView.setVisibility(View.VISIBLE);
//                        nointernet.setVisibility(View.GONE);


                    } else {
                        mAdView.setVisibility(View.GONE);
                        //   nointernet.setVisibility(View.VISIBLE);

                    }

                    navItemIndex = 0;
                    return true;
                case R.id.navigation_search:
                    fragment = new SearchFragment();
                    loadFragment(fragment);
                    appbarLL.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    mAdView.setVisibility(View.GONE);
                    //      nointernet.setVisibility(View.GONE);

                    navItemIndex = 1;


                    return true;
                case R.id.navigation_notifications:

                    fragment = new NotiFragment();
                    loadFragment(fragment);
                    appbarLL.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    mAdView.setVisibility(View.GONE);
                    //    nointernet.setVisibility(View.GONE);
                    sessionManager.setNotificationCount(0);
                    navItemIndex = 2;


                    sessionManager.setNotificationCount(0);

                    showBadge(String.valueOf(0));
                    return true;

                case R.id.navigation_setting:
                    fragment = new SettingFragment();
                    loadFragment(fragment);
                    appbarLL.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    mAdView.setVisibility(View.GONE);
                    //  nointernet.setVisibility(View.GONE);


                    navItemIndex = 4;

                    return true;
            }
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        isConnected = ConnectivityReceiver.isConnected();
        mAdView = findViewById(R.id.nativeAd);
        //     rl=findViewById(R.id.rl);
        adDialog = new Dialog(this, R.style.full_screen_dialog);
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        adDialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        adDialog.setContentView(R.layout.item_customad);
        adDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        adDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        adDialog.setCanceledOnTouchOutside(false);
        randomGenerator = new Random();
        homeActivity = this;

        myAppDatabase = Room.databaseBuilder(getApplicationContext(), MyAppDatabase.class, "postpolllist").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        //nointernet = findViewById(R.id.nointernet);
        intent = getIntent();
        appbarLL = findViewById(R.id.appbarLL);
        from = intent.getStringExtra("from");
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        MobileAds.initialize(this, "ca-app-pub-4921425641197600~8034013240");


        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.

            }
        });
        imagelist = new ArrayList<>();
        textlist = new ArrayList<>();

        tabClick = MediaPlayer.create(HomeActivity.this, R.raw.tick);
        myBundle = new Bundle();
        myBundle.putString("from", "home");
        utils = new ImageLoadingUtils(this);
        requestQueue = Volley.newRequestQueue(HomeActivity.this);
        sessionManager = new SessionManager(getApplicationContext());

//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());


        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);


        setupTabIcons();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        itemView = navigation.findViewById(R.id.navigation_notifications);
        badge = LayoutInflater.from(this).inflate(R.layout.layout_news_badge, navigation, false);
        text = badge.findViewById(R.id.badge_text_view);


        if (from.equalsIgnoreCase("inside"))
        {
            fragment = new SettingFragment();
            loadFragment(fragment);
            appbarLL.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            // nointernet.setVisibility(View.GONE);
            navigation.setSelectedItemId(R.id.navigation_setting);
            navItemIndex = 4;
        } else if (from.equalsIgnoreCase("outside")) {

            if (savedInstanceState == null) {
                navItemIndex = 0;
                loadFragment(new HIBFragment());
            }
            navigation.setSelectedItemId(R.id.navigation_home);

            appbarLL.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);

        } else if (from.equalsIgnoreCase("compose")) {

            if (savedInstanceState == null) {
                navItemIndex = 0;
                loadFragment(new HIBFragment());
            }
            navigation.setSelectedItemId(R.id.navigation_home);

            appbarLL.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            String bucketid = intent.getStringExtra("bucket_id");
            bucket_id = Integer.parseInt(bucketid);
            Log.e("bucketid", bucket_id + "");
            viewPager.setCurrentItem(bucket_id - 1);

        }


        m1 = navigation.getMenu();
        item = m1.findItem(R.id.navigation_notifications);
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        for (int i = 0; i < m1.size(); i++) {
            @SuppressLint({"NewApi", "LocalSuppress"}) MenuItem mi = m1.getItem(i);
            SpannableString s1 = new SpannableString(mi.getTitle());
            s1.setSpan(new CustomTypefaceSpan("", tf1), 0, s1.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s1);
        }


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, ComposePost.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                switch (am.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        Log.i("MyApp", "Silent mode");
                        tabClick.pause();
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        Log.i("MyApp", "Vibrate mode");
                        tabClick.pause();
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.i("MyApp", "Normal mode");
                        tabClick.start();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        runnable = handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                anyItem();
                handler.postDelayed(this, 120000);
            }
        }, 120000);

//        handler.postDelayed(new Runnable() {
//            public void run() {
//                //now is every 1 minutes
//            }
//        }, 20000); //Every 120000 ms (1 minutes)
        new CheckUserExistence().checkuser();
        loadAds();
        setNotiIcon(sessionManager.getNotificationCount());


    }





    public static HomeActivity getInstance() {
        return homeActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setNotiIcon(final int notificationCount) {

        Log.e("notificationCount", notificationCount + "");
        showBadge(String.valueOf(notificationCount));
    }


    public void runThread(final int notificationCount) {

        new Thread() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            Log.e("notificationCount", notificationCount + "");
                            showBadge(String.valueOf(notificationCount));
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void showAd(String pic, String aditemtext) {

        ImageView selectedImage = (ImageView) adDialog.findViewById(R.id.selectedImage); // init a ImageView
        TextView selectedtext = (TextView) adDialog.findViewById(R.id.text); // init a ImageView

        ImageView crossImg = adDialog.findViewById(R.id.crossImg);
        Picasso.with(this).load(pic).placeholder(R.color.gray).into(selectedImage);
        selectedtext.setText(aditemtext);

        crossImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adDialog.dismiss();
            }
        });
        if (!(HomeActivity.this.isFinishing())) {
            adDialog.show();
        }


    }

    public void anyItem() {
        try {
            if (imagelist.size() > 0) {
                int index = randomGenerator.nextInt((imagelist.size()));
                aditem = imagelist.get(index);
                aditemtext = textlist.get(index);

                if (adDialog.isShowing()) {
                    adDialog.dismiss();
                    showAd(aditem, aditemtext);
                } else {
                    showAd(aditem, aditemtext);
                }
            }
        } catch (IllegalArgumentException e) {
        }

    }


    public static class CheckUserExistence
    {

        public void checkuser()
        {
        //        dialog.setMessage("Loading..");
//        dialog.setCancelable(true);
//        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CHECK_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // dialog.dismiss();
                        Log.e("CHECK_USER", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");

                            if (status == 0 && message.equalsIgnoreCase("Data Not Found!")) {
                                Intent in7 = new Intent(getInstance(), Login.class);
                                in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                getInstance().startActivity(in7);
                                //  dialog.dismiss();
                                sessionManager.logoutUser();
                                getInstance().overridePendingTransition(0, 0);

                            } else {
                                JSONObject object = obj.getJSONObject("Data");
                                String userstatus = object.getString("user_status");
                                sessionManager.setUserStatus(userstatus);


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
                        //  dialog.dismiss();
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
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        fragment.setArguments(myBundle);

        transaction.addToBackStack(null);
        transaction.commit();
    }


    @SuppressLint("ResourceType")
    private void setupTabIcons() {


        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabOne.setTextSize(10);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        tabOne.setText("H1B");


        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabTwo.setTextSize(10);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
        tabTwo.setText("PERM");


        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabThree.setTextSize(10);
        tabLayout.getTabAt(2).setCustomView(tabThree);
        tabThree.setText("I-140");


        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabFour.setTextSize(10);
        tabLayout.getTabAt(3).setCustomView(tabFour);
        tabFour.setText("I-131(AP)");

        TextView tabFive = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFive.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabFive.setTextSize(10);
        tabLayout.getTabAt(4).setCustomView(tabFive);
        tabFive.setText("I-485");

        TextView tabSix = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSix.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabSix.setTextSize(10);
        tabLayout.getTabAt(5).setCustomView(tabSix);
        tabSix.setText("I-765(EAD)");

        TextView tabSeven = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSeven.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabSeven.setTextSize(10);
        tabLayout.getTabAt(6).setCustomView(tabSeven);
        tabSeven.setText("MISC");
    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HIBFragment(), "HIB");
        adapter.addFrag(new PermFragment(), "PERM");
        adapter.addFrag(new I140Fragment(), "I-140");
        adapter.addFrag(new I131Fragment(), "I-131(AP)");
        adapter.addFrag(new I485Fragment(), "I-485");
        adapter.addFrag(new I765Fragment(), "I-765(EAD)");
        adapter.addFrag(new MiscFragment(), "MISC");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(7);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        new HIBFragment().getNetwork(isConnected);
        new I131Fragment().getNetwork(isConnected);
        new I140Fragment().getNetwork(isConnected);
        new I485Fragment().getNetwork(isConnected);
        new I765Fragment().getNetwork(isConnected);
        new MiscFragment().getNetwork(isConnected);
        new PermFragment().getNetwork(isConnected);
        new NotificationFragment().getNetwork(isConnected);
        new AdminNotificationFragment().getNetwork(isConnected);

        showSnack(isConnected);

    }


    @Override
    protected void onStart() {
        super.onStart();
        isConnected = ConnectivityReceiver.isConnected();
        new HIBFragment().getNetwork(isConnected);
        new I131Fragment().getNetwork(isConnected);
        new I140Fragment().getNetwork(isConnected);
        new I485Fragment().getNetwork(isConnected);
        new I765Fragment().getNetwork(isConnected);
        new MiscFragment().getNetwork(isConnected);
        new PermFragment().getNetwork(isConnected);
        new NotificationFragment().getNetwork(isConnected);
        new AdminNotificationFragment().getNetwork(isConnected);

        if (!isConnected) {
            showSnack(isConnected);
        }


    }

    @Override
    public void onResume() {
        super.onResume();


        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {

            if (dialog.isShowing())
                dialog.dismiss();

            dialog = null;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (shouldLoadHomeFragOnBackPress) {

            if (navItemIndex != 0) {
                loadFragment(new HIBFragment());
                navItemIndex = 0;
                navigation.setSelectedItemId(R.id.navigation_home);
                return;
            }
        }


        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }


    public void openMainCommGallery(Context mContext, MainCommentAdapter adp) {
        this.commentAdapter = adp;
        fromAdp = "commentAdapter";
        opendialog();

//
//        Intent i = new Intent(
//                Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(i, REQUEST_CODE_FROM_GALLERY);


    }


    public void openHomeAdpGallery(Context mContext, HomeAdapter adapter) {
        this.adapter = adapter;
        fromAdp = "homeAdapter";

        opendialog();

    }

    public void openSearchAdpGallery(Context mContext, SearchAdapter adapter) {
        this.searchAdapter = adapter;
        fromAdp = "searchAdapter";

        opendialog();


    }


    public void openSearchCommGallery(Context mContext, SearchMainCommentAdp adapter) {
        this.searchMainCommentAdp = adapter;
        fromAdp = "searchcommentAdapter";
        opendialog();
    }


    public void opendialog() {

        dialog = new Dialog(HomeActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_gallerycamera);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cameraLBL = (TextView) dialog.findViewById(R.id.cameraLBL);
        TextView gallLBL = (TextView) dialog.findViewById(R.id.gallLBL);
        ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV);

        cameraLBL.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override

            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
                dialog.cancel();
            }
            //  }
        });
        gallLBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_CODE_FROM_GALLERY);
                dialog.cancel();
            }
        });
        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FROM_GALLERY:
                    new ImageCompressionAsyncTask().execute(data.getDataString());
                    break;
                case REQUEST_CODE_CLICK_IMAGE:
                    try {
                        String imageurl = getRealPathFromURI(imageUri);
                        new ImageCompressionAsyncTask().execute(imageurl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String filePath = compressImage(params[0]);

            return filePath;
        }

        public String compressImage(String imageUri) {

            String filePath = getRealPathFromURI(imageUri);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (fromAdp.equalsIgnoreCase("homeAdapter")) {
                adapter.setImage(scaledBitmap);
            } else if (fromAdp.equalsIgnoreCase("searchAdapter")) {
                searchAdapter.setImage(scaledBitmap);
            } else if (fromAdp.equalsIgnoreCase("searchcommentAdapter")) {
                searchMainCommentAdp.setImage(scaledBitmap);
            } else {
                commentAdapter.setImage(scaledBitmap);
            }

        }

    }


    public void loadAds() {
        StringRequest postRequest = new StringRequest(Request.Method.GET, Endpoints.Load_Ads,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");

                            if (status == 200 && message.equalsIgnoreCase("Success")) {
                                JSONArray data = obj.getJSONArray("Data");

                                for (int i = 0; i < data.length(); i++) {
                                    final JSONObject dataJSONObject = data.getJSONObject(i);

                                    String base_url = dataJSONObject.getString("base_url");
                                    String advertisement_image = dataJSONObject.getString("advertisement_image");
                                    String image = base_url + advertisement_image;
                                    String text = dataJSONObject.getString("description");
                                    textlist.add(text);

                                    imagelist.add(image);


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
                    }
                }
        ) {

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    private void showSnack(final boolean isConnect) {

        if (!isConnect) {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.viewSnack), "No Internet connection available.", Snackbar.LENGTH_LONG);
//            snackbar.setAction("Dismiss", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    snackbar.dismiss();
//                }
//            });

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.viewSnack), "Internet Connected", Snackbar.LENGTH_LONG);
//            snackbar.setAction("Dismiss", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    snackbar.dismiss();
//                }
//            });

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showBadge(String value) {

        if (value.equalsIgnoreCase("0")) {
            if (itemView != null) {
                itemView.removeView(badge);
            }
            if (text.getVisibility() == View.VISIBLE)
                text.setVisibility(View.INVISIBLE);

        } else {
            if (Integer.parseInt(value) > 1) {
                if (itemView != null) {
                    itemView.removeView(badge);
                }
            }
            text.setVisibility(View.VISIBLE);
            text.setText(value);
            itemView.addView(badge);
        }

    }


}
