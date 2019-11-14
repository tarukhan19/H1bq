package com.dbvertex.company.h1bq.Fragment;


import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Adapter.HomeAdapter;
import com.dbvertex.company.h1bq.Adapter.HomeRoomAdapter;
import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.HomePageApi;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.Network.MyApplication;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.model.HomeDTO;
import com.dbvertex.company.h1bq.session.SessionManager;

import com.dbvertex.company.h1bq.databinding.FragmentHibBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class HIBFragment extends Fragment {
    public ArrayList<HomeDTO> list;
    public HomeAdapter adapter;
    public HomeRoomAdapter homeRoomAdapter;
    RequestQueue requestQueue;
    public SessionManager session;
    public static final int DISMISS_TIMEOUT = 2000;
    HomePageApi homePageApi;
    FragmentHibBinding fragmentHibBinding;
    int offset = 0, scrollPosition;
    String offsetString, from;
    LinearLayoutManager linearLayoutManager;
    static boolean isConnected;
    Vibrator vibe;

    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;
    public HIBFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        fragmentHibBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_hib, container, false);
        View view = fragmentHibBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<HomeDTO>();
        adapter = new HomeAdapter((HomeActivity) getActivity(), list, "home");
        homeRoomAdapter=new HomeRoomAdapter((HomeActivity) getActivity(),Constants.H1B);
        session = new SessionManager(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());
        homePageApi = new HomePageApi();
        vibe=(Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        fragmentHibBinding.toparrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentHibBinding.recyclerStory.smoothScrollToPosition(0);
                animHide();

            }
        });

        if (isConnected)
        {
            HomeActivity.myAppDatabase.myDao().deleteTable(Constants.H1B);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            fragmentHibBinding.recyclerStory.setLayoutManager(linearLayoutManager);
            fragmentHibBinding.recyclerStory.setAdapter(adapter);
            fragmentHibBinding.recyclerStory.setHasFixedSize(true);
            offset = 0;
            offsetString = String.valueOf(offset);

            addDataToList();

        }

        else
        {
            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            fragmentHibBinding.recyclerStory.setLayoutManager(linearLayoutManager);
            fragmentHibBinding.recyclerStory.setAdapter(homeRoomAdapter);
            fragmentHibBinding.recyclerStory.setHasFixedSize(true);
            homeRoomAdapter.notifyDataSetChanged();

        }

        if (isConnected) {

            fragmentHibBinding.recyclerStory.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
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
                        animShow();
                        if (isLoading) {

                            if (totalitemcount > previoustotal) {
                                isLoading = false;
                                previoustotal = totalitemcount;

                            }


                        }

                        if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold)) {
                            offset = offset + 1;
                            offsetString = String.valueOf(offset);
                            addDataToList();
                            isLoading = true;
                        }

                    }

                    if (pastVisibleItems == 0) {
                        animHide();
                    }

                }
            });
        }
        fragmentHibBinding.swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        Log.e("swiperefreshisConnected",isConnected+"");
                        if (isConnected)
                        {
                            vibe.vibrate(50);

                            adapter.notifyDataSetChanged();
                            offset = 0;
                            offsetString = String.valueOf(offset);
                            addDataToList();
                            isLoading = true;
                            pastVisibleItems = 0;
                            visibleItemCount = 0;
                            totalitemcount = 0;
                            previoustotal = 0;
                            view_threshold = 10;
                            fragmentHibBinding.swiperefresh.setRefreshing(false);
                        }
                        else
                        {
                            fragmentHibBinding.swiperefresh.setRefreshing(false);
                        }


                    }
                }
        );


    }

    private void addDataToList()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                homePageApi.loadList(requestQueue, offsetString, Constants.H1B,
                        session.getLoginSession().get(SessionManager.KEY_USERID),
                        list, adapter, fragmentHibBinding.recyclerStory,homeRoomAdapter);
            }
        }, 0);

    }

    private void animShow() {
        // slide-up animation
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slidedown);

        if (fragmentHibBinding.toparrow.getVisibility() == View.INVISIBLE) {
            fragmentHibBinding.toparrow.setVisibility(View.VISIBLE);
            fragmentHibBinding.toparrow.startAnimation(slideUp);
        }
    }

    private void animHide() {
        // slide-up animation
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slidedown);

        if (fragmentHibBinding.toparrow.getVisibility() == View.VISIBLE) {
            fragmentHibBinding.toparrow.setVisibility(View.INVISIBLE);
            fragmentHibBinding.toparrow.startAnimation(slideUp);
        }
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    public void getNetwork(boolean isConnect)
    {
       isConnected=isConnect;
       Log.e("isconnecth1b",isConnect+"  "+isConnected);
    }

    @Override
    public void onResume() {
        super.onResume();
//        MyApplication.getInstance().setConnectivityListener(this);
    }






}
