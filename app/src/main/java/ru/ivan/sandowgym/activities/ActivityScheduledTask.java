package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.database.entities.ScheduledTask;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.scheduler.Scheduler.cancelWork;

public class ActivityScheduledTask extends ActivityAbstract {

    private ScheduledTask mCurrentScheduledTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_task);

        Intent intent = getIntent();
        boolean mScheduledTaskIsNew = intent.getBooleanExtra("isNew", false);

        if (mScheduledTaskIsNew) {
            mCurrentScheduledTask = new ScheduledTask.Builder(database.getScheduledTaskMaxNumber() + 1).build();
        } else {
            int id = intent.getIntExtra("currentScheduledTaskId", 0);
            try {
                mCurrentScheduledTask = database.getScheduledTask(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showScheduledTaskOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void showScheduledTaskOnScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {
            tvID.setText(String.valueOf(mCurrentScheduledTask.getId()));
        }

        int mDatetimePlanID = getResources().getIdentifier("tvDatetimePlan", "id", getPackageName());
        TextView tvDatetimePlan = findViewById(mDatetimePlanID);
        if (tvDatetimePlan != null) {
            tvDatetimePlan.setText(Common.getDate(mCurrentScheduledTask.getDatetimePlan()));
        }

        int mDatetimeFactID = getResources().getIdentifier("tvDatetimeFact", "id", getPackageName());
        TextView tvDatetimeFact = findViewById(mDatetimeFactID);
        if (tvDatetimeFact != null) {
            tvDatetimeFact.setText(Common.getDate(mCurrentScheduledTask.getDatetimeFact()));
        }

        int mStatusID = getResources().getIdentifier("tvStatus", "id", getPackageName());
        TextView tvStatus = findViewById(mStatusID);
        if (tvStatus != null) {
            tvStatus.setText(mCurrentScheduledTask.getStatus().getName());
        }

        int mPerformedID = getResources().getIdentifier("tvPerformed", "id", getPackageName());
        TextView tvPerformed = findViewById(mPerformedID);
        if (tvPerformed != null) {
            tvPerformed.setText(mCurrentScheduledTask.isPerformed() ? "true" : "false");
        }
    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        mCurrentScheduledTask.save(database);
        closeActivity();

    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        closeActivity();

    }

    private void closeActivity() {
        Intent intent = new Intent(getApplicationContext(), ActivityScheduledTasksList.class);
        intent.putExtra("currentScheduledTaskId", mCurrentScheduledTask.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current scheduled task?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        btCancelTask_onClick(view);
                        mCurrentScheduledTask.delete(database);
                        Intent intent = new Intent(getApplicationContext(), ActivityScheduledTasksList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }

    public void btCancelTask_onClick(View view) {
        cancelWork(this, mCurrentScheduledTask.getId());
        try {
            mCurrentScheduledTask = database.getScheduledTask(mCurrentScheduledTask.getId());
        } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
            tableDoesNotContainElementException.printStackTrace();
        }
        showScheduledTaskOnScreen();
    }
}