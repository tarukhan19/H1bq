package com.dbvertex.company.h1bq;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.model.HomeDTO;
import com.dbvertex.company.h1bq.session.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Apis {

    private SessionManager sessionManager;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    ArrayList<HomeDTO> homeDTOArrayList;

    public void bookmarkAdd(final String postId, final int adapterPosition, final Context mContext, RequestQueue requestQueue)
    {
        sessionManager = new SessionManager(mContext.getApplicationContext());
        this.requestQueue = requestQueue;
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

    public void commentLikeAdd(final String CommentId, final String postId, final String commentype,Context mContext)
    {
        sessionManager = new SessionManager(mContext.getApplicationContext());
        requestQueue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LIKE_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                        Log.e("commentresponse",response);
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String msg = obj.getString("Message");
                            String data = obj.getString("Data");
                            if (status == 200 && msg.equalsIgnoreCase("success")) {


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
                params.put("comment_id", CommentId);
                params.put("like_cooment_type", commentype);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));

                Log.e("params",params+"");
                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    public void likeAdd(final String postId, Context mContext, RequestQueue requestQueue) {
        sessionManager = new SessionManager(mContext.getApplicationContext());
        this.requestQueue = requestQueue;
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.POSTPOLL_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Log.e("response",response);

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String msg = obj.getString("Message");
                            String data = obj.getString("Data");
                            if (status == 200 && msg.equalsIgnoreCase("success")) {


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




    public void deletePost(final String postId, final int position, final Context context, final ArrayList<HomeDTO> homeDTOArrayList,
                           RequestQueue requestQueue)
    {
        this.homeDTOArrayList=homeDTOArrayList;
        this.requestQueue=requestQueue;
        progressDialog=new ProgressDialog(context,R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(true);
        progressDialog.show();
        //Log.e("DetailDialPos",position+"");

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
                                final Dialog dialog = new Dialog(context, R.style.CustomDialog);
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
                                        //Log.e("DetailDialPos",position+"");

                                        dialog.dismiss();
                                        homeDTOArrayList.remove(position);
//                                        homeAdapter.notifyDataSetChanged();
//                                        homeAdapter.notifyItemChanged(position);


//                                        if (detailDialog.isShowing()) {
//                                            detailDialog.dismiss();
//                                        }
//                                        if (detailPollDialog.isShowing()) {
//                                            detailPollDialog.dismiss();
//                                        }
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
        this.requestQueue.add(postRequest);
    }


}
