package com.dbvertex.company.h1bq.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import androidx.annotation.NonNull;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.dbvertex.company.h1bq.Apis;
import com.dbvertex.company.h1bq.FullSreenImageDialog;
import com.dbvertex.company.h1bq.ProductDetailActivity;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.Util.BitmapUtil;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.TimeConversion;
import com.dbvertex.company.h1bq.databinding.ItemCommentBinding;
import com.dbvertex.company.h1bq.databinding.ItemCommentdialBinding;
import com.dbvertex.company.h1bq.model.CommentDTO;
import com.dbvertex.company.h1bq.model.NestedCommentDTO;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainCommentOutsideAdapter extends RecyclerView.Adapter<MainCommentOutsideAdapter.ViewHolderProgressAdapter> {

    private Context mcontex;

    private List<CommentDTO> commentDTOList;
    private NestedCommentAdapter  nestedCommentDialogAdapter;

    private String from, likestatus, postId, userId, msgComment, commentid, singlecommentimage, gmtTime;
    private Bitmap scaledbitmap;
    private byte[] profilePicbyte = null;

    private SessionManager sessionManager;
    private TimeConversion timeConversion;
    private Apis apis;
    private ProductDetailActivity activity;
    int nestedPosition;

    private ItemCommentdialBinding itemSingleCommentBinding;
    private Dialog singlecommentDial;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;
    Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE);
    Vibrator vibe;


    public MainCommentOutsideAdapter(ProductDetailActivity mcontex, List<CommentDTO> commentDTOList, String from, String postId, String userId) {
        this.mcontex = mcontex;
        this.activity = mcontex;
        this.commentDTOList = commentDTOList;
        this.postId = postId;
        this.userId = userId;
        this.from = from;
        vibe = (Vibrator) mcontex.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @NonNull
    @Override
    public ViewHolderProgressAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ItemCommentBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_comment, parent, false);
        sessionManager = new SessionManager(mcontex.getApplicationContext());
        requestQueue = Volley.newRequestQueue(mcontex);
        timeConversion = new TimeConversion();
        apis = new Apis();
        progressDialog = new ProgressDialog(mcontex, R.style.MyAlertDialogStyle);
        singlecommentDial = new Dialog(mcontex, R.style.full_screen_dialog);
        itemSingleCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(singlecommentDial.getContext()), R.layout.item_commentdial, null, false);
        return new ViewHolderProgressAdapter(viewDataBinding);
    }

    public void setImage(Bitmap scaledBitmap) {
        Log.e("bitmap",scaledBitmap+"");
        itemSingleCommentBinding.imageRl.setVisibility(View.VISIBLE);
        itemSingleCommentBinding.commentImg.setImageBitmap(scaledBitmap);
        scaledbitmap = scaledBitmap;
        itemSingleCommentBinding.postcomment.setImageResource(R.mipmap.post_comment_blue);

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
    public void onBindViewHolder(@NonNull final ViewHolderProgressAdapter holder, final int position) {
//        nestedCommentAdapter = new NestedCommentAdapter(mcontex, commentDTOList.get(position).getNestedCommentDTOS(), from, userId,postId);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mcontex);
//        holder.itemDetailCommentBinding.nestCommRecycle.setHasFixedSize(false);
//        holder.itemDetailCommentBinding.nestCommRecycle.setNestedScrollingEnabled(false);
//        holder.itemDetailCommentBinding.nestCommRecycle.setLayoutManager(linearLayoutManager);
//        holder.itemDetailCommentBinding.nestCommRecycle.setAdapter(nestedCommentAdapter);

        if (commentDTOList.get(position).getNestedCommentDTOS().size()>0)
        {
            holder.itemDetailCommentBinding.viewrpl.setVisibility(View.VISIBLE);
            holder.itemDetailCommentBinding.commDashImg.setImageResource(R.drawable.comment_dash_blue);
        }
        else
        {
            holder.itemDetailCommentBinding.viewrpl.setVisibility(View.GONE);
            holder.itemDetailCommentBinding.commDashImg.setImageResource(R.drawable.comment_dash);

        }



        holder.itemDetailCommentBinding.likeCountTV.setText(commentDTOList.get(position).getLikeCount());
        holder.itemDetailCommentBinding.comCountTV.setText(commentDTOList.get(position).getCommentCount());

        if (commentDTOList.get(position).getImage().isEmpty()) {
            holder.itemDetailCommentBinding.commentimage.setVisibility(View.GONE);
        } else {
            holder.itemDetailCommentBinding.commentimage.setVisibility(View.VISIBLE);
            Picasso.with(mcontex).load(commentDTOList.get(position).getImage())
                    .placeholder(R.color.gray)
                    .error(R.color.gray).into(holder.itemDetailCommentBinding.commentimage);

        }

        if (commentDTOList.get(position).getCommentMsg().isEmpty()) {
            holder.itemDetailCommentBinding.messageTV.setVisibility(View.GONE);
        } else {
            holder.itemDetailCommentBinding.messageTV.setVisibility(View.VISIBLE);
            String msg="";
            try {
                msg=fromBase64(commentDTOList.get(position).getCommentMsg());

            }
            catch (IllegalArgumentException e){}
            final SpannableStringBuilder sb = new SpannableStringBuilder(msg);
            Matcher matcher = p.matcher(msg);

            while (matcher.find()) {
                final String s = matcher.group();

                Log.e("matchertext",s);

                final ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        mcontex.startActivity(browserIntent);
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
            holder.itemDetailCommentBinding.messageTV.setText(sb);
            holder.itemDetailCommentBinding.messageTV.setMovementMethod(LinkMovementMethod.getInstance());
        }

        String username = commentDTOList.get(position).getUserName();
        if (!username.isEmpty()) {
            holder.itemDetailCommentBinding.userNameTV.setText(username);

        } else {
            holder.itemDetailCommentBinding.userNameTV.setText("H1BQ User");

        }

        if (commentDTOList.get(position).getLikeStatus().equalsIgnoreCase("0")) {
            commentDTOList.get(position).setLikeSelected(false);
            holder.itemDetailCommentBinding.likeImg.setImageResource(R.drawable.like_dash);
        } else {
            commentDTOList.get(position).setLikeSelected(true);
            holder.itemDetailCommentBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
        }

        if (userId.equalsIgnoreCase(commentDTOList.get(position).getUserId())) {
            holder.itemDetailCommentBinding.opTV.setVisibility(View.VISIBLE);
        } else {
            holder.itemDetailCommentBinding.opTV.setVisibility(View.GONE);
        }



        String dateStr = commentDTOList.get(position).getTimeCount();
        if (!dateStr.equalsIgnoreCase(null))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = null, sendDate = null;
            String d2 = "";
            try {
                date = (Date) sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            catch (NullPointerException ex)
            {
                ex.printStackTrace();

            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            try {
                d2 = simpleDateFormat.format(date);
                sendDate = simpleDateFormat.parse(d2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            catch (NullPointerException ex)
            {
                ex.printStackTrace();

            }
            timeConversion.getTimeAgo(position, holder.itemDetailCommentBinding.dateTV, mcontex, sendDate);

        }

        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1"))
        {
            holder.itemDetailCommentBinding.likeLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibe.vibrate(50);

                    if (commentDTOList.get(position).getLikeStatus().equalsIgnoreCase("0")) {
                        commentDTOList.get(position).setLikeSelected(true);
                        int c = Integer.parseInt(commentDTOList.get(position).getLikeCount()) + 1;
                        holder.itemDetailCommentBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                        commentDTOList.get(position).setLikestatus("1");
                        commentDTOList.get(position).setLikeCount("" + c);
                        notifyItemChanged(position);
                        commentLikeAdd(commentDTOList.get(position).getCommentId(), postId, "1");
                    } else {
                        commentDTOList.get(position).setLikeSelected(false);
                        int c = Integer.parseInt(commentDTOList.get(position).getLikeCount()) - 1;
                        holder.itemDetailCommentBinding.likeImg.setImageResource(R.drawable.like_dash);
                        commentDTOList.get(position).setLikestatus("0");
                        commentDTOList.get(position).setLikeCount("" + c);
                        notifyItemChanged(position);
                        commentLikeAdd(commentDTOList.get(position).getCommentId(), postId, "1");

                    }

                }
            });

        }


        itemSingleCommentBinding.commentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mcontex.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                itemSingleCommentBinding.commentET.requestFocus();
            }
        });
        holder.itemDetailCommentBinding.commentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(postId, commentDTOList.get(position), holder.getAdapterPosition());

            }
        });

        holder.itemDetailCommentBinding.commentimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullSreenImageDialog fullSreenImageDialog = new FullSreenImageDialog();
                Intent intent = ((Activity) mcontex).getIntent();
                intent.putExtra("image", commentDTOList.get(position).getImage());
                fullSreenImageDialog.show(((Activity) mcontex).getFragmentManager(), "1");
            }
        });
        holder.itemDetailCommentBinding.viewrpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(postId, commentDTOList.get(position), holder.getAdapterPosition());

            }
        });

    }

    public void commentLikeAdd(final String CommentId, final String postId, final String commentype) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LIKE_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("commentresponse", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String msg = obj.getString("Message");
                            String data = obj.getString("Data");
                            if (status == 200 && msg.equalsIgnoreCase("success")) { }

                        } catch (Exception ex) {
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

                params.put("post_id", postId);
                params.put("comment_id", CommentId);
                params.put("like_cooment_type", commentype);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));

                //Log.e("params",params+"");
                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    private void openDialog(final String postId, final CommentDTO commentDTO, final int adapterPosition) {


        singlecommentDial.setContentView(itemSingleCommentBinding.getRoot());
        singlecommentDial.setCanceledOnTouchOutside(false);
        singlecommentDial.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        singlecommentDial.show();
        Toolbar toolbar_main = singlecommentDial.findViewById(R.id.toolbar_main);
        TextView titletoolTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        LinearLayout back_LL = toolbar_main.findViewById(R.id.back_LL);
        ImageView edit_img = toolbar_main.findViewById(R.id.edit_img);

        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singlecommentDial.dismiss();
            }
        });

        titletoolTV.setText("Replies");
        edit_img.setVisibility(View.GONE);

        nestedPosition=adapterPosition;
        nestedCommentDialogAdapter = new NestedCommentAdapter(mcontex, commentDTOList.get(adapterPosition).getNestedCommentDTOS(), "singlecomment", userId,postId);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mcontex);
        itemSingleCommentBinding.nestCommRecycle.setHasFixedSize(false);
        itemSingleCommentBinding.nestCommRecycle.setNestedScrollingEnabled(false);
        itemSingleCommentBinding.nestCommRecycle.setLayoutManager(linearLayoutManager);
        itemSingleCommentBinding.nestCommRecycle.setAdapter(nestedCommentDialogAdapter);
        itemSingleCommentBinding.nestCommRecycle.setHasFixedSize(false);
        itemSingleCommentBinding.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openMainCommGallery(mcontex, MainCommentOutsideAdapter.this);
            }
        });

        itemSingleCommentBinding.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSingleCommentBinding.imageRl.setVisibility(View.INVISIBLE);
                itemSingleCommentBinding.commentImg.setImageBitmap(null);
                scaledbitmap = null;
                itemSingleCommentBinding.postcomment.setImageResource(R.drawable.post_comment);


            }
        });
        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1"))
        {
            itemSingleCommentBinding.likeLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (likestatus.equalsIgnoreCase("0")) {
                        int c = Integer.parseInt( itemSingleCommentBinding.likeCountTV.getText().toString())+ 1;
                        itemSingleCommentBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                        itemSingleCommentBinding.likeCountTV.setText("" + c);
                        commentDTOList.get(adapterPosition).setLikeCount(String.valueOf(c));
                        commentDTOList.get(adapterPosition).setLikestatus("1");
                        commentDTOList.get(adapterPosition).setLikeSelected(true);
                        likestatus = "1";
                        notifyDataSetChanged();
                        commentLikeAdd(commentDTO.getCommentId(), postId, "1");
                    } else {
                        commentDTOList.get(adapterPosition).setLikestatus("0");
                        int c = Integer.parseInt( itemSingleCommentBinding.likeCountTV.getText().toString())- 1;
                        itemSingleCommentBinding.likeImg.setImageResource(R.drawable.like_dash);
                        commentDTOList.get(adapterPosition).setLikeCount("" + c);
                        itemSingleCommentBinding.likeCountTV.setText("" + c);
                        commentDTOList.get(adapterPosition).setLikeSelected(false);
                        likestatus = "0";
                        notifyDataSetChanged();
                        commentLikeAdd(commentDTO.getCommentId(), postId, "1");
                    }
                }
            });
            itemSingleCommentBinding.commentET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    itemSingleCommentBinding.postcomment.setImageResource(R.drawable.post_comment);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length()==0)
                        itemSingleCommentBinding.postcomment.setImageResource(R.drawable.post_comment);
                    else
                        itemSingleCommentBinding.postcomment.setImageResource(R.mipmap.post_comment_blue);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });



            itemSingleCommentBinding.sendll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!itemSingleCommentBinding.commentET.getText().toString().isEmpty() || scaledbitmap != null) {
                        String msg = itemSingleCommentBinding.commentET.getText().toString();
                        InputMethodManager imm = (InputMethodManager) mcontex.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        msgComment = msg;
                        commentid = commentDTO.getCommentId();


                        Date myDate = new Date();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                        calendar.setTime(myDate);
                        Date time = calendar.getTime();
                        SimpleDateFormat outputFmt = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                        gmtTime = outputFmt.format(time);

                        SendCommentTask task = new SendCommentTask();
                        task.execute();
                    } else Toast.makeText(mcontex, "error", Toast.LENGTH_SHORT).show();


                }
            });

        }


        if (singlecommentDial.isShowing()) {
            itemSingleCommentBinding.ll.setVisibility(View.GONE);
            loadSingleComment(postId, commentDTO.getCommentId(),commentDTOList.get(adapterPosition).getNestedCommentDTOS());
        }

        itemSingleCommentBinding.commentimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullSreenImageDialog fullSreenImageDialog = new FullSreenImageDialog();

                Intent intent = ((Activity) mcontex).getIntent();
                intent.putExtra("image", singlecommentimage);

                fullSreenImageDialog.show(((Activity) mcontex).getFragmentManager(), "1");
            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return commentDTOList.size();
    }

    public class ViewHolderProgressAdapter extends RecyclerView.ViewHolder {

        ItemCommentBinding itemDetailCommentBinding;

        public ViewHolderProgressAdapter(ItemCommentBinding itemView) {
            super(itemView.getRoot());
            itemDetailCommentBinding = itemView;


        }
    }


    private void loadSingleComment(final String postId, final String commentId, final List<NestedCommentDTO> nestedCommentDTOS) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LOAD_SINGLE_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("loadSingleComment", response);
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String msg = obj.getString("Message");
                            if (status == 200 && msg.equalsIgnoreCase("success")) {
                                itemSingleCommentBinding.ll.setVisibility(View.VISIBLE);
                                nestedCommentDTOS.clear();
                                JSONArray dataArray = obj.getJSONArray("Data");
                                JSONObject jsonObject = dataArray.getJSONObject(0);
                                JSONArray nestedcommArray = jsonObject.getJSONArray("nested_comment");
                                singlecommentimage = jsonObject.getString("image");

                                if (jsonObject.getString("image").isEmpty()) {
                                    itemSingleCommentBinding.commentimage.setVisibility(View.GONE);
                                } else {
                                    itemSingleCommentBinding.commentimage.setVisibility(View.VISIBLE);
                                    Picasso.with(mcontex).load(jsonObject.getString("image"))
                                            .placeholder(R.color.gray)
                                            .into(itemSingleCommentBinding.commentimage);

                                }

                                if (userId.equalsIgnoreCase(jsonObject.getString("user_id"))) {
                                    itemSingleCommentBinding.opTV.setVisibility(View.VISIBLE);
                                } else {
                                    itemSingleCommentBinding.opTV.setVisibility(View.GONE);

                                }

                                itemSingleCommentBinding.userNameTV.setText(jsonObject.getString("user_name"));

                                if (jsonObject.getString("message").isEmpty()) {
                                    itemSingleCommentBinding.messageTV.setVisibility(View.GONE);
                                } else {
                                    String msg1="";
                                    try {
                                        msg1=fromBase64(jsonObject.getString("message"));

                                    }
                                    catch (IllegalArgumentException e){}
                                    final SpannableStringBuilder sb = new SpannableStringBuilder(msg1);
                                    Matcher matcher = p.matcher(msg1);

                                    while (matcher.find()) {
                                        final String s = matcher.group();


                                        final ClickableSpan clickableSpan = new ClickableSpan() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                                                mcontex.startActivity(browserIntent);
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
                                    itemSingleCommentBinding.messageTV.setText(sb);
                                    itemSingleCommentBinding.messageTV.setMovementMethod(LinkMovementMethod.getInstance());


                                }

                                likestatus = jsonObject.getString("like_status");


                                SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                                Date date = null, sendDate = null;
                                String d2 = "";
                                try {
                                    date = (Date) sdf.parse(jsonObject.getString("date"));
                                    //Log.e("date",date+"");

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
                                }
                                catch (NullPointerException e){ e.printStackTrace();}

                                timeConversion.getTimeAgo(0, itemSingleCommentBinding.dateTV, mcontex, sendDate);


                                itemSingleCommentBinding.likeCountTV.setText(jsonObject.getString("count_like"));
                                itemSingleCommentBinding.comCountTV.setText(jsonObject.getString("count_comment"));

                                if (jsonObject.getString("like_status").equalsIgnoreCase("0")) {
                                    itemSingleCommentBinding.likeImg.setImageResource(R.drawable.like_dash);
                                } else {
                                    itemSingleCommentBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                                }

                                if (nestedcommArray.length()!=0)
                                {
                                    itemSingleCommentBinding.commDashImg.setImageResource(R.drawable.comment_dash_blue);

                                }
                                else
                                {
                                    itemSingleCommentBinding.commDashImg.setImageResource(R.drawable.comment_dash);

                                }

                                for (int j = 0; j < nestedcommArray.length(); j++) {
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
                                    nestedCommentDTOS.add(nestedCommentDTO);

                                }
                                nestedCommentDialogAdapter.notifyDataSetChanged();
                                notifyDataSetChanged();


                            }

                        } catch (Exception ex) {
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
                params.put("post_id", postId);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                params.put("comment_id", commentId);
                //Log.e("loadcommentparams", params + "");


                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
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

                if (scaledbitmap != null) {
                    String str = BitmapUtil.getStringFromBitmap(scaledbitmap);
                    profilePicbyte = android.util.Base64.decode(str, android.util.Base64.NO_WRAP);
                }

                JSONObject params = new JSONObject();
                params.put("post_id", postId);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                params.put("comment_type", "2");
                params.put("comment_id", commentid);
                params.put("message", toBase64(msgComment));
                params.put("add_date", gmtTime);


                Log.e("nestedcommentresponse", params + "");
                String result = comm.forComment(Endpoints.ADD_COMMENT, params, profilePicbyte);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("nested comment Resp", s);
            progressDialog.cancel();
            try {
                if (s != null) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        int status = obj.getInt("Status");
                        String msg = obj.getString("Message");
                        int count=Integer.parseInt(commentDTOList.get(nestedPosition).getCommentCount())+1;

                        itemSingleCommentBinding.imageRl.setVisibility(View.INVISIBLE);
                        itemSingleCommentBinding.commentImg.setImageBitmap(null);
                        scaledbitmap = null;
                        profilePicbyte = null;
                        itemSingleCommentBinding.commentET.setText("");

                        if (status == 200 && msg.equalsIgnoreCase("success")) {
                            JSONObject object = obj.getJSONObject("Data");
                            NestedCommentDTO cmt = new NestedCommentDTO();
                            cmt.setCommentId(object.getString("id"));
                            cmt.setUserId(object.getString("user_id"));
                            cmt.setUserName(object.getString("username"));
                            cmt.setCommentMsg(object.getString("message"));
                            cmt.setTimeCount(object.getString("add_date"));
                            cmt.setLikestatus("0");
                            cmt.setLikeCount("0");
                            cmt.setImage(object.getString("image"));
                            cmt.setCommentCount(String.valueOf(count));
                            itemSingleCommentBinding.comCountTV.setText(String.valueOf(count));

                            commentDTOList.get(nestedPosition).getNestedCommentDTOS().add(cmt);
                            commentDTOList.get(nestedPosition).setNestedCommentDTOS(commentDTOList.get(nestedPosition).getNestedCommentDTOS());
                            commentDTOList.get(nestedPosition).setCommentCount(String.valueOf(count));
                            nestedCommentDialogAdapter.notifyItemInserted(itemSingleCommentBinding.nestCommRecycle.getAdapter().getItemCount());
                            // nestedCommentAdapter.notifyDataSetChanged();
                            notifyDataSetChanged();
                        }

                    } catch (Exception ex) {
                    }
                }
            } catch (Exception ex) {
            }
        }
    }




}

