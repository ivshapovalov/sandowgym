package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DatabaseHandler db = new DatabaseHandler(this);
//
//        /**
//         * CRUD Operations
//         * */
//        // Inserting Contacts
//        Log.d("Insert: ", "Inserting ..");
//        db.addContact(new Exercise(1, "Описание 1"));
//        db.addContact(new Exercise(2, "Описание 2"));
//        db.addContact(new Exercise(3, "Описание 3"));
//
//
//        // Reading all contacts
//        Log.d("Reading: ", "Reading all exercises..");
//        List<Exercise> exercises = db.getAllExercises();
//
//        for (Exercise cn : exercises) {
//            String log = "Id: " + cn.getID() + " ,Number: " + cn.getNumber() + " ,Name: " + cn.getName();
//            // Writing Contacts to log
//            Log.d("Name: ", log);
//        }
    }

    public void bt_Exercises_onClick(View view) {

        Intent intent = new Intent(MainActivity.this, ExercisesListActivity.class);
        startActivity(intent);

    }

    public void btTrainings_onClick(View view) {
        Intent intent = new Intent(MainActivity.this, TrainingsListActivity.class);
        startActivity(intent);
    }

    public void bt_NewTraining_onClick(View view) {
        Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);
    }

    public void btSaveToCSV_onClick(View view) {

        Intent intent = new Intent(MainActivity.this, ExportToFileActivity.class);
        startActivity(intent);





    }
}
