package com.dbvertex.company.h1bq;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.databinding.DataBindingUtil;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Adapter.MainCommentOutsideAdapter;
import com.dbvertex.company.h1bq.Adapter.OptionSelectAdp;
import com.dbvertex.company.h1bq.Adapter.ResultPopUpADP;
import com.dbvertex.company.h1bq.UserAuth.Login;
import com.dbvertex.company.h1bq.Util.BitmapUtil;
import com.dbvertex.company.h1bq.Util.DynamiclinkCreate;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.TimeConversion;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.databinding.ActivityProductDetailBinding;
import com.dbvertex.company.h1bq.model.CommentDTO;
import com.dbvertex.company.h1bq.model.NestedCommentDTO;
import com.dbvertex.company.h1bq.model.Poll_OptionData;
import com.dbvertex.company.h1bq.model.ResultPopUpDTO;
import com.dbvertex.company.h1bq.session.SessionManager;

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductDetailActivity extends AppCompatActivity {
    Uri data;
    ProgressDialog progressDialog;
    private TimeConversion timeConversion;
    ActivityProductDetailBinding productDetailBinding;
    String detailDescription, postimage, postid, url, userId, fromAdp, detaillikestatus, detailbookmarkstatus, msgComment, gmtTime, detailTitle, detailreportAbuseStatus, postType;
    private RequestQueue requestQueue;
    SessionManager sessionManager;
    LinearLayout back_LL;
    CommentDTO commentDTO;
    private MainCommentOutsideAdapter mainCommentAdapter, maincommADP;
    private List<CommentDTO> commentDTOList;
    Uri imageUri;
    byte[] profilePicbyte = null;
    public static final int PERMISSION_REQUEST = 100;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Dialog dialog;
    ImageLoadingUtils utils;
    int offset, main_count_comment;
    Bitmap scaledBitmap = null;
    boolean data_status = false;
    Vibrator vibe;
    private Apis apis;
    Intent intent;
    String from;
    TextView titletoolTV;
    Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE);
    private FullSreenImageDialog fullSreenImageDialog = new FullSreenImageDialog();
    private JSONArray resultPopUpArray;
    private List<Poll_OptionData> mPollOptionData;
    private ArrayList<String> selectAnsId, selectAns, ansIdlist;
    private OptionSelectAdp optionSelectAdp;
    Uri deepLink = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);

        productDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);
        sessionManager = new SessionManager(getApplicationContext());
        commentDTOList = new ArrayList<>();
        apis = new Apis();
        mPollOptionData = new ArrayList<>();
        selectAnsId = new ArrayList<>();
        selectAns = new ArrayList<>();
        ansIdlist = new ArrayList<>();
        intent = getIntent();
        from = intent.getStringExtra("from");

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        productDetailBinding.recyclerviewComment.setHasFixedSize(false);
        productDetailBinding.recyclerviewComment.setNestedScrollingEnabled(false);
        productDetailBinding.recyclerviewComment.setLayoutManager(linearLayoutManager);


        productDetailBinding.recyclerviewOption.setHasFixedSize(true);
        productDetailBinding.recyclerviewOption.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this, 2));
        productDetailBinding.recyclerviewOption.setNestedScrollingEnabled(false);
        utils = new ImageLoadingUtils(this);


        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(ProductDetailActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
            finish();
        } else {


            requestQueue = Volley.newRequestQueue(this);
            timeConversion = new TimeConversion();
            progressDialog = new ProgressDialog(ProductDetailActivity.this);


            Toolbar toolbar_main = findViewById(R.id.toolbar_main);
            titletoolTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
            back_LL = toolbar_main.findViewById(R.id.back_LL);
            ImageView edit_img = toolbar_main.findViewById(R.id.edit_img);
            edit_img.setVisibility(View.GONE);

            productDetailBinding.mainlinear.setVisibility(View.GONE);

            back_LL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sessionManager.isLoggedIn()) {
                        Intent intent1 = new Intent(ProductDetailActivity.this, HomeActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent1.putExtra("from", "outside");
                        startActivity(intent1);
                        finish();

                    }
                }
            });
            productDetailBinding.postcardImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    intent.putExtra("image", postimage);
                    fullSreenImageDialog.show(getFragmentManager(), "1");
                }
            });

            productDetailBinding.viewresult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showResultPopup();
                }
            });
            productDetailBinding.loadcomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productDetailBinding.loadcomment.setText("Loading...");
                    offset = offset + 1;
                    loadComment(offset);
                }
            });

            productDetailBinding.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fromAdp = "activity";
                    opendialog();
                }
            });

            productDetailBinding.cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productDetailBinding.imageRl.setVisibility(View.INVISIBLE);
                    productDetailBinding.commentImg.setImageBitmap(null);
                    scaledBitmap = null;
                    productDetailBinding.postcomment.setImageResource(R.drawable.post_comment);


                }
            });


            if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1")) {

                productDetailBinding.descripTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData cData = ClipData.newPlainText("text", productDetailBinding.descripTV.getText());
                        cManager.setPrimaryClip(cData);
                        Toast.makeText(ProductDetailActivity.this, "Description Copied", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });


                productDetailBinding.submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date myDate = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                        calendar.setTime(myDate);
                        Date time = calendar.getTime();
                        SimpleDateFormat outputFmt = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                        gmtTime = outputFmt.format(time);

                        AnswerTask task = new AnswerTask();
                        task.execute();
                    }
                });
                productDetailBinding.likeLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibe.vibrate(50);


                        if (detaillikestatus.equalsIgnoreCase("0")) {
                            int c = Integer.parseInt(productDetailBinding.likeCountTV.getText().toString()) + 1;
                            productDetailBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                            productDetailBinding.likeCountTV.setText("" + c);
                            detaillikestatus = "1";
                            apis.likeAdd(postid, ProductDetailActivity.this, requestQueue);
                        } else {

                            int c = Integer.parseInt(productDetailBinding.likeCountTV.getText().toString()) - 1;
                            productDetailBinding.likeImg.setImageResource(R.drawable.like_dash);
                            productDetailBinding.likeCountTV.setText("" + c);
                            detaillikestatus = "0";
                            apis.likeAdd(postid, ProductDetailActivity.this, requestQueue);
                        }
                    }
                });

                productDetailBinding.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibe.vibrate(50);

                        if (detailbookmarkstatus.equalsIgnoreCase("0")) {
                            productDetailBinding.favorite.setImageResource(R.drawable.favorite_feel);
                            detailbookmarkstatus = "1";
                            apis.bookmarkAdd(postid, 0, ProductDetailActivity.this, requestQueue);
                        } else if (detailbookmarkstatus.equalsIgnoreCase("1")) {
                            productDetailBinding.favorite.setImageResource(R.drawable.favorite);
                            detailbookmarkstatus = "0";
                            apis.bookmarkAdd(postid, 0, ProductDetailActivity.this, requestQueue);
                        }


                    }
                });

                productDetailBinding.moreoption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        openPopUpMenu(0, productDetailBinding.moreoption, postid,
                                detailreportAbuseStatus, userId,
                                postType);
                    }
                });

                productDetailBinding.commentET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        productDetailBinding.postcomment.setImageResource(R.drawable.post_comment);
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() == 0)
                            productDetailBinding.postcomment.setImageResource(R.drawable.post_comment);
                        else
                            productDetailBinding.postcomment.setImageResource(R.mipmap.post_comment_blue);

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


                productDetailBinding.sendLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!productDetailBinding.commentET.getText().toString().isEmpty() || scaledBitmap != null) {

                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            msgComment = productDetailBinding.commentET.getText().toString();
                            productDetailBinding.commentET.setText("");


                            Date myDate = new Date();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                            calendar.setTime(myDate);
                            Date time = calendar.getTime();
                            SimpleDateFormat outputFmt = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                            gmtTime = outputFmt.format(time);

                            SendCommentTask task = new SendCommentTask();
                            task.execute();


                        } else {
                            Toast.makeText(ProductDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            productDetailBinding.shareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        if (postType.equalsIgnoreCase(Constants.POST_TYPE)) {
                            if (!postimage.isEmpty()) {
                                DynamiclinkCreate.shareImage
                                        (postimage, ProductDetailActivity.this, detailTitle, postid);
                                //sAux = sAux + postimage+"\n\n";
                            } else {
                                DynamiclinkCreate.shareImage
                                        (postimage, ProductDetailActivity.this, detailTitle, postid);
                            }
                        } else {
                            DynamiclinkCreate.shareImage
                                    (postimage, ProductDetailActivity.this, detailTitle, postid);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            if (intent.hasExtra("from")) {
                postid = intent.getStringExtra("post_id");

                url = Endpoints.POST_DETAIL + "?post_id=" + postid + "&user_id=" + sessionManager.getLoginSession().get(SessionManager.KEY_USERID);
                detailPost();
            } else {
                // [START get_deep_link]
                FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(getIntent())
                        .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                if (pendingDynamicLinkData != null) {
                                    deepLink = pendingDynamicLinkData.getLink();
                                }

                                if (deepLink != null) {
                                    url = deepLink.toString() + "&user_id=" + sessionManager.getLoginSession().get(SessionManager.KEY_USERID);
                                    detailPost();

                                } else {
                                    Log.d("deeplink", "getDynamicLink: no link found");
                                }
                                // [END_EXCLUDE]
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("deeplink", "getDynamicLink:onFailure", e);
                            }
                        });
                // [END get_deep_link]
            }


        }


    }


    private void detailPost() {
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        selectAnsId.clear();
                        selectAns.clear();
                        ansIdlist.clear();
                        mPollOptionData.clear();
                        try {
                            productDetailBinding.mainlinear.setVisibility(View.VISIBLE);

                            JSONObject obj = new JSONObject(String.valueOf(response));
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("Success")) {
                                JSONObject dataObj = obj.getJSONObject("Data");
                                Log.e("dataObj", String.valueOf(dataObj));
                                postimage = dataObj.getString("post_image");
                                postid = dataObj.getString("post_id");
                                userId = dataObj.getString("user_id");
                                detaillikestatus = dataObj.getString("like_status");
                                detailbookmarkstatus = dataObj.getString("bookmark_status");
                                detailreportAbuseStatus = dataObj.getString("report_abuse_status");
                                postType = dataObj.getString("post_type");

                                if (postType.equalsIgnoreCase(Constants.POST_TYPE)) {
                                    productDetailBinding.pollcardLL.setVisibility(View.GONE);
                                    productDetailBinding.postcardLL.setVisibility(View.VISIBLE);
                                    titletoolTV.setText("Post Detail");
                                    //  edit_img.setVisibility(View.GONE);
                                } else {
                                    productDetailBinding.pollcardLL.setVisibility(View.VISIBLE);
                                    productDetailBinding.postcardLL.setVisibility(View.GONE);
                                    titletoolTV.setText("Poll Detail");
                                    //edit_img.setVisibility(View.GONE);
                                }


                                main_count_comment = dataObj.getInt("count_comment");

                                try {
                                    productDetailBinding.titleDetailTV.setText((dataObj.getString("title")));
                                    detailTitle = dataObj.getString("title");

                                } catch (IllegalArgumentException e) {
                                }

                                if (!postimage.isEmpty()) {
                                    productDetailBinding.postcardImg.setVisibility(View.VISIBLE);
                                    Picasso.with(ProductDetailActivity.this).load(postimage)
                                            .placeholder(R.color.gray)
                                            .error(R.color.gray).into(productDetailBinding.postcardImg);
                                } else {
                                    productDetailBinding.postcardImg.setVisibility(View.GONE);
                                }
                                String datecount = dataObj.getString("add_date");
                                SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                                Date date = null, sendDate = null;
                                String d2 = "";
                                try {
                                    date = (Date) sdf.parse(datecount);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                simpleDateFormat.setTimeZone(TimeZone.getDefault());


                                try {
                                    d2 = simpleDateFormat.format(date);
                                    sendDate = simpleDateFormat.parse(d2);
                                    //Log.e("sendDate",sendDate+" "+sendDate.toLocaleString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                timeConversion.getTimeAgo(0, productDetailBinding.daysCount, ProductDetailActivity.this, sendDate);


                                String username = dataObj.getString("username");
                                if (username.isEmpty()) {
                                    productDetailBinding.userNameTV.setText("H1BQ User");
                                    productDetailBinding.polluserNameTV.setText("H1BQ User");

                                } else {
                                    productDetailBinding.userNameTV.setText(username);
                                    productDetailBinding.polluserNameTV.setText(username);
                                }

                                productDetailBinding.likeCountTV.setText(dataObj.getString("count_like"));
                                productDetailBinding.comCountTV.setText(dataObj.getString("count_comment"));
//                                main_count_comment = dataObj.getInt("count_comment");

                                productDetailBinding.viewCountTV.setText(dataObj.getString("count_view"));
                                String optionselectType = dataObj.getString("ans_selection_typ");

                                if (dataObj.getInt("count_comment") < 1) {
                                    productDetailBinding.comment.setText("No Comments");
                                    productDetailBinding.comDashImg.setImageResource(R.drawable.comment_dash);


                                } else if (dataObj.getInt("count_comment") == 1) {
                                    productDetailBinding.comment.setText("Comment");
                                    productDetailBinding.comDashImg.setImageResource(R.drawable.comment_dash_blue);


                                } else {
                                    productDetailBinding.comment.setText("Comments");
                                    productDetailBinding.comDashImg.setImageResource(R.drawable.comment_dash_blue);


                                }
                                if (dataObj.getString("post_type").equalsIgnoreCase(Constants.POST_TYPE)) {
                                    try {
                                        detailDescription = fromBase64(dataObj.getString("dicription"));

                                    } catch (IllegalArgumentException e) {
                                    } catch (NullPointerException e) {
                                    }


                                    final SpannableStringBuilder sb = new SpannableStringBuilder(detailDescription);
                                    Matcher matcher = p.matcher(detailDescription);

                                    while (matcher.find()) {
                                        final String s = matcher.group();

                                        Log.e("matchertext", s);

                                        final ClickableSpan clickableSpan = new ClickableSpan() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                                                startActivity(browserIntent);
                                                // listen click here
                                            }

                                            @Override
                                            public void updateDrawState(TextPaint ds) {
                                                super.updateDrawState(ds);
                                                ds.setUnderlineText(false);
                                                ds.setColor(Color.BLUE);
                                                //click=String.valueOf(ds);

                                            }
                                        };

                                        sb.setSpan(clickableSpan, matcher.start(), matcher.end(),
                                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                                    }
                                    productDetailBinding.descripTV.setText(sb);
                                    productDetailBinding.descripTV.setMovementMethod(LinkMovementMethod.getInstance());


                                } else {
                                    JSONArray ansArray = dataObj.getJSONArray("answer");
                                    JSONArray resultArray = dataObj.getJSONArray("count_percentage");
                                    resultPopUpArray = resultArray;
                                    JSONArray submitAnsArray = dataObj.getJSONArray("user_submit_answer");


                                    if (submitAnsArray.length() == 0) {
                                        productDetailBinding.submitll.setVisibility(View.VISIBLE);
                                    } else {
                                        productDetailBinding.submitll.setVisibility(View.GONE);
                                    }


                                    for (int j = 0; j < ansArray.length(); j++) {
                                        JSONObject jsonObject = ansArray.getJSONObject(j);
                                        Poll_OptionData pollOptionData = new Poll_OptionData();
                                        String ans_id = jsonObject.getString("id");
                                        String ans = jsonObject.getString("anser");
                                        pollOptionData.setOptionTV(ans);
                                        pollOptionData.setOptionId(ans_id);
                                        mPollOptionData.add(pollOptionData);

                                        for (int k = 0; k < submitAnsArray.length(); k++) {
                                            JSONObject submitAnsObject = submitAnsArray.getJSONObject(k);
                                            String select_ans_id = submitAnsObject.getString("answer_id");

                                            if (select_ans_id.equalsIgnoreCase(ans_id)) {
                                                if (optionselectType.equalsIgnoreCase(Constants.MULTIPLE_SELECTION))
                                                {
                                                    pollOptionData.setSelected(!pollOptionData.isSelected());
                                                    selectAnsId.add(select_ans_id);
                                                    selectAns.add(ans);
                                                } else {
                                                    pollOptionData.setChecked(!pollOptionData.isChecked());
                                                    selectAnsId.add(select_ans_id);
                                                    selectAns.add(ans);
                                                }


                                            }


                                        }

                                    }
                                    optionSelectAdp = new OptionSelectAdp(ProductDetailActivity.this, mPollOptionData, optionselectType, ansIdlist, selectAnsId);
                                    productDetailBinding.recyclerviewOption.setAdapter(optionSelectAdp);


                                }

                                if (dataObj.getString("like_status").equalsIgnoreCase("0")) {

                                    productDetailBinding.likeImg.setImageResource(R.drawable.like_dash);
                                } else {

                                    productDetailBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                                }

                                if (dataObj.getString("bookmark_status").equalsIgnoreCase("0")) {

                                    productDetailBinding.favorite.setImageResource(R.drawable.favorite);
                                } else {
                                    productDetailBinding.favorite.setImageResource(R.drawable.favorite_feel);
                                }

                                mainCommentAdapter = new MainCommentOutsideAdapter(ProductDetailActivity.this, commentDTOList, "dialog", postid, userId);
                                productDetailBinding.recyclerviewComment.setAdapter(mainCommentAdapter);

                                commentDTOList.clear();
                                offset = 0;
                                loadComment(offset);
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
                        progressDialog.dismiss();
                    }
                }
        ) {

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    private void loadComment(final int offset) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LOAD_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("loadcommentparams", response + "");

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String msg = obj.getString("Message");
                            if (status == 200 && msg.equalsIgnoreCase("success")) {
                                data_status = obj.getBoolean("data_status");

                                Log.e("datastatus", data_status + "");

                                JSONArray dataArray = obj.getJSONArray("Data");

                                if (dataArray.length() < 10) {
                                    productDetailBinding.loadcomment.setVisibility(View.GONE);

                                } else {
                                    productDetailBinding.loadcomment.setText("Load more comments");
                                    productDetailBinding.loadcomment.setVisibility(View.VISIBLE);

                                }


                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataJSONObject = dataArray.getJSONObject(i);
                                    commentDTO = new CommentDTO();
                                    commentDTO.setCommentId(dataJSONObject.getString("comment_id"));
                                    commentDTO.setUserId(dataJSONObject.getString("user_id"));
                                    commentDTO.setUserName(dataJSONObject.getString("user_name"));
                                    commentDTO.setCommentMsg(dataJSONObject.getString("message"));
                                    commentDTO.setLikeCount(dataJSONObject.getString("count_like"));
                                    commentDTO.setLikestatus(dataJSONObject.getString("like_status"));
                                    commentDTO.setCommentCount(dataJSONObject.getString("count_comment"));
                                    commentDTO.setImage(dataJSONObject.getString("image"));
                                    commentDTO.setTimeCount(dataJSONObject.getString("date"));

                                    List<NestedCommentDTO> nestedCommentDTOArrayList = new ArrayList<>();
                                    JSONArray nestedcommArray = dataJSONObject.getJSONArray("nested_comment");
                                    int n = 0;
                                    if (nestedcommArray.length() >= 3) {
                                        n = 3;
                                    } else {
                                        n = nestedcommArray.length();
                                    }

                                    for (int j = 0; j < n; j++) {
                                        JSONObject dataObject = nestedcommArray.getJSONObject(j);
                                        NestedCommentDTO nestedCommentDTO = new NestedCommentDTO();
                                        nestedCommentDTO.setCommentId(dataObject.getString("comment_id"));
                                        nestedCommentDTO.setNestedCommentId(dataObject.getString("nested_comment_id"));
                                        nestedCommentDTO.setUserId(dataObject.getString("user_id"));
                                        nestedCommentDTO.setUserName(dataObject.getString("user_name"));
                                        nestedCommentDTO.setCommentMsg(dataObject.getString("message"));
                                        nestedCommentDTO.setLikeCount(dataObject.getString("count_like"));
                                        nestedCommentDTO.setLikestatus(dataObject.getString("like_status"));
                                        nestedCommentDTO.setMentionusername(dataObject.getString("nested_username"));
                                        nestedCommentDTO.setCommentCount(dataObject.getString("count_comment"));

                                        nestedCommentDTO.setImage(dataObject.getString("image"));
                                        nestedCommentDTO.setTimeCount(dataObject.getString("date"));
                                        nestedCommentDTOArrayList.add(nestedCommentDTO);

                                    }
                                    commentDTO.setNestedCommentDTOS(nestedCommentDTOArrayList);
                                    commentDTOList.add(commentDTO);
                                    mainCommentAdapter.notifyDataSetChanged();

                                }

                            } else {
                                productDetailBinding.loadcomment.setVisibility(View.GONE);

                            }

                        } catch (Exception ex) {
                            //Log.e("Exception", ex + "");
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
                params.put("post_id", postid);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                params.put("offset", offset + "");
                //Log.e("loadcommentparams",params+"");


                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (sessionManager.isLoggedIn()) {
            Intent intent1 = new Intent(ProductDetailActivity.this, HomeActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent1.putExtra("from", "outside");
            startActivity(intent1);

        }
    }


    public void openMainCommGallery(Context mContext, MainCommentOutsideAdapter adp) {
        this.maincommADP = adp;
        fromAdp = "commentAdapter";
        opendialog();

    }


    public void opendialog() {

        dialog = new Dialog(ProductDetailActivity.this, R.style.CustomDialog);
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

            if (fromAdp.equalsIgnoreCase("commentAdapter")) {
                maincommADP.setImage(scaledBitmap);
            } else {
                productDetailBinding.imageRl.setVisibility(View.VISIBLE);
                productDetailBinding.commentImg.setImageBitmap(scaledBitmap);
                productDetailBinding.postcomment.setImageResource(R.mipmap.post_comment_blue);

            }

//            }

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


    class SendCommentTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading ...");
            progressDialog.setCancelable(true);
            progressDialog.show();
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

                JSONObject params = new JSONObject();
                params.put("post_id", postid);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                params.put("comment_type", "1");
                params.put("message", toBase64(msgComment));
                params.put("add_date", gmtTime);


                Log.e("commentresponse", params + "");
                String result = comm.forComment(Endpoints.ADD_COMMENT, params, profilePicbyte);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("comment Response ", s);
            progressDialog.cancel();
            try {
                if (s != null) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        int status = obj.getInt("Status");
                        String msg = obj.getString("Message");
                        List<NestedCommentDTO> nestedCommentDTOArrayList = new ArrayList<>();
                        int count = main_count_comment + 1;


                        productDetailBinding.imageRl.setVisibility(View.INVISIBLE);
                        productDetailBinding.commentImg.setImageBitmap(null);
                        scaledBitmap = null;
                        profilePicbyte = null;

                        if (productDetailBinding.comment.getText().toString().equalsIgnoreCase("No Comments")) {
                            productDetailBinding.comment.setText("Comment");
                        } else if (productDetailBinding.comment.getText().toString().equalsIgnoreCase("Comment")) {
                            productDetailBinding.comment.setText("Comments");
                        }


                        if (status == 200 && msg.equalsIgnoreCase("success")) {
                            JSONObject object = obj.getJSONObject("Data");
                            CommentDTO cmt = new CommentDTO();

                            if (!data_status) {
                                cmt.setCommentId(object.getString("id"));
                                cmt.setUserId(object.getString("user_id"));
                                cmt.setUserName(object.getString("username"));
                                cmt.setCommentMsg(object.getString("message"));
                                cmt.setTimeCount(object.getString("add_date"));
                                cmt.setLikestatus("0");
                                cmt.setLikeCount("0");
                                cmt.setCommentCount("0");
                                cmt.setImage(object.getString("image"));
                                cmt.setNestedCommentDTOS(nestedCommentDTOArrayList);
                                commentDTOList.add(cmt);
                                productDetailBinding.comCountTV.setText(String.valueOf(count));

                                mainCommentAdapter.notifyItemInserted(productDetailBinding.recyclerviewComment.getAdapter().getItemCount());
                                mainCommentAdapter.notifyDataSetChanged();
                            }

                        }

                    } catch (Exception ex) {
                    }
                }
            } catch (Exception ex) {
            }
        }
    }


    private void openPopUpMenu(final int position, ImageView moreoption, final String postId, final String reportabuseStatus, String userId, String postType) {

        if (userId.equalsIgnoreCase(sessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {
            if (postType.equalsIgnoreCase(Constants.POST_TYPE)) {
                //Log.e("DetailDialPos", position + "");

                PopupMenu popup = new PopupMenu(ProductDetailActivity.this, moreoption);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_updatedel);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                showDeleteDialog(postId, position);
                                return true;
                            case R.id.update:
                                Intent intent = new Intent(ProductDetailActivity.this, UpdatePost.class);
                                intent.putExtra("from", "outside");

                                intent.putExtra("postId", postId);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            } else {
                PopupMenu popup = new PopupMenu(ProductDetailActivity.this, moreoption);
                //inflating menu from xml resource
                popup.inflate(R.menu.menudel);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                showDeleteDialog(postId, position);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }

        } else {
            PopupMenu popup = new PopupMenu(ProductDetailActivity.this, moreoption);
            popup.inflate(R.menu.menu_reportabuse);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.reportabuse:
                            if (reportabuseStatus.equalsIgnoreCase("0")) {
                                openReportDialog(postId, position);
                            } else {
                                openDialog("You have already given your feedback for this post!");
                            }
                            return true;

                        default:
                            return false;
                    }
                }
            });
            //displaying the popup
            popup.show();
        }


    }


    private void showDeleteDialog(final String postId, final int adapterPosition) {

        final Dialog dialog = new Dialog(ProductDetailActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_logout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout ok = (LinearLayout) dialog.findViewById(R.id.ok);
        LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
        TextView msgTV = (TextView) dialog.findViewById(R.id.msgTV);
        msgTV.setText("You want to delete this post!");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deletePost(postId, adapterPosition);
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

    private void openReportDialog(final String postId, final int adapterPosition) {

        final Dialog dialog = new Dialog(ProductDetailActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_reportabuse);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();


        LinearLayout ok = dialog.findViewById(R.id.ok);
        LinearLayout cancel = dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final EditText descripationET = dialog.findViewById(R.id.descripationET);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descriptionS = descripationET.getText().toString();
                if (descriptionS.isEmpty()) {
                    openDialog("Please enter the reason for report abuse");
                } else {
                    reportAbuse(descriptionS, postId, adapterPosition);
                    dialog.dismiss();
                    HideKeyboard.hideKeyboard(ProductDetailActivity.this);
                }


            }
        });
    }


    private void openDialog(String s) {
        final Dialog dialog = new Dialog(ProductDetailActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msg = dialog.findViewById(R.id.msg);
        ImageView image = dialog.findViewById(R.id.image);

        msg.setText(s);
        image.setVisibility(View.GONE);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }

    public void deletePost(final String postId, final int position) {
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(true);
        progressDialog.show();
        //Log.e("DetailDialPos", position + "");

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.DELETE_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //    //Log.e("loadList",response+"");
                        progressDialog.dismiss();
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");


                            if (status == 200 && message.equals("success")) {
                                final Dialog dialog = new Dialog(ProductDetailActivity.this, R.style.CustomDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_contactus);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                dialog.show();

                                LinearLayout ok = dialog.findViewById(R.id.ok);
                                TextView msgTV = dialog.findViewById(R.id.msgTV);

                                msgTV.setText("Post Deleted Successfully");
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Log.e("DetailDialPos", position + "");

                                        Intent intent1 = new Intent(ProductDetailActivity.this, HomeActivity.class);
                                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent1.putExtra("from", "outside");
                                        startActivity(intent1);
                                        finish();
                                    }
                                });
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
                params.put("post_id", postId);
                //   //Log.e("params",params+"");

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    private void reportAbuse(final String descriptionS, final String postId, final int adapterPosition) {
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.REPORT_ABUSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");


                            if (status == 200 && message.equals("success")) {
                                // reportAbuseStatus = "1";
                                openDialog("Thank you for reporting us, we will look into this and take necessary action.");

                            } else if (status == 0 && message.equals("Failed")) {
                                openDialog(obj.getString("Data"));

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
                //user_id & post_id & message
                params.put("post_id", postId);
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("message", descriptionS);


                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private void showResultPopup() {
        final Dialog dialog = new Dialog(ProductDetailActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_resultpopup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ArrayList<ResultPopUpDTO> resultPopUpDTOS = new ArrayList<>();
        ResultPopUpADP resultPopUpADP = new ResultPopUpADP(ProductDetailActivity.this, resultPopUpDTOS);
        RecyclerView recyclerview_progress = (RecyclerView) dialog.findViewById(R.id.recyclerview_progress);
        LinearLayout close = dialog.findViewById(R.id.close);
        recyclerview_progress.setNestedScrollingEnabled(false);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailActivity.this);
        recyclerview_progress.setLayoutManager(linearLayoutManager);
        resultPopUpDTOS.clear();
        for (int i = 0; i < resultPopUpArray.length(); i++) {
            JSONObject jsonResultObject = null;
            try {
                jsonResultObject = resultPopUpArray.getJSONObject(i);
                ResultPopUpDTO resultPopUpDTO = new ResultPopUpDTO();
                resultPopUpDTO.setOptionTV(jsonResultObject.getString("anser"));
                resultPopUpDTO.setPercentageTV(jsonResultObject.getInt("percentage"));
                resultPopUpDTO.setDataTV(jsonResultObject.getString("count"));
                resultPopUpDTOS.add(resultPopUpDTO);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        recyclerview_progress.setAdapter(resultPopUpADP);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }


    class AnswerTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Loading ...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {
                JSONObject ob = new JSONObject();
                ob.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                ob.put("post_id", postid);
                ob.put("add_date", gmtTime);
                String result = comm.forAnsPost(Endpoints.SUBMIT_ANS, ob, ansIdlist);
                Log.e("SUBMIT_ANS", ob.toString() + "  " + ansIdlist);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("submitansResponse ", s);
            progressDialog.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    ansIdlist.clear();
                    optionSelectAdp.notifyDataSetChanged();
                    //{"Status":200,"Message":"success","Data":"Your vote submitted successfully"}
                    //{"Status":0,"Message":"failed","Data":"You have already given your vote"}
                    int status = obj.getInt("Status");
                    String message = obj.getString("Message");
                    if (status == 200 && message.equals("success")) {
                        openDialog(obj.getString("Data"));
                        productDetailBinding.submitll.setVisibility(View.GONE);
                        detailPost();
                    } else {
                        openDialog(obj.getString("Data"));
                    }
                }
            } catch (Exception ex) {
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        deepLink = null;
    }
}
