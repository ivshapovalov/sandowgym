package ru.ivan.sandowgym.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Tasks.Digit;
import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.entities.Training;
import ru.ivan.sandowgym.database.entities.TrainingContent;
import ru.ivan.sandowgym.database.entities.WeightChangeCalendar;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.ConvertDateToString;
import static ru.ivan.sandowgym.common.Common.ConvertMillisToDate;
import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.convertStringToDate;
import static ru.ivan.sandowgym.common.Common.dbCurrentUser;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityTraining extends ActivityAbstract {

    private final int numberOfViews = 30000;
    private final int maxNumberOfTransferButtons = 7;

    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);
    private SharedPreferences mSettings;
    private int mPlusMinusButtonValue;
    private boolean mUseCalendarForWeight;
    private boolean mShowPicture;
    private boolean mShowExplanation;
    private boolean mShowAmountDefaultButton;
    private boolean mShowAmountLastDayButton;

    private Training mCurrentTraining;
    private TrainingContent mCurrentTrainingContent;
    private Exercise mCurrentExercise;
    private int mExerciseAmountLastDay;
    private boolean mTrainingIsNew;
    private int mHeight;
    private int mWidth;
    private int mTextSize;
    private List<Exercise> mActiveExercises;
    private int mCurrentExerciseNumberInList;
    private List<TrainingContent> mTrainingContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        getPreferencesFromFile();
        setPreferencesOnScreen();

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        mHeight = displaymetrics.heightPixels / 2;
        mWidth = displaymetrics.widthPixels;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("isNew", false);

        long currentDateInMillis = intent.getLongExtra("currentDateInMillis", 0);

        boolean weightIsNeedToUpdate = false;
        if (mTrainingIsNew) {
            weightIsNeedToUpdate = true;
        }

        int id = intent.getIntExtra("currentTrainingId", 0);

        defineCurrentTraining(id, currentDateInMillis);

        showTrainingOnScreen();

        SwipeDetectorActivity swipeDetectorActivity = new SwipeDetectorActivity(ActivityTraining.this);
        ScrollView sv = this.findViewById(R.id.svMain);
        sv.setOnTouchListener(swipeDetectorActivity);

        if (mTrainingIsNew) {
            getAllActiveExercises();
        } else {
            getAllExercisesOfTraining();
        }

        long currentDateOldInMillis = mCurrentTraining.getDay();
        if (currentDateInMillis != 0) {
            mCurrentTraining.setDay(currentDateInMillis);
            updateDayOnScreen(currentDateInMillis);

        }
        saveTraining();
        updateButtonsListOfExercises();

        int exerciseIndex = intent.getIntExtra("currentExerciseIndex", 0);

        if (exerciseIndex != 0) {
            saveCurrentTrainingContent(false);
            moveToNewExercise(exerciseIndex);
        }

        int currentDigit = intent.getIntExtra("currentDigit", 0);
        String currentDigitTitle = intent.getStringExtra("currentDigitTitle");

        if (Digit.WEIGHT.equalValue(currentDigitTitle)) {
            mCurrentTrainingContent.setWeight(currentDigit);
        } else if (Digit.AMOUNT.equalValue(currentDigitTitle)) {
            mCurrentTrainingContent.setAmount(currentDigit);
        }

        if (weightIsNeedToUpdate || (currentDateInMillis != 0 && mCurrentTraining != null && currentDateInMillis != currentDateOldInMillis)) {

            updateCurrentWeightOfTrainingContent();
        }

        showTrainingContentOnScreen();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);

//        int mOptionsID = getResources().getIdentifier("btOptions", "id", getPackageName());
//        Button btOptions = findViewById(mOptionsID);
//        if (btOptions != null) {
//            btOptions.setOnClickListener(viewClickListener);
//        }
    }

