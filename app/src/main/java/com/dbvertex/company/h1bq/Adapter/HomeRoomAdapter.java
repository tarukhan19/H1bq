package com.dbvertex.company.h1bq.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.FullSreenImageDialog;
import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.User;
import com.dbvertex.company.h1bq.Util.RecycleviewDecorator;
import com.dbvertex.company.h1bq.Util.TimeConversion;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.databinding.ItemPostdetailBinding;
import com.dbvertex.company.h1bq.databinding.ItemPostpollBinding;
import com.dbvertex.company.h1bq.model.Poll_OptionData;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public Context mContext;
    static String dateStr;
    private List<User> homeDTOArrayList;
    private static final int DEFAULT_VIEW_TYPE = 1;
    private static final int STATIC_FOOTER = 2;
    public static final int DISMISS_TIMEOUT = 2000;
    private RequestQueue requestQueue;
    private SessionManager sessionManager;
    private TimeConversion timeConversion;
    boolean isConnected;

    Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE);

    Vibrator vibe;

    public HomeRoomAdapter(HomeActivity context,String bucketid)
    {
        this.mContext = context;
        homeDTOArrayList=HomeActivity.myAppDatabase.myDao().getUsers(bucketid);
        sessionManager = new SessionManager(mContext.getApplicationContext());
        requestQueue = Volley.newRequestQueue(mContext);
        timeConversion = new TimeConversion();
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // View v;

        switch (viewType) {
        default:
        ItemPostpollBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_postpoll, viewGroup, false);
        return new MyViewHolder(viewDataBinding);
        case STATIC_FOOTER:
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_footer,
                viewGroup, false);
        return new FooterViewHolder(view);

