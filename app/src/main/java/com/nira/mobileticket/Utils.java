package com.nira.mobileticket;

import android.view.View;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    static void showProgessBar(View v){
        v.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        v.findViewById(R.id.parent_view).setVisibility(View.GONE);
    }

    static void hideProgressBar(View v){
        v.findViewById(R.id.progress_bar).setVisibility(View.GONE);
        v.findViewById(R.id.parent_view).setVisibility(View.VISIBLE);
    }

    public static String convertTime(String time) {
        Date date = new Date(Long.valueOf(time));
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

}
