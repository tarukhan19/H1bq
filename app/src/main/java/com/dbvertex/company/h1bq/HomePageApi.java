package com.dbvertex.company.h1bq;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dbvertex.company.h1bq.Adapter.HomeAdapter;
import com.dbvertex.company.h1bq.Adapter.HomeRoomAdapter;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.model.AnsListDTO;
import com.dbvertex.company.h1bq.model.HomeDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageApi implements ConnectivityReceiver.ConnectivityReceiverListener{

    //ProgressDialog dialog;
    public ArrayList<HomeDTO> list;
    public HomeAdapter adapter;
    boolean isConnected;

    public void loadList(RequestQueue requestQueue, final String offsetString, final String bucketId, final String userid,
                         final ArrayList<HomeDTO> list, final HomeAdapter adapter, final RecyclerView recyclerStory,
                          final HomeRoomAdapter homeRoomAdapter)
    {
        this.list = list;
        this.adapter = adapter;

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.POSTPOLL_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("loadList", response + "");


                        try {

                            //  {"Status":200,"Message":"success","Data":[]}
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");

                            String message = obj.getString("Message");


                            if (status == 200 && message.equals("success"))
                            {
                                String ans="";
                                JSONArray data = obj.getJSONArray("Data");


                                  if (offsetString.equalsIgnoreCase("0"))
                                  {
                                      list.clear();

                                  }

                                for (int x = 0; x < data.length(); x++)
                                {

                                    final JSONObject dataJSONObject = data.getJSONObject(x);

                                    JSONArray ansArray = dataJSONObject.getJSONArray("answer");

                                    HomeDTO homeDTO = new HomeDTO();
                                    User user = new User();
                                    user.setUserNameRoom(dataJSONObject.getString("username"));
                                    user.setPostidRoom(dataJSONObject.getString("post_id"));
                                    user.setUserIdRoom(dataJSONObject.getString("user_id"));
                                    user.setTitleRoom(dataJSONObject.getString("title"));
                                    user.setDescriptionRoom(dataJSONObject.getString("dicription"));
                                    user.setPosttypeRoom(dataJSONObject.getString("post_type"));
                                    user.setImageRoom(dataJSONObject.getString("post_image"));
                                    user.setAnsslecetiontypeRoom(dataJSONObject.getString("ans_selection_typ"));
                                    user.setNoOfDaysCountRoom(dataJSONObject.getString("add_date"));
                                    user.setVotesCountRoom(dataJSONObject.getString("count_vote"));
                                    user.setCommentCountRoom(dataJSONObject.getString("count_comment"));
                                    user.setLikeCountRoom(dataJSONObject.getString("count_like"));
                                    user.setViewCountRoom(dataJSONObject.getString("count_view"));
                                    user.setBookmarkstatusRoom(dataJSONObject.getString("bookmark_status"));
                                    user.setLikestatusRoom(dataJSONObject.getString("like_status"));
                                    user.setBucketIdRoom(bucketId);


                                    homeDTO.setUserName(dataJSONObject.getString("username"));
                                    homeDTO.setPostId(dataJSONObject.getString("post_id"));
                                    homeDTO.setUserId(dataJSONObject.getString("user_id"));
                                    homeDTO.setTitle(dataJSONObject.getString("title"));
                                    homeDTO.setDescription(dataJSONObject.getString("dicription"));
                                    homeDTO.setPostType(dataJSONObject.getString("post_type"));
                                    homeDTO.setImage(dataJSONObject.getString("post_image"));
                                    homeDTO.setDescLength(dataJSONObject.getString("dicription").length());
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
                                        JSONObject jsonObject = ansArray.getJSONObject(j);
                                        String answer=jsonObject.getString("anser");

                                        ans+=(answer);
                                        if (j < ansArray.length() - 1) {
                                            ans+=(",");
                                        }

                                    }

                                    user.setAnswers(ans);
                                    homeDTO.setAnswers(dtos);

                                  list.add(homeDTO);

                                  String result=  HomeActivity.myAppDatabase.myDao().getPostId(dataJSONObject.getString("post_id"));
                                  if (result==null)
                                  HomeActivity.myAppDatabase.myDao().insertMultipleMovies(user);

                                }
                                adapter.notifyDataSetChanged();
                                homeRoomAdapter.notifyDataSetChanged();

                            } else if (status == 0 && message.equalsIgnoreCase("failed")) {
//{"Status":200,"Message":"success","Data":[]}

                            } else if (status == 0 && message.equalsIgnoreCase("Record Not Found")) {
                            }

                        } catch (Exception ex) {

                            Log.e("exception",ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof NoConnectionError)
                        {

                            homeRoomAdapter.notifyDataSetChanged();

                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //user_id & offset  & buket_id
                params.put("buket_id", bucketId);
                params.put("user_id", userid);
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;

    }
}