//                ItemPostpollBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_postpoll, viewGroup, false);
//
//                return new MyViewHolder(viewDataBinding);


        }   }



    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public View View;

        public FooterViewHolder(View v) {
            super(v);
            View = v;
            // Add your UI Components here
        }

    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {

//        if (!(h instanceof MyViewHolder)) {
//            return;
//        }

        if (h instanceof MyViewHolder)
        {

            //set the Value from List to corresponding UI component as shown below.

            //similarly bind other UI components or perform operations
            final MyViewHolder holder = (MyViewHolder) h;

            final User homeDTO = homeDTOArrayList.get(position);

            try {
                holder.itemPostpollBinding.titleTV.setText((homeDTO.getTitleRoom()));
                Log.e("homeDTO.getTitleRoom()",homeDTO.getTitleRoom());

            }
            catch (IllegalArgumentException e)
            {}
            holder.itemPostpollBinding.viewCountTV.setText(homeDTO.getViewCountRoom());
            holder.itemPostpollBinding.likeCountTV.setText(homeDTO.getLikeCountRoom());
            holder.itemPostpollBinding.comCountTV.setText(homeDTO.getCommentCountRoom());




            if (homeDTO.getLikestatusRoom().equalsIgnoreCase("0")) {
                holder.itemPostpollBinding.likeImg.setImageResource(R.drawable.like_dash);
            } else {
                holder.itemPostpollBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
            }

            if (homeDTO.getBookmarkstatusRoom().equalsIgnoreCase("0")) {
            //    homeDTO.setBookmarkSelected(false);
                holder.itemPostpollBinding.favorite.setImageResource(R.drawable.favorite);
            } else {
               // homeDTO.setBookmarkSelected(true);
                holder.itemPostpollBinding.favorite.setImageResource(R.drawable.favorite_feel);
            }


            if (homeDTO.getPosttypeRoom().equalsIgnoreCase("1")) {
                String username = homeDTO.getUserNameRoom();
                holder.itemPostpollBinding.postcardLL.setVisibility(View.VISIBLE);
                holder.itemPostpollBinding.polecardRLL.setVisibility(View.GONE);

                if (username.isEmpty()) {
                    holder.itemPostpollBinding.userNameTV.setText("H1BQ User");
                } else {
                    holder.itemPostpollBinding.userNameTV.setText(username);
                }

                if (!homeDTO.getImageRoom().isEmpty()) {
                    holder.itemPostpollBinding.postcardImg.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(homeDTO.getImageRoom())
                            .placeholder(R.color.gray)
                            .error(R.color.gray).into(holder.itemPostpollBinding.postcardImg);

                } else {
                    holder.itemPostpollBinding.postcardImg.setVisibility(View.GONE);
                }


                String description="";
                try {
                    description   =fromBase64(homeDTO.getDescriptionRoom());

                }
                catch (IllegalArgumentException e){}



                SpannableStringBuilder sb1 = new SpannableStringBuilder(" See More");



                if (description.length() >= 55)
                {
                    String subDesc = description.substring(0, 50);
                    SpannableStringBuilder sb = new SpannableStringBuilder(subDesc);
                    Matcher matcher = p.matcher(subDesc);
                    while (matcher.find()) {

                        final String s = matcher.group();
                        Log.e("matchertext",s);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View view)
                            {
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

                } else
                {
                    final SpannableStringBuilder sb = new SpannableStringBuilder(description);
                    Matcher matcher = p.matcher(description);

                    while (matcher.find()) {
                        final String s = matcher.group();

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
                String username = homeDTO.getUserNameRoom();
                holder.itemPostpollBinding.postcardLL.setVisibility(View.GONE);
                holder.itemPostpollBinding.polecardRLL.setVisibility(View.VISIBLE);
                if (username.isEmpty()) {
                    holder.itemPostpollBinding.polluserNameTV.setText("H1BQ User");
                } else {
                    holder.itemPostpollBinding.polluserNameTV.setText(username);
                }

                holder.itemPostpollBinding.votesTV.setText(homeDTO.getViewCountRoom()+" Votes");



            }


            if (!homeDTO.getNoOfDaysCountRoom().isEmpty())
            {
                dateStr = homeDTO.getNoOfDaysCountRoom();
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
                }
                catch (NullPointerException e){ e.printStackTrace();}

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


                holder.itemPostpollBinding.cardviewPostcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //openPostDetailDialog(homeDTO.getPostidRoom(),mContext);
                        isConnected = ConnectivityReceiver.isConnected();
                        if (isConnected)
                        {
//                        Intent intent=new Intent(mContext,HomeActivity.class);
//                        mContext.startActivity(intent);
                        }
                        else
                        {
                            openPostDetailDialog(homeDTO.getPostidRoom(),mContext);

                        }


                    }
                });





            }





        }

    }





    public static void openPostDetailDialog(final String postId, final Context mContext)
    {

        final Dialog detailDialog = new Dialog(mContext, R.style.full_screen_dialog);
        Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
                Pattern.CASE_INSENSITIVE);
        final ItemPostdetailBinding itemPostdetailBinding = DataBindingUtil.inflate(LayoutInflater.from(detailDialog.getContext()),
                R.layout.item_postdetail, null, false);
        final FullSreenImageDialog fullSreenImageDialog = new FullSreenImageDialog();
        SessionManager sessionManager= new SessionManager(mContext.getApplicationContext());
        TimeConversion timeConversion= new TimeConversion();


        detailDialog.setContentView(itemPostdetailBinding.getRoot());
        detailDialog.setCanceledOnTouchOutside(false);
        detailDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        detailDialog.show();



        List<User> homeList=HomeActivity.myAppDatabase.myDao().getUsersDetail(postId);
        String title= homeList.get(0).getTitleRoom();
        String detailDescription="";
        try {
            detailDescription = fromBase64(homeList.get(0).getDescriptionRoom());

        } catch (IllegalArgumentException e) {
        }
        String username= homeList.get(0).getUserNameRoom();
        final String postimage=homeList.get(0).getImageRoom();
        String bookmarkstatus=homeList.get(0).getBookmarkstatusRoom();
        String likestatus=homeList.get(0).getLikestatusRoom();
        String likecount=homeList.get(0).getLikeCountRoom();
        String commentcount=homeList.get(0).getCommentCountRoom();
        String days=homeList.get(0).getNoOfDaysCountRoom();
        String postType=homeList.get(0).getPosttypeRoom();
        String answer=homeList.get(0).getAnswers();


        Toolbar toolbar_main = detailDialog.findViewById(R.id.toolbar_main);
        TextView titletoolTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        LinearLayout back_LL = toolbar_main.findViewById(R.id.back_LL);
        ImageView edit_img = toolbar_main.findViewById(R.id.edit_img);
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
            public void onClick(View view) {
            detailDialog.dismiss();
            }
        });

        itemPostdetailBinding.mainlinear.setVisibility(View.VISIBLE);
        itemPostdetailBinding.nointernet.setVisibility(View.VISIBLE);
        itemPostdetailBinding.comCountTV.setText(commentcount);
        itemPostdetailBinding.titleDetailTV.setText(title);
        if (username.isEmpty()) {
            itemPostdetailBinding.userNameTV.setText("H1BQ User");
            itemPostdetailBinding.polluserNameTV.setText("H1BQ User");

        } else {
            itemPostdetailBinding.userNameTV.setText(username);
            itemPostdetailBinding.polluserNameTV.setText(username);
        }


        final SpannableStringBuilder sb = new SpannableStringBuilder(detailDescription);
        Matcher matcher = p.matcher(detailDescription);

        while (matcher.find()) {
            final String s = matcher.group() ;

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

        if (likestatus.equalsIgnoreCase("0")) {

            itemPostdetailBinding.likeImg.setImageResource(R.drawable.like_dash);
        } else {

            itemPostdetailBinding.likeImg.setImageResource(R.drawable.like_dash_blue);
        }

        if (bookmarkstatus.equalsIgnoreCase("0")) {

            itemPostdetailBinding.favorite.setImageResource(R.drawable.favorite);
        } else {
            itemPostdetailBinding.favorite.setImageResource(R.drawable.favorite_feel);
        }

        itemPostdetailBinding.likeCountTV.setText(likecount);
        itemPostdetailBinding.commentCount.setText(commentcount);

        SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null, sendDate = null;
        String d2 = "";
        try {
            date = (Date) sdf.parse(days);
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


        if (sessionManager.getUserStatus().get(SessionManager.KEY_USER_STATUS).equalsIgnoreCase("1"))
        {

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


            itemPostdetailBinding.postcardImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ((Activity) mContext).getIntent();
                    intent.putExtra("image", postimage);
                    fullSreenImageDialog.show(((Activity)mContext).getFragmentManager(), "1");
                }
            });


           ArrayList aList= new ArrayList(Arrays.asList(answer.split(",")));


            OptionSelectAdp optionSelectAdp = new OptionSelectAdp(mContext,aList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
            itemPostdetailBinding.recyclerviewOption.addItemDecoration(new RecycleviewDecorator(2, dpToPx(10,mContext), false));
            itemPostdetailBinding.recyclerviewOption.setLayoutManager(gridLayoutManager);
            itemPostdetailBinding.recyclerviewOption.setItemAnimator(new DefaultItemAnimator());
            itemPostdetailBinding.recyclerviewOption.setAdapter(optionSelectAdp);





        }


    }


    public static int dpToPx(int dp, Context mContext) {

        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemPostpollBinding itemPostpollBinding;

        public MyViewHolder(ItemPostpollBinding itemView) {
            super(itemView.getRoot());
            itemPostpollBinding = itemView;
        }

    }





