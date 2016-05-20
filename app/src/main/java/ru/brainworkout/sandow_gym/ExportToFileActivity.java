package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportToFileActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    private String mDateFrom;
    private String mDateTo;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_to_file);


        Intent intent = getIntent();
        String mCurrentDate = intent.getStringExtra("CurrentDate");
        String mCurrentDateTo = intent.getStringExtra("CurrentDateTo");
        Boolean isBeginDate = intent.getBooleanExtra("IsBeginDate", true);
        mDateFrom = mCurrentDate;
        mDateTo=mCurrentDateTo;

        updateScreen();

    }

    private void updateScreen() {
        //Имя
        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView etDayFrom = (TextView) findViewById(mDayFromID);
        if (etDayFrom != null) {
            if (mDateFrom == null || mDateFrom == "") {
                etDayFrom.setText("");
            } else {
                etDayFrom.setText(mDateFrom);
            }
        }

        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView etDayTo = (TextView) findViewById(mDayToID);
        if (etDayTo != null) {
            if (mDateTo == null || mDateTo == "") {
                etDayTo.setText("");
            } else {
                etDayTo.setText(mDateTo);
            }
        }
    }

    private void saveToFile() {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists())

        {
            exportDir.mkdirs();
        }


        File file = new File(exportDir, "trainings.csv");


        try

        {

            if (file.createNewFile()) {
                System.out.println("File is created!");
                System.out.println("trainings.csv " + file.getAbsolutePath());
            } else {
                System.out.println("File already exists.");
            }


            CSVWriter writer = new CSVWriter(new FileWriter(file));

            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[]{"India", "New Delhi"});
            data.add(new String[]{"United States", "Washington D.C"});
            data.add(new String[]{"Germany", "Berlin"});

            writer.writeAll(data);

            writer.close();
        /*String data="";
        data=readSavedData();
        data= data.replace(",", ";");
        writeData(data);*/


        } catch (
                Exception e) {
        }
    }


    public void btExport_onClick(View view) {

        saveToFile();
        Intent intent = new Intent(ExportToFileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(View view) {

        Intent intent = new Intent(ExportToFileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void tvDayFrom_onClick(View view) {
        day_onClick(true);
    }

    public void tvDayTo_onClick(View view) {

        day_onClick(false);
    }

    private void day_onClick(boolean isBeginDate) {

        Intent intent = new Intent(ExportToFileActivity.this, CalendarViewActivity.class);
        intent.putExtra("IsBeginDate", isBeginDate);
        intent.putExtra("CurrentActivity", "ExportToFileActivity");

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        if (tvDayFrom != null) {
            intent.putExtra("CurrentDate", String.valueOf(tvDayFrom.getText()));
        } else {
            intent.putExtra("CurrentDate", "");
        }
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            intent.putExtra("CurrentDateTo", String.valueOf(tvDayTo.getText()));
        } else {
            intent.putExtra("CurrentDateTo", "");
        }

        startActivity(intent);
    }
}
