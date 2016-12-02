package ru.brainworkout.sandowgym.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.manager.AndroidDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.database.entities.Exercise;
import ru.brainworkout.sandowgym.R;

public class ActivityExercisesList extends ActivityAbstract {

    //TODO height count
    private final int MAX_VERTICAL_BUTTON_COUNT = 15;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 10000;
    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    private SharedPreferences mSettings;
    private int rows_number = 17;
    Map<Integer, List<Exercise>> pagingExercices = new HashMap<>();
    private int currentPage = 1;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btExercisesDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }
        getPreferencesFromFile();
        pageExercises();
        showExercises();
        setTitleOfActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferencesFromFile();
        pageExercises();
        showExercises();
        setTitleOfActivity(this);
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

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rows_number = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rows_number = 17;
        }
    }

    public void btExercisesAdd_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityExercise.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    public void bt_ExercisesFillDefault_onClick(final View view) {

        blink(view, this);

        List<Exercise> exercises = CreateDefaultExercises();
        for (Exercise ex : exercises) {

            ex.dbSave(DB);

        }
        pageExercises();
        showExercises();

    }

    private void pageExercises() {
        List<Exercise> exercises = new ArrayList<Exercise>();
        if (dbCurrentUser == null) {
            //exercises = DB.getAllExercises();
        } else {
            exercises = DB.getAllExercisesOfUser(dbCurrentUser.getID());
        }
        List<Exercise> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < exercises.size(); i++) {
            pageContent.add(exercises.get(i));
            if (pageContent.size() == rows_number) {
                pagingExercices.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size()!=0) {
            pagingExercices.put(pageNumber, pageContent);
        }
    }

    private void showExercises() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage));
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableExercises);
        try {
            sv.removeAllViews();
        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        mWidth = displaymetrics.widthPixels / MAX_HORIZONTAL_BUTTON_COUNT;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 /
                getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);
        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }
        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);

        List<Exercise> page = pagingExercices.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            Exercise exercise=page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + exercise.getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowExercise_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(exercise.getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(String.valueOf(exercise.getName()));
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

    private void rowExercise_onClick(final TableRow view) {

        blink(view, this);

        int id = view.getId() % NUMBER_OF_VIEWS;

        Intent intent = new Intent(getApplicationContext(), ActivityExercise.class);
        intent.putExtra("CurrentExerciseID", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {
        blink(view, this);
        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    public void buttonHome_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDeleteAllExercises_onClick(final View view) {

        blink(view, this);

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить все упражения пользователя?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (dbCurrentUser != null) {
                            DB.deleteAllExercisesOfUser(dbCurrentUser.getID());
                            showExercises();
                        }

                    }
                }).setNegativeButton("Нет", null).show();
    }

    private List<Exercise> CreateDefaultExercises() {

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

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagingExercices.size()) {
            currentPage++;
        }
        showExercises();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage != 1) {
            currentPage--;
        }
        showExercises();
    }
}
