package com.dbvertex.company.h1bq;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbvertex.company.h1bq.Fragment.AdminNotificationFragment;
import com.dbvertex.company.h1bq.Fragment.NotificationFragment;
import com.dbvertex.company.h1bq.databinding.FragmentNotiBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotiFragment extends Fragment {
    FragmentNotiBinding binding;
    ViewPagerAdapter viewPagerAdapter;
    Toolbar toolbar_main;
    ImageView edit_img;
    TextView titleTV;
    LinearLayout back_LL;
    public NotiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_noti, container, false);
        View view = binding.getRoot();
        toolbar_main = view.findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        edit_img = toolbar_main.findViewById(R.id.edit_img);
        edit_img.setImageResource(R.drawable.ic_del);
        titleTV.setText("Notifications");

        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationFragment.clearNotifications();
                AdminNotificationFragment.clearNotifications();

            }
        });

        back_LL.setVisibility(View.GONE);
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        binding.viewpager.setAdapter(viewPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewpager);

        setupViewPager(binding.viewpager);
        setupTabIcons();

    }


    @SuppressLint("ResourceType")
    private void setupTabIcons() {


        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabOne.setTextSize(10);
        binding.tabs.getTabAt(0).setCustomView(tabOne);
        tabOne.setText("Notification");


        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabTwo.setTextSize(10);
        binding.tabs.getTabAt(1).setCustomView(tabTwo);
        tabTwo.setText("Admin Notification");


    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new NotificationFragment(), "Notification");
        adapter.addFrag(new AdminNotificationFragment(), "Admin Notification");
        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
