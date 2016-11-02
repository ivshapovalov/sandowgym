package ru.brainworkout.sandowgym.common;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TableRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.brainworkout.sandowgym.database.entities.Training;
import ru.brainworkout.sandowgym.database.entities.User;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class Common{

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static User dbCurrentUser;
    public static final boolean isDebug=true;

    public static Date ConvertStringToDate(final String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static Date ConvertMillisToDate(final long Millis) {
        return new Date(Millis);
    }

    public static String ConvertMillisToString(final long Millis) {
        return ConvertDateToString(new Date(Millis));
    }
    public static String ConvertDateToString(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMAT_STRING);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static void blink(final View v, final Activity activity) {

        long mills = 50L;
        Vibrator vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mills);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        v.startAnimation(anim);
    }


    public static void setTitleOfActivity(Activity currentActivity) {
        if (Common.dbCurrentUser != null) {
            CharSequence title = currentActivity.getTitle();
            if (title.toString().contains("(")) {
                title = title.subSequence(0, title.toString().indexOf("("));
            }
            title = title + "(" + Common.dbCurrentUser.getName() + ")";
            currentActivity.setTitle(title);
        } else {
            CharSequence title = currentActivity.getTitle();
            if (title.toString().contains("(")) {
                title = title.subSequence(0, title.toString().indexOf("("));
            }
            currentActivity.setTitle(title);
        }
    }

    public static void HideEditorButton(Button btEditor) {

        if (btEditor != null) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.span = 0;
            btEditor.setLayoutParams(params);
        }
    }

}
