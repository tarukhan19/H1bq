package com.dbvertex.company.h1bq;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Util.BitmapUtil;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.UIValidation;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ComposePost extends AppCompatActivity {

    ImageView edit_img;
    TextView titleTV;
    LinearLayout back_LL, bucketLL;
    Toolbar toolbar_main;
    Button post, cancel;
    RequestQueue requestQueue;
    Intent intent;
    ProgressDialog pd;
    private ExpandableHeightListView optionLV;
    private EditText option1ET, title_ET, descripET;
    ImageView addImg, checkedActive_img, UncheckedActive_img;
    private ArrayList<String> optionList;
    private OptionAdapter optionAdp;
    private LinearLayout ll, postLL, poleLL, postVisible, pollVisible, optLL;
    private TextView chooseBucketTV;
    private ImageView drop_downImg, uploadImg, uploadImg1, postactive, postunactive, poleactive, poleunactive;
    SessionManager session;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Bitmap scaledBitmap = null;
    Uri imageUri;
    byte[] profilePicbyte = null;
    public static final int PERMISSION_REQUEST = 100;

    ArrayAdapter<String> bucketArrayadapter;
    ArrayList<String> bucketNameArray;
    String bucket_name = "", post_Type, titleS, choice_Type, descriptionS, bucket_id;
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

    ImageLoadingUtils utils;
    private RelativeLayout uploadLLO;
    SimpleDateFormat sdf;
    String gmtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_post);

        requestQueue = Volley.newRequestQueue(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.blue));
        }


        session = new SessionManager(getApplicationContext());
        pd = new ProgressDialog(ComposePost.this, R.style.MyAlertDialogStyle);

        toolbar_main = findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        titleTV.setText("Compose Post");
        edit_img.setVisibility(View.GONE);
        intent = getIntent();
        optLL = findViewById(R.id.optLL);


        title_ET = findViewById(R.id.title_ET);
        descripET = findViewById(R.id.descripET);
        bucketLL = findViewById(R.id.bucketLL);
        chooseBucketTV = findViewById(R.id.chooseBucketTV);
        uploadImg = findViewById(R.id.uploadImg);
        uploadImg1 = findViewById(R.id.uploadImg1);
        UncheckedActive_img = findViewById(R.id.UncheckedActive_img);
        checkedActive_img = findViewById(R.id.checkedActive_img);
        uploadLLO = findViewById(R.id.uploadLLO);

        optionLV = findViewById(R.id.optionLV);
        addImg = findViewById(R.id.addImg);
        option1ET = findViewById(R.id.option1ET);
        optionList = new ArrayList<>();
        optionAdp = new OptionAdapter(this, optionList);
        optionLV.setAdapter(optionAdp);
        optionLV.setExpanded(true);

        postLL = findViewById(R.id.postLL);
        poleLL = findViewById(R.id.poleLL);

        postactive = findViewById(R.id.postactive);
        postunactive = findViewById(R.id.postunactive);

        poleactive = findViewById(R.id.poleactive);
        poleunactive = findViewById(R.id.poleunactive);

        postVisible = findViewById(R.id.postVisible);
        pollVisible = findViewById(R.id.pollVisible);
        utils = new ImageLoadingUtils(this);

        post = findViewById(R.id.post);
        cancel = findViewById(R.id.cancel);


        bucketbuilder = new AlertDialog.Builder(ComposePost.this);
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
                Intent intent = new Intent(ComposePost.this, HomeActivity.class);
                intent.putExtra("from", "outside");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                bucket_name = txt.getText().toString();


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
                Intent intent = new Intent(ComposePost.this, HomeActivity.class);
                intent.putExtra("from", "outside");
                // external permission code
                mayRequestPermissions();

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        uploadLLO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(ComposePost.this, R.style.CustomDialog);
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
                        intent.putExtra("put", "put");

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


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (session.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1"))
                {
                    HideKeyboard.hideKeyboard(ComposePost.this);
                    if (postactive.getVisibility() == View.VISIBLE) {
                        post_Type = Constants.POST_TYPE;
                    }
                    if (poleactive.getVisibility() == View.VISIBLE) {
                        post_Type = Constants.POLL_TYPE;
                    }
                    //multiple=2, single=1
                    if (checkedActive_img.getVisibility() == View.VISIBLE) {
                        choice_Type = Constants.MULTIPLE_SELECTION;
                    } else {
                        choice_Type = Constants.SINGLE_SELECTION;
                    }

                    if (!option1ET.getText().toString().isEmpty())
                    {
                        optionList.add(option1ET.getText().toString());
                        optionAdp.notifyDataSetChanged();
                        option1ET.setText("");
                    }

                    submitform();
                }

            }
        });


        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listoption = option1ET.getText().toString();
                if (option1ET.getText().toString().isEmpty()) {
                    openValidateDialog("Option is required");
                    option1ET.requestFocus();
                } else {
                    optionList.add(listoption);
                    optionAdp.notifyDataSetChanged();
                    option1ET.setText("");
                    if (optionList.size() > 9) {
                        optLL.setVisibility(View.GONE);

                    }
                }
            }
        });

        UncheckedActive_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UncheckedActive_img.setVisibility(View.GONE);
                checkedActive_img.setVisibility(View.VISIBLE);
            }
        });

        checkedActive_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedActive_img.setVisibility(View.GONE);
                UncheckedActive_img.setVisibility(View.VISIBLE);
            }
        });


        postLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title_ET.setText("");
                if (optionList.size() > 0)
                {
                    optionList.clear();
                    optionAdp.notifyDataSetChanged();

                }
                if (checkedActive_img.getVisibility() == View.VISIBLE)
                {
                    checkedActive_img.setVisibility(View.GONE);
                    UncheckedActive_img.setVisibility(View.VISIBLE);

                }

                poleunactive.setVisibility(View.VISIBLE);
                poleactive.setVisibility(View.GONE);
                pollVisible.setVisibility(View.GONE);
                postVisible.setVisibility(View.VISIBLE);
                postactive.setVisibility(View.VISIBLE);
                postunactive.setVisibility(View.GONE);

            }
        });

        poleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title_ET.setText("");
                descripET.setText("");
                uploadImg1.setImageBitmap(null);


                poleunactive.setVisibility(View.GONE);
                poleactive.setVisibility(View.VISIBLE);
                pollVisible.setVisibility(View.VISIBLE);
                postVisible.setVisibility(View.GONE);
                postactive.setVisibility(View.GONE);
                postunactive.setVisibility(View.VISIBLE);
            }
        });

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

    // start code net

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

        if (postactive.getVisibility() == View.VISIBLE) {
            if (!validateDescrip()) {
                return;
            } else {



                Date myDate = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                calendar.setTime(myDate);
                Date time = calendar.getTime();
                SimpleDateFormat outputFmt = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                gmtTime = outputFmt.format(time);
                //Log.e("gmtTime",gmtTime);
                /// Sun Sep 2 08:56:48 +0000 2018
                PostPoll task = new PostPoll();
                task.execute();
            }
        }

        if (poleactive.getVisibility() == View.VISIBLE) {
            if (!validateoption()) {
                return;
            } else {
                Date myDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                calendar.setTime(myDate);
                Date time = calendar.getTime();
                SimpleDateFormat outputFmt = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                gmtTime = outputFmt.format(time);
                //Log.e("gmtTime",gmtTime);

                PostPoll task = new PostPoll();
                task.execute();
            }
        }


    }

    // Profile Upload
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
        protected String doInBackground(String... strings)
        {


            Endpoints comm = new Endpoints();

            try {
                if (scaledBitmap != null) {
                    String str = BitmapUtil.getStringFromBitmap(scaledBitmap);
                    profilePicbyte = android.util.Base64.decode(str, android.util.Base64.NO_WRAP);
                }

                //user_id, & post_type, & bucket, & title, & dicription, & image  &ans_selection_typ  & anser[]

                JSONObject ob = new JSONObject();
                ob.put("user_id", session.getuserId().get(SessionManager.KEY_USERID));
                ob.put("post_type", post_Type);
                ob.put("bucket", bucket_id);
                ob.put("title", (title_ET.getText().toString()));
                ob.put("dicription", toBase64(descripET.getText().toString()));
                ob.put("ans_selection_typ", choice_Type);
                ob.put("add_date", gmtTime);

                Log.e("params",ob.toString());

                String result = comm.forPost(Endpoints.CREATE_POSTPOLL, ob, optionList, profilePicbyte);
                Log.e("JSON", result);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Upload Response ", s);
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
                            //  openDialog();
                            Intent in = new Intent(ComposePost.this, HomeActivity.class);
                            in.putExtra("from", "compose");
                            in.putExtra("bucket_id",bucket_id);
                            Log.e("bucketid",bucket_id+"");

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


    private void openDialog() {
        final Dialog dialog = new Dialog(ComposePost.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msg = dialog.findViewById(R.id.msg);
        msg.setText("Post Successfully!");


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
    }


    private boolean validateDescrip() {

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
    private boolean validateTitle() {

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

        if (bucket_name.isEmpty()) {
            openValidateDialog("Select Bucket");

            chooseBucketTV.requestFocus();
            return false;
        } else {
        }
        return true;

    }

    // option validation
    private boolean validateoption() {

        if (optionList.size() < 2) {
            openValidateDialog("Min 2 Options are required");

            option1ET.requestFocus();
            return false;
        } else {

        }
        return true;
    }


    public class OptionAdapter extends BaseAdapter
    {

        private Context ctx;
        private ArrayList<String> optionList;

        public OptionAdapter(Context ctx, ArrayList<String> optionList) {
            this.ctx = ctx;
            this.optionList = optionList;
        }

        @Override
        public int getCount() {
            return optionList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View itemView = LayoutInflater.from(ctx).inflate(R.layout.item_option_cross, null, false);
            TextView optionText = (TextView) itemView.findViewById(R.id.option1ET);
            ImageView crossImg = (ImageView) itemView.findViewById(R.id.crossImg);
            String url = optionList.get(position);
            optionText.setText(url);
            crossImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    optLL.setVisibility(View.VISIBLE);
                    optionList.remove(position);
                    notifyDataSetChanged();
                }
            });

            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ComposePost.this, HomeActivity.class);
        intent.putExtra("from", "outside");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    private void openValidateDialog(String msg) {
        final Dialog dialog = new Dialog(ComposePost.this, R.style.CustomDialog);
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




}
