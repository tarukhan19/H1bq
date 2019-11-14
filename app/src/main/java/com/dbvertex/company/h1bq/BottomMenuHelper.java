//package com.dbvertex.company.hibq;
//
//import android.content.Context;
//import android.support.annotation.IdRes;
//import android.support.design.internal.BottomNavigationItemView;
//import android.support.design.internal.BottomNavigationMenuView;
//import android.support.design.widget.BottomNavigationView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class BottomMenuHelper {
//
//    public static void showBadge(Context context, BottomNavigationView bottomNavigationView, @IdRes final int itemId, String value) {
//        BottomNavigationMenuView bottomNavigationMenuView =
//                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
//        View v = bottomNavigationMenuView.getChildAt(3);
//        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
//        View badge = LayoutInflater.from(context)
//                .inflate(R.layout.layout_news_badge, bottomNavigationMenuView, false);
//
//        if (value.equalsIgnoreCase("0")) {
//            Toast.makeText(context, "remove", Toast.LENGTH_SHORT).show();
//            ((ViewGroup)v.getParent()).removeView(badge);
//        } else {
//
//            final TextView text = badge.findViewById(R.id.notificationsbadge);
//            text.setText(value);
//            itemView.addView(badge);
//        }
//
//
//    }
//
//    public void removeBadge(BottomNavigationView navigationView, int index) {
//        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
//        View v = bottomNavigationMenuView.getChildAt(index);
//        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
//        itemView.removeViewAt(itemView.getChildCount() - 1);
//    }
//}
