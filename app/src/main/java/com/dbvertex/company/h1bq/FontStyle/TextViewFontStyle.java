package com.dbvertex.company.h1bq.FontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 22-11-2017.
 */

public class TextViewFontStyle extends TextView {

    public TextViewFontStyle(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public TextViewFontStyle(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public TextViewFontStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Regular.ttf");
        setTypeface(customFont);
    }
}
