package ru.brainworkout.sandow_gym.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
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

import ru.brainworkout.sandow_gym.commons.Common;
import ru.brainworkout.sandow_gym.MainActivity;
import ru.brainworkout.sandow_gym.database.DatabaseManager;
import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.commons.Training;
import ru.brainworkout.sandow_gym.commons.TrainingContent;


/**
 * Created by Ivan on 16.05.2016.
 */
public class TrainingActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    private SharedPreferences mSettings;
    private boolean mShowPicture;
    private boolean mShowExplanation;
    private boolean mShowVolumeDefaultButton;
    private boolean mShowVolumeLastDayButton;
    private Training mCurrentTraining;
    private TrainingContent mCurrentTrainingContent;
    private Exercise mCurrentExercise;
    private String mVolumeLastDay = "";
    private DatabaseManager db;
    private int mHeight;
    private int mWidth;
    private int mTextSize;
    private boolean mTrainingIsNew;
    private List<Exercise> mActiveExercises;
    private int mCurrentExerciseNumberInList;
    private List<TrainingContent> mTrainingContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        getPreferencesFromFile();
        setPreferencesOnScreen();

        db = new DatabaseManager(this);


        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        //допустим 15 строк тренировок
        mHeight = displaymetrics.heightPixels / 2;
        mWidth = displaymetrics.widthPixels / 1;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        String mCurrentDate = intent.getStringExtra("CurrentDate");

        if (mTrainingIsNew) {

            mCurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
            //Calendar calendar = Calendar.getInstance();
            if ((mCurrentDate == null)) {
                String cal = ((Date) Calendar.getInstance().getTime()).toLocaleString();
                try {
                    mCurrentTraining.setDay((Date) Calendar.getInstance().getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mCurrentTraining.setDay(Common.ConvertStringToDate(mCurrentDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } else {
            int id = intent.getIntExtra("CurrentID", 0);
            if (id == 0) {
                mCurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
            } else {
                mCurrentTraining = db.getTraining(id);
            }
            try {
                if ((mCurrentDate != null)) {
                    mCurrentTraining.setDayString(mCurrentDate);
                }
            } catch (Exception e) {
            }
        }


        showTrainingOnScreen();

        //не используем. Почему то на Philips W732 не работает прокрутка вниз. на всех остальных устройствах работает.
        //убираю скрол вправо, влево. перемещение кнопками внизу

//        SwipeDetectorActivity swipeDetectorActivity = new SwipeDetectorActivity(TrainingActivity.this);
//        ScrollView sv = (ScrollView) this.findViewById(R.id.svMain);
//        sv.setOnTouchListener(swipeDetectorActivity);


        if (mTrainingIsNew) {
            getAllActiveExercises();
        } else {
            getAllExercisesOfTraining();
        }

        saveTraining();
        updateTrainingList();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferencesFromFile();
        setPreferencesOnScreen();
    }

    public void btVolumeDefault_onClick(View view) {
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

    public void btVolumeLastDay_onClick(View view) {

        if (!"".equals(mVolumeLastDay)) {
            int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
            TextView etVolume = (TextView) findViewById(mVolumeID);
            if (etVolume != null) {
                etVolume.setText(mVolumeLastDay);
            }
        }
    }

    public void btOptions_onClick(View view) {

        Intent intent = new Intent(TrainingActivity.this, TrainingActivityOptions.class);

        startActivity(intent);
    }


    class ExerciseComp implements Comparator {
        public int compare(Object ex1, Object ex2) {
            return ((Exercise) (ex1)).getID() - ((Exercise) (ex2)).getID();
        }
    }

    private void setNextExercise() {
        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {

            mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));
        }
        EditText etComment = (EditText) findViewById(R.id.etComment);
        if (etComment != null) {

            mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));
        }
        mTrainingContentList.add(mCurrentTrainingContent);
        db.updateTrainingContent(mCurrentTrainingContent);
        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != mActiveExercises.size() - 1) {

                mCurrentExerciseNumberInList++;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
                //ищем есть ли в списке упражнение с ID. Если нет - создаем новое, есть - выводим на экран
                boolean isFound = false;
                for (TrainingContent mTr : mTrainingContentList) {
                    if (mTr.getIdExercise() == mCurrentExercise.getID()) {
                        isFound = true;
                        mCurrentTrainingContent = mTr;
                        mTrainingContentList.remove(mTrainingContentList.indexOf(mTr));
                        break;
                    }
                }
                if (!isFound) {
                    mCurrentTrainingContent = new TrainingContent();
                    mCurrentTrainingContent.setID(db.getTrainingContentMaxNumber() + 1);
                    mCurrentTrainingContent.setIdExercise(mCurrentExercise.getID());
                    mCurrentTrainingContent.setIdTraining(mCurrentTraining.getID());
                    mCurrentTrainingContent.setVolume("");
                    db.addTrainingContent(mCurrentTrainingContent);
                }
            }
        }
    }

    private void showExercise() {
        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {
            ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
            ivPicture.setMinimumHeight(mHeight);
        }
        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(mCurrentExercise.getExplanation());
        }
        showTrainingContentOnScreen(mCurrentTrainingContent.getIdExercise());
        updateTrainingList();

    }

    private void setPreviousExercise() {
        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {

            mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));
        }
        EditText etComment = (EditText) findViewById(R.id.etComment);
        if (etComment != null) {

            mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));
        }
        mTrainingContentList.add(mCurrentTrainingContent);
        db.updateTrainingContent(mCurrentTrainingContent);
        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != 0) {

                mCurrentExerciseNumberInList--;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);

                //ищем есть ли в списке упражнение с ID. Если нет - создаем новое, есть - выводим на экран
                boolean isFound = false;
                for (TrainingContent mTr : mTrainingContentList) {
                    if (mTr.getIdExercise() == mCurrentExercise.getID()) {
                        isFound = true;
                        mCurrentTrainingContent = mTr;
                        mTrainingContentList.remove(mTrainingContentList.indexOf(mTr));
                        break;
                    }
                }

                if (!isFound) {
                    mCurrentTrainingContent = new TrainingContent();
                    mCurrentTrainingContent.setID(db.getTrainingContentMaxNumber() + 1);
                    mCurrentTrainingContent.setIdExercise(mCurrentExercise.getID());
                    mCurrentTrainingContent.setIdTraining(mCurrentTraining.getID());
                    db.addTrainingContent(mCurrentTrainingContent);
                }
            }
        }

    }

    private void getAllExercisesOfTraining() {


        Log.d("Reading: ", "Reading all active exercises..");
        mActiveExercises = db.getAllActiveExercises();
        Log.d("Reading: ", "Reading all exercises of training ..");
        mTrainingContentList = db.getAllTrainingContentOfTraining(mCurrentTraining.getID());

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
                Exercise ex = db.getExercise(id_ex);
                mActiveExercises.add(ex);
            }
        }
        ;
        //отсортируем по ID список упражнений
        Collections.sort(mActiveExercises, new ExerciseComp());

        if (mActiveExercises.size() != 0) {
            mCurrentExerciseNumberInList = 0;
            mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
            //покажем первое упражнение


            if (mTrainingContentList.size() != 0 && mTrainingContentList.get(0).getIdExercise() == mCurrentExercise.getID()) {
                mCurrentTrainingContent = mTrainingContentList.get(0);
            } else {
                int maxNum = db.getTrainingContentMaxNumber() + 1;
                mCurrentTrainingContent = new TrainingContent(maxNum, "", mCurrentExercise.getID(), mCurrentTraining.getID());
                db.addTrainingContent(mCurrentTrainingContent);
            }
            showTrainingContentOnScreen(mCurrentExercise);

        }
    }

    private void getAllActiveExercises() {

        Log.d("Reading: ", "Reading all active exercises..");
        mActiveExercises = db.getAllActiveExercises();

        mTrainingContentList = new ArrayList<>();

        Exercise ex1;
        if (mActiveExercises.size() != 0) {
            mCurrentExerciseNumberInList = 0;
            mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
            //покажем первое упражнение
            showTrainingContentOnScreen(mCurrentExercise);
            int maxNum = db.getTrainingContentMaxNumber() + 1;
            mCurrentTrainingContent = new TrainingContent(maxNum, "", mCurrentExercise.getID(), mCurrentTraining.getID());
            db.addTrainingContent(mCurrentTrainingContent);
        }
    }

    private void showTrainingContentOnScreen(Exercise ex) {

        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {

            ivPicture.setImageResource(getResources().getIdentifier(ex.getPicture(), "drawable", getPackageName()));
        }
        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(ex.getExplanation());
        }
        TextView tvExerciseName = (TextView) findViewById(R.id.tvExerciseName);
        if (tvExerciseName != null) {

            tvExerciseName.setText("Упражнение: " + ex.getName());
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
            btDefaultVolume.setText("По умолчанию: " + String.valueOf("".equals(mVolumeDefault) ? "--" : mVolumeDefault));
        }
        Button btYesterdayVolume = (Button) findViewById(R.id.btVolumeLastDay);
        if (btYesterdayVolume != null) {

            List<Training> mTrainingList = db.getLastTrainingsByDates(Common.ConvertDateToString(mCurrentTraining.getDay()));
            if (mTrainingList.size() == 1) {
                try {
                    mVolumeLastDay = db.getTrainingContent(mCurrentExercise.getID(), mTrainingList.get(0).getID()).getVolume();
                } catch (Exception e) {
                    mVolumeLastDay = "";
                }
            }
            btYesterdayVolume.setText("Вчера: " + String.valueOf("".equals(mVolumeLastDay) ? "--" : mVolumeLastDay));

        }


    }

