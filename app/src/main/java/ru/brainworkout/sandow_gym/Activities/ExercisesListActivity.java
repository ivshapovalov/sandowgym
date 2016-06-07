package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.MainActivity;
import ru.brainworkout.sandow_gym.database.AndroidDatabaseManager;
import ru.brainworkout.sandow_gym.database.DatabaseManager;
import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.R;

public class ExercisesListActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();
    private final int mNumOfView = 10000;

    DatabaseManager db;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);


        db = new DatabaseManager(this);

        showExercises();

    }

    // Вызывается в начале "активного" состояния.
    @Override
    public void onResume() {
        super.onResume();

        showExercises();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        TableRow mRow = (TableRow) findViewById(mNumOfView + id);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableExercises", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
    }


    public void bt_ExercisesAdd_onClick(View view) {

        Intent intent = new Intent(getApplicationContext(), ExerciseActivity.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    public void bt_ExercisesFillDefault_onClick(View view) {

        MyLogger(TAG, "До обращения в базу");

        DatabaseManager db = new DatabaseManager(this);

        MyLogger(TAG, "До добавления");

        ArrayList<Exercise> exercises = CreateDefaultExercises();
        for (Exercise ex : exercises) {
            db.addExercise(ex);

            MyLogger(TAG, "Добавили " + String.valueOf(ex.getID()));
        }


        showExercises();


    }

    private void showExercises() {

        Log.d("Reading: ", "Reading all exercises..");
        List<Exercise> exercises = db.getAllExercises();

        ScrollView sv = (ScrollView) findViewById(R.id.svTableExercises);
        //TableLayout layout = (TableLayout) findViewById(R.id.tableExercises);
        try {
            sv.removeAllViews();
        } catch (Exception e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        //допустим 15 строк тренировок
        mHeight = displaymetrics.heightPixels / 17;
        mWidth = displaymetrics.widthPixels/2;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        //layout.removeAllViews();
        layout.setStretchAllColumns(true);
        //layout.setShrinkAllColumns(true);

        //TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0f);

        for (int numEx = 0; numEx < exercises.size(); numEx++) {
            TableRow mRow = new TableRow(this);
            mRow.setId(mNumOfView + exercises.get(numEx).getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowExercise_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.textview_border);
            //mRow.setBackgroundResource(R.drawable.textview_border);

            //mRow.setPadding(0,40,0,40);
            //mRow.setGravity(Gravity.LEFT);
            TextView txt = new TextView(this);
            //txt.setId(10000 + numEx);
            txt.setText(String.valueOf(exercises.get(numEx).getID()));
            txt.setBackgroundResource(R.drawable.textview_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            //txt.setBackgroundResource(R.drawable.textview_border);
            //params.span = 3;
            //txt.setLayoutParams(params);

            mRow.addView(txt);

            txt = new TextView(this);
            //txt.setId(20000 + numEx);
            txt.setText(String.valueOf(exercises.get(numEx).getName()));
            txt.setBackgroundResource(R.drawable.textview_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.textview_border);
            layout.addView(mRow);
        }
        sv.addView(layout);

    }

    private void rowExercise_onClick(TableRow v) {

        int id = v.getId() % mNumOfView;
        //System.out.println(String.valueOf(a));

        Intent intent = new Intent(getApplicationContext(), ExerciseActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("id", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public static void MyLogger(String TAG, String statement) {
        if (isDebug) {
            Log.e(TAG, statement);
        }
    }


    public void bt_Edit_onClick(View view) {

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    private ArrayList<Exercise> CreateDefaultExercises() {
        ArrayList<Exercise> exercises = new ArrayList<>();
        int i = 0;
        int maxNum = db.getExerciseMaxNumber() + 1;
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища, ладони обращены вперед (хват снизу), смотреть прямо перед собой.\n" +
                "Попеременно сгибайте и разгибайте руки в локтевых суставах. Локти должны быть неподвижными." +
                "Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча (бицепсы).", "120", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища, ладони обращены назад (хват сверху), смотреть прямо перед собой.\n" +
                "Попеременно сгибайте и разгибайте руки в локтевых суставах. Локти должны быть неподвижными." +
                "Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча (бицепсы).", "53", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями в стороны, ладони вверх," +
                " смотреть прямо перед собой. Поочередно сгибайте и разгибайте руки в локтевых суставах. Во время упражнения локти не опускать." +
                " Дыхание равномерное, произвольное. Упражнение развивает двуглавые мышцы плеча и " +
                "трехглавые мышцы плеча(трицепсы).", "24", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями в стороны, ладони вверх. Одновременно " +
                "сгибайте и разгибайте руки в локтевых суставах. Сгибая руки, делайте вдох, разгибая — выдох. Упражнение" +
                " развивает бицепсы и трицепсы.", "14", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями подняты вперед, ладони внутрь. " +
                "Разведите прямые руки в стороны и сделайте вдох, быстро вернитесь в исходное положение — выдох." +
                "Упражнение развивает грудные мышцы, мышцы спины и плечевого пояса.", "12", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями к плечам, разверните плечи, смотрите прямо перед собой." +
                " Попеременно поднимайте и опускайте руки. Дыхание равномерное. " +
                "Упражнение развивает трехглавые мышцы плеча, дельтовидные и трапециевидные мышцы.", "22", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища, спина несколько согнута. " +
                "Поочередно поднимайте прямые руки вперед до уровня плеч." +
                "Поднимая правую руку, делайте вдох, поднимая левую — выдох." +
                "Упражнение развивает дельтовидные мышцы.", "17", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями в стороны, ладони вниз. Одновременно и быстро" +
                " поворачивайте кисти вверх и вниз, затем вперед и назад. Дыхание равномерное. Упражнение выполнять до наступления усталости. " +
                "Развивает мышцы предплечья и укрепляет лучезапястные суставы.", "--", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Возьмите гантели за один конец и разведите руки в стороны. " +
                "Не сгибая рук, вращайте кисти вперед и назад. Дыхание равномерное. Упражнение выполняйте до утомления." +
                "Упражнение развивает мышцы предплечья и укрепляет лучезапястные суставы.", "--", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями подняты вверх. Не сгибая колен, наклонитесь" +
                " вперед и коснитесь руками пола — выдох. Вернитесь в исходное положение — вдох. Первое время упражнение выполняйте без гантелей." +
                "Упражнение развивает мышцы спины.", "17", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища. Сделайте выпад левой ногой" +
                " вперед, правую руку дугообразным движением поднимите на уровень груди — вдох. Вернитесь в исходное положение — выдох." +
                " Затем сделайте выпад правой ногой, а левую руку поднимите вперед. Упражнение развивает дельтовидные мышцы и мышцы ног" +
                ".", "17", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки вдоль туловища, смотрите прямо перед собой. Поднимите прямые руки через " +
                "стороны вверх — вдох. Опустите в исходное положение — выдох. " +
                "Упражнение развивает дельтовидные и трапециевидные мышцы.", "17", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Отжимания в упоре лежа на полу. Туловище и ноги должны составлять прямую линию." +
                " Сгибая руки, делайте вдох, разгибая — выдох. Сгибая руки, касайтесь грудью пола. Упражнение развивает трехглавые мышцы плеча, " +
                "грудные мышцы и мышцы плечевого пояса.", "7", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями вдоль туловища. Наклоните туловище в левую сторону, " +
                "правую руку согните так, чтобы гантелью коснуться подмышки. Затем проделайте наклон в другую сторону, сгибая левую руку. Наклоняясь, " +
                "делайте выдох, возвращаясь в исходное положение — вдох. Упражнение развивает боковые мышцы живота, бицепсы, трапециевидные и" +
                " дельтовидные мышцы.", "53", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Лежа на спине на полу, ноги закреплены за" +
                " неподвижную опору, руки с гантелями подняты вверх, Сядьте и сделайте наклон вперед — выдох. Медленно вернитесь" +
                " в исходное положение — вдох. Первое время упражнение можно выполнять без гантелей. Упражнение развивает мышцы " +
                "брюшного пресса.", "10", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Лежа на спине на полу, руки за головой." +
                " Поднимите прямые ноги вверх — выдох. Медленно опустите ноги в исходное положение — вдох. Упражнение развивает мышцы" +
                " брюшного пресса и четырехглавые мышцы бедра", "7", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, пятки вместе, носки врозь, руки с гантелями " +
                "опущены вдоль туловища. Медленно поднимитесь на носки — вдох, затем, опускаясь на пятки, присядьте — выдох. " +
                "Упражнение развивает икроножные мышцы и четырехглавые мышцы бедра.", "17", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Стоя, руки с гантелями опущены вдоль туловища." +
                " Сгибайте и разгибайте кисти в лучезапястных суставах. Упражнение развивает мышцы" +
                " предплечья и укрепляет лучезапястные суставы.", "53", "ic_ex_" + String.valueOf(i)));
        exercises.add(new Exercise(maxNum + (i++), 1, "Сандов №" + String.valueOf(i), "Подтягивания на перекладине любым хватом.", "--", "--"));


        return exercises;
    }

    public void buttonHome_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void btDeleteAllExercises_onClick(View view) {

        db.deleteAllExercises();
        showExercises();

    }
}
