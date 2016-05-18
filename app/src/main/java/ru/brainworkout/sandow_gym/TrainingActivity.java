package ru.brainworkout.sandow_gym;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Ivan on 16.05.2016.
 */
public class TrainingActivity extends AppCompatActivity {
    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    Training mCurrentTraining;
    TrainingContent mCurrentTrainingContent;
    Exercise mCurrentExercise;

    DatabaseManager db;

    private boolean mTrainingIsNew;

    public static int mDirection = 0;

    List<Exercise> mActiveExercises;
    private int mCurrentExerciseNumberInList;

    private List<TrainingContent> mTrainingContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        db = new DatabaseManager(this);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        mCurrentTraining = intent.getParcelableExtra("CurrentTraining");

        if (mCurrentTraining == null) {
            if (mTrainingIsNew) {
                mCurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
            } else {
                int id = intent.getIntExtra("CurrentID", 0);
                if (id == 0) {
                    mCurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
                } else {
                    mCurrentTraining = db.getTraining(id);
                }
            }
        }

        showTrainingOnScreen();

        SwipeDetectorActivity swipeDetectorActivity = new SwipeDetectorActivity(this);
        ScrollView sv = (ScrollView) this.findViewById(R.id.svMain);
        sv.setOnTouchListener(swipeDetectorActivity);

