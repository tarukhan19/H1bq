package com.dbvertex.company.h1bq;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class HideKeyboard {
    public static void hideKeyboard(Context mContext)
    {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) mContext).getWindow()
                .getCurrentFocus().getWindowToken(), 0);
    }
}
