package com.dbvertex.company.h1bq.FontStyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by admin on 22-11-2017.
 */

@SuppressLint("AppCompatCustomView")
public class EditTextFontStyle extends EditText
{
    public EditTextFontStyle(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public EditTextFontStyle(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public EditTextFontStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Regular.ttf");
        setTypeface(customFont);
    }
}

