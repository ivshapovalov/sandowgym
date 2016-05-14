package ru.brainworkout.sandow_gym;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ivan on 14.05.2016.
 */
public class ExerciseActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

    }
}