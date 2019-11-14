package com.dbvertex.company.h1bq;

import android.content.Context;

import com.dbvertex.company.h1bq.Adapter.HomeAdapter;
import com.dbvertex.company.h1bq.Adapter.MainCommentAdapter;
import com.dbvertex.company.h1bq.Adapter.SearchAdapter;
import com.dbvertex.company.h1bq.Adapter.SearchMainCommentAdp;

public interface MyInterface {
    public void openGallery(Context context, HomeAdapter homeAdapter);
    public void openGallery(Context context, SearchAdapter homeAdapter);
    public void openGallery(Context context, MainCommentAdapter homeAdapter);
    public void openGallery(Context context, SearchMainCommentAdp homeAdapter);

}