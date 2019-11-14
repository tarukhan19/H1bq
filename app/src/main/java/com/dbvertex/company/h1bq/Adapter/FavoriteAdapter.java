package com.dbvertex.company.h1bq.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Apis;
import com.dbvertex.company.h1bq.FavouriteActivity;
import com.dbvertex.company.h1bq.FullSreenImageDialog;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.UpdatePost;
import com.dbvertex.company.h1bq.Util.BitmapUtil;
import com.dbvertex.company.h1bq.Util.DynamiclinkCreate;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.RecycleviewDecorator;
import com.dbvertex.company.h1bq.Util.TimeConversion;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.databinding.ItemPostdetailBinding;
import com.dbvertex.company.h1bq.databinding.ItemPostpollBinding;
import com.dbvertex.company.h1bq.model.CommentDTO;
import com.dbvertex.company.h1bq.model.HomeDTO;
import com.dbvertex.company.h1bq.model.NestedCommentDTO;
import com.dbvertex.company.h1bq.model.Poll_OptionData;
import com.dbvertex.company.h1bq.model.ResultPopUpDTO;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    private ArrayList<HomeDTO> homeDTOArrayList;
    private ArrayList<String> selectAnsId, selectAns, ansIdlist;
    private List<Poll_OptionData> mPollOptionData;
    private List<CommentDTO> commentDTOList;
    String detailDescription;

    boolean data_status = false;

    private OptionSelectAdp optionSelectAdp;
    private FavouriteMainCommentAdp mainCommentAdapter;

    public static final int DISMISS_TIMEOUT = 2000;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    private SessionManager sessionManager;
    private TimeConversion timeConversion;
    private Apis apis;
    private FullSreenImageDialog fullSreenImageDialog = new FullSreenImageDialog();
    private CommentDTO commentDTO;
    private FavouriteActivity activity;

    private Dialog detailDialog;
    private ItemPostdetailBinding itemPostdetailBinding;

    private int offset = 0;
    private String detaillikestatus = "0", detailreportAbuseStatus, detailbookmarkstatus,detailTitle,
            ansPostId, postIdComment, commentTypeComment, msgComment, postimage, gmtTime;
    static String dateStr;
    private byte[] profilePicbyte = null;
    private Bitmap scaledbitmap;

    private JSONArray resultPopUpArray;
    Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE);

    Vibrator vibe;
    int nestedPosition,main_count_comment;


    public FavoriteAdapter(FavouriteActivity context, ArrayList<HomeDTO> homeDTOArrayList) {
        this.mContext = context;
        this.activity = context;
        this.homeDTOArrayList = homeDTOArrayList;
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public FavoriteAdapter(Context context) {
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // View v;
//        switch (viewType) {
//            default:
        ItemPostpollBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_postpoll, viewGroup, false);
        sessionManager = new SessionManager(mContext.getApplicationContext());
        requestQueue = Volley.newRequestQueue(mContext);
        detailDialog = new Dialog(mContext, R.style.full_screen_dialog);
        timeConversion = new TimeConversion();
        selectAnsId = new ArrayList<>();
        selectAns = new ArrayList<>();
        ansIdlist = new ArrayList<>();
        commentDTOList = new ArrayList<>();
        mPollOptionData = new ArrayList<>();
        progressDialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        apis = new Apis();
        itemPostdetailBinding = DataBindingUtil.inflate(LayoutInflater.from(detailDialog.getContext()), R.layout.item_postdetail, null, false);
        return new MyViewHolder(viewDataBinding);
//            case NATIVE_AD_VIEW_TYPE:
//                ItemAdmobBinding itemAdmobBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_admob, viewGroup, false);
//                return new NativeAdViewHolder(itemAdmobBinding);
//        }
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
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position)
    {


        final MyViewHolder holder = (MyViewHolder) h;

        try {
            holder.itemPostpollBinding.titleTV.setText((homeDTOArrayList.get(position).getTitle()));
        } catch (IllegalArgumentException e) {
        }
        holder.itemPostpollBinding.viewCountTV.setText(homeDTOArrayList.get(position).getViewCount());
        holder.itemPostpollBinding.likeCountTV.setText(homeDTOArrayList.get(position).getLikeCount());
        holder.itemPostpollBinding.comCountTV.setText(homeDTOArrayList.get(position).getCommentCount());
        holder.itemPostpollBinding.votesTV.setText(homeDTOArrayList.get(position).getVotesCount());

        holder.itemPostpollBinding.daysll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String sAux = "\n"+ homeDTOArrayList.get(position).getTitle()+"\n\n";

                    if (!homeDTOArrayList.get(position).getTitle().isEmpty())
                    { sAux = sAux + homeDTOArrayList.get(position).getTitle()+"\n\n";}
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    mContext.startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (!homeDTOArrayList.get(position).getCommentCount().equalsIgnoreCase("0"))
        {
            holder.itemPostpollBinding.comDashImg.setImageResource(R.drawable.comment_dash_blue);

        }
        else
        {
            holder.itemPostpollBinding.comDashImg.setImageResource(R.drawable.comment_dash);

        }

        holder.itemPostpollBinding.daysll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (homeDTOArrayList.get(position).getPostType().equalsIgnoreCase(Constants.POST_TYPE)) {
                        if (!homeDTOArrayList.get(position).getImage().isEmpty()) {
                           DynamiclinkCreate.shareImage(homeDTOArrayList.get(position).getImage(), mContext,homeDTOArrayList.get(position).getTitle(),homeDTOArrayList.get(position).getPostId());
                            //sAux = sAux + postimage+"\n\n";
                        } else {
                            DynamiclinkCreate.shareImage(homeDTOArrayList.get(position).getImage(), mContext,homeDTOArrayList.get(position).getTitle(),homeDTOArrayList.get(position).getPostId());
                        }
                    } else {
                        DynamiclinkCreate.shareImage
                                (homeDTOArrayList.get(position).getImage(), mContext,homeDTOArrayList.get(position).getTitle(),homeDTOArrayList.get(position).getPostId());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (homeDTOArrayList.get(position).getDeleteStatus().equalsIgnoreCase("0")) {
            holder.itemPostpollBinding.greybackLL.setVisibility(View.VISIBLE);
        } else {
            holder.itemPostpollBinding.greybackLL.setVisibility(View.GONE);
        }

        if (!homeDTOArrayList.get(position).getImage().isEmpty()) {
            holder.itemPostpollBinding.postcardImg.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(homeDTOArrayList.get(position).getImage())
                    .placeholder(R.color.gray)
                    .error(R.color.gray).into(holder.itemPostpollBinding.postcardImg);

        } else {
            holder.itemPostpollBinding.postcardImg.setVisibility(View.GONE);
        }


        if (homeDTOArrayList.get(position).getLikeStatus().equalsIgnoreCase("0")) {
            homeDTOArrayList.get(position).setLikeSelected(false);
            holder.itemPostpollBinding.likeImg.setImageResource(R.drawable.like_dash);
        } else {
            homeDTOArrayList.get(position).setLikeSelected(true);
            holder.itemPostpollBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
        }

        if (homeDTOArrayList.get(position).getBookmarkStatus().equalsIgnoreCase("0")) {
            homeDTOArrayList.get(position).setBookmarkSelected(false);
            holder.itemPostpollBinding.favorite.setImageResource(R.drawable.favorite);
        } else {
            homeDTOArrayList.get(position).setBookmarkSelected(true);
            holder.itemPostpollBinding.favorite.setImageResource(R.drawable.favorite_feel);
        }


        if (homeDTOArrayList.get(position).getPostType().equalsIgnoreCase("1")) {
            String username = homeDTOArrayList.get(position).getUserName();
            holder.itemPostpollBinding.postcardLL.setVisibility(View.VISIBLE);
            holder.itemPostpollBinding.polecardRLL.setVisibility(View.GONE);

            if (username.isEmpty()) {
                holder.itemPostpollBinding.userNameTV.setText("H1BQ User");
            } else {
                holder.itemPostpollBinding.userNameTV.setText(username);
            }


            String description = "";
            try {
                description = fromBase64(homeDTOArrayList.get(position).getDescription());

            } catch (IllegalArgumentException e) {
            }

            SpannableStringBuilder sb1 = new SpannableStringBuilder(" See More");
            if (description.length() >= 55) {
                String subDesc = description.substring(0, 50);
                SpannableStringBuilder sb = new SpannableStringBuilder(subDesc);
                Matcher matcher = p.matcher(subDesc);
                while (matcher.find()) {

                    final String s = matcher.group();
                    Log.e("matchertext", s);
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {


                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                            mContext.startActivity(browserIntent);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(Color.BLUE);
                        }
                    };
                    sb.setSpan(clickableSpan, matcher.start(), matcher.end(),
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    holder.itemPostpollBinding.descripTV.setMovementMethod(LinkMovementMethod.getInstance());

                }
                sb1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary)), 0, 9,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                holder.itemPostpollBinding.descripTV.setText(sb.append(sb1));
                holder.itemPostpollBinding.descripTV.setMovementMethod(LinkMovementMethod.getInstance());

            } else {
                final SpannableStringBuilder sb = new SpannableStringBuilder(description);
                Matcher matcher = p.matcher(description);

                while (matcher.find()) {
                    final String s = matcher.group();

                    Log.e("matchertext", s);

                    final ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                            mContext.startActivity(browserIntent);
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
                holder.itemPostpollBinding.descripTV.setText(sb);
                holder.itemPostpollBinding.descripTV.setMovementMethod(LinkMovementMethod.getInstance());

            }


        } else {
            String username = homeDTOArrayList.get(position).getUserName();
            holder.itemPostpollBinding.postcardLL.setVisibility(View.GONE);
            holder.itemPostpollBinding.polecardRLL.setVisibility(View.VISIBLE);
            if (username.isEmpty()) {
                holder.itemPostpollBinding.polluserNameTV.setText("H1BQ User");
            } else {
                holder.itemPostpollBinding.polluserNameTV.setText(username);
            }

        }


        if (!homeDTOArrayList.get(position).getDaysCount().isEmpty()) {
            dateStr = homeDTOArrayList.get(position).getDaysCount();
            SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = null, sendDate = null;
            String d2 = "";
            try {
                date = (Date) sdf.parse(dateStr);
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
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            timeConversion.getTimeAgo(position, holder.itemPostpollBinding.daysCount, mContext, sendDate);
        }

        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1")) {
            holder.itemPostpollBinding.descripTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager cManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cData = ClipData.newPlainText("text", holder.itemPostpollBinding.descripTV.getText());
                    cManager.setPrimaryClip(cData);
                    Toast.makeText(mContext, "Description Copied", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            holder.itemPostpollBinding.moreoption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPopUpMenu(holder.getAdapterPosition(), holder.itemPostpollBinding.moreoption, homeDTOArrayList.get(position).getPostId(),
                            homeDTOArrayList.get(position).getReportabuseStatus(), homeDTOArrayList.get(position).getUserId(),
                            homeDTOArrayList.get(position).getPostType()
                    );
                    notifyDataSetChanged();
                    notifyItemChanged(position);

                }
            });


            holder.itemPostpollBinding.likeLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibe.vibrate(50);

                    if (homeDTOArrayList.get(position).getLikeStatus().equalsIgnoreCase("0")) {
                        homeDTOArrayList.get(position).setLikestatus("1");
                        int c = Integer.parseInt(homeDTOArrayList.get(position).getLikeCount()) + 1;
                        holder.itemPostpollBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                        homeDTOArrayList.get(position).setLikeCount("" + c);
                        notifyDataSetChanged();
                        apis.likeAdd(homeDTOArrayList.get(position).getPostId(), mContext, requestQueue);
                    } else {
                        homeDTOArrayList.get(position).setLikestatus("0");
                        int c = Integer.parseInt(homeDTOArrayList.get(position).getLikeCount()) - 1;
                        holder.itemPostpollBinding.likeImg.setImageResource(R.drawable.like_dash);
                        homeDTOArrayList.get(position).setLikeCount("" + c);
                        notifyDataSetChanged();
                        apis.likeAdd(homeDTOArrayList.get(position).getPostId(), mContext, requestQueue);
                    }

                }
            });


            holder.itemPostpollBinding.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibe.vibrate(50);


                    bookmarkAdd(homeDTOArrayList.get(position).getPostId(), position);



                }
            });


        }


        itemPostdetailBinding.commentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                itemPostdetailBinding.commentET.requestFocus();
            }
        });

        holder.itemPostpollBinding.cardviewPostcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeDTOArrayList.get(position).setViewstatus("1");
                int c = Integer.parseInt(homeDTOArrayList.get(position).getViewCount()) + 1;
                homeDTOArrayList.get(position).setViewCount("" + c);
                notifyDataSetChanged();
                openPostDetailDialog(homeDTOArrayList.get(position).getPostId(), position,
                        homeDTOArrayList.get(position).getPostType(), homeDTOArrayList.get(position).getUserId());


            }
        });


        holder.itemPostpollBinding.commentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.parseInt(homeDTOArrayList.get(position).getViewCount()) + 1;
                homeDTOArrayList.get(position).setViewCount("" + c);
                notifyDataSetChanged();
                openPostDetailDialog(homeDTOArrayList.get(position).getPostId(), position,
                        homeDTOArrayList.get(position).getPostType(), homeDTOArrayList.get(position).getUserId());
            }
        });


    }

    public void setImage(Bitmap scaledBitmap) {
        itemPostdetailBinding.imageRl.setVisibility(View.VISIBLE);
        itemPostdetailBinding.commentImg.setImageBitmap(scaledBitmap);
        scaledbitmap = scaledBitmap;
        itemPostdetailBinding.postcomment.setImageResource(R.mipmap.post_comment_blue);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemPostpollBinding itemPostpollBinding;

        public MyViewHolder(ItemPostpollBinding itemView) {
            super(itemView.getRoot());
            itemPostpollBinding = itemView;
        }

    }


    @Override
    public int getItemCount() {

        if (homeDTOArrayList == null) return 0;
        return homeDTOArrayList.size();

    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private void openPopUpMenu(final int position, ImageView moreoption, final String postId, final String reportabuseStatus, String userId, String postType) {

        if (userId.equalsIgnoreCase(sessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {
            if (postType.equalsIgnoreCase(Constants.POST_TYPE)) {
                //Log.e("DetailDialPos", position + "");

                PopupMenu popup = new PopupMenu(mContext, moreoption);
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

                                if (homeDTOArrayList.get(position).getDeleteStatus().equalsIgnoreCase("1")) {

                                    Intent intent = new Intent(mContext, UpdatePost.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    intent.putExtra("postId", postId);
                                    mContext.startActivity(intent);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            } else {
                PopupMenu popup = new PopupMenu(mContext, moreoption);
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
            PopupMenu popup = new PopupMenu(mContext, moreoption);
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

        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
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

        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
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
//                    HideKeyboard.hideKeyboard(mContext);
                }


            }
        });
    }

    private void showResultPopup() {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_resultpopup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ArrayList<ResultPopUpDTO> resultPopUpDTOS = new ArrayList<>();
        ResultPopUpADP resultPopUpADP = new ResultPopUpADP(mContext, resultPopUpDTOS);
        RecyclerView recyclerview_progress = (RecyclerView) dialog.findViewById(R.id.recyclerview_progress);
        LinearLayout close = dialog.findViewById(R.id.close);
        recyclerview_progress.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
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

    private void openDialog(String s) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
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


    public int dpToPx(int dp) {

        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void openPostDetailDialog(final String postId, final int adapterPosition, final String postType, final String userId) {
        detailDialog.setContentView(itemPostdetailBinding.getRoot());
        detailDialog.setCanceledOnTouchOutside(false);
        detailDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        detailDialog.show();
        nestedPosition=adapterPosition;

        itemPostdetailBinding.mainlinear.setVisibility(View.GONE);


        itemPostdetailBinding.daysll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String sAux = "\n"+ detailTitle+"\n\n";

                    if (!postimage.isEmpty())
                    { sAux = sAux + postimage+"\n\n";}
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    mContext.startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Toolbar toolbar_main = detailDialog.findViewById(R.id.toolbar_main);
        TextView titletoolTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        LinearLayout back_LL = toolbar_main.findViewById(R.id.back_LL);
        ImageView edit_img = toolbar_main.findViewById(R.id.edit_img);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        itemPostdetailBinding.recyclerviewOption.addItemDecoration(new RecycleviewDecorator(2, dpToPx(10), false));
        itemPostdetailBinding.recyclerviewOption.setLayoutManager(gridLayoutManager);
        itemPostdetailBinding.recyclerviewOption.setItemAnimator(new DefaultItemAnimator());
        itemPostdetailBinding.recyclerviewOption.setNestedScrollingEnabled(false);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        itemPostdetailBinding.recyclerviewComment.setHasFixedSize(false);
        itemPostdetailBinding.recyclerviewComment.setNestedScrollingEnabled(false);
        itemPostdetailBinding.recyclerviewComment.setItemViewCacheSize(3);
        itemPostdetailBinding.recyclerviewComment.setLayoutManager(linearLayoutManager);

        mainCommentAdapter = new FavouriteMainCommentAdp((FavouriteActivity) mContext, commentDTOList, "dialog", postId, userId);
        itemPostdetailBinding.recyclerviewComment.setAdapter(mainCommentAdapter);


        itemPostdetailBinding.daysll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                try {

                    if (postType.equalsIgnoreCase(Constants.POST_TYPE)) {
                        if (!postimage.isEmpty()) {
                            DynamiclinkCreate.shareImage
                                    (postimage, mContext,detailTitle,postId);
                        } else {
                            DynamiclinkCreate.shareImage
                                    (postimage, mContext,detailTitle,postId);
                        }
                    } else {
                        DynamiclinkCreate.shareImage
                                (postimage, mContext,detailTitle,postId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        itemPostdetailBinding.postcardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeDTOArrayList.get(adapterPosition).getDeleteStatus().equalsIgnoreCase("1")) {
                    Intent intent = ((Activity) mContext).getIntent();
                    intent.putExtra("image", postimage);
                    fullSreenImageDialog.show(((Activity) mContext).getFragmentManager(), "1");
                }



            }
        });


//        itemPostdetailBinding.descripTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("descriptive",detailDescription);
//                if (detailDescription.startsWith("http"))
//                {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detailDescription));
//                    mContext.startActivity(browserIntent);
//                }
//
//            }
//        });

        itemPostdetailBinding.descripTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", itemPostdetailBinding.descripTV.getText());
                cManager.setPrimaryClip(cData);
                Toast.makeText(mContext, "Description Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        itemPostdetailBinding.viewresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResultPopup();
            }
        });

        itemPostdetailBinding.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openGallery(mContext, FavoriteAdapter.this);
            }
        });

        itemPostdetailBinding.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPostdetailBinding.imageRl.setVisibility(View.INVISIBLE);
                itemPostdetailBinding.commentImg.setImageBitmap(null);
                scaledbitmap = null;
                itemPostdetailBinding.postcomment.setImageResource(R.drawable.post_comment);

            }
        });


        if (postType.equalsIgnoreCase(Constants.POST_TYPE)) {
            itemPostdetailBinding.pollcardLL.setVisibility(View.GONE);
            itemPostdetailBinding.postcardLL.setVisibility(View.VISIBLE);
            titletoolTV.setText("Post Detail");
            edit_img.setVisibility(View.GONE);
        } else {
            itemPostdetailBinding.pollcardLL.setVisibility(View.VISIBLE);
            itemPostdetailBinding.postcardLL.setVisibility(View.GONE);
            titletoolTV.setText("Poll Detail");
            edit_img.setVisibility(View.GONE);
        }

        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailDialog.dismiss();
            }
        });

        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1")) {
            itemPostdetailBinding.submit.setOnClickListener(new View.OnClickListener() {
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
            itemPostdetailBinding.likeLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    vibe.vibrate(50);

                    if (detaillikestatus.equalsIgnoreCase("0")) {
                        int c = Integer.parseInt(itemPostdetailBinding.likeCountTV.getText().toString()) + 1;
                        itemPostdetailBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                        itemPostdetailBinding.likeCountTV.setText("" + c);
                        homeDTOArrayList.get(adapterPosition).setLikeCount(String.valueOf(c));
                        homeDTOArrayList.get(adapterPosition).setLikestatus("1");
                        detaillikestatus = "1";
                        notifyDataSetChanged();
                        apis.likeAdd(postId, mContext, requestQueue);
                    } else {

                        homeDTOArrayList.get(adapterPosition).setLikestatus("0");
                        int c = Integer.parseInt(itemPostdetailBinding.likeCountTV.getText().toString()) - 1;
                        itemPostdetailBinding.likeImg.setImageResource(R.drawable.like_dash);
                        homeDTOArrayList.get(adapterPosition).setLikeCount("" + c);
                        itemPostdetailBinding.likeCountTV.setText("" + c);
                        detaillikestatus = "0";
                        notifyDataSetChanged();
                        apis.likeAdd(postId, mContext, requestQueue);
                    }
                }
            });

            itemPostdetailBinding.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibe.vibrate(50);
                    bookmarkAdd(postId, adapterPosition);

