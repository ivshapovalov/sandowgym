package ru.ivan.sandowgym.common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.service.notification.StatusBarNotification;
import android.text.format.Time;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.activities.ActivityMain;
import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.entities.User;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Common {
    public static SharedPreferences mSettings;
    public static int mRowsOnPageInLists;
    public static String mFtpHost;
    public static String mFtpLogin;
    public static String mFtpPassword;
    public static String mDropboxAccessToken;
    public static boolean mBackupScheduleEnabled;
    public static int mBackupScheduleTimeHour;
    public static int mBackupScheduleTimeMinutes;

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static User dbCurrentUser;
    public static final boolean isDebug = true;
    public static volatile boolean processingInProgress;
    private static final int MAX_NOTIFICATION_CHARSEQUENCE_LENGTH = 5 * 1024;

    public static Date convertStringToDate(final String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static void getPreferences(Context context) {
        mSettings = context.getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            mRowsOnPageInLists = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            mRowsOnPageInLists = 17;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST)) {
            mFtpHost = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST, "");
        } else {
            mFtpHost = "";
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN)) {
            mFtpLogin = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN, "");
        } else {
            mFtpLogin = "";
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD)) {
            mFtpPassword = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD, "");
        } else {
            mFtpPassword = "";
        }
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN)) {
            mDropboxAccessToken = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, "");
        } else {
            mDropboxAccessToken = "";
        }
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_ENABLED)) {
            mBackupScheduleEnabled = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_ENABLED, false);
        } else {
            mBackupScheduleEnabled = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_HOUR)) {
            mBackupScheduleTimeHour = mSettings.getInt(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_HOUR, 0);
        } else {
            mBackupScheduleTimeHour = 0;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_MINUTES)) {
            mBackupScheduleTimeMinutes = mSettings.getInt(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_MINUTES, 0);
        } else {
            mBackupScheduleTimeMinutes = 0;
        }

    }

    public static Date convertMillisToDate(final long Millis) {
        return new Date(Millis);
    }

    public static String convertMillisToString(final long Millis) {
        return convertDateToString(new Date(Millis));
    }

    public static String convertDateToString(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMAT_STRING);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;
    }

    public static int convertTextToDigit(String text) {

        if (text == null || text.trim().equals("")) {
            return 0;
        } else {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    public static void blink(final View v, final Activity activity) {

        long mills = 50L;
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mills);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        v.startAnimation(anim);
    }

    public static void displayMessage(Context context, String message, boolean makeToast) {
        if (makeToast) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        Intent resultIntent = new Intent(context, context.getClass());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        StringBuilder notifications = new StringBuilder();

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String now = today.format("%k:%M:%S");
        notifications.append(now + ": " + message).append(System.lineSeparator());

        for (StatusBarNotification sbm : notificationManager.getActiveNotifications()) {
            Bundle extras = sbm.getNotification().extras;
            Object text = extras.get(Notification.EXTRA_BIG_TEXT);
            if (text != null) {
                notifications.append(text.toString());
            }
        }
        notificationManager.cancelAll();

        List<CharSequence> newNotifications = new ArrayList<>();
        CharSequence cs = notifications.toString();
        while (true) {
            if (cs.length() > MAX_NOTIFICATION_CHARSEQUENCE_LENGTH) {
                CharSequence csOld = cs.subSequence(0, MAX_NOTIFICATION_CHARSEQUENCE_LENGTH);
                cs = cs.subSequence(MAX_NOTIFICATION_CHARSEQUENCE_LENGTH, cs.length());
                newNotifications.add(csOld);
            } else {
                newNotifications.add(cs);
                break;
            }
        }
        for (int i = newNotifications.size() - 1; i >= 0; i--) {
            cs = newNotifications.get(i);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "1")
                            .setSmallIcon(R.drawable.ic_sandow)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sandow_big))
                            .setContentIntent(resultPendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(cs))
                            .setAutoCancel(true);

            Notification notification = builder.build();
            notificationManager.notify(i + 1, notification);
        }

        //tests
