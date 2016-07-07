package ru.brainworkout.sandow_gym;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.activities.ExercisesListActivity;
import ru.brainworkout.sandow_gym.activities.FileExportImportActivity;
import ru.brainworkout.sandow_gym.activities.TrainingActivity;
import ru.brainworkout.sandow_gym.activities.TrainingsListActivity;
import ru.brainworkout.sandow_gym.activities.UsersListActivity;
import ru.brainworkout.sandow_gym.commons.Common;
import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.commons.User;
import ru.brainworkout.sandow_gym.database.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TRAINING_SHOW_PICTURE = "training_show_picture";
    public static final String APP_PREFERENCES_TRAINING_SHOW_EXPLANATION = "training_show_explanation";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON = "training_show_volume_default_button";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON = "training_show_volume_last_day_button";

    private static final int MAX_VERTICAL_BUTTON_COUNT = 8;
    private final DatabaseManager DB = new DatabaseManager(this);
    private boolean isFirstBoot = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        for (int i = 0; i <= 5; i++) {
            int btID = getResources().getIdentifier("btMain" + String.valueOf(i), "id", getPackageName());
            Button btName = (Button) findViewById(btID);
            if (btName != null) {
                btName.setHeight(mHeight);
            }
        }

        if (Common.mCurrentUser == null) {
            List<User> userList = DB.getAllUsers();
            if (userList.size() == 1) {
                Common.mCurrentUser = userList.get(0);
            } else {
                //ищем активного
                for (User user:userList
                     ) {
                    if (user.getIsCurrentUser()==1) {
                        Common.mCurrentUser=user;
                        break;
                    }
                }
                if (Common.mCurrentUser == null) {
                    Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                    startActivity(intent);
                }
            }

        }
        if (Common.mCurrentUser!=null) {
            this.setTitle(getTitle() + "(" + Common.mCurrentUser.getName() + ")");
        }

    }

    public void btUsers_onClick(final View view) {

        Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
        startActivity(intent);

    }

    public void btExercises_onClick(final View view) {

        if (Common.mCurrentUser==null) {
            Toast toast = Toast.makeText(MainActivity.this,
                    "Не выбран пользатель. Создайте пользователя и сделайте его активным!", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Intent intent = new Intent(MainActivity.this, ExercisesListActivity.class);
            startActivity(intent);
        }

    }

    public void btTrainings_onClick(final View view) {

        if (!dbIsEmpty()) {
            Intent intent = new Intent(MainActivity.this, TrainingsListActivity.class);
            startActivity(intent);
        }

    }

    public void bt_NewTraining_onClick(final View view) {

        if (!dbIsEmpty()) {
            Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
            intent.putExtra("IsNew", true);
            startActivity(intent);
        }

    }

    private boolean dbIsEmpty() {
        List<Exercise> list=new ArrayList<Exercise>();
        if (Common.mCurrentUser == null) {
            //list = DB.getAllActiveExercises();
        } else {
            list = DB.getAllActiveExercisesOfUser(Common.mCurrentUser.getID());
        }
        if (list.size() == 0) {
            Toast toast = Toast.makeText(MainActivity.this,
                    "Отсутствуют активные упражнения. Заполните список упражнений!", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else {
            return false;

        }
    }

    public void btExportImport_onClick(final View view) {

        Intent intent = new Intent(MainActivity.this, FileExportImportActivity.class);
        startActivity(intent);

    }

    public void btClearBD_onClick(final View view) {

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите очистить базу данных?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            SQLiteDatabase dbSQL = DB.getWritableDatabase();
                            DB.onUpgrade(dbSQL, 1, 2);

                            if (Common.mCurrentUser!=null) {
                                setTitle(getTitle().toString().substring(0,getTitle().toString().indexOf("(")));
                            }
                            Common.mCurrentUser=null;
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(MainActivity.this,
                                    "Невозможно подключиться к базе данных!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }).setNegativeButton("Нет", null).show();

    }

    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите покинуть программу?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).setNegativeButton("Нет", null).show();

    }
}