//                    if (detailbookmarkstatus.equalsIgnoreCase("0")) {
//                        homeDTOArrayList.get(adapterPosition).setBookmarkSelected(true);
//                        itemPostdetailBinding.favorite.setImageResource(R.drawable.favorite_feel);
//                        homeDTOArrayList.get(adapterPosition).setBookmarkstatus("1");
//                        detailbookmarkstatus = "1";
//                        notifyDataSetChanged();
//                        apis.bookmarkAdd(postId, adapterPosition, mContext, requestQueue);
//                    } else if (detailbookmarkstatus.equalsIgnoreCase("1")) {
//                        homeDTOArrayList.get(adapterPosition).setBookmarkSelected(false);
//                        itemPostdetailBinding.favorite.setImageResource(R.drawable.favorite);
//                        homeDTOArrayList.get(adapterPosition).setBookmarkstatus("0");
//                        detailbookmarkstatus = "0";
//                        notifyDataSetChanged();
//                        apis.bookmarkAdd(postId, adapterPosition, mContext, requestQueue);
//                    }


                }
            });

            itemPostdetailBinding.moreoption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openPopUpMenu(adapterPosition, itemPostdetailBinding.moreoption, postId,
                            detailreportAbuseStatus, userId,
                            postType);
                }
            });

            itemPostdetailBinding.commentET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    itemPostdetailBinding.postcomment.setImageResource(R.drawable.post_comment);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length()==0)
                        itemPostdetailBinding.postcomment.setImageResource(R.drawable.post_comment);
                    else
                        itemPostdetailBinding.postcomment.setImageResource(R.mipmap.post_comment_blue);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            itemPostdetailBinding.sendLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!itemPostdetailBinding.commentET.getText().toString().isEmpty() || scaledbitmap != null) {

                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        postIdComment = postId;
                        commentTypeComment = "1";
                        msgComment = itemPostdetailBinding.commentET.getText().toString();
                        itemPostdetailBinding.commentET.setText("");


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
                        Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        itemPostdetailBinding.loadcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPostdetailBinding.loadcomment.setText("Loading...");
                offset = offset + 1;
                loadComment(postId, offset);
            }
        });


        commentDTOList.clear();
        offset = 0;
        loadComment(postId, offset);
        detailPost(postId);


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
                ob.put("post_id", ansPostId);
                ob.put("add_date", gmtTime);
                String result = comm.forAnsPost(Endpoints.SUBMIT_ANS, ob, ansIdlist);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            ////Log.e("Upload Response ", s);
            progressDialog.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    ansIdlist.clear();
                    int status = obj.getInt("Status");
                    String message = obj.getString("Message");
                    if (status == 200 && message.equals("success")) {
                        openDialog(obj.getString("Data"));
                        itemPostdetailBinding.submitll.setVisibility(View.GONE);
                        detailPost(ansPostId);
                    } else {
                        openDialog(obj.getString("Data"));
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    private void loadComment(final String postId, final int offset) {
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
                                JSONArray dataArray = obj.getJSONArray("Data");

                                if (dataArray.length() < 10) {
                                    itemPostdetailBinding.loadcomment.setVisibility(View.GONE);

                                } else {
                                    itemPostdetailBinding.loadcomment.setText("Load more comments");
                                    itemPostdetailBinding.loadcomment.setVisibility(View.VISIBLE);

                                }


                                for (int i = 0; i < dataArray.length(); i++) {
                                    data_status = obj.getBoolean("data_status");

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
                                itemPostdetailBinding.loadcomment.setVisibility(View.GONE);

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
                params.put("post_id", postId);
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
                params.put("post_id", postIdComment);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                params.put("comment_type", commentTypeComment);
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
                        int count=main_count_comment+1;

                        List<NestedCommentDTO> nestedCommentDTOArrayList = new ArrayList<>();

                        itemPostdetailBinding.imageRl.setVisibility(View.INVISIBLE);
                        itemPostdetailBinding.commentImg.setImageBitmap(null);
                        scaledbitmap = null;
                        profilePicbyte = null;

                        if (itemPostdetailBinding.comment.getText().toString().equalsIgnoreCase("No Comments")) {
                            itemPostdetailBinding.comment.setText("Comment");
                        } else if (itemPostdetailBinding.comment.getText().toString().equalsIgnoreCase("Comment")) {
                            itemPostdetailBinding.comment.setText("Comments");
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
                                itemPostdetailBinding.comCountTV.setText(String.valueOf(count));
                                homeDTOArrayList.get(nestedPosition).setCommentCount(String.valueOf(count));

                                mainCommentAdapter.notifyItemInserted(itemPostdetailBinding.recyclerviewComment.getAdapter().getItemCount());
                                mainCommentAdapter.notifyDataSetChanged();
                                notifyDataSetChanged();
                            }

                        }

                    } catch (Exception ex) {
                    }
                }
            } catch (Exception ex) {
            }
        }
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
                                final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
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

                                        dialog.dismiss();
                                        homeDTOArrayList.remove(position);
                                        notifyDataSetChanged();
                                        notifyItemChanged(position);


                                        if (detailDialog.isShowing()) {
                                            detailDialog.dismiss();
                                        }

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
                                homeDTOArrayList.get(adapterPosition).setReportabusestatus("1");
                                // reportAbuseStatus = "1";
                                notifyDataSetChanged();
                                openDialog("Thank you for reporting us, we will look into this and take necessary action.");

                            } else if (status == 0 && message.equals("Failed")) {
                                notifyDataSetChanged();
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


    private void detailPost(final String postId)
    {
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.POST_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        selectAnsId.clear();
                        selectAns.clear();
                        ansIdlist.clear();
                        mPollOptionData.clear();
                        progressDialog.dismiss();
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("Success")) {

                                itemPostdetailBinding.mainlinear.setVisibility(View.VISIBLE);
                                itemPostdetailBinding.nointernet.setVisibility(View.GONE);

                                JSONObject dataObj = obj.getJSONObject("Data");
                                try {
                                    itemPostdetailBinding.titleDetailTV.setText((dataObj.getString("title")));
                                    detailTitle=dataObj.getString("title");

                                } catch (IllegalArgumentException e) {
                                }
                                detailreportAbuseStatus = dataObj.getString("report_abuse_status");
                                detaillikestatus = dataObj.getString("like_status");
                                detailbookmarkstatus = dataObj.getString("bookmark_status");
                                ansPostId = dataObj.getString("post_id");
                                postimage = dataObj.getString("post_image");
                                String datecount = dataObj.getString("add_date");

                                SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                                Date date = null, sendDate = null;
                                String d2 = "";
                                try {
                                    date = (Date) sdf.parse(datecount);
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
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                timeConversion.getTimeAgo(0, itemPostdetailBinding.daysCount, mContext, sendDate);

                                String username = dataObj.getString("username");
                                if (username.isEmpty()) {
                                    itemPostdetailBinding.userNameTV.setText("H1BQ User");
                                    itemPostdetailBinding.polluserNameTV.setText("H1BQ User");

                                } else {
                                    itemPostdetailBinding.userNameTV.setText(username);
                                    itemPostdetailBinding.polluserNameTV.setText(username);
                                }

                                itemPostdetailBinding.likeCountTV.setText(dataObj.getString("count_like"));
                                main_count_comment=dataObj.getInt("count_comment");

                                itemPostdetailBinding.comCountTV.setText(dataObj.getString("count_comment"));
                                itemPostdetailBinding.viewCountTV.setText(dataObj.getString("count_view"));
                                String optionselectType = dataObj.getString("ans_selection_typ");

                                if (dataObj.getInt("count_comment") < 1) {
                                    itemPostdetailBinding.comment.setText("No Comments");
                                    itemPostdetailBinding.comDashImg.setImageResource(R.drawable.comment_dash);


                                } else if (dataObj.getInt("count_comment") == 1) {
                                    itemPostdetailBinding.comment.setText("Comment");
                                    itemPostdetailBinding.comDashImg.setImageResource(R.drawable.comment_dash_blue);


                                } else {
                                    itemPostdetailBinding.comment.setText("Comments");
                                    itemPostdetailBinding.comDashImg.setImageResource(R.drawable.comment_dash_blue);

                                }


                                if (dataObj.getString("post_type").equalsIgnoreCase(Constants.POST_TYPE)) {
                                    try {
                                        detailDescription = fromBase64(dataObj.getString("dicription"));

                                    } catch (IllegalArgumentException e) {
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
                                                mContext.startActivity(browserIntent);
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
                                    itemPostdetailBinding.descripTV.setText(sb);
                                    itemPostdetailBinding.descripTV.setMovementMethod(LinkMovementMethod.getInstance());

                                    if (!postimage.isEmpty()) {
                                        itemPostdetailBinding.postcardImg.setVisibility(View.VISIBLE);
                                        Picasso.with(mContext).load(postimage)
                                                .placeholder(R.color.gray)
                                                .error(R.color.gray).into(itemPostdetailBinding.postcardImg);

                                    } else {
                                        itemPostdetailBinding.postcardImg.setVisibility(View.GONE);
                                    }

                                } else {
                                    JSONArray ansArray = dataObj.getJSONArray("answer");
                                    JSONArray resultArray = dataObj.getJSONArray("count_percentage");
                                    resultPopUpArray = resultArray;
                                    JSONArray submitAnsArray = dataObj.getJSONArray("user_submit_answer");


                                    if (submitAnsArray.length() == 0) {
                                        itemPostdetailBinding.submitll.setVisibility(View.VISIBLE);
                                    } else {
                                        itemPostdetailBinding.submitll.setVisibility(View.GONE);
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
                                                if (optionselectType.equalsIgnoreCase(Constants.MULTIPLE_SELECTION)) {
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
                                    optionSelectAdp = new OptionSelectAdp(mContext, mPollOptionData, optionselectType, ansIdlist, selectAnsId);
                                    itemPostdetailBinding.recyclerviewOption.setAdapter(optionSelectAdp);

                                }

                                if (dataObj.getString("like_status").equalsIgnoreCase("0")) {

                                    itemPostdetailBinding.likeImg.setImageResource(R.drawable.like_dash);
                                } else {

                                    itemPostdetailBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
                                }

                                if (dataObj.getString("bookmark_status").equalsIgnoreCase("0")) {

                                    itemPostdetailBinding.favorite.setImageResource(R.drawable.favorite);
                                } else {
                                    itemPostdetailBinding.favorite.setImageResource(R.drawable.favorite_feel);
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
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("post_id", postId);
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }



    public void bookmarkAdd(final String postId, final int adapterPosition)
    {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.POSTPOLL_BOOKMARK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("favresponse",response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String msg = obj.getString("Message");
                            String data = obj.getString("Data");
                            if (status == 200 && msg.equalsIgnoreCase("success")) {

                                homeDTOArrayList.get(adapterPosition).setBookmarkSelected(false);
                                homeDTOArrayList.get(adapterPosition).setBookmarkstatus("0");
                                homeDTOArrayList.remove(adapterPosition);
                                notifyDataSetChanged();
                                notifyItemChanged(adapterPosition);

                                if(detailDialog.isShowing())
                                {
                                    detailDialog.dismiss();

                                }
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
                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);


    }


}