//    View.OnClickListener viewClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            showPopupMenu(v);
//        }
//    };
//
//    private void showPopupMenu(View v) {
//        PopupMenu popupMenu = new PopupMenu(this, v);
//        popupMenu.inflate(R.menu.popupmenu);
//
//        popupMenu
//                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.menu1:
//                                Toast.makeText(getApplicationContext(),
//                                        "Вы выбрали PopupMenu 1",
//                                        Toast.LENGTH_SHORT).show();
//                                return true;
//                            case R.id.menu2:
//                                Toast.makeText(getApplicationContext(),
//                                        "Вы выбрали PopupMenu 2",
//                                        Toast.LENGTH_SHORT).show();
//                                return true;
//                            case R.id.menu3:
//                                Toast.makeText(getApplicationContext(),
//                                        "Вы выбрали PopupMenu 3",
//                                        Toast.LENGTH_SHORT).show();
//                                return true;
//                            default:
//                                return false;
//                        }
//                    }
//                });
//
//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getApplicationContext(), "onDismiss",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//        popupMenu.show();
//    }
//}

    private void updateDayOnScreen(long currentDateInMillis) {

        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = findViewById(mDayID);
        if (etDay != null) {
            etDay.setText(ConvertDateToString(ConvertMillisToDate(currentDateInMillis)));
        }
    }

    private void updateCurrentWeightOfTrainingContent() {

        int mExerciseWeightLastDay = 0;
        int mWeightInCalendar = 0;
        List<TrainingContent> mTrainingContentNotNullAmount = new ArrayList<>();
        List<WeightChangeCalendar> mWeightChangeCalendarList = new ArrayList<>();
        if (dbCurrentUser != null) {
            mTrainingContentNotNullAmount = DB.getLastExerciseNotNullAmountAndWeightOfUser(dbCurrentUser.getId(),
                    mCurrentTraining.getDay(), mCurrentExercise.getId());
            mWeightChangeCalendarList = DB.getWeightOfUserFromWeightCalendar(dbCurrentUser.getId(),
                    mCurrentTraining.getDay());
        }
        if (mTrainingContentNotNullAmount.size() == 1) {
            try {
                mExerciseWeightLastDay = mTrainingContentNotNullAmount.get(0).getWeight();
            } catch (Exception e) {
                mExerciseWeightLastDay = 0;
            }
        }
        if (mWeightChangeCalendarList.size() == 1) {
            try {
                mWeightInCalendar = mWeightChangeCalendarList.get(0).getWeight();
            } catch (Exception e) {
                mWeightInCalendar = 0;
            }
            List<TrainingContent> trainingContentList = DB.getAllTrainingContentOfTraining(mCurrentTraining.getId());
            for (TrainingContent trainingContent : trainingContentList) {
                if (trainingContent.getWeight() == 0 && mWeightInCalendar != 0) {
                    trainingContent.setWeight(mWeightInCalendar);
                    if (mCurrentTrainingContent != null) {
                        if (trainingContent.getId() == mCurrentTrainingContent.getId()) {
                            trainingContent.setWeight(mExerciseWeightLastDay > mWeightInCalendar ? mExerciseWeightLastDay : mWeightInCalendar);
                            mCurrentTrainingContent.setWeight(mExerciseWeightLastDay > mWeightInCalendar ? mExerciseWeightLastDay : mWeightInCalendar);
                        }
                    }
                    trainingContent.dbSave(DB);
                }
            }

            int mWeight = getResources().getIdentifier("btWeight", "id", getPackageName());
            Button btWeight = findViewById(mWeight);
            if (btWeight != null) {
                if (mCurrentTrainingContent != null && mCurrentTrainingContent.getWeight() != 0) {
                    btWeight.setText(String.valueOf(mCurrentTrainingContent.getWeight()));
                }
            }
        }
    }

    private void defineCurrentTraining(int mCurrentId, long currentDateInMillis) {
        if (mTrainingIsNew) {
            mCurrentTraining = new Training.Builder(DB).build();
            if (currentDateInMillis == 0) {
                Calendar cal = Calendar.getInstance();
                cal.clear(Calendar.MILLISECOND);
                mCurrentTraining.setDay(cal.getTimeInMillis());
            } else {
                mCurrentTraining.setDay(currentDateInMillis);
            }
        } else {
            if (mCurrentId == 0) {
                mCurrentTraining = new Training.Builder(DB).build();
            } else {
                try {
                    mCurrentTraining = Training.getTrainingFromDB(DB, mCurrentId);
                } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                    tableDoesNotContainElementException.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferencesFromFile();
        setPreferencesOnScreen();
    }

    public void btAmountDefault_onClick(final View view) {
        blink(view, this);
        if (mCurrentExercise != null) {
            if (!"".equals(mCurrentExercise.getAmountDefault())) {
                int mAmountID = getResources().getIdentifier("btAmount", "id", getPackageName());
                Button btAmount = findViewById(mAmountID);
                if (btAmount != null) {
                    btAmount.setText(String.valueOf(mCurrentExercise.getAmountDefault()));
                }
            }
        }
    }

    public void btAmountLastDay_onClick(final View view) {
        blink(view, this);
        int mAmountID = getResources().getIdentifier("btAmount", "id", getPackageName());
        Button btAmount = findViewById(mAmountID);
        if (btAmount != null) {
            if ("".equals(mExerciseAmountLastDay)) {
                btAmount.setText("0");
            } else {
                btAmount.setText(String.valueOf(mExerciseAmountLastDay));
            }
        }
    }

    public void btOptions_onClick(final View view) {

//            PopupMenu popupMenu = new PopupMenu(this, view);
//            // popupMenu.inflate(R.menu.popupmenu); // если добавлять к существующему меню
//            popupMenu.getMenu().add(1, R.id.menu1, 1, "slot1");
//            popupMenu.getMenu().add(1, R.id.menu2, 2, "slot2");
//            popupMenu.show();

        blink(view, this);
        Intent intent = new Intent(ActivityTraining.this, ActivityTrainingOptions.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void setNextExercise() {

        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != mActiveExercises.size() - 1) {
                mCurrentExerciseNumberInList++;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
                if (isTrainingContentNew()) {
                    createNewTrainingContent();
                }
            }
        }
    }

    private void showExercise() {
        ImageView ivPicture = findViewById(R.id.ivPicture);
        if (ivPicture != null) {
            if (mCurrentExercise.getPicture() != null && !"".equals(mCurrentExercise.getPicture())) {
                ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
                ivPicture.setMinimumHeight(mHeight);
            } else {
                ivPicture.setBackgroundColor(Color.WHITE);
            }
        }
        TextView tvExplanation = findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(mCurrentExercise.getExplanation());
        }
        showTrainingContentOnScreen(mCurrentTrainingContent.getExerciseId());
        updateButtonsListOfExercises();

    }

    private void setPreviousExercise() {

        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != 0) {

                mCurrentExerciseNumberInList--;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);

                if (isTrainingContentNew()) {
                    createNewTrainingContent();
                }
            }
        }
    }

    private boolean isTrainingContentNew() {
        for (TrainingContent mTr : mTrainingContentList) {
            if (mTr.getExerciseId() == mCurrentExercise.getId()) {
                mCurrentTrainingContent = mTr;
                return false;
            }
        }
        return true;
    }

    private void getAllExercisesOfTraining() {

        mActiveExercises = DB.getAllActiveExercisesOfUser(dbCurrentUser.getId());
        mTrainingContentList = DB.getAllTrainingContentOfTraining(mCurrentTraining.getId());

        for (TrainingContent tr : mTrainingContentList) {
            boolean isFound = false;
            int idExercise = tr.getExerciseId();
            for (Exercise ex : mActiveExercises) {
                if (ex.getId() == idExercise) {
                    isFound = true;
                    break;
                }

            }
            //если в текущих активных не нашли - добавляем новое
            if (!isFound) {
                //добавим в список упражнений упражнение старое
                Exercise ex = null;
                try {
                    ex = Exercise.getExerciseFromDB(DB, idExercise);
                } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                    tableDoesNotContainElementException.printStackTrace();
                }
                mActiveExercises.add(ex);
            }
        }

        Collections.sort(mActiveExercises, new Comparator() {
            public int compare(Object ex1, Object ex2) {
                return ((Exercise) (ex1)).getId() - ((Exercise) (ex2)).getId();
            }
        });

        if (mActiveExercises.size() != 0) {
            mCurrentExerciseNumberInList = 0;
            mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);

            if (mTrainingContentList.size() != 0 && mTrainingContentList.get(0).getExerciseId() == mCurrentExercise.getId()) {
                mCurrentTrainingContent = mTrainingContentList.get(0);
            } else {
                createNewTrainingContent();
            }
        }
    }

    private void getAllActiveExercises() {
        //при инициализации тренировки создадим сразу контент
        if (dbCurrentUser != null) {
            mActiveExercises = DB.getAllActiveExercisesOfUser(dbCurrentUser.getId());
            mTrainingContentList = new ArrayList<>();
            for (Exercise ex : mActiveExercises
                    ) {
                mCurrentExercise = ex;
                createNewTrainingContent();
                if (!mTrainingContentList.contains(mCurrentTrainingContent)) {
                    mTrainingContentList.add(mCurrentTrainingContent);
                }
            }
            //перемещаемся на первое
            if (mActiveExercises.size() != 0) {
                mCurrentExerciseNumberInList = 0;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
                mCurrentTrainingContent = mTrainingContentList.get(mCurrentExerciseNumberInList);
                //покажем первое упражнение
                //createNewTrainingContent();
                showTrainingContentOnScreen();
            }
        }
    }

    private void createNewTrainingContent() {
        int mExerciseWeightLastDay = 0;
        int mWeightInCalendar = 0;
        List<TrainingContent> mTrainingContentNotNullAmount = new ArrayList<>();
        List<WeightChangeCalendar> mWeightChangeCalendarList = new ArrayList<>();
        if (dbCurrentUser != null) {
            mTrainingContentNotNullAmount = DB.getLastExerciseNotNullAmountAndWeightOfUser(dbCurrentUser.getId(),
                    mCurrentTraining.getDay(), mCurrentExercise.getId());
            mWeightChangeCalendarList = DB.getWeightOfUserFromWeightCalendar(dbCurrentUser.getId(),
                    mCurrentTraining.getDay());
        }
        mWeightInCalendar = 0;
        if (mWeightChangeCalendarList.size() == 1) {
            try {
                mWeightInCalendar = mWeightChangeCalendarList.get(0).getWeight();
            } catch (Exception e) {
                mWeightInCalendar = 0;
            }
        }
        mExerciseWeightLastDay = 0;
        if (mTrainingContentNotNullAmount.size() == 1) {
            try {
                mExerciseWeightLastDay = mTrainingContentNotNullAmount.get(0).getWeight();
            } catch (Exception e) {
                mExerciseWeightLastDay = 0;
            }
        }

        int weight = 0;
        if (mUseCalendarForWeight) {
            weight = mExerciseWeightLastDay > mWeightInCalendar ? mExerciseWeightLastDay : mWeightInCalendar;
        } else {
            weight = mExerciseWeightLastDay;

        }
        mCurrentTrainingContent = new TrainingContent.Builder(DB)
                .addExercise(mCurrentExercise)
                .addTraining(mCurrentTraining)
                .addWeight(weight)
                .build();
        saveTraining();
    }

    private void showTrainingContentOnScreen() {

        ImageView ivPicture = findViewById(R.id.ivPicture);
        if (ivPicture != null) {

            if (mCurrentExercise.getPicture() != null && !"".equals(mCurrentExercise.getPicture())) {
                ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
            } else {
                ivPicture.setBackgroundColor(Color.WHITE);
            }
        }

        TextView tvExplanation = findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(mCurrentExercise.getExplanation());
        }
        TextView tvExerciseName = findViewById(R.id.tvExerciseName);
        if (tvExerciseName != null) {

            tvExerciseName.setText("Exercise: " + mCurrentExercise.getName());
        }

        EditText etComment = findViewById(R.id.etComment);
        if (etComment != null) {
            if (mCurrentTrainingContent != null) {
                etComment.setText(mCurrentTrainingContent.getComment());
            }
        }
        int mAmountID = getResources().getIdentifier("btAmount", "id", getPackageName());
        Button btAmount = findViewById(mAmountID);
        if (btAmount != null) {
            if (mCurrentTrainingContent != null) {
                int amount = mCurrentTrainingContent.getAmount();
                if (amount != 0) {
                    btAmount.setText(String.valueOf(amount));
                } else {
                    btAmount.setText("");
                }
            }
        }

        Button btDefaultAmount = findViewById(R.id.btAmountDefault);
        if (btDefaultAmount != null) {
            int mAmountDefault = mCurrentExercise.getAmountDefault();
            btDefaultAmount.setText("DEFAULT VOL: " + String.valueOf(mAmountDefault == 0 ? "--" : mAmountDefault));
        }

        Button btYesterdayAmount = findViewById(R.id.btAmountLastDay);
        if (btYesterdayAmount != null) {
            List<TrainingContent> mTrainingsContentList = new ArrayList<>();

            if (dbCurrentUser != null) {
                mTrainingsContentList = DB.getLastExerciseNotNullAmountAndWeightOfUser(dbCurrentUser.getId(),
                        mCurrentTraining.getDay(), mCurrentExercise.getId());
            }
            mExerciseAmountLastDay = 0;
            if (mTrainingsContentList.size() == 1) {
                try {
                    mExerciseAmountLastDay = mTrainingsContentList.get(0).getAmount();
                } catch (Exception e) {
                    mExerciseAmountLastDay = 0;
                }
            }
            btYesterdayAmount.setText("LAST VOL: " + String.valueOf("".equals(mExerciseAmountLastDay) ? "--" : mExerciseAmountLastDay));
        }

        int mWeight = getResources().getIdentifier("btWeight", "id", getPackageName());
        Button btWeight = findViewById(mWeight);
        if (btWeight != null) {
            String btWeightText = "";
            if (mCurrentTrainingContent != null && mCurrentTrainingContent.getWeight() != 0) {
                btWeightText = String.valueOf(mCurrentTrainingContent.getWeight());
            }
            btWeight.setText(btWeightText);
        }
    }

    private void showTrainingContentOnScreen(final int ex_id) {
        try {
            mCurrentExercise = Exercise.getExerciseFromDB(DB, ex_id);
        } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
            tableDoesNotContainElementException.printStackTrace();
        }

        showTrainingContentOnScreen();
    }

