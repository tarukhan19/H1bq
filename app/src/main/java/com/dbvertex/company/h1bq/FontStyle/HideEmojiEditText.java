package com.dbvertex.company.h1bq.FontStyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class HideEmojiEditText extends EditText
{
    public HideEmojiEditText(Context context) {
        super(context);
        init();
        applyCustomFont(context);
    }

    public HideEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        applyCustomFont(context);
    }

    public HideEmojiEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Regular.ttf");
        setTypeface(customFont);
    }

    private void init() {
        setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }
}

