package ru.brainworkout.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ru.brainworkout.sandowgym.R;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.brainworkout.sandowgym.common.Common.dbCurrentUser;
import static ru.brainworkout.sandowgym.common.Common.setTitleOfActivity;

public class ActivityTools extends ActivityAbstract {

    private static final int MAX_VERTICAL_BUTTON_COUNT = 10;
    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        showElementsOnScreen();
        setTitleOfActivity(this);
    }

    private void showElementsOnScreen() {

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

    public void btExportImport_onClick(final View view) {
        Intent intent = new Intent(ActivityTools.this, ActivityFileExportImport.class);
        startActivity(intent);
    }

    public void btAbout_onClick(final View view) {

        Intent intent = new Intent(ActivityTools.this, ActivityAbout.class);
        startActivity(intent);
    }

    public void btOptions_onClick(View view) {

        Intent intent = new Intent(ActivityTools.this, ActivityOptions.class);
        startActivity(intent);

    }

    public void btClearBD_onClick(final View view) {

        new AlertDialog.Builder(this)
                .setMessage("Do you wish to clear database?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            SQLiteDatabase dbSQL = DB.getWritableDatabase();
                            DB.clearDB(dbSQL);
                            dbCurrentUser = null;

                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "Database cleared!", Toast.LENGTH_SHORT);
                            toast.show();
                            setTitleOfActivity(ActivityTools.this);

                        } catch (Exception e) {
                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "Unable to connect database!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }).setNegativeButton("No", null).show();
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
