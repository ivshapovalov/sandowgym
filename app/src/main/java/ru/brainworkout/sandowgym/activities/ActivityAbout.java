package ru.brainworkout.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ru.brainworkout.sandowgym.R;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;

import static ru.brainworkout.sandowgym.common.Common.dbCurrentUser;
import static ru.brainworkout.sandowgym.common.Common.setTitleOfActivity;

public class ActivityAbout extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitleOfActivity(this);
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


}
