package ru.brainworkout.sandow_gym;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHandler db = new DatabaseHandler(this);

        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addContact(new Exercise(1, "Описание 1"));
        db.addContact(new Exercise(2, "Описание 2"));
        db.addContact(new Exercise(3, "Описание 3"));


        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Exercise> exercises = db.getAllExercises();

        for (Exercise cn : exercises) {
            String log = "Id: " + cn.getID() + " ,Number: " + cn.getNumber() + " ,Name: " + cn.getName();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }
    }
}
