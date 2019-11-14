//package com.dbvertex.company.h1bq;
//
//import android.content.Context;
//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.core.view.ViewCompat;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//
//public class FloatBehaviour extends CoordinatorLayout.Behavior<ImageView> {
//
//    public FloatBehaviour() {
//        super();
//    }
//
//    public FloatBehaviour(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
//        boolean dependsOn = dependency instanceof FrameLayout;
//        return dependsOn;
//    }
//
//    @Override
//    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, ImageView child, View directTargetChild, View target,
//                                       int nestedScrollAxes) {
//        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
//    }
//
//    @Override
//    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, ImageView child, View target, int dx, int dy, int[] consumed) {
//        if (dy < 0) {
//            hideBottomNavigationView(child);
//
//        } else if (dy > 0) {
//            showBottomNavigationView(child);
//
//        }
//    }
//
//    private void hideBottomNavigationView(ImageView view) {
//        view.animate().translationY(view.getHeight());
//    }
//
//    private void showBottomNavigationView(ImageView view) {
//        view.animate().translationY(0);
//    }
//}
//