        if (mTrainingIsNew) {
            getAllActiveExercises();
        }else {
            getAllExercisesOfTraining();
        }
        saveTraining();



    }

    private void setNextExercise() {
        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {

            mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));
        }
        mTrainingContentList.add(mCurrentTrainingContent);
        db.updateTrainingContent(mCurrentTrainingContent);
        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != mActiveExercises.size() - 1) {

                mCurrentExerciseNumberInList++;
                Exercise ex = mActiveExercises.get(mCurrentExerciseNumberInList);

                //покажем первое упражнение
                ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
                if (ivPicture != null) {
                    ivPicture.setImageResource(getResources().getIdentifier(ex.getPicture(), "drawable", getPackageName()));
                }
                TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
                if (tvExplanation != null) {

                    tvExplanation.setText(ex.getExplanation());
                }

                //ищем есть ли в списке упражнение с ID. Если нет - создаем новое, есть - выводим на экран

                boolean isFound = false;
                for (TrainingContent mTr : mTrainingContentList) {
                    if (mTr.getIdExercise() == ex.getID()) {
                        isFound = true;
                        mCurrentTrainingContent = mTr;
                        mTrainingContentList.remove(mTrainingContentList.indexOf(mTr));
                        break;
                    }
                }
                if (!isFound) {
                    mCurrentTrainingContent = new TrainingContent();
                    mCurrentTrainingContent.setID(db.getTrainingContentMaxNumber() + 1);
                    mCurrentTrainingContent.setIdExercise(ex.getID());
                    mCurrentTrainingContent.setIdTraining(mCurrentTraining.getID());
                    mCurrentTrainingContent.setVolume("");
                    db.addTrainingContent(mCurrentTrainingContent);
                }
                showTrainingContentOnScreen(mCurrentTrainingContent.getIdExercise());

            }

        }
    }

    private void setPreviousExercise() {
        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {

            mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));
        }
        mTrainingContentList.add(mCurrentTrainingContent);
        db.updateTrainingContent(mCurrentTrainingContent);
        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != 0) {

                mCurrentExerciseNumberInList--;
                Exercise ex = mActiveExercises.get(mCurrentExerciseNumberInList);

                //покажем первое упражнение
                ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
                if (ivPicture != null) {
                    ivPicture.setImageResource(getResources().getIdentifier(ex.getPicture(), "drawable", getPackageName()));
                }
                TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
                if (tvExplanation != null) {

                    tvExplanation.setText(ex.getExplanation());
                }

                //ищем есть ли в списке упражнение с ID. Если нет - создаем новое, есть - выводим на экран
                boolean isFound = false;
                for (TrainingContent mTr : mTrainingContentList) {
                    if (mTr.getIdExercise() == ex.getID()) {
                        isFound = true;
                        mCurrentTrainingContent = mTr;
                        mTrainingContentList.remove(mTrainingContentList.indexOf(mTr));
                        break;
                    }
                }

                if (!isFound) {
                    mCurrentTrainingContent = new TrainingContent();
                    mCurrentTrainingContent.setID(db.getTrainingContentMaxNumber() + 1);
                    mCurrentTrainingContent.setIdExercise(ex.getID());
                    mCurrentTrainingContent.setIdTraining(mCurrentTraining.getID());
                    db.addTrainingContent(mCurrentTrainingContent);
                }
                showTrainingContentOnScreen(mCurrentTrainingContent.getIdExercise());
            }
        }
    }

    private void getAllExercisesOfTraining() {


        Log.d("Reading: ", "Reading all active exercises..");
        mActiveExercises = db.getAllActiveExercises();
        Log.d("Reading: ", "Reading all exercises of training ..");
        mTrainingContentList = db.getAllTrainingContentOfTraining(mCurrentTraining.getID());
        Exercise ex1;
        if (mActiveExercises.size() != 0) {
            mCurrentExerciseNumberInList = 0;
            ex1 = mActiveExercises.get(mCurrentExerciseNumberInList);
            //покажем первое упражнение
            showTrainingContentOnScreen(ex1);

            if (mTrainingContentList.size() != 0) {
                mCurrentTrainingContent = mTrainingContentList.get(0);
            } else {
                int maxNum = db.getTrainingContentMaxNumber() + 1;
                mCurrentTrainingContent = new TrainingContent(maxNum, "", ex1.getID(), mCurrentTraining.getID());
            }
            db.addTrainingContent(mCurrentTrainingContent);
        }
    }

    private void getAllActiveExercises() {

        Log.d("Reading: ", "Reading all active exercises..");
        mActiveExercises = db.getAllActiveExercises();

        mTrainingContentList = new ArrayList<>();

        Exercise ex1;
        if (mActiveExercises.size() != 0) {
            mCurrentExerciseNumberInList = 0;
            ex1 = mActiveExercises.get(mCurrentExerciseNumberInList);
            //покажем первое упражнение
            showTrainingContentOnScreen(ex1);
            int maxNum = db.getTrainingContentMaxNumber() + 1;
            mCurrentTrainingContent = new TrainingContent(maxNum, "", ex1.getID(), mCurrentTraining.getID());
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
        TextView tvExerciseID = (TextView) findViewById(R.id.tvExerciseID);
        if (tvExerciseID != null) {

            tvExerciseID.setText(ex.getName());
        }
        int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
        TextView etVolume = (TextView) findViewById(mVolumeID);
        if (etVolume != null) {
            if (mCurrentTrainingContent != null) {
                String vol = mCurrentTrainingContent.getVolume();
                if (vol != null) {
                    etVolume.setText(vol);
                } else {
                    etVolume.setText("");
                }
            }
        }
    }

    private void showTrainingContentOnScreen(int ex_id) {

        Exercise ex = db.getExercise(ex_id);

        showTrainingContentOnScreen(ex);
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
                etDay.setText(ConvertDateToString(mCurrentTraining.getDay()));
            }
        }

    }

    private String ConvertDateToString(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = dateformat.format(date);

        return sDate;
    }

    private Date ConvertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
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

            Date d = ConvertStringToDate(String.valueOf(tvDay.getText()));

            if (d != null) {
                try {
                    mCurrentTraining.setDay(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
        MyLogger(TAG, "Добавили " + String.valueOf(mCurrentTraining.getID()));
    }

    public static void MyLogger(String TAG, String statement) {
        if (isDebug) {
            Log.e(TAG, statement);
        }
    }

    public void btDelete_onClick(View view) {
//
//        if (!mTrainingIsNew) {
//
//            MyLogger(TAG, "Удалили " + String.valueOf(CurrentTraining.getID()));
//            //потом закрываем
//            db.deleteTraining(CurrentTraining);
//
//            Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//
//        }

    }

    public void tvDay_onClick(View view) {

        Intent intent = new Intent(TrainingActivity.this, CalendarViewActivity.class);

        intent.putExtra("CurrentTraining", mCurrentTraining);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentActivity", "NewTrainingActivity");
//        if (!mTrainingIsNew) {
//        intent.putExtra("CurrentID",CurrentTraining.getID());
//        }
//        if (CurrentTraining.getDay()==null) {
//            intent.putExtra("CurrentDate","");
//        }else {
//        intent.putExtra("CurrentDate",ConvertDateToString(CurrentTraining.getDay()));}

        startActivity(intent);
    }

    private class SwipeDetectorActivity extends AppCompatActivity implements View.OnTouchListener {

        private Activity activity;
        static final int MIN_DISTANCE = 100;
        private float downX, downY, upX, upY;

        public SwipeDetectorActivity(final Activity activity) {
            this.activity = activity;
        }

        public final void onRightToLeftSwipe() {
            System.out.println("Right to Left swipe [Previous]");
            setNextExercise();
        }

        public void onLeftToRightSwipe() {
            System.out.println("Left to Right swipe [Next]");
            setPreviousExercise();

        }

        public void onTopToBottomSwipe() {
            //System.out.println("Top to Bottom swipe [Down]");
        }

        public void onBottomToTopSwipe() {
            //System.out.println("Bottom to Top swipe [Up]");
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
                        if (deltaY < 0) {
                            this.onTopToBottomSwipe();
                            return true;
                        }
                        if (deltaY > 0) {
                            this.onBottomToTopSwipe();
                            return true;
                        }
                    } else {

                    }
                    //break;
                    return true;
                }
            }
            return false;
        }
    }
}