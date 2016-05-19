package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Environment;
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

    Training mCurrentTraining;
    TrainingContent mCurrentTrainingContent;
    Exercise mCurrentExercise;

    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_to_file);
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

    public void tvDayFrom_onClick(View view) {

        Intent intent = new Intent(ExportToFileActivity.this, CalendarViewActivity.class);
        intent.putExtra("BEGIN", true);
        intent.putExtra("CurrentActivity", "ExportToFileActivity");

        int mDayID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDay = (TextView) findViewById(mDayID);
        if (tvDay != null) {
            intent.putExtra("Date",String.valueOf(tvDay.getText()));
        }
        startActivity(intent);
    }
    public void tvDayTo_onClick(View view) {

        Intent intent = new Intent(ExportToFileActivity.this, CalendarViewActivity.class);
        intent.putExtra("BEGIN", false);
        intent.putExtra("CurrentActivity", "ExportToFileActivity");
        startActivity(intent);
    }
}
