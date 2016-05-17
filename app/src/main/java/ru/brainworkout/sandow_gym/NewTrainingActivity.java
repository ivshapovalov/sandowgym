package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Ivan on 16.05.2016.
 */
public class NewTrainingActivity extends AppCompatActivity {
    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    Training CurrentTraining;

    DatabaseManager db;

    private boolean mTrainingIsNew;

    private GestureDetector gestureDetector;



    public class SwipeDetector {

        private int swipe_distance;
        private int swipe_velocity;
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public SwipeDetector(int distance, int velocity) {
            super();
            this.swipe_distance = distance;
            this.swipe_velocity = velocity;
        }

        public SwipeDetector() {
            super();
            this.swipe_distance = SWIPE_MIN_DISTANCE;
            this.swipe_velocity = SWIPE_THRESHOLD_VELOCITY;
        }

        public boolean isSwipeDown(MotionEvent e1, MotionEvent e2, float velocityY) {
            return isSwipe(e2.getY(), e1.getY(), velocityY);
        }

        public boolean isSwipeUp(MotionEvent e1, MotionEvent e2, float velocityY) {
            return isSwipe(e1.getY(), e2.getY(), velocityY);
        }

        public boolean isSwipeLeft(MotionEvent e1, MotionEvent e2, float velocityX) {
            return isSwipe(e1.getX(), e2.getX(), velocityX);
        }

        public boolean isSwipeRight(MotionEvent e1, MotionEvent e2, float velocityX) {
            return isSwipe(e2.getX(), e1.getX(), velocityX);
        }

        private boolean isSwipeDistance(float coordinateA, float coordinateB) {
            return (coordinateA - coordinateB) > this.swipe_distance;
        }

        private boolean isSwipeSpeed(float velocity) {
            return Math.abs(velocity) > this.swipe_velocity;
        }

        private boolean isSwipe(float coordinateA, float coordinateB, float velocity) {
            return isSwipeDistance(coordinateA, coordinateB)
                    && isSwipeSpeed(velocity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_new);

        db = new DatabaseManager(this);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        CurrentTraining = intent.getParcelableExtra("CurrentTraining");

        if (CurrentTraining==null ) {
            if (mTrainingIsNew) {
                CurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
            } else {
                int id = intent.getIntExtra("CurrentID", 0);
                if (id == 0) {
                    CurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
                } else {
                    CurrentTraining = db.getTraining(id);
                }
            }
        }

        showTrainingOnScreen();

        gestureDetector = initGestureDetector();

        View view = findViewById(R.id.svMain);

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            }
        });
    }

    private GestureDetector initGestureDetector() {
        return new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            private SwipeDetector detector = new SwipeDetector();

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                try {
                    if (detector.isSwipeDown(e1, e2, velocityY)) {
                        return false;
                    } else if (detector.isSwipeUp(e1, e2, velocityY)) {
                        showToast("Up Swipe");
                    }else if (detector.isSwipeLeft(e1, e2, velocityX)) {
                        showToast("Left Swipe");
                    } else if (detector.isSwipeRight(e1, e2, velocityX)) {
                        showToast("Right Swipe");
                    }
                } catch (Exception e) {} //for now, ignore
                return false;
            }

            private void showToast(String phrase){
                Toast.makeText(getApplicationContext(), phrase, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTrainingOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(CurrentTraining.getID()));
        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = (TextView) findViewById(mDayID);
        if (etDay != null) {
            if (CurrentTraining.getDay() == null) {
                etDay.setText("");
            } else {
                etDay.setText(ConvertDateToString(CurrentTraining.getDay()));
            }
        }

    }

    private String ConvertDateToString(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = dateformat.format(date);

        return  sDate;
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
        intent.putExtra("id", CurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            CurrentTraining.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = (TextView) findViewById(mDayID);
        if (tvDay != null) {

            Date d=ConvertStringToDate(String.valueOf(tvDay.getText()));

            if (d!=null) {
                try {
                    CurrentTraining.setDay(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void btSave_onClick(View view) {

        //сначала сохраняем
        getPropertiesFromScreen();

        if (mTrainingIsNew) {
            db.addTraining(CurrentTraining);
        } else {
            db.updateTraining(CurrentTraining);

        }

        mTrainingIsNew=false;
        MyLogger(TAG, "Добавили " + String.valueOf(CurrentTraining.getID()));
        //потом закрываем

//        Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
//        intent.putExtra("id", CurrentTraining.getID());
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
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

        Intent intent = new Intent(NewTrainingActivity.this, CalendarViewActivity.class);

        intent.putExtra("CurrentTraining",CurrentTraining);
        intent.putExtra("IsNew",mTrainingIsNew);
        intent.putExtra("CurrentActivity","NewTrainingActivity");
//        if (!mTrainingIsNew) {
//        intent.putExtra("CurrentID",CurrentTraining.getID());
//        }
//        if (CurrentTraining.getDay()==null) {
//            intent.putExtra("CurrentDate","");
//        }else {
//        intent.putExtra("CurrentDate",ConvertDateToString(CurrentTraining.getDay()));}

        startActivity(intent);
    }
}