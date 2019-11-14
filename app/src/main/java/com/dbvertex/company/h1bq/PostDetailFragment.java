package com.dbvertex.company.h1bq;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.session.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class PostDetailFragment extends Fragment {
    int offset=0;
    String offsetString;
    int direction=0;
    public SessionManager session;
    public static final int DISMISS_TIMEOUT = 2000;
    RecyclerView recyclerView;
    Intent intent;
    String postId,from;
    ProgressDialog pd;
    RequestQueue requestQueue;
    long diff ;
    String likecount;
    LinearLayout cardview_postcard;
    ImageView favorite,moreoption,postcard_Img,likeImg,shareImg;
    TextView userNameTV,titleTV,descripTV,votesTV,viewCountTV,comCountTV,daysCount,likeCountTV;
    LinearLayout postcard_LL,shareLL,likeLL;
    RelativeLayout polecard_RLL;
    public PostDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(View itemView, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(itemView, savedInstanceState);
        session = new SessionManager(getActivity());
        intent=getActivity().getIntent();
        recyclerView=(RecyclerView)itemView.findViewById(R.id.recyclerview_comm);
        requestQueue = Volley.newRequestQueue(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        pd= new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        cardview_postcard=itemView.findViewById(R.id.cardview_postcard);
        favorite=itemView.findViewById(R.id.favorite);
        moreoption=itemView.findViewById(R.id.moreoption);

        userNameTV=itemView.findViewById(R.id.userNameTV);
        titleTV=itemView.findViewById(R.id.titleTV);
        descripTV=itemView.findViewById(R.id.descripTV);
        postcard_Img=itemView.findViewById(R.id.postcard_Img);

        viewCountTV=itemView.findViewById(R.id.viewCountTV);
        likeCountTV=itemView.findViewById(R.id.likeCountTV);
        comCountTV=itemView.findViewById(R.id.comCountTV);
        daysCount=itemView.findViewById(R.id.daysCount);
        shareImg=itemView.findViewById(R.id.shareImg);
        likeImg=itemView.findViewById(R.id.likeImg);
        likeLL=itemView.findViewById(R.id.likeLL);
        recyclerView.setLayoutManager(linearLayoutManager);

        postId=intent.getStringExtra("postid");
        from=intent.getStringExtra("from");

        likeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeAdd(postId);

            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest postRequest = new StringRequest ( Request.Method.POST, Endpoints.POSTPOLL_BOOKMARK,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try
                                {
                                    //{"status":200,"Message":"success","Data":"Like"}
                                    //{"Status":200,"Message":"success","Data":"Unlike"}
                                    //Log.e("bookmarkresponse",response);

                                    JSONObject obj = new JSONObject(response);
                                    int status=obj.getInt("Status");
                                    String msg=obj.getString("Message");
                                    String data=obj.getString("Data");
                                    if (status==200 && msg.equalsIgnoreCase("success"))
                                    {
                                        if (data.equalsIgnoreCase("Like"))
                                        {

                                            favorite.setImageResource( R.drawable.favorite_feel );
                                        }
                                        else if (data.equalsIgnoreCase("Unlike"))
                                        {
                                            favorite.setImageResource( R.drawable.favorite );

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

                        params.put("post_id",postId );
                        params.put("user_id",session.getuserId().get(SessionManager.KEY_USERID) );
                        return params;
                    }

                };
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                postRequest.setRetryPolicy(policy);
                requestQueue.add(postRequest);

            }
        });

        detailPost(postId);


    }

    private void likeAdd(final String postId) {
        StringRequest postRequest = new StringRequest ( Request.Method.POST, Endpoints.POSTPOLL_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {

                            //Log.e("likecount",likecount+"");
                            JSONObject obj = new JSONObject(response);
                            int status=obj.getInt("Status");
                            String msg=obj.getString("Message");
                            String data=obj.getString("Data");
                            if (status==200 && msg.equalsIgnoreCase("success"))
                            {
                                if (data.equalsIgnoreCase("Like"))
                                {

                                    likeImg.setImageResource( R.drawable.like_dash_blue );
                                    int c = Integer.parseInt(likecount) + 1;
                                    likeCountTV.setText(String.valueOf(c));
                                    //Log.e("like",c+"");
                                    Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();

                                }
                                else if (data.equalsIgnoreCase("Unlike"))
                                {
                                    likeImg.setImageResource( R.drawable.like_dash );
                                    int c = Integer.parseInt(likecount)  - 1;

                                    likeCountTV.setText(String.valueOf(c));


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

                params.put("post_id",postId );
                params.put("user_id",session.getuserId().get(SessionManager.KEY_USERID) );
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
                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                JSONObject dataObj=obj.getJSONObject("Data");
                                titleTV.setText(dataObj.getString("title"));
                                descripTV.setText(dataObj.getString("dicription"));
                                String bucketId=dataObj.getString("bucket");
                                String image=dataObj.getString("post_image");
                                //Log.e("image",image);

                                userNameTV.setText(dataObj.getString("username"));
                                likeCountTV.setText(dataObj.getString("count_like"));
                                comCountTV.setText(dataObj.getString("count_comment"));
                                viewCountTV.setText(dataObj.getString("count_view"));
                                if (! image.isEmpty())
                                {
                                    postcard_Img.setVisibility(View.VISIBLE);
                                    Picasso.with(getActivity()).load(image)
                                            .placeholder(R.color.gray)
                                            .error(R.color.gray).into(postcard_Img);

                                }
                                else
                                {
                                    postcard_Img.setVisibility(View.GONE);
                                }

                                if (dataObj.getString("like_status").equalsIgnoreCase("0"))
                                {

                                    likeImg.setImageResource(R.drawable.like_dash);
                                }
                                else
                                {

                                   likeImg.setImageResource(R.drawable.like_dash_blue);
                                }

                                if (dataObj.getString("bookmark_status").equalsIgnoreCase("0"))
                                {

                                    favorite.setImageResource(R.drawable.favorite);
                                }
                                else
                                {
                                    favorite.setImageResource(R.drawable.favorite_feel);
                                }

                                try
                                {
                                    getDays(dataObj.getString("add_date"));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                if (diff==0)
                                {        daysCount.setText("Today");}
                                else if (diff==1)
                                {
                                    daysCount.setText(diff +" day ago");
                                }
                                else
                                {
                                    daysCount.setText(diff +" days ago");
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

    private int getDays(String add_date) throws ParseException {
        long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.US);

        long begin = dateFormat.parse(add_date).getTime();
        long end = new Date().getTime(); // 2nd date want to compare
        diff = (end - begin) / (MILLIS_PER_DAY);


        return (int) diff;
    }


}
