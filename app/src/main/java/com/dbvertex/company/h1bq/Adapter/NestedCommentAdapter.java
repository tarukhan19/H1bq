package com.dbvertex.company.h1bq.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Apis;
import com.dbvertex.company.h1bq.FullSreenImageDialog;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.Util.TimeConversion;
import com.dbvertex.company.h1bq.databinding.ItemNestedcommentBinding;
import com.dbvertex.company.h1bq.model.NestedCommentDTO;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NestedCommentAdapter extends RecyclerView.Adapter<NestedCommentAdapter.ViewHolderProgressAdapter> {

    private Context mcontex;
    private List<NestedCommentDTO> nestedCommentDTOList;
    String from, userId, postId;
    SessionManager sessionManager;
    private TimeConversion timeConversion;
    RequestQueue requestQueue;
    private Apis apis;
    Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE);
    Vibrator vibe;

    public NestedCommentAdapter(Context mcontex, List<NestedCommentDTO> nestedCommentDTOList, String from, String userId, String postId) {
        this.mcontex = mcontex;
        this.nestedCommentDTOList = nestedCommentDTOList;
        this.from = from;
        this.userId = userId;
        this.postId = postId;
        setHasStableIds(true);
        vibe = (Vibrator) mcontex.getSystemService(Context.VIBRATOR_SERVICE);

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

    @NonNull
    @Override
    public ViewHolderProgressAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemNestedcommentBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_nestedcomment, parent,
                false);
        timeConversion = new TimeConversion();
        requestQueue = Volley.newRequestQueue(mcontex);
        apis = new Apis();

        sessionManager = new SessionManager(mcontex.getApplicationContext());
        return new ViewHolderProgressAdapter(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderProgressAdapter holder, final int position) {


        if (nestedCommentDTOList.get(position).getImage().isEmpty())
        {
            holder.itemNestedcommentBinding.commentimage.setVisibility(View.GONE);
        } else {
            holder.itemNestedcommentBinding.commentimage.setVisibility(View.VISIBLE);
            Picasso.with(mcontex).load(nestedCommentDTOList.get(position).getImage())
                    .placeholder(R.color.gray)
                    .into(holder.itemNestedcommentBinding.commentimage);
        }
        holder.itemNestedcommentBinding.commentimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullSreenImageDialog fullSreenImageDialog = new FullSreenImageDialog();
                Intent intent = ((Activity) mcontex).getIntent();
                intent.putExtra("image", nestedCommentDTOList.get(position).getImage());
                fullSreenImageDialog.show(((Activity) mcontex).getFragmentManager(), "1");
            }
        });

        String dateStr = nestedCommentDTOList.get(position).getTimeCount();
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null, sendDate = null;
        String d2 = "";
        try {
            date = (Date) sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
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

        timeConversion.getTimeAgo(position, holder.itemNestedcommentBinding.dateTV, mcontex, sendDate);


        String username = nestedCommentDTOList.get(position).getUserName();
        if (nestedCommentDTOList.get(position).getUserId().equalsIgnoreCase(userId)) {

            holder.itemNestedcommentBinding.opTV.setVisibility(View.VISIBLE);
            holder.itemNestedcommentBinding.userNameTV.setText(username);

        } else {
            holder.itemNestedcommentBinding.userNameTV.setText(username);
            holder.itemNestedcommentBinding.opTV.setVisibility(View.GONE);

        }


        if (from.equalsIgnoreCase("dialog"))
        {
            holder.itemNestedcommentBinding.likecommLL.setVisibility(View.GONE);
          //  holder.itemNestedcommentBinding.messageTV.setSingleLine(true);

            if (nestedCommentDTOList.get(position).getCommentMsg().isEmpty()) {
                holder.itemNestedcommentBinding.messageTV.setVisibility(View.GONE);
            } else {
                holder.itemNestedcommentBinding.messageTV.setVisibility(View.VISIBLE);
                String msg="";
                try {
                 msg=fromBase64(nestedCommentDTOList.get(position).getCommentMsg());

                }
                catch (IllegalArgumentException e){}
                Log.e("msgdialog",msg);

                final SpannableStringBuilder sb = new SpannableStringBuilder(msg);
                Matcher matcher = p.matcher(msg);

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
                holder.itemNestedcommentBinding.messageTV.setText(sb);
                holder.itemNestedcommentBinding.messageTV.setMovementMethod(LinkMovementMethod.getInstance());

            }


        }
        else
        {
            holder.itemNestedcommentBinding.likecommLL.setVisibility(View.VISIBLE);

            if (nestedCommentDTOList.get(position).getCommentMsg().isEmpty()) {
                holder.itemNestedcommentBinding.messageTV.setVisibility(View.GONE);
            } else {
                holder.itemNestedcommentBinding.messageTV.setVisibility(View.VISIBLE);
                String msg="";
                try {
                 msg=fromBase64(nestedCommentDTOList.get(position).getCommentMsg());

            }
                                catch (IllegalArgumentException e){}
                Log.e("msgdialog1",msg);

                final SpannableStringBuilder sb = new SpannableStringBuilder(msg);
                Matcher matcher = p.matcher(msg);

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
                holder.itemNestedcommentBinding.messageTV.setText(sb);
                holder.itemNestedcommentBinding.messageTV.setMovementMethod(LinkMovementMethod.getInstance());

            }


        }

        holder.itemNestedcommentBinding.likeCountTV.setText(nestedCommentDTOList.get(position).getLikeCount());

        if (nestedCommentDTOList.get(position).getLikeStatus().equalsIgnoreCase("0")) {
            nestedCommentDTOList.get(position).setLikeSelected(false);
            holder.itemNestedcommentBinding.likeImg.setImageResource(R.drawable.like_dash);
        } else {
            nestedCommentDTOList.get(position).setLikeSelected(true);
            holder.itemNestedcommentBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
        }
        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1"))
        {
            holder.itemNestedcommentBinding.likeLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibe.vibrate(50);

                    if (nestedCommentDTOList.get(position).getLikeStatus().equalsIgnoreCase("0")) {
                        nestedCommentDTOList.get(position).setLikestatus("1");
                        int c = Integer.parseInt(nestedCommentDTOList.get(position).getLikeCount()) + 1;
                        holder.itemNestedcommentBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                        nestedCommentDTOList.get(position).setLikeCount("" + c);
                        //commentAdapter.notifyDataSetChanged();
                        notifyDataSetChanged();
                        apis.commentLikeAdd(nestedCommentDTOList.get(position).getNestedCommentId(), postId, "2", mcontex);
                    } else {
                        nestedCommentDTOList.get(position).setLikestatus("0");
                        int c = Integer.parseInt(nestedCommentDTOList.get(position).getLikeCount()) - 1;
                        holder.itemNestedcommentBinding.likeImg.setImageResource(R.drawable.like_dash);
                        nestedCommentDTOList.get(position).setLikeCount("" + c);
                        notifyDataSetChanged();
                        apis.commentLikeAdd(nestedCommentDTOList.get(position).getNestedCommentId(), postId, "2", mcontex);

                    }

                }
            });
        }





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
    public int getItemCount()
    {
        if(nestedCommentDTOList==null) return 0;
        return nestedCommentDTOList.size();
    }



    public class ViewHolderProgressAdapter extends RecyclerView.ViewHolder {

        ItemNestedcommentBinding itemNestedcommentBinding;
        public ViewHolderProgressAdapter(ItemNestedcommentBinding itemView) {
            super(itemView.getRoot());
            itemNestedcommentBinding = itemView;


        }
    }


}

