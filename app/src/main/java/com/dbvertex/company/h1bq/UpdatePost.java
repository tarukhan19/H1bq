package com.dbvertex.company.h1bq;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Util.BitmapUtil;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.UIValidation;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UpdatePost extends AppCompatActivity {
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Bitmap scaledBitmap = null;
    Uri imageUri;
    byte[] profilePicbyte = null;
    public static final int PERMISSION_REQUEST = 100;
    ImageLoadingUtils utils;
    ImageView edit_img;
    TextView titleTV;
    LinearLayout back_LL, bucketLL;
    Toolbar toolbar_main;
    Button post, cancel;
    boolean isValidate;
    Intent intent;
    ProgressDialog pd;
    private EditText title_ET, descripET;
    private LinearLayout ll, postLL, postVisible;
    private TextView chooseBucketTV;
    // private TextInputLayout optionTIL,titleTIL,descripTIL,bucketTIL;
    private ImageView drop_downImg, uploadImg, uploadImg1, postactive;
    SessionManager session;
    RequestQueue requestQueue;


    ArrayAdapter<String> bucketArrayadapter;
    ArrayList<String> bucketNameArray;
    String bucket_name = "", titleS, descriptionS, bucket_id, postId;
    boolean isConnected;
    AlertDialog.Builder bucketbuilder;
    AlertDialog bucketdialog;
    ListView bucketLV;
    String[] bucket = {"H1B",
            "PERM",
            "I-140",
            "I-131(AP)",
            "I-485",
            "I-765(EAD)",
            "MISC"};


    private RelativeLayout uploadLLO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.blue));
        }
        session = new SessionManager(getApplicationContext());
        pd = new ProgressDialog(UpdatePost.this, R.style.MyAlertDialogStyle);
        requestQueue = Volley.newRequestQueue(UpdatePost.this);
        utils = new ImageLoadingUtils(this);

        toolbar_main = findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        titleTV.setText("Update");
        edit_img.setVisibility(View.GONE);
        intent = getIntent();
        postId = intent.getStringExtra("postId");
        // start option

        title_ET = findViewById(R.id.title_ET);
        descripET = findViewById(R.id.descripET);
        bucketLL = findViewById(R.id.bucketLL);
        chooseBucketTV = findViewById(R.id.chooseBucketTV);
        uploadImg = findViewById(R.id.uploadImg);
        uploadImg1 = findViewById(R.id.uploadImg1);

        uploadLLO = findViewById(R.id.uploadLLO);


        post = findViewById(R.id.post);
        cancel = findViewById(R.id.cancel);


        bucketbuilder = new AlertDialog.Builder(UpdatePost.this);
        bucketLV = new ListView(this);

        bucketNameArray = new ArrayList<>();
        bucketNameArray.addAll(Arrays.asList(bucket));

        bucketArrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_countryspinner, R.id.text, bucketNameArray);

        bucketLV.setAdapter(bucketArrayadapter);

        bucketbuilder.setView(bucketLV);
        bucketdialog = bucketbuilder.create();

        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdatePost.this, HomeActivity.class);
                intent.putExtra("from", "outside");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        bucketLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                chooseBucketTV.setText(txt.getText().toString());
                bucket_name = chooseBucketTV.getText().toString();


                if (bucket_name.equalsIgnoreCase("H1B")) {
                    bucket_id = Constants.H1B;
                }

                if (bucket_name.equalsIgnoreCase("PERM")) {
                    bucket_id = Constants.PERM;
                }
                if (bucket_name.equalsIgnoreCase("I-140")) {
                    bucket_id = Constants.I140;
                }

                if (bucket_name.equalsIgnoreCase("I-131(AP)")) {
                    bucket_id = Constants.I131;
                }

                if (bucket_name.equalsIgnoreCase("I-485")) {
                    bucket_id = Constants.I485;
                }
                if (bucket_name.equalsIgnoreCase("I-765(EAD)")) {
                    bucket_id = Constants.I765;
                }
                if (bucket_name.equalsIgnoreCase("MISC")) {
                    bucket_id = Constants.MISC;
                }


                //Toast.makeText(AddProductActivity.this, prodTypestring, Toast.LENGTH_LONG).show();
                bucketdialog.dismiss();

            }
        });

        bucketLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bucketdialog.setView(bucketLV);
                bucketdialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdatePost.this, HomeActivity.class);
                intent.putExtra("from", "outside");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        // external permission code
        mayRequestPermissions();

        uploadLLO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(UpdatePost.this, R.style.CustomDialog);
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
        });


        //be continue

        // post button
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideKeyboard(UpdatePost.this);

                submitform();
            }
        });


        postDetail();


    }


    private boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) + checkSelfPermission(READ_EXTERNAL_STORAGE) +
                checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.permission_rationale);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage("Please confirm access to files & folders");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                                    WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA,
                            },
                            PERMISSION_REQUEST);
                }
            });
            builder.show();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA},
                    PERMISSION_REQUEST);
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FROM_GALLERY:
                    ////Log.e("data", "" + data);
                    new ImageCompressionAsyncTask().execute(data.getDataString());
                    ////Log.e("data.getDataString()", "" + data.getDataString());

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

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

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


            uploadImg1.setImageBitmap(scaledBitmap);

        }

    }


    // start validation
    public void submitform() {


        if (!validateBucket()) {
            return;
        }

        if (!validateTitle()) {
            return;
        }


        if (!validateDescrip()) {
            return;
        } else {
            PostPoll task = new PostPoll();
            task.execute();
        }

    }


    private boolean validateDescrip()
    {
        descriptionS = descripET.getText().toString();
        String validationMSG = UIValidation.nameValidate(descriptionS, true, "Description is required");
        if (!validationMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            openValidateDialog(validationMSG);
            descripET.requestFocus();
            return false;
        } else {
        }

        return true;
    }

    // title
    private boolean validateTitle()
    {

        titleS = title_ET.getText().toString();
        String validationMSG = UIValidation.nameValidate(titleS, true, "Title is required");
        if (!validationMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            openValidateDialog(validationMSG);

            title_ET.requestFocus();
            return false;
        } else {
        }
        return true;
    }

    private boolean validateBucket() {

        if (chooseBucketTV.getText().toString().isEmpty()) {
            openValidateDialog("Select Bucket");

            chooseBucketTV.requestFocus();
            return false;
        } else {
        }
        return true;

    }

    private void openValidateDialog(String msg) {
        final Dialog dialog = new Dialog(UpdatePost.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msgTV = dialog.findViewById(R.id.msg);
        ImageView image = dialog.findViewById(R.id.image);

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

    class PostPoll extends AsyncTask<String, Void, String>
    {


        @Override
        protected void onPreExecute() {

            pd.setMessage("Loading..");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {
                if (scaledBitmap != null) {
                    String str = BitmapUtil.getStringFromBitmap(scaledBitmap);
                    profilePicbyte = android.util.Base64.decode(str, android.util.Base64.NO_WRAP);
                }

                //user_id, & post_type, & bucket, & title, & dicription, & image  &ans_selection_typ  & anser[]

                JSONObject ob = new JSONObject();
                ob.put("post_id", postId);
                ob.put("bucket", bucket_id);
                ob.put("title", title_ET.getText().toString());
                ob.put("dicription", toBase64(descripET.getText().toString()));


                //Log.e("JSON", ob.toString());
                String result = comm.forUpdatePost(Endpoints.UPDATE_POST, ob, profilePicbyte);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            //Log.e("Upload Response ", s);
            pd.cancel();
            try {
                if (s != null) {

                    //{"Status":200,"Message":"success","Data":"Data Success Fully Submit"}
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("Status");
                    String message = obj.getString("Message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        String data = obj.getString("Data");
                        if (data.equalsIgnoreCase("Data Success Fully Submit")) {
                            Intent in = new Intent(UpdatePost.this, HomeActivity.class);
                            in.putExtra("from", "compose");
                            in.putExtra("bucket_id", bucket_id);
                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(in);
                            overridePendingTransition(0, 0);
                        }
                    }

                }
            } catch (Exception ex) {
            }
        }
    }


    private void postDetail() {
        pd.setMessage("Loading..");
        pd.setCancelable(true);
        pd.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.POST_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Log.e("response", response);
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("Success")) {
                                JSONObject dataObj = obj.getJSONObject("Data");
                                title_ET.setText(dataObj.getString("title"));
                                descripET.setText(fromBase64(dataObj.getString("dicription")));
                                String bucketId = dataObj.getString("bucket");
                                String image = dataObj.getString("post_image");
                                if (!image.isEmpty())
                                {
                                    Picasso.with(UpdatePost.this).load(image)
                                            .placeholder(R.color.gray)
                                            .error(R.color.gray).into(uploadImg1);
                                }


                                if (bucketId.equalsIgnoreCase(Constants.H1B)) {
                                    chooseBucketTV.setText("H1B");
                                    bucket_id = Constants.H1B;
//                                    bucket_name="H1B";
                                }

                                if (bucketId.equalsIgnoreCase(Constants.PERM)) {
                                    chooseBucketTV.setText("PERM");
                                    bucket_id = Constants.PERM;
//                                    bucket_name="PERM";

                                }

                                if (bucketId.equalsIgnoreCase(Constants.I140)) {
                                    chooseBucketTV.setText("I-140");
                                    bucket_id = Constants.I140;
//                                    bucket_name="H1B";

                                }
                                if (bucketId.equalsIgnoreCase(Constants.I131)) {
                                    chooseBucketTV.setText("I-131(AP)");
                                    bucket_id = Constants.I131;
//                                    bucket_name="H1B";

                                }
                                if (bucketId.equalsIgnoreCase(Constants.I485)) {
                                    chooseBucketTV.setText("I-485");
                                    bucket_id = Constants.I485;
//                                    bucket_name="H1B";

                                }
                                if (bucketId.equalsIgnoreCase(Constants.I765)) {
                                    chooseBucketTV.setText("I-765(EAD)");
                                    bucket_id = Constants.I765;

                                }
                                if (bucketId.equalsIgnoreCase(Constants.MISC)) {
                                    chooseBucketTV.setText("MISC");
                                    bucket_id = Constants.MISC;
//                                    bucket_name="H1B";

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
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("post_id", postId);
                //user_id & post_id
                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    private void openDialog() {
        final Dialog dialog = new Dialog(UpdatePost.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msg = dialog.findViewById(R.id.msg);
        msg.setText("Post Updated Successfully!");


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
    }


    public String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.DEFAULT);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(UpdatePost.this, HomeActivity.class);
        intent.putExtra("from", "outside");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