//        notifications = new StringBuilder();
//        for (StatusBarNotification sbm : notificationManager.getActiveNotifications()) {
//            Bundle extras = sbm.getNotification().extras;
//            Object text = extras.get(Notification.EXTRA_BIG_TEXT);
//            if (text != null) {
//                notifications.append(text.toString());
//            }
//        }
//        System.out.println(notifications);
    }

    public static boolean isProcessingInProgress(Context context) {
        if (processingInProgress) {
            Toast.makeText(context,
                    "Sorry, other background processing in progress. Please wait!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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

    public static void hideEditorButton(Button btEditor) {

        if (btEditor != null) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.span = 0;
            btEditor.setLayoutParams(params);
        }
    }

    public static TableRow.LayoutParams paramsTextViewWithSpanInList(int i) {
        TableRow.LayoutParams paramsTextView = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        paramsTextView.span = i;
        return paramsTextView;
    }

    public static List<Exercise> createDefaultExercises(SQLiteDatabaseManager DB) {

        List<Exercise> exercises = new ArrayList<>();
        int i = 0;
        int maxNum = DB.getExerciseMaxNumber() + 1;
        //1
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями вдоль туловища, ладони обращены вперед (хват снизу), смотреть прямо перед собой.\n" +
                                "Попеременно сгибайте и разгибайте руки в локтевых суставах. Локти должны быть неподвижными." +
                                "Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча (бицепсы)."
                )
                .addAmountDefault(120)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());
        //2
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями вдоль туловища, ладони обращены назад (хват сверху), смотреть прямо перед собой.\n" +
                                "Попеременно сгибайте и разгибайте руки в локтевых суставах. Локти должны быть неподвижными." +
                                "Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча (бицепсы)."
                )
                .addAmountDefault(53)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());
        //3
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями в стороны, ладони вверх," +
                                " смотреть прямо перед собой. Поочередно сгибайте и разгибайте руки в локтевых суставах. Во время упражнения локти не опускать." +
                                " Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча и " +
                                "трехглавые мышцы плеча(трицепсы)."
                )
                .addAmountDefault(24)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());
        //4
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями в стороны, ладони вверх. Одновременно " +
                                "сгибайте и разгибайте руки в локтевых суставах. Сгибая руки, делайте вдох, разгибая — выдох. Упражнение" +
                                " развивает бицепсы и трицепсы."
                )
                .addAmountDefault(14)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());
        //5
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями подняты вперед, ладони внутрь. " +
                                "Разведите прямые руки в стороны и сделайте вдох, быстро вернитесь в исходное положение — выдох." +
                                "Упражнение развивает грудные мышцы, мышцы спины и плечевого пояса."
                )
                .addAmountDefault(12)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());
        //6
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями к плечам, разверните плечи, смотрите прямо перед собой." +
                                " Попеременно поднимайте и опускайте руки. Дыхание равномерное. " +
                                "Упражнение развивает трехглавые мышцы плеча, дельтовидные и трапециевидные мышцы."
                )
                .addAmountDefault(22)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //7
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями вдоль туловища, спина несколько согнута. " +
                                "Поочередно поднимайте прямые руки вперед до уровня плеч." +
                                "Поднимая правую руку, делайте вдох, поднимая левую — выдох." +
                                "Упражнение развивает дельтовидные мышцы."
                )
                .addAmountDefault(17)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //8
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями в стороны, ладони вниз. Одновременно и быстро" +
                                " поворачивайте кисти вверх и вниз, затем вперед и назад. Дыхание равномерное. Упражнение выполнять до наступления усталости. " +
                                "Развивает мышцы предплечья и укрепляет лучезапястные суставы."
                )
                .addAmountDefault(0)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //9
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Возьмите гантели за один конец и разведите руки в стороны. " +
                                "Не сгибая рук, вращайте кисти вперед и назад. Дыхание равномерное. Упражнение выполняйте до утомления." +
                                "Упражнение развивает мышцы предплечья и укрепляет лучезапястные суставы."
                )
                .addAmountDefault(0)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //10
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями подняты вверх. Не сгибая колен, наклонитесь" +
                                " вперед и коснитесь руками пола — выдох. Вернитесь в исходное положение — вдох. Первое время упражнение выполняйте без гантелей." +
                                "Упражнение развивает мышцы спины."
                )
                .addAmountDefault(17)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());
        //11
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями вдоль туловища. Сделайте выпад левой ногой" +
                                " вперед, правую руку дугообразным движением поднимите на уровень груди — вдох. Вернитесь в исходное положение — выдох." +
                                " Затем сделайте выпад правой ногой, а левую руку поднимите вперед. Упражнение развивает дельтовидные мышцы и мышцы ног" +
                                "."
                )
                .addAmountDefault(17)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //12
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки вдоль туловища, смотрите прямо перед собой. Поднимите прямые руки через " +
                                "стороны вверх — вдох. Опустите в исходное положение — выдох. " +
                                "Упражнение развивает дельтовидные и трапециевидные мышцы."
                )
                .addAmountDefault(17)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //13
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Отжимания в упоре лежа на полу. Туловище и ноги должны составлять прямую линию." +
                                " Сгибая руки, делайте вдох, разгибая — выдох. Сгибая руки, касайтесь грудью пола. Упражнение развивает трехглавые мышцы плеча, " +
                                "грудные мышцы и мышцы плечевого пояса."
                )
                .addAmountDefault(7)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //14
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями вдоль туловища. Наклоните туловище в левую сторону, " +
                                "правую руку согните так, чтобы гантелью коснуться подмышки. Затем проделайте наклон в другую сторону, сгибая левую руку. Наклоняясь, " +
                                "делайте выдох, возвращаясь в исходное положение — вдох. Упражнение развивает боковые мышцы живота, бицепсы, трапециевидные и" +
                                " дельтовидные мышцы."
                )
                .addAmountDefault(53)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //15
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Лежа на спине на полу, ноги закреплены за" +
                                " неподвижную опору, руки с гантелями подняты вверх, Сядьте и сделайте наклон вперед — выдох. Медленно вернитесь" +
                                " в исходное положение — вдох. Первое время упражнение можно выполнять без гантелей. Упражнение развивает мышцы " +
                                "брюшного пресса."
                )
                .addAmountDefault(10)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //16
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Лежа на спине на полу, руки за головой." +
                                " Поднимите прямые ноги вверх — выдох. Медленно опустите ноги в исходное положение — вдох. Упражнение развивает мышцы" +
                                " брюшного пресса и четырехглавые мышцы бедра"
                )
                .addAmountDefault(7)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //17
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, пятки вместе, носки врозь, руки с гантелями " +
                                "опущены вдоль туловища. Медленно поднимитесь на носки — вдох, затем, опускаясь на пятки, присядьте — выдох. " +
                                "Упражнение развивает икроножные мышцы и четырехглавые мышцы бедра."
                )
                .addAmountDefault(53)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());
        //18
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Сандов №" + String.valueOf(i))
                .addExplanation(
                        "Стоя, руки с гантелями опущены вдоль туловища." +
                                " Сгибайте и разгибайте кисти в лучезапястных суставах. Упражнение развивает мышцы" +
                                " предплечья и укрепляет лучезапястные суставы."
                )
                .addAmountDefault(53)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //19
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Подтягивания")
                .addExplanation(
                        "Подтягивания на перекладине любым хватом."
                )
                .addAmountDefault(0)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Планка")
                .addExplanation(
                        "Планка"
                )
                .addAmountDefault(0)
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        return exercises;
    }
}
