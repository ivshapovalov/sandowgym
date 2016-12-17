package ru.brainworkout.sandowgym.common;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TableRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.brainworkout.sandowgym.database.entities.Exercise;
import ru.brainworkout.sandowgym.database.entities.User;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;

public class Common{

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static User dbCurrentUser;
    public static final boolean isDebug=true;

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
                .addVolumeDefault("120")
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
                .addVolumeDefault("53")
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
                .addVolumeDefault("24")
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
                .addVolumeDefault("14")
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
                .addVolumeDefault("12")
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
                .addVolumeDefault("22")
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
                .addVolumeDefault("17")
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
                .addVolumeDefault("--")
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
                .addVolumeDefault("--")
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
                .addVolumeDefault("17")
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
                .addVolumeDefault("17")
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
                .addVolumeDefault("17")
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
                .addVolumeDefault("7")
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
                .addVolumeDefault("53")
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
                .addVolumeDefault("10")
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
                .addVolumeDefault("7")
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
                .addVolumeDefault("53")
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
                .addVolumeDefault("53")
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        //19
        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Подтягивания")
                .addExplanation(
                        "Подтягивания на перекладине любым хватом."
                )
                .addVolumeDefault("--")
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        exercises.add(new Exercise.Builder(maxNum + (i++))
                .addIsActive(1)
                .addName("Планка")
                .addExplanation(
                        "Планка"
                )
                .addVolumeDefault("--")
                .addPicture("ic_ex_" + String.valueOf(i))
                .build());

        return exercises;
    }
}
