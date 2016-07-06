package ru.brainworkout.sandow_gym.commons;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ivan on 20.05.2016.
 */
public class Common {

    public static Date ConvertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static String ConvertDateToString(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate="";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {}

        return sDate;
    }

    public static void blink(View v) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        //anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatCount(1);
        v.startAnimation(anim);
    }
}
