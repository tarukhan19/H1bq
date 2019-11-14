package com.dbvertex.company.h1bq;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Adapter.FavoriteAdapter;
import com.dbvertex.company.h1bq.Adapter.FavouriteMainCommentAdp;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.model.AnsListDTO;
import com.dbvertex.company.h1bq.model.HomeDTO;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavouriteActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public ArrayList<HomeDTO> list;
    public FavoriteAdapter adapter;
    int offset = 0;
    String offsetString;
    int direction = 0;
    public SessionManager session;
    public static final int DISMISS_TIMEOUT = 2000;
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    ImageView edit_img;
    TextView titleTV;
    LinearLayout back_LL;
    LinearLayoutManager linearLayoutManager;
    Toolbar toolbar_main;
    Bitmap scaledBitmap = null;
    ImageLoadingUtils utils;
    ImageView pic;
    TextView nodatafound;
    FavouriteMainCommentAdp commentAdapter;
    boolean isConnected;
    LinearLayout toparrow;
    SwipeRefreshLayout swiperefresh;
    Vibrator vibe;
    Uri imageUri;
    byte[] profilePicbyte = null;
    public static final int PERMISSION_REQUEST = 100;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;

    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;
    String fromAdp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        isConnected = ConnectivityReceiver.isConnected();

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        utils = new ImageLoadingUtils(this);
        nodatafound = findViewById(R.id.nodatafound);
        toolbar_main = findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        titleTV.setText("Favorites");
        // viewProgressBar = (ProgressBar) findViewById(R.id.viewProgressBar);
        edit_img.setVisibility(View.GONE);
        toparrow = findViewById(R.id.toparrow);
        swiperefresh = findViewById(R.id.swiperefresh);
        requestQueue = Volley.newRequestQueue(this);
        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavouriteActivity.this, HomeActivity.class);
                intent.putExtra("from", "inside");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        list = new ArrayList<HomeDTO>();
        adapter = new FavoriteAdapter(this, list);
        session = new SessionManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_story);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        offset = 0;
        offsetString = String.valueOf(offset);

        if (isConnected) {
            loadFavList(offsetString, session.getLoginSession().get(SessionManager.KEY_USERID));

        } else {
            recyclerView.setVisibility(View.GONE);
            nodatafound.setVisibility(View.VISIBLE);
            nodatafound.setText("No internet connection available.");

        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = linearLayoutManager.getChildCount();
                totalitemcount = linearLayoutManager.getItemCount();
                pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (dy > 0) //check for scroll down
                {


                    animShow();

                    if (isLoading) {

                        if (totalitemcount > previoustotal) {
                            isLoading = false;
                            previoustotal = totalitemcount;

                        }


                    }

                    if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold)) {
                        offset = offset + 1;
                        offsetString = String.valueOf(offset);
                        if (isConnected) {
                            loadFavList(offsetString, session.getLoginSession().get(SessionManager.KEY_USERID));

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodatafound.setVisibility(View.VISIBLE);
                            nodatafound.setText("No internet connection available.");

                        }
                        isLoading = true;
                    }

                }

                if (pastVisibleItems == 0) {
                    animHide();
                }

            }
        });


        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        vibe.vibrate(50);

                        list.clear();
                        adapter.notifyDataSetChanged();
                        offset = 0;
                        offsetString = String.valueOf(offset);
                        if (isConnected) {
                            loadFavList(offsetString, session.getLoginSession().get(SessionManager.KEY_USERID));

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodatafound.setVisibility(View.VISIBLE);
                            nodatafound.setText("No internet connection available.");

                        }
                        isLoading = true;
                        pastVisibleItems = 0;
                        visibleItemCount = 0;
                        totalitemcount = 0;
                        previoustotal = 0;
                        view_threshold = 10;
                        swiperefresh.setRefreshing(false);


                    }
                }
        );

        toparrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
                animHide();

            }
        });

    }

    private void loadFavList(final String offsetString, final String userId) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.FAV_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("loadfavList", response + "");

                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (offset == 0) {
                                list.clear();
                            }

                            if (status == 200 && message.equals("success")) {


                                JSONArray data = obj.getJSONArray("Data");


                                for (int x = 0; x < data.length(); x++) {
                                    swiperefresh.setVisibility(View.VISIBLE);
                                    nodatafound.setVisibility(View.GONE);
                                    JSONObject dataJSONObject = data.getJSONObject(x);
                                    JSONArray ansArray = dataJSONObject.getJSONArray("answer");

                                    HomeDTO homeDTO = new HomeDTO();
                                    homeDTO.setUserName(dataJSONObject.getString("username"));
                                    homeDTO.setPostId(dataJSONObject.getString("post_id"));
                                    homeDTO.setUserId(dataJSONObject.getString("user_id"));
                                    homeDTO.setTitle(dataJSONObject.getString("title"));
                                    homeDTO.setDescription(dataJSONObject.getString("dicription"));
                                    homeDTO.setPostType(dataJSONObject.getString("post_type"));
                                    homeDTO.setImage(dataJSONObject.getString("post_image"));
                                    homeDTO.setDeleteStatus(dataJSONObject.getString("delete_status"));
                                    homeDTO.setAnsSlectType(dataJSONObject.getString("ans_selection_typ"));
                                    homeDTO.setDaysCount(dataJSONObject.getString("add_date"));
                                    homeDTO.setVotesCount(dataJSONObject.getString("count_vote"));
                                    homeDTO.setCommentCount(dataJSONObject.getString("count_comment"));
                                    homeDTO.setLikeCount(dataJSONObject.getString("count_like"));
                                    homeDTO.setViewCount(dataJSONObject.getString("count_view"));
                                    homeDTO.setBookmarkstatus(dataJSONObject.getString("bookmark_status"));
                                    homeDTO.setLikestatus(dataJSONObject.getString("like_status"));
                                    homeDTO.setViewstatus(dataJSONObject.getString("view_status"));
                                    homeDTO.setReportabusestatus(dataJSONObject.getString("report_abuse_status"));


                                    List<AnsListDTO> dtos = new ArrayList<>();
                                    for (int j = 0; j < ansArray.length(); j++) {
                                        dtos.add(new AnsListDTO(ansArray.getString(j)));

                                    }
                                    homeDTO.setAnswers(dtos);

                                    list.add(homeDTO);
                                }
                                //prodrecycle.setAdapter(productListAdapter);
                                adapter.notifyDataSetChanged();
                            } else if (status == 0 && message.equalsIgnoreCase("Record Not Found")) {
                                swiperefresh.setVisibility(View.GONE);
                                nodatafound.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception ex) {
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
                //user_id & offset  & buket_id
                params.put("user_id", userId);
                params.put("offset", offsetString);
                Log.e("params", params + "");

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FavouriteActivity.this, HomeActivity.class);
        intent.putExtra("from", "inside");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }


    public void galleryOpen(Context mContext, FavouriteMainCommentAdp adp) {
        this.commentAdapter = adp;
        fromAdp = "commentAdapter";
        opendialog();
    }


    public void openGallery(Context mContext, FavoriteAdapter adapter) {
        this.adapter = adapter;
        fromAdp = "homeAdapter";
        opendialog();


//        Intent i = new Intent(
//                Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(i, REQUEST_CODE_FROM_GALLERY);


    }


    public void opendialog() {
        final Dialog dialog = new Dialog(FavouriteActivity.this, R.style.CustomDialog);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        showSnack(isConnected);

    }

    @Override
    protected void onStart() {
        super.onStart();
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            showSnack(isConnected);
        }


    }

    private void showSnack(final boolean isConnect) {

        if (!isConnect) {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.container), "No Internet connection available.", Snackbar.LENGTH_LONG);
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
                    .make(findViewById(R.id.container), "Internet Connected", Snackbar.LENGTH_LONG);
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
            Log.e("scaledBitmap", scaledBitmap + " " + scaledBitmap.getHeight() + " " + scaledBitmap.getWidth());
            if (fromAdp.equalsIgnoreCase("homeAdapter")) {
                adapter.setImage(scaledBitmap);
            } else {
                commentAdapter.setImage(scaledBitmap);
            }

        }

    }


    private void animShow() {
        // slide-up animation
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slidedown);

        if (toparrow.getVisibility() == View.INVISIBLE) {
            toparrow.setVisibility(View.VISIBLE);
            toparrow.startAnimation(slideUp);
        }
    }

    private void animHide() {
        // slide-up animation
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slidedown);

        if (toparrow.getVisibility() == View.VISIBLE) {
            toparrow.setVisibility(View.INVISIBLE);
            toparrow.startAnimation(slideUp);
        }
    }
}
