package com.dbvertex.company.h1bq.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dbvertex.company.h1bq.Adapter.HomeAdapter;
import com.dbvertex.company.h1bq.Adapter.HomeRoomAdapter;
import com.dbvertex.company.h1bq.HomeActivity;
import com.dbvertex.company.h1bq.HomePageApi;
import com.dbvertex.company.h1bq.Network.ConnectivityReceiver;
import com.dbvertex.company.h1bq.R;
import com.dbvertex.company.h1bq.constant.Constants;
import com.dbvertex.company.h1bq.databinding.FragmentMiscBinding;
import com.dbvertex.company.h1bq.model.HomeDTO;
import com.dbvertex.company.h1bq.session.SessionManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiscFragment extends Fragment {
    public ArrayList<HomeDTO> list;
    public HomeAdapter adapter;
    int offset = 0;
    String offsetString, from;
    public SessionManager session;
    public static final int DISMISS_TIMEOUT = 2000;
    HomePageApi homePageApi;
    FragmentMiscBinding fragmentMiscBinding;
    RequestQueue requestQueue;
    long totalRequestTime;
    private int mLoadedItems = 0;
    public HomeRoomAdapter homeRoomAdapter;
    static boolean isConnected;
    Vibrator vibe;

    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;
    LinearLayoutManager linearLayoutManager;

    public MiscFragment() {
        // Required I140Fragment public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentMiscBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_misc, container, false);
        View view = fragmentMiscBinding.getRoot();


        return view;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<HomeDTO>();
        vibe=(Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        adapter = new HomeAdapter((HomeActivity) getActivity(), list,"home");
        session = new SessionManager(getActivity());
        homePageApi = new HomePageApi();
        homeRoomAdapter = new HomeRoomAdapter((HomeActivity) getActivity(), Constants.MISC);
        requestQueue = Volley.newRequestQueue(getActivity());
        if (isConnected)
        {
            HomeActivity.myAppDatabase.myDao().deleteTable(Constants.MISC);

            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            fragmentMiscBinding.recyclerStory.setLayoutManager(linearLayoutManager);
            fragmentMiscBinding.recyclerStory.setAdapter(adapter);
            fragmentMiscBinding.recyclerStory.setHasFixedSize(true);



            offset=0;
            offsetString=String.valueOf(offset);
            addDataToList();
        }
        else
        {
            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            fragmentMiscBinding.recyclerStory.setLayoutManager(linearLayoutManager);
            fragmentMiscBinding.recyclerStory.setAdapter(homeRoomAdapter);
            fragmentMiscBinding.recyclerStory.setHasFixedSize(true);
            homeRoomAdapter.notifyDataSetChanged();
        }




        fragmentMiscBinding.toparrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentMiscBinding.recyclerStory.smoothScrollToPosition(0);
                animHide();

            }
        });

        fragmentMiscBinding.recyclerStory.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                            Log.e("reach", " Reached Last Item");
//                            fragmentHibBinding.recyclerStory.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {


//                                }
//                            }, totalRequestTime);
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

        fragmentMiscBinding.swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (isConnected){
                            vibe.vibrate(50);
                            fragmentMiscBinding.swiperefresh.setRefreshing(true);

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
                        fragmentMiscBinding.swiperefresh.setRefreshing(false);

                        }
                        else {
                        fragmentMiscBinding.swiperefresh.setRefreshing(false);
                    }

                    }
                }
        );


    }

    private void addDataToList() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                long mRequestStartTime = System.currentTimeMillis();

                homePageApi.loadList(requestQueue,offsetString, Constants.MISC,session.getLoginSession().get(SessionManager.KEY_USERID)
                        ,list,adapter, fragmentMiscBinding.recyclerStory, homeRoomAdapter);
                totalRequestTime = System.currentTimeMillis() - mRequestStartTime;

            }
        }, 0);
        //  homePageApi.loadList(getActivity(),offsetString, Constants.MISC,session.getLoginSession().get(SessionManager.KEY_USERID),list,adapter);


    }

    private void animShow() {
        // slide-up animation
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slidedown);

        if (fragmentMiscBinding.toparrow.getVisibility() == View.INVISIBLE) {
            fragmentMiscBinding.toparrow.setVisibility(View.VISIBLE);
            fragmentMiscBinding.toparrow.startAnimation(slideUp);
        }
    }

    private void animHide() {
        // slide-up animation
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slidedown);

        if (fragmentMiscBinding.toparrow.getVisibility() == View.VISIBLE) {
            fragmentMiscBinding.toparrow.setVisibility(View.INVISIBLE);
            fragmentMiscBinding.toparrow.startAnimation(slideUp);
        }
    }

    public void getNetwork(boolean isConnect)
    {
        isConnected=isConnect;
        Log.e("isconnecth1b",isConnect+"  "+isConnected);
    }
}