//    private class Container extends ScrollView {
//
//        public Container(Context context) {
//            super(context);
//            setBackgroundColor(0xFF0000FF);
//        }
//
//        @Override
//        public boolean onInterceptTouchEvent(MotionEvent ev) {
//            Log.i(TAG, "onInterceptTouchEvent");
//            int action = ev.getActionMasked();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    Log.i(TAG, "onInterceptTouchEvent.ACTION_DOWN");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    Log.i(TAG, "onInterceptTouchEvent.ACTION_MOVE");
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_UP:
//                    Log.i(TAG, "onInterceptTouchEvent.ACTION_UP");
//                    break;
//            }
//            return super.onInterceptTouchEvent(ev);
//        }
//    }

    private void showTrainingContentOnScreen(int ex_id) {

        mCurrentExercise = db.getExercise(ex_id);

        showTrainingContentOnScreen(mCurrentExercise);
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

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentTraining.getID()));
        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = (TextView) findViewById(mDayID);
        if (etDay != null) {
            if (mCurrentTraining.getDay() == null) {
                etDay.setText("");
            } else {
                etDay.setText(Common.ConvertDateToString(mCurrentTraining.getDay()));
            }
        }

        int mWeight = getResources().getIdentifier("etWeight", "id", getPackageName());
        TextView etWeight = (TextView) findViewById(mWeight);
        if (etWeight != null) {

            etWeight.setText(String.valueOf(mCurrentTraining.getWeight()));
        }


    }


    public void btClose_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
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

            Date d = Common.ConvertStringToDate(String.valueOf(tvDay.getText()));

            if (d != null) {
                try {
                    mCurrentTraining.setDay(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            try {
                mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));

            } catch (Exception e) {

            }
        }

        EditText etWeight = (EditText) findViewById(R.id.etWeight);
        if (etWeight != null) {
            try {
                mCurrentTraining.setWeight(Integer.parseInt(String.valueOf(etWeight.getText())));

            } catch (Exception e) {

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

    public void btSave_onClick(View view) {

        saveTraining();

    }

    public void saveTraining() {

        //сначала сохраняем
        getPropertiesFromScreen();

        if (mTrainingIsNew) {
            db.addTraining(mCurrentTraining);
            if (mCurrentTrainingContent != null) {

                db.addTrainingContent(mCurrentTrainingContent);
            }

        } else {
            db.updateTraining(mCurrentTraining);
            if (mCurrentTrainingContent != null) {

                db.updateTrainingContent(mCurrentTrainingContent);
            }
        }

        mTrainingIsNew = false;
        //MyLogger(TAG, "Добавили " + String.valueOf(mCurrentTraining.getID()));
    }

    public static void MyLogger(String TAG, String statement) {
        if (isDebug) {
            Log.e(TAG, statement);
        }
    }

    public void btDelete_onClick(View view) {
//
        if (!mTrainingIsNew) {

            MyLogger(TAG, "Удалили " + String.valueOf(mCurrentTraining.getID()));
            //потом закрываем
            db.deleteTrainingContentOfTraining(mCurrentTraining.getID());

            db.deleteTraining(mCurrentTraining);

            Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }

    public void tvDay_onClick(View view) {

        Intent intent = new Intent(TrainingActivity.this, CalendarViewActivity.class);

        //intent.putExtra("CurrentTraining", mCurrentTraining);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentActivity", "TrainingActivity");
        if (!mTrainingIsNew) {
            intent.putExtra("CurrentTrainingID", mCurrentTraining.getID());
        }
        if (mCurrentTraining.getDay() == null) {
            intent.putExtra("CurrentDate", "");
        } else {
            intent.putExtra("CurrentDate", Common.ConvertDateToString(mCurrentTraining.getDay()));
        }

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
            Toast.makeText(TrainingActivity.this, "[Следующее упражнение]", Toast.LENGTH_SHORT).show();
            setNextExercise();
            showExercise();

        }

        public void onLeftToRightSwipe() {
            // System.out.println("Left to Right swipe [Next]");
            Toast.makeText(TrainingActivity.this, "[Предыдущее упражнение]", Toast.LENGTH_SHORT).show();
            setPreviousExercise();
            showExercise();


        }

        public void onTopToBottomSwipe() {

            //Toast.makeText(TrainingActivity.this, "Top to Bottom swipe [Down]", Toast.LENGTH_SHORT).show();

        }

        public void onBottomToTopSwipe() {

            //Toast.makeText(TrainingActivity.this, "Bottom to Top swipe [Up]", Toast.LENGTH_SHORT).show ();

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
                        //Toast.makeText(TrainingActivity.this, "DeltaX="+String.valueOf(deltaX), Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(TrainingActivity.this, "DeltaY="+String.valueOf(deltaY), Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(TrainingActivity.this, "ЖОПА", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void btVolumeLeft_onClick(View view) {

        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            int a = 0;
            try {
                a = Integer.parseInt(String.valueOf(etVolume.getText()));

                a = a == 0 ? 0 : a - 1;

                etVolume.setText(String.valueOf(a));
            } catch (Exception e) {

            }


        }
    }

    public void btVolumeLeft10_onClick(View view) {

        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            int a = 0;
            try {
                a = Integer.parseInt(String.valueOf(etVolume.getText()));

                a = a <= 10 ? 0 : a - 10;

                etVolume.setText(String.valueOf(a));
            } catch (Exception e) {

            }


        }
    }

    public void btVolumeRight_onClick(View view) {

        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            int a = 0;
            try {
                a = Integer.parseInt(String.valueOf(etVolume.getText()));
                a++;
                etVolume.setText(String.valueOf(a));
            } catch (Exception e) {

            }

        }
    }

    public void btVolumeRight10_onClick(View view) {

        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            int a = 0;
            try {
                a = Integer.parseInt(String.valueOf(etVolume.getText()));
                a += 10;
                etVolume.setText(String.valueOf(a));

            } catch (Exception e) {

            }

        }
    }

    private void updateTrainingList() {
        //TableLayout mTableMain=(TableLayout)findViewById(R.id.mTableMain);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        //допустим 15 строк тренировок
        int btWidth = displaymetrics.widthPixels / 7;
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

            //кнопка назад
            Button but = new Button(this);
            but.setLayoutParams(params);
            but.setText("<-");
            but.setTextSize(mTextSize);
            but.setWidth(btWidth);
            but.setBackgroundResource(R.drawable.textview_border);
            but.setGravity(Gravity.CENTER);
            but.setWidth(btWidth);
            but.setHeight(btWidth);
            trow.addView(but);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btTrainingListPrevious_onClick((TextView) v);
                }
            });

            for (int mCount = mNumBegin; mCount <= mNumEnd; mCount++) {
                but = new Button(this);
                but.setLayoutParams(params);
                but.setId(10000 + mCount);
                but.setText(String.valueOf(mCount));
                //txt.setMinimumHeight(25);
                but.setTextSize(mTextSize);
                but.setWidth(btWidth);
                but.setHeight(btWidth);
                but.setBackgroundResource(R.drawable.textview_border);
                but.setGravity(Gravity.CENTER);
                if (mCount - 1 == mCurrentExerciseNumberInList) {
                    but.setTextColor(Color.RED);
                    but.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    //but.setTextSize(but.getTextSize());
                }
                trow.addView(but);
                but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btTrainingList_onClick((TextView) v);
                    }
                });
            }
            but = new Button(this);
            but.setLayoutParams(params);
            but.setText("->");
            but.setWidth(btWidth);
            but.setHeight(btWidth);
            but.setTextSize(mTextSize);
            but.setBackgroundResource(R.drawable.textview_border);
            but.setGravity(Gravity.CENTER);
            trow.addView(but);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btTrainingListNext_onClick((TextView) v);
                }
            });
        }

    }

    private void btTrainingList_onClick(TextView v) {

//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(50); //You can manage the blinking time with this parameter
//        anim.setStartOffset(0);
//        anim.setRepeatMode(Animation.REVERSE);
//        //anim.setRepeatCount(Animation.INFINITE);
//        anim.setRepeatCount(3);
//        v.startAnimation(anim);

        int id = v.getId() % 10000;
        //System.out.println(String.valueOf(a));

        int a = (mCurrentExerciseNumberInList + 1) - id;
        if (a > 0) {
            for (int i = 1; i <= a; i++) {
                setPreviousExercise();
            }
            showExercise();

        } else if (a < 0) {
            for (int i = 1; i <= Math.abs(a); i++) {
                setNextExercise();
            }
            showExercise();
        }

    }

    private void btTrainingListPrevious_onClick(TextView v) {


        for (int i = 1; i <= 5; i++) {
            setPreviousExercise();
        }
        showExercise();


    }

    private void btTrainingListNext_onClick(TextView v) {


        for (int i = 1; i <= 5; i++) {
            setNextExercise();
        }
        showExercise();


    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(MainActivity.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION)) {
            // Получаем язык из настроек
            mShowExplanation = mSettings.getBoolean(MainActivity.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, false);
        } else {
            mShowExplanation = false;
        }

        if (mSettings.contains(MainActivity.APP_PREFERENCES_TRAINING_SHOW_PICTURE)) {
            // Получаем язык из настроек
            mShowPicture = mSettings.getBoolean(MainActivity.APP_PREFERENCES_TRAINING_SHOW_PICTURE, false);
        } else {
            mShowPicture = false;
        }

        if (mSettings.contains(MainActivity.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON)) {
            // Получаем язык из настроек
            mShowVolumeDefaultButton = mSettings.getBoolean(MainActivity.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON, false);
        } else {
            mShowVolumeDefaultButton = false;
        }

        if (mSettings.contains(MainActivity.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON)) {
            // Получаем язык из настроек
            mShowVolumeLastDayButton = mSettings.getBoolean(MainActivity.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON, false);
        } else {
            mShowVolumeLastDayButton = false;
        }
    }

    private void setPreferencesOnScreen() {

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