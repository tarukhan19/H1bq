package com.dbvertex.company.h1bq.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.ProductDetailActivity;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.Util.TimeConversion;
import com.dbvertex.company.h1bq.model.NotificationListDto;
import com.dbvertex.company.h1bq.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminNotificationFragment extends Fragment {
    RecyclerView recycler_notify;
    static   ArrayList<NotificationListDto> notificationListDtos;
    static  RequestQueue requestQueue;
    static SessionManager sessionManager;
    static int offset=0;
    static String offsetString,from;
    static boolean isConnected;

    static  ProgressDialog dialog;
    static NotificationListAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;
    static String dateStr;
    private TimeConversion timeConversion;
    TextView norecord;

    public AdminNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_notification, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        timeConversion = new TimeConversion();
        norecord= view.findViewById(R.id.norecord);

        recycler_notify = view.findViewById(R.id.notifyRecycleview);
        requestQueue = Volley.newRequestQueue(getActivity());
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        notificationListDtos = new ArrayList<>();
        adapter = new NotificationListAdapter( getActivity(), notificationListDtos);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_notify.setLayoutManager(linearLayoutManager);
        recycler_notify.setAdapter(adapter);

        offset = 0;
        offsetString = String.valueOf(offset);
        TimeZone timeZone = TimeZone.getDefault();
        final String name = timeZone.getID();
        Log.d("Time zone","="+name);



        if (isConnected)
        {
            loadNotifications(String.valueOf(name));
        }
        else
        {
            recycler_notify.setVisibility(View.GONE);
            norecord.setVisibility(View.VISIBLE);
            norecord.setText("No internet connection available.");

        }



        recycler_notify.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    if (isLoading) {

                        if (totalitemcount > previoustotal) {
                            isLoading = false;
                            previoustotal = totalitemcount;
                        }


                    }

                    if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold)) {
                        offset = offset + 1;
                        offsetString = String.valueOf(offset);
                        loadNotifications(String.valueOf(name));
                        isLoading = true;
                    }

                }


            }
        });

    }
    public void getNetwork(boolean isConnect)
    {
        isConnected=isConnect;
        Log.e("isconnecth1b",isConnect+"  "+isConnected);
    }
    private void loadNotifications(final String tz)
    {
//        dialog.setMessage("Loading..");
//        dialog.setCancelable(true);
//        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LOADadmin_NOTIFICATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // dialog.dismiss();
                        Log.e("LOAD_NOTIFICATIONS", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");



                            if (status==200 && message.equalsIgnoreCase("success"))
                            {
                                if (offset==0)
                                {
                                    notificationListDtos.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                recycler_notify.setVisibility(View.VISIBLE);
                                norecord.setVisibility(View.GONE);



                                JSONArray data=obj.getJSONArray("Data");

                                for (int i=0; i<data.length();i++)
                                {
                                    JSONObject jsonObject=data.getJSONObject(i);
                                    String id=jsonObject.getString("id");
                                    String sender_user=jsonObject.getString("sender_user");
                                    String msg=jsonObject.getString("message");
                                    String created_at=jsonObject.getString("created_at");
                                    String post_type=jsonObject.getString("post_type");

                                    NotificationListDto notificationListDto=new NotificationListDto();
                                    notificationListDto.setCreated_at(created_at);
                                    notificationListDto.setId(id);
                                    notificationListDto.setPost_id(jsonObject.getString("post_id"));
                                    notificationListDto.setMessage(msg);
                                    notificationListDto.setSender_user(sender_user);
                                    notificationListDto.setPost_Type(post_type);

                                    notificationListDtos.add(notificationListDto);

                                }
                                adapter.notifyDataSetChanged();
                            }


                            else
                            {
                               if (offset==0)
                               {
                                   recycler_notify.setVisibility(View.GONE);
                                   norecord.setVisibility(View.VISIBLE);

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
                        // dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID) );
                params.put("offset", offsetString);
                params.put("zone", tz);

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }



    public static void clearNotifications()
    {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CLEAR_NOTIFICATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("LOAD_NOTIFICATIONS", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
                            if (status==200 && message.equalsIgnoreCase("success"))
                            {
                                notificationListDtos.clear();
                                adapter.notifyDataSetChanged();
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
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID) );
                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolderProgressAdapter>
    {

        private Context mcontex;
        private List<NotificationListDto> mprogressData;
        long diff;

        public NotificationListAdapter(Context mcontex, List<NotificationListDto> mprogressData) {
            this.mcontex = mcontex;
            this.mprogressData = mprogressData;
        }

        @NonNull
        @Override
        public ViewHolderProgressAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;
            LayoutInflater mInflater = LayoutInflater.from(mcontex);
            view = mInflater.inflate(R.layout.item_notificationlist,parent,false);
            return new ViewHolderProgressAdapter(view);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderProgressAdapter holder, final int position)
        {
            holder.msg.setText(mprogressData.get(position).getMessage());

            String prodDate = mprogressData.get(position).getCreated_at();


            if (!prodDate.isEmpty()) {
                dateStr = mprogressData.get(position).getCreated_at();
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

                timeConversion.getTimeAgo(position, holder.date, mcontex, sendDate);
            }


            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mprogressData.get(position).getPost_Type().equalsIgnoreCase("null")) {
                        Intent intent = new Intent(mcontex, ProductDetailActivity.class);
                        intent.putExtra("from", "notiList");
                        intent.putExtra("post_id", mprogressData.get(position).getPost_id());
                        mcontex.startActivity(intent);
                    }
                }
            });

            //     holder.date.setText(mprogressData.get(position).getCreated_at());

        }


        public int getDays(String begin1) throws ParseException {
            long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            long begin = dateFormat.parse(begin1).getTime();
            long end = new Date().getTime(); // 2nd date want to compare
            diff = (end - begin) / (MILLIS_PER_DAY);


            return (int) diff;
        }


        @Override
        public int getItemCount() {
            return mprogressData.size();
        }

        public class ViewHolderProgressAdapter extends RecyclerView.ViewHolder{

            TextView msg,date;
            CardView cardview;
            public ViewHolderProgressAdapter(View itemView) {
                super(itemView);
                msg =(TextView)itemView.findViewById(R.id.msg);
                date =(TextView)itemView.findViewById(R.id.date);
                cardview=itemView.findViewById(R.id.cardview);
            }
        }
    }


}
