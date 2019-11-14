package com.dbvertex.company.h1bq.Fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Adapter.SearchAdapter;
import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.Util.Endpoints;
import com.dbvertex.company.h1bq.model.SearchDTO;
import com.dbvertex.company.h1bq.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    EditText titleET;
    LinearLayout search_LL;
    RecyclerView recycler_search;
    ArrayList<SearchDTO> searchDTOArrayList;
    RequestQueue requestQueue;
    TextView noresultfound;
    SearchAdapter searchAdapter;
    SessionManager sessionManager;
    TopSearchAdapter topSearchAdapter;
    ProgressDialog dialog;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar_main = view.findViewById(R.id.toolbar_search);
        titleET = toolbar_main.findViewById(R.id.titleET);
        search_LL = toolbar_main.findViewById(R.id.search_LL);
        recycler_search = view.findViewById(R.id.recycler_search);
        requestQueue = Volley.newRequestQueue(getActivity());
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        dialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        noresultfound = view.findViewById(R.id.noresultfound);
        searchDTOArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_search.setLayoutManager(linearLayoutManager);



        searchAdapter = new SearchAdapter((HomeActivity) getActivity(), searchDTOArrayList);
        topSearchAdapter = new TopSearchAdapter(getActivity(), searchDTOArrayList);


//        titleET.addTextChangedListener(new TextWatcher()
//        {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                loadSearchList();
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////                loadSearchList();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });


        titleET.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                loadSearchList();
                return false;

            }
        });

        search_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!titleET.getText().toString().isEmpty()) {
                    searchList(titleET.getText().toString());
                }
            }
        });


    }


    private void loadSearchList()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.SEARCH_LIST,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e("response", response);
                        searchDTOArrayList.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");


                            if (status == 200 && message.equals("success")) {
                                JSONArray data = obj.getJSONArray("Data");
                                if (data.length() > 0) {
                                    recycler_search.setVisibility(View.VISIBLE);
                                    noresultfound.setVisibility(View.GONE);

                                    for (int x = 0; x < data.length(); x++) {
                                        JSONObject dataJSONObject = data.getJSONObject(x);

                                        SearchDTO searchDTO = new SearchDTO();
//                                    searchDTO.setPostId(dataJSONObject.getString("id"));
                                        searchDTO.setTitle(dataJSONObject.getString("keyword"));
//                                    searchDTO.setBucketId(dataJSONObject.getString("id"));
                                        searchDTOArrayList.add(searchDTO);

                                    }
                                    recycler_search.setAdapter(topSearchAdapter);
                                    topSearchAdapter.notifyDataSetChanged();
                                } else {
                                    recycler_search.setVisibility(View.GONE);
                                    noresultfound.setVisibility(View.VISIBLE);
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }


    private void searchList(final CharSequence s) {
        dialog.setMessage("Loading..");
        dialog.setCancelable(true);
        dialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.SEARCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        dialog.dismiss();

                        searchDTOArrayList.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");

// {"Status":200,"Message":"success","Data":[]}
                            if (status == 200 && message.equals("success")) {

                                JSONArray data = obj.getJSONArray("Data");
                                if (data.length() > 0) {
                                    recycler_search.setVisibility(View.VISIBLE);
                                    noresultfound.setVisibility(View.GONE);
                                    for (int x = 0; x < data.length(); x++) {
                                        JSONObject dataJSONObject = data.getJSONObject(x);

                                        SearchDTO searchDTO = new SearchDTO();
                                        searchDTO.setPostId(dataJSONObject.getString("post_id"));
                                        searchDTO.setTitle(dataJSONObject.getString("title"));
                                        searchDTO.setBucketId(dataJSONObject.getString("bucket"));
                                        searchDTO.setUserId(dataJSONObject.getString("user_id"));
                                        searchDTO.setPostType(dataJSONObject.getString("post_type"));
                                        searchDTOArrayList.add(searchDTO);


                                    }
                                    recycler_search.setAdapter(searchAdapter);
                                    searchAdapter.notifyDataSetChanged();
                                } else {
                                    recycler_search.setVisibility(View.GONE);
                                    noresultfound.setVisibility(View.VISIBLE);
                                }


                            }


                        } catch (Exception ex) {
                            dialog.dismiss();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();

                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("keyword", s + "");
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("offset", "0");
                Log.e("params", params.toString());

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }


    public class TopSearchAdapter extends RecyclerView.Adapter<TopSearchAdapter.ViewHolderProgressAdapter> {

        private Context mcontex;
        private List<SearchDTO> searchDTOS;

        public TopSearchAdapter(Context mcontex, List<SearchDTO> searchDTOS) {
            this.mcontex = mcontex;
            this.searchDTOS = searchDTOS;
        }

        @NonNull
        @Override
        public ViewHolderProgressAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;
            LayoutInflater mInflater = LayoutInflater.from(mcontex);
            view = mInflater.inflate(R.layout.item_search, parent, false);
            return new ViewHolderProgressAdapter(view);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderProgressAdapter holder, final int position) {
            holder.title.setText(searchDTOS.get(position).getTitle());
            holder.bucket.setVisibility(View.GONE);
            holder.ll.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    titleET.setText(searchDTOS.get(position).getTitle());
                }
            });
        }


        @Override
        public int getItemCount() {
            return searchDTOS.size();
        }

        public class ViewHolderProgressAdapter extends RecyclerView.ViewHolder {

            TextView title, bucket;
            LinearLayout ll;

            public ViewHolderProgressAdapter(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                bucket = (TextView) itemView.findViewById(R.id.bucket);
                ll = itemView.findViewById(R.id.ll);

            }
        }
    }


}
