package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import ru.brainworkout.sandow_gym.activities.ExercisesListActivity;
import ru.brainworkout.sandow_gym.activities.FileExportImportActivity;
import ru.brainworkout.sandow_gym.activities.TrainingActivity;
import ru.brainworkout.sandow_gym.activities.TrainingsListActivity;
import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.database.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TRAINING_SHOW_PICTURE = "training_show_picture";
    public static final String APP_PREFERENCES_TRAINING_SHOW_EXPLANATION = "training_show_explanation";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON = "training_show_volume_default_button";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON = "training_show_volume_last_day_button";

    private SharedPreferences mSettings;
    private DatabaseManager db;
    private int mHeight;
    private int mWidth;
    private int mTextSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseManager(this);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        //допустим 15 строк тренировок
        mHeight = displaymetrics.heightPixels / 8;
        mWidth = displaymetrics.widthPixels/1;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 4 / getApplicationContext().getResources().getDisplayMetrics().density);
        for (int i = 0; i <=5 ; i++) {
            int btID = getResources().getIdentifier("btMain"+String.valueOf(i), "id", getPackageName());
            Button btName = (Button) findViewById(btID);
            if (btName != null) {
                btName.setHeight(mHeight);
                btName.setTextSize(mTextSize);
            }
        }

    }

    public void bt_Exercises_onClick(View view) {

        Intent intent = new Intent(MainActivity.this, ExercisesListActivity.class);
        startActivity(intent);

    }

    public void btTrainings_onClick(View view) {

        List<Exercise> list = db.getAllActiveExercises();
        if (list.size() == 0) {
            Toast toast = Toast.makeText(MainActivity.this,
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
            Toast toast = Toast.makeText(MainActivity.this,
                    "Отсутствуют активные упражнения. Заполните список упражнений!", Toast.LENGTH_SHORT);
            toast.show();
        } else {

            Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
            intent.putExtra("IsNew", true);
            startActivity(intent);
        }
    }

    public void btExportImport_onClick(View view) {

        Intent intent = new Intent(MainActivity.this, FileExportImportActivity.class);
        startActivity(intent);


    }

    public void btClearBD_onClick(View view) {

        try {
            SQLiteDatabase dbSQL = db.getWritableDatabase();
            db.onUpgrade(dbSQL, 1, 2);
        } catch (Exception e) {
            Toast toast = Toast.makeText(MainActivity.this,
                    "Невозможно подключиться к базе данных!", Toast.LENGTH_SHORT);
            toast.show();
        }



    }
}
