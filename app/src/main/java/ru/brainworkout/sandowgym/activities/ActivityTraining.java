package ru.brainworkout.sandowgym.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.database.entities.WeightChangeCalendar;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.entities.Exercise;
import ru.brainworkout.sandowgym.R;
import ru.brainworkout.sandowgym.database.entities.Training;
import ru.brainworkout.sandowgym.database.entities.TrainingContent;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class ActivityTraining extends AppCompatActivity {

    private static final int NUMBER_OF_VIEWS = 30000;
    private static final int MAX_NUMBER_OF_TRANSFER_BUTTONS = 7;

    private SharedPreferences mSettings;
    private int mPlusMinusButtonValue;
    private boolean mShowPicture;
    private boolean mShowExplanation;
    private boolean mShowVolumeDefaultButton;
    private boolean mShowVolumeLastDayButton;

    private Training mCurrentTraining;
    private TrainingContent mCurrentTrainingContent;
    private Exercise mCurrentExercise;
    private String mExerciseVolumeLastDay = "";
    private final DatabaseManager DB = new DatabaseManager(this);
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
        mWidth = displaymetrics.widthPixels / 1;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis",0);


        boolean weightIsNeedToUpdate = false;
        if (mTrainingIsNew) {
            weightIsNeedToUpdate = true;
        }

        int id = intent.getIntExtra("CurrentTrainingID", 0);

        defineCurrentTraining(id, currentDateInMillis);
        showTrainingOnScreen();

        //не используем. Почему то на Philips W732 не работает прокрутка вниз. на всех остальных устройствах работает.
        //убираю скрол вправо, влево. перемещение кнопками внизу

//        SwipeDetectorActivity swipeDetectorActivity = new SwipeDetectorActivity(ActivityTraining.this);
//        ScrollView sv = (ScrollView) this.findViewById(R.id.svMain);
//        sv.setOnTouchListener(swipeDetectorActivity);




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

        int exID = intent.getIntExtra("CurrentExerciseID", 0);

        if (exID != 0) {
            saveAndGoToNewExercise(exID);
        }

        if (weightIsNeedToUpdate || (currentDateInMillis != 0 && mCurrentTraining != null && currentDateInMillis!=currentDateOldInMillis)) {

            updateCurrentWeightOfTrainingContent();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void updateDayOnScreen(long currentDateInMillis) {

        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = (TextView) findViewById(mDayID);
        if (etDay != null) {

            etDay.setText(ConvertDateToString(ConvertMillisToDate(currentDateInMillis)));

        }
    }

    private void updateCurrentWeightOfTrainingContent() {

        int mExerciseWeightLastDay = 0;
        int mWeightInCalendar = 0;
        List<TrainingContent> mTrainingContentNotNullVolume = new ArrayList<>();
        List<WeightChangeCalendar> mWeightChangeCalendarList = new ArrayList<>();
        if (dbCurrentUser != null) {
            mTrainingContentNotNullVolume = DB.getLastExerciseNotNullVolumeAndWeightOfUser(dbCurrentUser.getID(),
                    mCurrentTraining.getDay(), mCurrentExercise.getID());
            mWeightChangeCalendarList = DB.getWeightOfUserFromWeightCalendar(dbCurrentUser.getID(),
                    mCurrentTraining.getDay());
        }
        if (mTrainingContentNotNullVolume.size() == 1) {
            try {
                mExerciseWeightLastDay = mTrainingContentNotNullVolume.get(0).getWeight();
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
            List<TrainingContent> trainingContentList = DB.getAllTrainingContentOfTraining(mCurrentTraining.getID());
            for (TrainingContent trainingContent : trainingContentList
                    ) {
                if (trainingContent.getWeight() == 0) {
                    trainingContent.setWeight(mWeightInCalendar);
                    if (mCurrentTrainingContent != null) {
                        if (trainingContent.getID() == mCurrentTrainingContent.getID()) {
                            trainingContent.setWeight(mExerciseWeightLastDay > mWeightInCalendar ? mExerciseWeightLastDay : mWeightInCalendar);
                            mCurrentTrainingContent.setWeight(mExerciseWeightLastDay > mWeightInCalendar ? mExerciseWeightLastDay : mWeightInCalendar);
                        }
                    }



                    trainingContent.dbSave(DB);
                }
            }

            int mWeight = getResources().getIdentifier("etWeight", "id", getPackageName());
            TextView etWeight = (TextView) findViewById(mWeight);
            if (etWeight != null) {

                if (mCurrentTrainingContent != null) {
                    etWeight.setText(String.valueOf(mCurrentTrainingContent.getWeight()));
                }
            }

        }

    }

    private void defineCurrentTraining(int mCurrentId, long currentDateInMillis) {
        if (mTrainingIsNew) {

            mCurrentTraining = new Training.Builder(DB).build();
            //Calendar calendar = Calendar.getInstance();
            if (currentDateInMillis ==0) {
                Calendar cal = Calendar.getInstance();
                cal.clear(Calendar.MILLISECOND);
                //cal.set(9999,11,31,12,59,59);
                //cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0,0);
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
                    //возможно удалили элемент
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

    public void btVolumeDefault_onClick(final View view) {

        blink(view,this);
        if (mCurrentExercise != null) {
            if (!"".equals(mCurrentExercise.getVolumeDefault())) {
                int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
                TextView etVolume = (TextView) findViewById(mVolumeID);
                if (etVolume != null) {
                    etVolume.setText(mCurrentExercise.getVolumeDefault());
                }
            }
        }

    }

    public void btVolumeLastDay_onClick(final View view) {

        blink(view,this);
        int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
        TextView etVolume = (TextView) findViewById(mVolumeID);
        if (etVolume != null) {
            if ("".equals(mExerciseVolumeLastDay)) {
                etVolume.setText("0");
            } else {
                etVolume.setText(mExerciseVolumeLastDay);
            }
        }

    }

    public void btOptions_onClick(final View view) {

        blink(view,this);

        Intent intent = new Intent(ActivityTraining.this, ActivityTrainingOptions.class);

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
        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {
            if (mCurrentExercise.getPicture() != null && !"".equals(mCurrentExercise.getPicture())) {
                ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
                ivPicture.setMinimumHeight(mHeight);
            } else {
                ivPicture.setBackgroundColor(Color.WHITE);
            }
        }
        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(mCurrentExercise.getExplanation());
        }
        showTrainingContentOnScreen(mCurrentTrainingContent.getIdExercise());
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
            if (mTr.getIdExercise() == mCurrentExercise.getID()) {
                mCurrentTrainingContent = mTr;
                //mTrainingContentList.remove(mTrainingContentList.indexOf(mTr));
                return false;
            }
        }
        return true;
    }

    private void getAllExercisesOfTraining() {

        mActiveExercises = DB.getAllActiveExercisesOfUser(dbCurrentUser.getID());
        mTrainingContentList = DB.getAllTrainingContentOfTraining(mCurrentTraining.getID());

        for (TrainingContent tr : mTrainingContentList
                ) {
            boolean isFound = false;
            int id_ex = tr.getIdExercise();
            for (Exercise ex : mActiveExercises
                    ) {
                if (ex.getID() == id_ex) {
                    isFound = true;
                    break;
                }

            }
            //если в текущих активных не нашли - добавляем новое
            if (!isFound) {
                //добавим в список упражнений упражнение старое
                Exercise ex = null;
                try {
                    ex = Exercise.getExerciseFromDB(DB, id_ex);
                } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                    tableDoesNotContainElementException.printStackTrace();
                }
                mActiveExercises.add(ex);
            }
        }

        Collections.sort(mActiveExercises, new Comparator() {
            public int compare(Object ex1, Object ex2) {
                return ((Exercise) (ex1)).getID() - ((Exercise) (ex2)).getID();
            }
        });

        if (mActiveExercises.size() != 0) {
            mCurrentExerciseNumberInList = 0;
            mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);


            if (mTrainingContentList.size() != 0 && mTrainingContentList.get(0).getIdExercise() == mCurrentExercise.getID()) {
                mCurrentTrainingContent = mTrainingContentList.get(0);
            } else {
                createNewTrainingContent();
            }
            showTrainingContentOnScreen();

        }
    }

    private void getAllActiveExercises() {

        if (dbCurrentUser != null) {
            mActiveExercises = DB.getAllActiveExercisesOfUser(dbCurrentUser.getID());
            mTrainingContentList = new ArrayList<>();

            Exercise ex1;
            if (mActiveExercises.size() != 0) {
                mCurrentExerciseNumberInList = 0;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
                //покажем первое упражнение
                createNewTrainingContent();
                showTrainingContentOnScreen();

            }
        }
    }

    private void createNewTrainingContent() {
        int mExerciseWeightLastDay = 0;
        int mWeightInCalendar = 0;
        List<TrainingContent> mTrainingContentNotNullVolume = new ArrayList<>();
        List<WeightChangeCalendar> mWeightChangeCalendarList = new ArrayList<>();
        if (dbCurrentUser != null) {
            mTrainingContentNotNullVolume = DB.getLastExerciseNotNullVolumeAndWeightOfUser(dbCurrentUser.getID(),
                    mCurrentTraining.getDay(), mCurrentExercise.getID());
            mWeightChangeCalendarList = DB.getWeightOfUserFromWeightCalendar(dbCurrentUser.getID(),
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
        if (mTrainingContentNotNullVolume.size() == 1) {
            try {
                mExerciseWeightLastDay = mTrainingContentNotNullVolume.get(0).getWeight();
            } catch (Exception e) {
                mExerciseWeightLastDay = 0;
            }
        }

        mCurrentTrainingContent = new TrainingContent.Builder(DB)
                .addExerciseId(mCurrentExercise.getID())
                .addTrainingId(mCurrentTraining.getID())
                .addVolume("")
                .addWeight(mExerciseWeightLastDay > mWeightInCalendar ? mExerciseWeightLastDay : mWeightInCalendar)
                .build();
        mCurrentTrainingContent.dbSave(DB);

    }

    private void showTrainingContentOnScreen() {

        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {

            if (mCurrentExercise.getPicture() != null && !"".equals(mCurrentExercise.getPicture())) {
                ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
            } else {
                ivPicture.setBackgroundColor(Color.WHITE);
            }

        }
        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(mCurrentExercise.getExplanation());
        }
        TextView tvExerciseName = (TextView) findViewById(R.id.tvExerciseName);
        if (tvExerciseName != null) {

            tvExerciseName.setText("Упражнение: " + mCurrentExercise.getName());
        }

        EditText etComment = (EditText) findViewById(R.id.etComment);
        if (etComment != null) {
            if (mCurrentTrainingContent != null) {
                etComment.setText(mCurrentTrainingContent.getComment());
            }
        }
        int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
        TextView etVolume = (TextView) findViewById(mVolumeID);
        if (etVolume != null) {
            if (mCurrentTrainingContent != null) {
                String vol = mCurrentTrainingContent.getVolume();
                if (vol != null && vol != "") {
                    etVolume.setText(vol);
                } else {
                    etVolume.setText("");
                }
            }
        }

        Button btDefaultVolume = (Button) findViewById(R.id.btVolumeDefault);
        if (btDefaultVolume != null) {

            String mVolumeDefault = mCurrentExercise.getVolumeDefault();
            btDefaultVolume.setText("DEFAULT VOL: " + String.valueOf("".equals(mVolumeDefault) ? "--" : mVolumeDefault));
        }

        Button btYesterdayVolume = (Button) findViewById(R.id.btVolumeLastDay);
        if (btYesterdayVolume != null) {
            List<TrainingContent> mTrainingsContentList = new ArrayList<TrainingContent>();

            if (dbCurrentUser != null) {
                mTrainingsContentList = DB.getLastExerciseNotNullVolumeAndWeightOfUser(dbCurrentUser.getID(),
                        mCurrentTraining.getDay(), mCurrentExercise.getID());
            }
            mExerciseVolumeLastDay = "";
            if (mTrainingsContentList.size() == 1) {
                try {
                    mExerciseVolumeLastDay = mTrainingsContentList.get(0).getVolume();
                } catch (Exception e) {
                    mExerciseVolumeLastDay = "";
                }
            }
            btYesterdayVolume.setText("LAST VOL: " + String.valueOf("".equals(mExerciseVolumeLastDay) ? "--" : mExerciseVolumeLastDay));
        }

        int mWeight = getResources().getIdentifier("etWeight", "id", getPackageName());
        TextView etWeight = (TextView) findViewById(mWeight);
        if (etWeight != null) {
            String etWeightText = "";
            if (mCurrentTrainingContent != null) {
                etWeightText = String.valueOf(mCurrentTrainingContent.getWeight());
            }
            etWeight.setText(etWeightText);
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
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentTraining.getID()));
        }

        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = (TextView) findViewById(mDayID);
        if (etDay != null) {
            if (mCurrentTraining.getDay() == 0) {
                etDay.setText("");
            } else {
                etDay.setText(mCurrentTraining.getDayString());
            }
        }
    }

    public void btClose_onClick(final View view) {

        blink(view,this);

        Intent intent = new Intent(getApplicationContext(), ActivityTrainingsList.class);
        intent.putExtra("id", mCurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            mCurrentTraining.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = (TextView) findViewById(mDayID);
        if (tvDay != null) {

            Date d = ConvertStringToDate(String.valueOf(tvDay.getText()));

            if (d != null) {
                    mCurrentTraining.setDay(d.getTime());
            }

        }
        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            try {
                mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));

            } catch (Exception e) {
                mCurrentTrainingContent.setVolume(String.valueOf(0));
            }
        }

        EditText etWeight = (EditText) findViewById(R.id.etWeight);
        if (etWeight != null) {
            try {
                mCurrentTrainingContent.setWeight(Integer.parseInt(String.valueOf(etWeight.getText())));

            } catch (Exception e) {
                mCurrentTrainingContent.setWeight(0);
            }
        }

        EditText etComment = (EditText) findViewById(R.id.etComment);
        if (etComment != null) {
            try {
                mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));

            } catch (Exception e) {

            }
        }
    }

    public void btSave_onClick(final View view) {

        blink(view,this);
        saveTraining();

    }

    public void saveTraining() {

        getPropertiesFromScreen();

        mCurrentTraining.dbSave(DB);

        if (mCurrentTrainingContent != null) {

            mCurrentTrainingContent.dbSave(DB);
        }
        mTrainingIsNew = false;

    }

    private void saveCurrentTrainingContent(final boolean readFromScreen) {

        if (readFromScreen) {
            EditText etVolume = (EditText) findViewById(R.id.etVolume);
            if (etVolume != null) {

                mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));
            }
            EditText etComment = (EditText) findViewById(R.id.etComment);
            if (etComment != null) {

                mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));
            }
            EditText etWeight = (EditText) findViewById(R.id.etWeight);
            if (etWeight != null) {

                String weight = String.valueOf(etWeight.getText());
                if (weight.trim().equals("")) {
                    mCurrentTrainingContent.setWeight(0);
                } else {
                    mCurrentTrainingContent.setWeight(Integer.parseInt(weight));
                }
            }
        }
        mCurrentTrainingContent.dbSave(DB);
        if (!mTrainingContentList.contains(mCurrentTrainingContent)) {
            mTrainingContentList.add(mCurrentTrainingContent);
        }

    }

    public void btDelete_onClick(final View view) {

        blink(view,this);

        if (!mTrainingIsNew) {

            new AlertDialog.Builder(this)
                    .setMessage("Вы действительно хотите удалить текущую тренировку?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            List<Training> trainings=DB.getLastTrainingsByDates(mCurrentTraining.getDayString());

                            mCurrentTrainingContent.dbDelete(DB);
                            mCurrentTraining.dbDelete(DB);

                            Intent intent = new Intent(getApplicationContext(), ActivityTrainingsList.class);
                            if (!trainings.isEmpty()) {
                                intent.putExtra("id", trainings.get(0).getID());
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Нет", null).show();
        }
    }

    public void tvDay_onClick(final View view) {

        getPropertiesFromScreen();
        mCurrentTrainingContent.dbSave(DB);
        mCurrentTraining.dbSave(DB);
        blink(view,this);

        Intent intent = new Intent(ActivityTraining.this, ActivityCalendarView.class);

        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentActivity", "ActivityTraining");
        if (!mTrainingIsNew) {
            intent.putExtra("CurrentTrainingID", mCurrentTraining.getID());
        }
        if (mCurrentTraining.getDay() == 0) {
            intent.putExtra("CurrentDateInMillis", 0);
        } else {
            intent.putExtra("CurrentDateInMillis", mCurrentTraining.getDay());
        }
        intent.putExtra("CurrentExerciseID", mCurrentExerciseNumberInList);

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
            Toast.makeText(ActivityTraining.this, "[Следующее упражнение]", Toast.LENGTH_SHORT).show();
            setNextExercise();
            showExercise();

        }

        public void onLeftToRightSwipe() {
            // System.out.println("Left to Right swipe [Next]");
            Toast.makeText(ActivityTraining.this, "[Предыдущее упражнение]", Toast.LENGTH_SHORT).show();
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

    public void btVolumeLeft_onClick(final View view) {

        blink(view,this);
        VolumeChange(-1);
    }

    public void btVolumeLeft10_onClick(final View view) {

        blink(view,this);
        VolumeChange(-1 * mPlusMinusButtonValue);
    }

    public void btVolumeRight_onClick(final View view) {

        blink(view,this);
        VolumeChange(1);
    }

    public void btVolumeRight10_onClick(final View view) {

        blink(view,this);
        VolumeChange(mPlusMinusButtonValue);
    }

    private void VolumeChange(final int dx) {

        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            int mVolume = 0;
            try {
                mVolume = Integer.parseInt(String.valueOf(etVolume.getText()));
            } catch (Exception e) {
                mVolume = 0;
            }
            mVolume = mVolume + dx;
            mVolume = mVolume < 0 ? 0 : mVolume;
            etVolume.setText(String.valueOf(mVolume));
        }

    }

    private void updateButtonsListOfExercises() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int btWidth = displaymetrics.widthPixels / MAX_NUMBER_OF_TRANSFER_BUTTONS;
        mTextSize = (int) (btWidth / 3.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trow = (TableRow) findViewById(R.id.trowTrainingList);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        params.span = 4;
        if (trow != null) {
            trow.removeAllViews();
            trow.setMinimumHeight(btWidth);

            int mNumBegin = 0;
            int mNumEnd = 0;
            if (mCurrentExerciseNumberInList + 1 <= 3) {
                mNumBegin = 1;
                mNumEnd = 5;
            } else if (mCurrentExerciseNumberInList >= mActiveExercises.size() - 3) {
                mNumBegin = mActiveExercises.size() - 4;
                mNumEnd = mActiveExercises.size();
            } else {
                mNumBegin = (mCurrentExerciseNumberInList + 1) - 2;
                mNumEnd = (mCurrentExerciseNumberInList + 1) + 2;
            }

            Button butPrevious = createNewExerciseButtonInButtonsList(trow, btWidth, params, "<-",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btTrainingListPrevious_onClick((TextView) v);
                        }
                    }
            );

            for (int mCount = mNumBegin; mCount <= mNumEnd; mCount++) {
                Button butNumber = createNewExerciseButtonInButtonsList(trow, btWidth, params, String.valueOf(mCount), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btTrainingList_onClick((TextView) v);
                    }
                });
                butNumber.setId(NUMBER_OF_VIEWS + mCount);
                if (mCount - 1 == mCurrentExerciseNumberInList) {
                    butNumber.setTextColor(Color.RED);
                    butNumber.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }

            Button butNext = createNewExerciseButtonInButtonsList(trow, btWidth, params, "->",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btTrainingListNext_onClick((TextView) v);
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

    public void btTrainingList_onClick(TextView view) {

        blink(view,this);
        int newId = view.getId() % NUMBER_OF_VIEWS;
        int step = newId - (mCurrentExerciseNumberInList + 1);
        saveAndGoToNewExercise(step);

    }

    private void saveAndGoToNewExercise(final int steps) {

        boolean readFromScreen = true;

        saveCurrentTrainingContent(readFromScreen);
        int mStepsABS = Math.abs(steps);
        for (int i = 1; i <= mStepsABS; i++) {
            readFromScreen = false;
            if (steps < 0) {
                setPreviousExercise();
            } else {
                setNextExercise();
            }
            saveCurrentTrainingContent(readFromScreen);
        }
        showExercise();
    }

    private void btTrainingListPrevious_onClick(final TextView view) {

        saveAndGoToNewExercise(-5);

    }

    private void btTrainingListNext_onClick(final TextView view) {

        saveAndGoToNewExercise(5);

    }

    private void getPreferencesFromFile() {

        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE)) {
            mPlusMinusButtonValue = mSettings.getInt(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE, 10);
        } else {
            mPlusMinusButtonValue = 10;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION)) {
            mShowExplanation = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, false);
        } else {
            mShowExplanation = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE)) {
            mShowPicture = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE, false);
        } else {
            mShowPicture = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON)) {
            mShowVolumeDefaultButton = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON, false);
        } else {
            mShowVolumeDefaultButton = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON)) {
            mShowVolumeLastDayButton = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON, false);
        } else {
            mShowVolumeLastDayButton = false;
        }
    }

    private void setPreferencesOnScreen() {

        Button btVolumePlus = (Button) findViewById(R.id.btVolumePlus);

        if (btVolumePlus != null) {
            btVolumePlus.setText(String.valueOf(mPlusMinusButtonValue));
        }

        Button btVolumeMinus = (Button) findViewById(R.id.btVolumeMinus);

        if (btVolumeMinus != null) {
            btVolumeMinus.setText(String.valueOf(-1 * mPlusMinusButtonValue));
        }

        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);

        if (ivPicture != null) {
            if (mShowPicture) {
                ivPicture.setVisibility(View.VISIBLE);
            } else {
                ivPicture.setVisibility(View.GONE);
            }
        }

        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);

        if (tvExplanation != null) {
            if (mShowExplanation) {
                tvExplanation.setVisibility(View.VISIBLE);
            } else {
                tvExplanation.setVisibility(View.GONE);
            }
        }

        Button btVolumeDefault = (Button) findViewById(R.id.btVolumeDefault);

        if (btVolumeDefault != null) {
            if (mShowVolumeDefaultButton) {
                btVolumeDefault.setVisibility(View.VISIBLE);
            } else {
                btVolumeDefault.setVisibility(View.GONE);
            }
        }

        Button btVolumeLastDay = (Button) findViewById(R.id.btVolumeLastDay);

        if (btVolumeLastDay != null) {
            if (mShowVolumeLastDayButton) {
                btVolumeLastDay.setVisibility(View.VISIBLE);
            } else {
                btVolumeLastDay.setVisibility(View.GONE);
            }
        }
    }
}