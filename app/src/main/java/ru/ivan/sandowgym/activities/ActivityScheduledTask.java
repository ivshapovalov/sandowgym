package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.common.scheduler.Scheduler;
import ru.ivan.sandowgym.database.entities.ScheduledTask;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.scheduler.Scheduler.cancelWork;

public class ActivityScheduledTask extends ActivityAbstract {

    static final int TIME_DIALOG_ID = 999;

    private TimePicker mTimePicker;
    private TextView timeDisplay;

    private ScheduledTask mCurrentScheduledTask;

    private int mDatetimePlanHour = 0;
    private int mDatetimePlanMinutes = 0;
    private boolean mScheduledTaskIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_task);

        Intent intent = getIntent();
        mScheduledTaskIsNew = intent.getBooleanExtra("isNew", false);
        long currentDateInMillis = intent.getLongExtra("currentDateInMillis", 0);
        int id = intent.getIntExtra("currentScheduledTaskId", 0);

        if (mScheduledTaskIsNew) {
            if (id == 0) {
                id = database.getScheduledTaskMaxNumber() + 1;
            }
            if (currentDateInMillis == 0) {
                currentDateInMillis = Calendar.getInstance().getTimeInMillis() + 60 * 5 * 1000;
            }
            mCurrentScheduledTask = new ScheduledTask.Builder(id)
                    .setPerformed(false)
                    .addType(ScheduledTask.Type.MANUAL)
                    .addStatus(ScheduledTask.Status.ENQUEUED)
                    .addDatetimePlan(currentDateInMillis)
                    .build();
        } else {
            try {
                mCurrentScheduledTask = database.getScheduledTask(id);
                if (currentDateInMillis != 0) {
                    mCurrentScheduledTask.setDatetimePlan(currentDateInMillis);
                }
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mCurrentScheduledTask.getDatetimePlan());
        mDatetimePlanHour = calendar.get(Calendar.HOUR_OF_DAY);
        mDatetimePlanMinutes = calendar.get(Calendar.MINUTE);

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

        int mDatePlanID = getResources().getIdentifier("tvDatePlan", "id", getPackageName());
        TextView tvDatePlan = findViewById(mDatePlanID);
        if (tvDatePlan != null) {
            tvDatePlan.setText(Common.getDate(mCurrentScheduledTask.getDatetimePlan(), Constants.DATE_FORMAT_STRING));
            if (mCurrentScheduledTask.getType().equals(ScheduledTask.Type.MANUAL) &&
                    mCurrentScheduledTask.getStatus().equals(ScheduledTask.Status.ENQUEUED)) {
                tvDatePlan.setEnabled(true);
            } else {
                tvDatePlan.setEnabled(false);
            }
        }

        timeDisplay = findViewById(R.id.tvTimePlan);

        mTimePicker = findViewById(R.id.timePicker);
        mTimePicker.setHour(mDatetimePlanHour);
        mTimePicker.setMinute(mDatetimePlanMinutes);
        if (timeDisplay != null) {
            timeDisplay.setText(new StringBuilder().append(pad(mDatetimePlanHour))
                    .append(":").append(pad(mDatetimePlanMinutes)));
            if (mCurrentScheduledTask.getType().equals(ScheduledTask.Type.MANUAL) &&
                    mCurrentScheduledTask.getStatus().equals(ScheduledTask.Status.ENQUEUED)) {
                timeDisplay.setEnabled(true);
            } else {
                timeDisplay.setEnabled(false);
            }
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
        int mTypeID = getResources().getIdentifier("tvType", "id", getPackageName());
        TextView tvType = findViewById(mTypeID);
        if (tvType != null) {
            tvType.setText(mCurrentScheduledTask.getType().getName());
        }

        int mPerformedID = getResources().getIdentifier("tvPerformed", "id", getPackageName());
        TextView tvPerformed = findViewById(mPerformedID);
        if (tvPerformed != null) {
            tvPerformed.setText(mCurrentScheduledTask.isPerformed() ? "true" : "false");
        }
    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        ScheduledTask task = null;
        try {
            task = database.getScheduledTask(mCurrentScheduledTask.getId());
        } catch (TableDoesNotContainElementException e) {
        }
        if (!mCurrentScheduledTask.equals(task)) {
            if (mCurrentScheduledTask.getStatus().equals(ScheduledTask.Status.ENQUEUED)) {
                Scheduler.cancelWork(this, mCurrentScheduledTask.getId());
                Scheduler.scheduleNewManualBackupTask(this, mCurrentScheduledTask);
                mCurrentScheduledTask.save(database);
            } else {
                Common.displayMessage(this, "Task '" + mCurrentScheduledTask.getId() + "' is " + mCurrentScheduledTask.getStatus() + " already", true);
            }
        }
        closeActivity();
    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to close current scheduled task?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        closeActivity();
                    }
                }).setNegativeButton("No", null).show();
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
                        btInterruptTask_onClick(view);
                        mCurrentScheduledTask.delete(database);
                        Intent intent = new Intent(getApplicationContext(), ActivityScheduledTasksList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }

    public void btInterruptTask_onClick(View view) {
        if (!mCurrentScheduledTask.getStatus().equals(ScheduledTask.Status.ENQUEUED)) {
            Common.displayMessage(this, "Task '" + mCurrentScheduledTask.getId() + "' is " + mCurrentScheduledTask.getStatus() + " already", true);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to interrupt current scheduled task?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        cancelWork(this, mCurrentScheduledTask.getId());
                        try {
                            mCurrentScheduledTask = database.getScheduledTask(mCurrentScheduledTask.getId());
                            showScheduledTaskOnScreen();
                            Common.displayMessage(this, "Task '" + mCurrentScheduledTask.getId() + "' interrupted", true);
                        } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                            tableDoesNotContainElementException.printStackTrace();
                            Common.displayMessage(this, "Task '" + mCurrentScheduledTask.getId() + "' does not exist", true);
                        }
                    }).setNegativeButton("No", null).show();
        }
    }

    public void tvDatePlan_onClick(View view) {
        blink(view, this);
        ScheduledTask task = mCurrentScheduledTask;
        try {
            task = database.getScheduledTask(mCurrentScheduledTask.getId());
        } catch (TableDoesNotContainElementException e) {
        }
        if (task.getStatus().equals(ScheduledTask.Status.RUNNING)) {
            displayMessage(this, "Failed. Task is already running!", true);
        } else {
            Intent intent = new Intent(ActivityScheduledTask.this, ActivityCalendarView.class);
            intent.putExtra("isNew", mScheduledTaskIsNew);
            intent.putExtra("currentActivity", getClass().getName());
            intent.putExtra("currentScheduledTaskId", mCurrentScheduledTask.getId());
            intent.putExtra("currentDateInMillis", mCurrentScheduledTask.getDatetimePlan());
            intent.putExtra("isTimeRemains", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void tvTimePlan_onClick(View view) {
        ScheduledTask task = mCurrentScheduledTask;
        try {
            task = database.getScheduledTask(mCurrentScheduledTask.getId());
        } catch (TableDoesNotContainElementException e) {
        }
        if (task.getStatus().equals(ScheduledTask.Status.RUNNING)) {
            displayMessage(this, "Failed. Task is already running!", true);
        } else {
            showDialog(TIME_DIALOG_ID);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mCurrentScheduledTask.getDatetimePlan());

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        timePickerListener, hour, minutes, DateFormat.is24HourFormat(this));
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            (view, selectedHour, selectedMinute) -> {
                mDatetimePlanHour = selectedHour;
                mDatetimePlanMinutes = selectedMinute;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mCurrentScheduledTask.getDatetimePlan());
                calendar.set(Calendar.HOUR_OF_DAY, mDatetimePlanHour);
                calendar.set(Calendar.MINUTE, mDatetimePlanMinutes);
                mCurrentScheduledTask.setDatetimePlan(calendar.getTimeInMillis());

                timeDisplay.setText(new StringBuilder().append(pad(mDatetimePlanHour))
                        .append(":").append(pad(mDatetimePlanMinutes)));

                mTimePicker.setHour(mDatetimePlanHour);
                mTimePicker.setMinute(mDatetimePlanMinutes);
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }
}