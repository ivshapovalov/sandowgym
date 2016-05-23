package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSettings;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TRAINING_SHOW_PICTURE = "training_show_picture";
    public static final String APP_PREFERENCES_TRAINING_SHOW_EXPLANATION = "training_show_explanation";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON = "training_show_volume_default_button";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON = "training_show_volume_last_day_button";

    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void bt_Exercises_onClick(View view) {

        Intent intent = new Intent(MainActivity.this, ExercisesListActivity.class);
        startActivity(intent);

        db = new DatabaseManager(this);


    }

    public void btTrainings_onClick(View view) {

        List<Exercise> list = db.getAllActiveExercises();
        if (list.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Отсутствуют активные упражнения. Заполните список упражнений!", Toast.LENGTH_SHORT);
            toast.show();
        } else {

            Intent intent = new Intent(MainActivity.this, TrainingsListActivity.class);
            startActivity(intent);
        }
    }

    public void bt_NewTraining_onClick(View view) {

        List<Exercise> list = db.getAllActiveExercises();
        if (list.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Отсутствуют активные упражнения. Заполните список упражнений!", Toast.LENGTH_SHORT);
            toast.show();
        } else {

            Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
            intent.putExtra("IsNew", true);
            startActivity(intent);
        }
    }

    public void btSaveToCSV_onClick(View view) {

        Intent intent = new Intent(MainActivity.this, ExportToFileActivity.class);
        startActivity(intent);


    }

    public void btClearBD_onClick(View view) {

        SQLiteDatabase dbSQL = db.getWritableDatabase();

        db.onUpgrade(dbSQL, 1, 2);

    }
}
