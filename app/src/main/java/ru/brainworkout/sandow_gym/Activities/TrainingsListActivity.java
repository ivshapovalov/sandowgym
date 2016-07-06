package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ru.brainworkout.sandow_gym.MainActivity;
import ru.brainworkout.sandow_gym.commons.Common;
import ru.brainworkout.sandow_gym.database.AndroidDatabaseManager;
import ru.brainworkout.sandow_gym.database.DatabaseManager;
import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.commons.Training;

public class TrainingsListActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();
    private final int mNumOfView = 20000;

    DatabaseManager db;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private String mCurrentDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);

        db = new DatabaseManager(this);

        Intent intent = getIntent();

        mCurrentDate = intent.getStringExtra("CurrentDate");
        if (mCurrentDate==null) {mCurrentDate="";}

        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = (Button) findViewById(mDayID);
        if (btDay != null) {
            if (mCurrentDate == null || mCurrentDate.equals("")) {
                btDay.setText("");
            } else {
                btDay.setText(mCurrentDate);
            }
        }


        showTrainings();

    }

    // Вызывается в начале "активного" состояния.
    @Override
    public void onResume() {
        super.onResume();

        showTrainings();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        if (id == 0) {
            if (!( mCurrentDate == null || mCurrentDate.equals(""))) {
                List <Training> trainings = db.getTrainingsByDates(mCurrentDate, mCurrentDate);
                if (trainings.size()==1) {
                     id=trainings.get(0).getID();
                }
            }

        }
        if (id!=0) {
            TableRow mRow = (TableRow) findViewById(mNumOfView + id);
            if (mRow != null) {

                int mScrID = getResources().getIdentifier("svTableTrainings", "id", getPackageName());
                ScrollView mScrollView = (ScrollView) findViewById(mScrID);
                if (mScrollView != null) {

                    mScrollView.requestChildFocus(mRow, mRow);
                }
            }
        }
    }


    public void bt_TrainingsAdd_onClick(View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void showTrainings() {

        Log.d("Reading: ", "Reading all trainings..");
        List<Training> trainings;
        //if (mCurrentDate == null || mCurrentDate.equals("")) {
            trainings = db.getAllTrainings();
//        } else {
//            trainings = db.getTrainingsByDates(mCurrentDate, mCurrentDate);
//        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableTrainings);
        //TableLayout layout = (TableLayout) findViewById(R.id.tableExercises);
        try {
            sv.removeAllViews();
        } catch (Exception e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        //допустим 10 строк тренировок
        mHeight = displaymetrics.heightPixels / 15;
        mWidth = displaymetrics.widthPixels / 2;
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

        for (int numEx = 0; numEx < trainings.size(); numEx++) {
            TableRow mRow = new TableRow(this);
            String data = "";
            data =trainings.get(numEx).getDayString();
//            TableLayout.LayoutParams params=new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.setMargins(0,20,0,20);
//            mRow.setGravity(Gravity.CENTER_VERTICAL);
//            mRow.setLayoutParams(params);
            //mRow.setPadding(0,30,0,30);
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.textview_border);
            mRow.setId(mNumOfView + trainings.get(numEx).getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowTraining_onClick((TableRow) v);
                }
            });
            //mRow.setGravity(Gravity.LEFT);
            TextView txt = new TextView(this);
            //txt.setId(10000 + numEx);
            txt.setText(String.valueOf(trainings.get(numEx).getID()));
            txt.setBackgroundResource(R.drawable.textview_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            if (mCurrentDate!=null && mCurrentDate.equals(data)) {
                txt.setTextColor(Color.RED);
            } else {
                txt.setTextColor(Color.BLUE);
            }

            //params.span = 3;
            //txt.setLayoutParams(params);

            mRow.addView(txt);

            txt = new TextView(this);
            //txt.setId(20000 + numEx);
          txt.setText(data);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setBackgroundResource(R.drawable.textview_border);
            if (mCurrentDate!=null && mCurrentDate.equals(data)) {
                txt.setTextColor(Color.RED);
            } else {
                txt.setTextColor(Color.BLUE);
            }
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.textview_border);
            layout.addView(mRow);
        }
        sv.addView(layout);

    }

    private void rowTraining_onClick(TableRow v) {

        Common.blink(v);
//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(100); //You can manage the blinking time with this parameter
//        anim.setStartOffset(0);
//        anim.setRepeatMode(Animation.REVERSE);
//        //anim.setRepeatCount(Animation.INFINITE);
//        anim.setRepeatCount(3);
//        v.startAnimation(anim);

        int id = v.getId() % mNumOfView;
        //System.out.println(String.valueOf(a));

        Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("CurrentID", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public static void MyLogger(String TAG, String statement) {
        if (isDebug) {
            Log.e(TAG, statement);
        }
    }


    public void bt_Edit_onClick(View view) {

        Common.blink(view);
        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(View view) {
        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void btDeleteAllTrainings_onClick(View view) {

        Common.blink(view);
        db.deleteAllTrainings();
        db.deleteAllTrainingContent();
        showTrainings();

    }


    public void btDay_onClick(View view) {

        Common.blink(view);
        Intent intent = new Intent(TrainingsListActivity.this, CalendarViewActivity.class);
        intent.putExtra("CurrentDate", mCurrentDate);
        intent.putExtra("CurrentActivity", "TrainingsListActivity");


        startActivity(intent);
    }

    public void btTrainingsFilterDelete_onClick(View view) {

        Common.blink(view);
        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay= (Button) findViewById(mDayID);
        if (btDay != null) {

            btDay.setText("");
            mCurrentDate = "";
            showTrainings();

        }

    }
}