//    @Override
//    public int getItemCount() {
//
//        if (homeDTOArrayList.size()<30)
//        return homeDTOArrayList.size();
//        else
//            return 30;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//
//        return super.getItemViewType(position);
//    }


    @Override
    public int getItemCount() {
        if (homeDTOArrayList.size()<30)
            return homeDTOArrayList.size()+1;
        else
            return 31;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= homeDTOArrayList.size()) {
            // This is where we'll add footer.
            return STATIC_FOOTER;
        }



        return super.getItemViewType(position);
    }




    @Override
    public long getItemId(int position) {
        return position;
    }







    public static class OptionSelectAdp extends RecyclerView.Adapter<OptionSelectAdp.ViewHolderPollAdapter>
    {
        private Context mcontex;
        ArrayList answer;
        private List<Poll_OptionData> mPollOptionData;


        public OptionSelectAdp(Context mcontex, ArrayList answer) {
            this.mcontex = mcontex;
            this.answer=answer;
            mPollOptionData = new ArrayList<>();

        }

        @NonNull
        @Override
        public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater mInflater = LayoutInflater.from(mcontex);
            view = mInflater.inflate(R.layout.item_poll_option, parent, false);
            return new ViewHolderPollAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

            for(int i=0;i<answer.size();i++)
            {
                Poll_OptionData pollOptionData = new Poll_OptionData();
               pollOptionData.setOptionTV(answer.get(i)+"");
               mPollOptionData.add(pollOptionData);
            }

            holder.option1TV.setText(mPollOptionData.get(position).getOptionTV());


        }

        @Override
        public int getItemCount() {
            return answer != null ? answer.size() : 0;
        }

        public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

            TextView option1TV;
            LinearLayout optionLLO, circleA;
            ImageView imgradio;

            private Poll_OptionData mmdata;


            public ViewHolderPollAdapter(View itemView) {
                super(itemView);

                circleA = (LinearLayout) itemView.findViewById(R.id.circleA);
                optionLLO = (LinearLayout) itemView.findViewById(R.id.optionLLO);
                option1TV = (TextView) itemView.findViewById(R.id.option1TV);
                imgradio = (ImageView) itemView.findViewById(R.id.imgradio);

            }


        }


    }






}

