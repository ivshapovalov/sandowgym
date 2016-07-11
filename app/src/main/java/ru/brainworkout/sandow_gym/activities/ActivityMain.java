package ru.brainworkout.sandow_gym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.common.Common;
import ru.brainworkout.sandow_gym.database.entities.Exercise;
import ru.brainworkout.sandow_gym.database.entities.User;
import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;

public class ActivityMain extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TRAINING_SHOW_PICTURE = "training_show_picture";
    public static final String APP_PREFERENCES_TRAINING_SHOW_EXPLANATION = "training_show_explanation";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON = "training_show_volume_default_button";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON = "training_show_volume_last_day_button";

    private static final int MAX_VERTICAL_BUTTON_COUNT = 8;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showButtons();

        defineCurrentUser();
    }

    private void showButtons() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        for (int i = 0; i <= MAX_VERTICAL_BUTTON_COUNT; i++) {
            int btID = getResources().getIdentifier("btMain" + String.valueOf(i), "id", getPackageName());
            Button btName = (Button) findViewById(btID);
            if (btName != null) {
                btName.setHeight(mHeight);
            }
        }

    }

    private void defineCurrentUser() {

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
                    Intent intent = new Intent(ActivityMain.this, ActivityUsersList.class);
                    startActivity(intent);
                }
            }

        }
        if (Common.mCurrentUser!=null) {
            this.setTitle(getTitle() + "(" + Common.mCurrentUser.getName() + ")");
        }

    }

    public void btUsers_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityUsersList.class);
        startActivity(intent);

    }

    public void btExercises_onClick(final View view) {

        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityExercisesList.class);
            startActivity(intent);
        }

    }

    public void btTrainings_onClick(final View view) {

        if (isUserDefined() & dbNotEmpty()) {
            Intent intent = new Intent(ActivityMain.this, ActivityTrainingsList.class);
            startActivity(intent);
        }

    }

    public void bt_NewTraining_onClick(final View view) {

        if (dbNotEmpty()) {
            Intent intent = new Intent(ActivityMain.this, ActivityTraining.class);
            intent.putExtra("IsNew", true);
            startActivity(intent);
        }

    }

    private boolean isUserDefined() {
        if (Common.mCurrentUser==null) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "Не выбран пользатель. Создайте пользователя и сделайте его активным!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean dbNotEmpty() {

        List<Exercise> list=new ArrayList<Exercise>();
        if (Common.mCurrentUser == null) {
            //list = DB.getAllActiveExercises();
        } else {
            list = DB.getAllActiveExercisesOfUser(Common.mCurrentUser.getID());
        }
        if (list.size() == 0) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "Отсутствуют активные упражнения. Заполните список упражнений!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;

        }
    }

    public void btExportImport_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityFileExportImport.class);
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
                            Toast toast = Toast.makeText(ActivityMain.this,
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