//    public boolean onTouchEvent(MotionEvent event)
//    {
//        switch(event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//                x1 = event.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                float deltaX = x2 - x1;
//
//                if (Math.abs(deltaX) > MIN_DISTANCE)
//                {
//                    // Left to Right swipe action
//                    if (x2 > x1)
//                    {
//                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
//                    }
//
//                    // Right to left swipe action
//                    else
//                    {
//                        Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
//                    }
//
//                }
//                else
//                {
//                    // consider as something else - a screen tap for example
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }


    private void showTrainingOnScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentTraining.getId()));
        }

        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = findViewById(mDayID);
        if (etDay != null) {
            if (mCurrentTraining.getDay() == 0) {
                etDay.setText("");
            } else {
                etDay.setText(mCurrentTraining.getDayString());
            }
        }
    }

    public void saveTrainingFromScreen() {

        getPropertiesFromScreen();
        saveTraining();
    }

    public void saveTraining() {

        mCurrentTraining.dbSave(DB);
        if (mCurrentTrainingContent != null) {
            mCurrentTrainingContent.dbSave(DB);
        }
        mTrainingIsNew = false;
    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityTrainingsList.class);
        intent.putExtra("id", mCurrentTraining.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {
            mCurrentTraining.setID(Integer.parseInt(String.valueOf(tvID.getText())));
        }

        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = findViewById(mDayID);
        if (tvDay != null) {
            Date d = convertStringToDate(String.valueOf(tvDay.getText()));
            if (d != null) {
                mCurrentTraining.setDay(d.getTime());
            }
        }

        Button btAmount = findViewById(R.id.btAmount);
        if (btAmount != null) {
            mCurrentTrainingContent.setAmount(convertButtonTextToDigit(String.valueOf(btAmount.getText())));
        }

        Button btWeight = findViewById(R.id.btWeight);
        if (btWeight != null) {
            mCurrentTrainingContent.setWeight(convertButtonTextToDigit(String.valueOf(btWeight.getText())));
        }

        EditText etComment = findViewById(R.id.etComment);
        if (etComment != null) {
            try {
                mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));
            } catch (Exception e) {
            }
        }
    }

    public void btSave_onClick(final View view) {
        blink(view, this);
        saveTrainingFromScreen();
    }

    private void saveCurrentTrainingContent(final boolean readFromScreen) {

        if (readFromScreen) {
            Button btAmount = findViewById(R.id.btAmount);
            if (btAmount != null) {
                mCurrentTrainingContent.setAmount(convertButtonTextToDigit(String.valueOf(btAmount.getText())));
            }

            EditText etComment = findViewById(R.id.etComment);
            if (etComment != null) {
                mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));
            }

            Button btWeight = findViewById(R.id.btWeight);
            if (btWeight != null) {
                mCurrentTrainingContent.setWeight(convertButtonTextToDigit(String.valueOf(btWeight.getText())));
            }
        }
        mCurrentTrainingContent.dbSave(DB);
        if (!mTrainingContentList.contains(mCurrentTrainingContent)) {
            mTrainingContentList.add(mCurrentTrainingContent);
        }
    }

    private int convertButtonTextToDigit(String textAmount) {

        if (textAmount == null || textAmount.trim().equals("")) {
            return 0;
        } else {
            return Integer.parseInt(textAmount);
        }
    }

    public void btDelete_onClick(final View view) {

        blink(view, this);
        if (!mTrainingIsNew) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to delete current training??")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            List<Training> trainings = DB.getLastTrainingsByDates(mCurrentTraining.getDay());
                            mCurrentTraining.dbDelete(DB);
                            Intent intent = new Intent(getApplicationContext(), ActivityTrainingsList.class);
                            if (!trainings.isEmpty()) {
                                intent.putExtra("id", trainings.get(0).getId());
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).setNegativeButton("No", null).show();
        }
    }

    public void tvDay_onClick(final View view) {

        getPropertiesFromScreen();
        mCurrentTrainingContent.dbSave(DB);
        mCurrentTraining.dbSave(DB);
        blink(view, this);
        Intent intent = new Intent(ActivityTraining.this, ActivityCalendarView.class);
        intent.putExtra("isNew", mTrainingIsNew);
        intent.putExtra("currentActivity", getClass().getName());
        if (!mTrainingIsNew) {
            intent.putExtra("currentTrainingId", mCurrentTraining.getId());
        }
        if (mCurrentTraining.getDay() == 0) {
            intent.putExtra("currentDateInMillis", 0);
        } else {
            intent.putExtra("currentDateInMillis", mCurrentTraining.getDay());
        }
        intent.putExtra("currentExerciseIndex", mCurrentExerciseNumberInList);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btAmountChange_onClick(final View view) {

        blink(view, this);
        Button button = (Button) view;
        String value = String.valueOf(button.getText());
        DigitChange(Integer.parseInt(value), R.id.btAmount);

    }

    public void btWeightChange_onClick(final View view) {

        blink(view, this);
        Button button = (Button) view;
        String value = String.valueOf(button.getText());
        DigitChange(Integer.parseInt(value), R.id.btWeight);

    }

    private void DigitChange(final int dx, final int buttonId) {

        Button bt = findViewById(buttonId);
        if (bt != null) {
            int digit = 0;
            try {
                digit = Integer.parseInt(String.valueOf(bt.getText()));
            } catch (Exception e) {
                digit = 0;
            }
            digit = digit + dx;
            digit = digit < 0 ? 0 : digit;
            bt.setText(String.valueOf(digit));
        }
    }

    private void updateButtonsListOfExercises() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int btWidth = displaymetrics.widthPixels / maxNumberOfTransferButtons;
        mTextSize = (int) (btWidth / 3.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trow = findViewById(R.id.trowTrainingList);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        params.span = 4;
        if (trow != null) {
            trow.removeAllViews();
            trow.setMinimumHeight(btWidth);

            int mNumBegin = 0;
            int mNumEnd = 0;
            if (mCurrentExerciseNumberInList + 1 <= 2) {
                mNumBegin = 1;
                mNumEnd = 3;
            } else if (mCurrentExerciseNumberInList >= mActiveExercises.size() - 2) {
                mNumBegin = mActiveExercises.size() - 2;
                mNumEnd = mActiveExercises.size();
            } else {
                mNumBegin = (mCurrentExerciseNumberInList + 1) - 1;
                mNumEnd = (mCurrentExerciseNumberInList + 1) + 1;
            }

            Button butFirst = createNewExerciseButtonInButtonsList(trow, btWidth, params, "1",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btTrainingListFirst_onClick((TextView) v);
                        }
                    }
            );
            Button butPrevious = createNewExerciseButtonInButtonsList(trow, btWidth, params, "...",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btChooseExercise_onClick((TextView) v);
                        }
                    }
            );

            for (int mCount = mNumBegin; mCount <= mNumEnd; mCount++) {
                Button butNumber = createNewExerciseButtonInButtonsList(trow, btWidth, params, String.valueOf(mCount), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btTrainingContentButtons_onClick((TextView) v);
                    }
                });
                butNumber.setId(numberOfViews + mCount);
                if (mCount - 1 == mCurrentExerciseNumberInList) {
                    butNumber.setTextColor(Color.RED);
                    butNumber.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }

            Button butNext = createNewExerciseButtonInButtonsList(trow, btWidth, params, "...",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btChooseExercise_onClick((TextView) v);
                        }
                    }
            );
            Button butLast = createNewExerciseButtonInButtonsList(trow, btWidth, params, String.valueOf(mActiveExercises.size()),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btTrainingListLast_onClick((TextView) v);
                        }
                    }
            );
        }
    }

    private Button createNewExerciseButtonInButtonsList(TableRow trow, int btWidth, TableRow.LayoutParams params, String mName, View.OnClickListener mListener) {

        Button but = new Button(this);
        but.setLayoutParams(params);
        but.setText(String.valueOf(mName));
        but.setTextSize(mTextSize);
        but.setWidth(btWidth);
        but.setHeight(btWidth);
        but.setBackgroundResource(R.drawable.bt_border);
        but.setGravity(Gravity.CENTER);
        but.setTextColor(getResources().getColor(R.color.text_color));
        but.setOnClickListener(mListener);
        trow.addView(but);
        return but;
    }

    public void btAmount_onClick(View view) {
        getPropertiesFromScreen();
        mCurrentTrainingContent.dbSave(DB);
        mCurrentTraining.dbSave(DB);
        blink(view, this);
        try {
            Intent intent = new Intent(ActivityTraining.this, ActivityDigitPickerDialog.class);
            intent.putExtra("isNew", mTrainingIsNew);
            intent.putExtra("currentDigitTitle", Digit.AMOUNT.name());
            intent.putExtra("currentActivity", getClass().getName());
            if (!mTrainingIsNew) {
                intent.putExtra("currentTrainingId", mCurrentTraining.getId());
            }
            intent.putExtra("currentExerciseIndex", mCurrentExerciseNumberInList);
            int amount = 0;
            if (mCurrentTrainingContent.getAmount() != 0) {
                amount = mCurrentTrainingContent.getAmount();
            }
            intent.putExtra("currentDigit", amount);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (NumberFormatException e) {
            // Amount is String. Can be text or smth

        }
    }

    public void btWeight_onClick(View view) {
        getPropertiesFromScreen();
        mCurrentTrainingContent.dbSave(DB);
        mCurrentTraining.dbSave(DB);
        blink(view, this);
        Intent intent = new Intent(ActivityTraining.this, ActivityDigitPickerDialog.class);
        intent.putExtra("isNew", mTrainingIsNew);
        intent.putExtra("currentDigitTitle", Digit.WEIGHT.name());
        intent.putExtra("currentActivity", getClass().getName());

        if (!mTrainingIsNew) {
            intent.putExtra("currentTrainingId", mCurrentTraining.getId());
        }
        intent.putExtra("currentExerciseIndex", mCurrentExerciseNumberInList);
        intent.putExtra("currentDigit", mCurrentTrainingContent.getWeight());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btTrainingContentButtons_onClick(TextView view) {

        blink(view, this);
        int newId = view.getId() % numberOfViews;
        int step = newId - (mCurrentExerciseNumberInList + 1);
        saveCurrentTrainingContent(true);
        moveToNewExercise(step);

    }

    private void moveToNewExercise(final int steps) {

        //boolean readFromScreen = true;
        //saveCurrentTrainingContent(readFromScreen);
        int mStepsABS = Math.abs(steps);

        for (int i = 1; i <= mStepsABS; i++) {
            boolean readFromScreen = false;
            if (steps < 0 && mCurrentExerciseNumberInList == 0) {
                break;
            } else if (steps < 0 && mCurrentExerciseNumberInList != 0) {
                setPreviousExercise();
            } else if (steps > 0 && mCurrentExerciseNumberInList == mActiveExercises.size() - 1) {
                break;
            } else if (steps > 0 && mCurrentExerciseNumberInList != mActiveExercises.size() - 1) {
                setNextExercise();
            }
            saveCurrentTrainingContent(readFromScreen);
        }
        showExercise();
    }

    private void btChooseExercise_onClick(final TextView view) {
        blink(view, this);
        getPropertiesFromScreen();
        mCurrentTrainingContent.dbSave(DB);
        mCurrentTraining.dbSave(DB);
        Intent intent = new Intent(ActivityTraining.this, ActivityExerciseChoice.class);
        intent.putExtra("currentActivity", getClass().getName());
        intent.putExtra("currentTrainingId", mCurrentTraining.getId());
        intent.putExtra("currentExerciseIndex", mCurrentExerciseNumberInList);
        intent.putExtra("currentExerciseListSize", mActiveExercises.size());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void btTrainingListFirst_onClick(final TextView view) {
        blink(view, this);
        moveToNewExercise(-mCurrentExerciseNumberInList);
    }

    private void btTrainingListLast_onClick(final TextView view) {

        blink(view, this);
        moveToNewExercise(mActiveExercises.size() - mCurrentExerciseNumberInList);

    }

    private void getPreferencesFromFile() {

        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE)) {
            mPlusMinusButtonValue = mSettings.getInt(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE, 10);
        } else {
            mPlusMinusButtonValue = 10;
        }

        mUseCalendarForWeight = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_USE_CALENDAR_FOR_WEIGHT) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_USE_CALENDAR_FOR_WEIGHT, false);
        mShowExplanation = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, false);

        mShowPicture = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE, false);

        mShowAmountDefaultButton = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_DEFAULT_BUTTON) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_DEFAULT_BUTTON, false);

        mShowAmountLastDayButton = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_LAST_DAY_BUTTON) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_LAST_DAY_BUTTON, false);
    }

    private void setPreferencesOnScreen() {

        Button btAmountPlus = findViewById(R.id.btAmountPlus);

        if (btAmountPlus != null) {
            btAmountPlus.setText(String.valueOf(mPlusMinusButtonValue));
        }

        Button btVolumeMinus = findViewById(R.id.btAmountMinus);

        if (btVolumeMinus != null) {
            btVolumeMinus.setText(String.valueOf(-1 * mPlusMinusButtonValue));
        }

        Button btWeightPlus = findViewById(R.id.btWeightPlus);

        if (btWeightPlus != null) {
            btWeightPlus.setText(String.valueOf(mPlusMinusButtonValue));
        }

        Button btWeightMinus = findViewById(R.id.btWeightMinus);

        if (btWeightMinus != null) {
            btWeightMinus.setText(String.valueOf(-1 * mPlusMinusButtonValue));
        }

        ImageView ivPicture = findViewById(R.id.ivPicture);

        if (ivPicture != null) {
            if (mShowPicture) {
                ivPicture.setVisibility(View.VISIBLE);
            } else {
                ivPicture.setVisibility(View.GONE);
            }
        }

        TextView tvExplanation = findViewById(R.id.tvExplanation);

        if (tvExplanation != null) {
            if (mShowExplanation) {
                tvExplanation.setVisibility(View.VISIBLE);
            } else {
                tvExplanation.setVisibility(View.GONE);
            }
        }

        Button btAmountDefault = findViewById(R.id.btAmountDefault);

        if (btAmountDefault != null) {
            if (mShowAmountDefaultButton) {
                btAmountDefault.setVisibility(View.VISIBLE);
            } else {
                btAmountDefault.setVisibility(View.GONE);
            }
        }

        Button btAmountLastDay = findViewById(R.id.btAmountLastDay);

        if (btAmountLastDay != null) {
            if (mShowAmountLastDayButton) {
                btAmountLastDay.setVisibility(View.VISIBLE);
            } else {
                btAmountLastDay.setVisibility(View.GONE);
            }
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityTrainingsList.class);
        intent.putExtra("id", mCurrentTraining.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class SwipeDetectorActivity extends AppCompatActivity implements View.OnTouchListener {
        private Activity activity;
        static final int MIN_DISTANCE = 200;
        private float downX, downY, upX, upY;

        public SwipeDetectorActivity(final Activity activity) {
            this.activity = activity;
        }

        public final void onRightToLeftSwipe() {
            // System.out.println("Right to Left swipe [Previous]");
            Toast.makeText(ActivityTraining.this, "[NEXT EXERCISE]", Toast.LENGTH_SHORT).show();
            setNextExercise();
            showExercise();

        }

        public void onLeftToRightSwipe() {
            // System.out.println("Left to Right swipe [Next]");
            Toast.makeText(ActivityTraining.this, "[PREVIOUS EXERCISE]", Toast.LENGTH_SHORT).show();
            setPreviousExercise();
            showExercise();

        }

        public void onTopToBottomSwipe() {
            //Toast.makeText(ActivityTraining.this, "Top to Bottom swipe [Down]", Toast.LENGTH_SHORT).show();
        }

        public void onBottomToTopSwipe() {
            //Toast.makeText(ActivityTraining.this, "Bottom to Top swipe [Up]", Toast.LENGTH_SHORT).show ();

        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                    //break;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // swipe horizontal?
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        //Toast.makeText(ActivityTraining.this, "DeltaX="+String.valueOf(deltaX), Toast.LENGTH_SHORT).show();
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                            return true;
                        }
                    } else {

                    }

                    // swipe vertical?
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        //Toast.makeText(ActivityTraining.this, "DeltaY="+String.valueOf(deltaY), Toast.LENGTH_SHORT).show();
                        if (deltaY < 0) {
                            this.onTopToBottomSwipe();
                            //break;
                            return true;
                        }
                        if (deltaY > 0) {
                            this.onBottomToTopSwipe();
                            //break;

                            return true;

                        }
                    } else {

                    }
                    //break;
                    return true;
                }
            }
            //Toast.makeText(ActivityTraining.this, "ЖОПА", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}