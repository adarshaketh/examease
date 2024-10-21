package com.example.examease.helpers;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class Functions {
    static public SpannableString makeBold(String textData){
        SpannableString text = new SpannableString(textData);
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }
}
