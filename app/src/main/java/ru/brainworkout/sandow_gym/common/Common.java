package ru.brainworkout.sandow_gym.common;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.brainworkout.sandow_gym.database.entities.User;

public class Common {

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static User mCurrentUser;

    public static Date ConvertStringToDate(final String date,final String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static String ConvertDateToString(final Date date,final String format) {

        SimpleDateFormat dateformat = new SimpleDateFormat(format);
        String sDate="";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {}

        return sDate;

    }

    public static void blink(final View v) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        v.startAnimation(anim);
    }

}
