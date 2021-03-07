package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.database.entities.Log;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityLog extends ActivityAbstract {

    private Log mCurrentLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Intent intent = getIntent();
        boolean mLogIsNew = intent.getBooleanExtra("isNew", false);

        if (mLogIsNew) {
            mCurrentLog = new Log.Builder(database.getLogMaxNumber() + 1).build();
        } else {
            int id = intent.getIntExtra("currentLogId", 0);
            try {
                mCurrentLog = database.getLog(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showLogOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showLogOnScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {
            tvID.setText(String.valueOf(mCurrentLog.getId()));
        }

        int mDatetimeID = getResources().getIdentifier("tvDatetime", "id", getPackageName());
        TextView tvDatetime = findViewById(mDatetimeID);
        if (tvDatetime != null) {
            tvDatetime.setText(Common.getDate(mCurrentLog.getDatetime()));
        }

        int mTextID = getResources().getIdentifier("etText", "id", getPackageName());
        EditText etText = findViewById(mTextID);
        if (etText != null) {
            etText.setText(mCurrentLog.getText());
        }
    }

    private void getPropertiesFromScreen() {

        int mTextID = getResources().getIdentifier("etText", "id", getPackageName());
        EditText etText = findViewById(mTextID);
        if (etText != null) {
            mCurrentLog.setText(String.valueOf(etText.getText()));
        }
    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        getPropertiesFromScreen();
        mCurrentLog.save(database);
        closeActivity();

    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        closeActivity();

    }

    private void closeActivity() {
        Intent intent = new Intent(getApplicationContext(), ActivityLogsList.class);
        intent.putExtra("currentLogId", mCurrentLog.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current log?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCurrentLog.delete(database);
                        Intent intent = new Intent(getApplicationContext(), ActivityLogsList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }
}