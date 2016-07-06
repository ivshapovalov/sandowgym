package ru.brainworkout.sandow_gym.activities;


import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.MainActivity;
import ru.brainworkout.sandow_gym.commons.Common;
import ru.brainworkout.sandow_gym.database.AndroidDatabaseManager;
import ru.brainworkout.sandow_gym.database.DatabaseManager;
import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.R;

public class ExercisesListActivity extends AppCompatActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 10000;

    private final DatabaseManager DB = new DatabaseManager(this);

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);

        showExercises();

    }

    @Override
    public void onResume() {
        super.onResume();

        showExercises();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableExercises", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
    }


    public void bt_ExercisesAdd_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), ExerciseActivity.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    public void bt_ExercisesFillDefault_onClick(final View view) {

        Common.blink(view);

        ArrayList<Exercise> exercises = CreateDefaultExercises();
        for (Exercise ex : exercises) {

            ex.dbSave(DB);

        }
        showExercises();

    }

    private void showExercises() {

        List<Exercise> exercises = DB.getAllExercises();

        ScrollView sv = (ScrollView) findViewById(R.id.svTableExercises);
        try {

            sv.removeAllViews();

        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        mWidth = displaymetrics.widthPixels/ MAX_HORIZONTAL_BUTTON_COUNT;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);

        for (int numEx = 0; numEx < exercises.size(); numEx++) {
            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + exercises.get(numEx).getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowExercise_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(exercises.get(numEx).getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(String.valueOf(exercises.get(numEx).getName()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }

    private void rowExercise_onClick(final TableRow v) {

        Common.blink(v);

        int id = v.getId() % NUMBER_OF_VIEWS;

        Intent intent = new Intent(getApplicationContext(), ExerciseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {

        Common.blink(view);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);

    }

    private ArrayList<Exercise> CreateDefaultExercises() {

        ArrayList<Exercise> exercises = new ArrayList<>();
        int i = 0;
        int maxNum = DB.getExerciseMaxNumber() + 1;
        //1
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища, ладони обращены вперед (хват снизу), смотреть прямо перед собой.\n" +
                "Попеременно сгибайте и разгибайте руки в локтевых суставах. Локти должны быть неподвижными." +
                "Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча (бицепсы).", "120", "ic_ex_" + String.valueOf(i)));
        //2
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища, ладони обращены назад (хват сверху), смотреть прямо перед собой.\n" +
                "Попеременно сгибайте и разгибайте руки в локтевых суставах. Локти должны быть неподвижными." +
                "Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча (бицепсы).", "53", "ic_ex_" + String.valueOf(i)));
        //3
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями в стороны, ладони вверх," +
                " смотреть прямо перед собой. Поочередно сгибайте и разгибайте руки в локтевых суставах. Во время упражнения локти не опускать." +
                " Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча и " +
                "трехглавые мышцы плеча(трицепсы).", "24", "ic_ex_" + String.valueOf(i)));
        //4
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями в стороны, ладони вверх. Одновременно " +
                "сгибайте и разгибайте руки в локтевых суставах. Сгибая руки, делайте вдох, разгибая — выдох. Упражнение" +
                " развивает бицепсы и трицепсы.", "14", "ic_ex_" + String.valueOf(i)));
        //5
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями подняты вперед, ладони внутрь. " +
                "Разведите прямые руки в стороны и сделайте вдох, быстро вернитесь в исходное положение — выдох." +
                "Упражнение развивает грудные мышцы, мышцы спины и плечевого пояса.", "12", "ic_ex_" + String.valueOf(i)));
        //6
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями к плечам, разверните плечи, смотрите прямо перед собой." +
                " Попеременно поднимайте и опускайте руки. Дыхание равномерное. " +
                "Упражнение развивает трехглавые мышцы плеча, дельтовидные и трапециевидные мышцы.", "22", "ic_ex_" + String.valueOf(i)));
        //7
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища, спина несколько согнута. " +
                "Поочередно поднимайте прямые руки вперед до уровня плеч." +
                "Поднимая правую руку, делайте вдох, поднимая левую — выдох." +
                "Упражнение развивает дельтовидные мышцы.", "17", "ic_ex_" + String.valueOf(i)));
        //8
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями в стороны, ладони вниз. Одновременно и быстро" +
                " поворачивайте кисти вверх и вниз, затем вперед и назад. Дыхание равномерное. Упражнение выполнять до наступления усталости. " +
                "Развивает мышцы предплечья и укрепляет лучезапястные суставы.", "--", "ic_ex_" + String.valueOf(i)));
        //9
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Возьмите гантели за один конец и разведите руки в стороны. " +
                "Не сгибая рук, вращайте кисти вперед и назад. Дыхание равномерное. Упражнение выполняйте до утомления." +
                "Упражнение развивает мышцы предплечья и укрепляет лучезапястные суставы.", "--", "ic_ex_" + String.valueOf(i)));
        //10
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями подняты вверх. Не сгибая колен, наклонитесь" +
                " вперед и коснитесь руками пола — выдох. Вернитесь в исходное положение — вдох. Первое время упражнение выполняйте без гантелей." +
                "Упражнение развивает мышцы спины.", "17", "ic_ex_" + String.valueOf(i)));
        //11
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища. Сделайте выпад левой ногой" +
                " вперед, правую руку дугообразным движением поднимите на уровень груди — вдох. Вернитесь в исходное положение — выдох." +
                " Затем сделайте выпад правой ногой, а левую руку поднимите вперед. Упражнение развивает дельтовидные мышцы и мышцы ног" +
                ".", "17", "ic_ex_" + String.valueOf(i)));
        //12
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки вдоль туловища, смотрите прямо перед собой. Поднимите прямые руки через " +
                "стороны вверх — вдох. Опустите в исходное положение — выдох. " +
                "Упражнение развивает дельтовидные и трапециевидные мышцы.", "17", "ic_ex_" + String.valueOf(i)));
        //13
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Отжимания в упоре лежа на полу. Туловище и ноги должны составлять прямую линию." +
                " Сгибая руки, делайте вдох, разгибая — выдох. Сгибая руки, касайтесь грудью пола. Упражнение развивает трехглавые мышцы плеча, " +
                "грудные мышцы и мышцы плечевого пояса.", "7", "ic_ex_" + String.valueOf(i)));
        //14
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища. Наклоните туловище в левую сторону, " +
                "правую руку согните так, чтобы гантелью коснуться подмышки. Затем проделайте наклон в другую сторону, сгибая левую руку. Наклоняясь, " +
                "делайте выдох, возвращаясь в исходное положение — вдох. Упражнение развивает боковые мышцы живота, бицепсы, трапециевидные и" +
                " дельтовидные мышцы.", "53", "ic_ex_" + String.valueOf(i)));
        //15
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Лежа на спине на полу, ноги закреплены за" +
                " неподвижную опору, руки с гантелями подняты вверх, Сядьте и сделайте наклон вперед — выдох. Медленно вернитесь" +
                " в исходное положение — вдох. Первое время упражнение можно выполнять без гантелей. Упражнение развивает мышцы " +
                "брюшного пресса.", "10", "ic_ex_" + String.valueOf(i)));
        //16
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Лежа на спине на полу, руки за головой." +
                " Поднимите прямые ноги вверх — выдох. Медленно опустите ноги в исходное положение — вдох. Упражнение развивает мышцы" +
                " брюшного пресса и четырехглавые мышцы бедра", "7", "ic_ex_" + String.valueOf(i)));
        //17
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, пятки вместе, носки врозь, руки с гантелями " +
                "опущены вдоль туловища. Медленно поднимитесь на носки — вдох, затем, опускаясь на пятки, присядьте — выдох. " +
                "Упражнение развивает икроножные мышцы и четырехглавые мышцы бедра.", "17", "ic_ex_" + String.valueOf(i)));
        //18
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями опущены вдоль туловища." +
                " Сгибайте и разгибайте кисти в лучезапястных суставах. Упражнение развивает мышцы" +
                " предплечья и укрепляет лучезапястные суставы.", "53", "ic_ex_" + String.valueOf(i)));
        //19
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Подтягивания на перекладине любым хватом.", "--", "--"));


        return exercises;

    }

    public void buttonHome_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
