package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrainingsListActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();
    private final int mNumOfView = 20000;

    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);

        db = new DatabaseManager(this);

        showTrainings();

    }

    // Вызывается в начале "активного" состояния.
    @Override
    public void onResume() {
        super.onResume();

        showTrainings();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        TableRow mRow = (TableRow) findViewById(mNumOfView + id);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableTrainings", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
    }


    public void bt_TrainingsAdd_onClick(View view) {

        Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void showTrainings() {

        Log.d("Reading: ", "Reading all trainings..");
        List<Training> trainings = db.getAllTrainings();

        ScrollView sv = (ScrollView) findViewById(R.id.svTableTrainings);
        //TableLayout layout = (TableLayout) findViewById(R.id.tableExercises);
        try {
            sv.removeAllViews();
        } catch (Exception e) {
        }

        TableLayout layout = new TableLayout(this);
        //layout.removeAllViews();
        layout.setStretchAllColumns(true);
        //layout.setShrinkAllColumns(true);

        //TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0f);

        for (int numEx = 0; numEx < trainings.size(); numEx++) {
            TableRow mRow = new TableRow(this);
            mRow.setId(mNumOfView + trainings.get(numEx).getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowTraining_onClick((TableRow) v);
                }
            });
            //mRow.setGravity(Gravity.LEFT);
//            TextView txt = new TextView(this);
//            //txt.setId(10000 + numEx);
//            txt.setText(String.valueOf(trainings.get(numEx).getID()));
//            txt.setGravity(Gravity.LEFT);
//            txt.setBackgroundResource(R.drawable.textview_border);
//            //params.span = 3;
//            //txt.setLayoutParams(params);
//
//            mRow.addView(txt);

            TextView txt = new TextView(this);
            //txt.setId(20000 + numEx);
            String data="";
            if (trainings.get(numEx).getDay() != null) {
                data = String.valueOf(trainings.get(numEx).getDay().getYear() + 1900) + "-" + String.valueOf(trainings.get(numEx).getDay().getMonth() + 1) + "-" + String.valueOf(trainings.get(numEx).getDay().getDate());
            }

            txt.setText(data);
            txt.setGravity(Gravity.LEFT);
            txt.setBackgroundResource(R.drawable.textview_border);
            //params.span = 15;
            //txt.setLayoutParams(params);
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.textview_border);
            layout.addView(mRow);
        }
        sv.addView(layout);

    }

    private void rowTraining_onClick(TableRow v) {

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

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void btDeleteAllTrainings_onClick(View view) {

        db.deleteAllTrainings();
        showTrainings();

    }
}
