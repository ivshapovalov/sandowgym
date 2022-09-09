package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.common.scheduler.Scheduler;
import ru.ivan.sandowgym.database.entities.ScheduledTask;
import ru.ivan.sandowgym.database.manager.AndroidDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.hideEditorButton;
import static ru.ivan.sandowgym.common.Common.paramsTextViewWithSpanInList;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;
import static ru.ivan.sandowgym.common.scheduler.Scheduler.cancelWork;

public class ActivityScheduledTasksList extends ActivityAbstract {

    private final int maxVerticalButtonCount = 17;
    private final int maxHorizontalButtonCount = 2;
    private final int numberOfViews = 40000;

    private SharedPreferences mSettings;
    private int rowsNumber;
    private Map<Integer, List<ScheduledTask>> pagedScheduledTasks = new HashMap<>();
    private int currentPage = 1;
    private int idIntentScheduledTask;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_tasks_list);

        if (!Constants.IS_DEBUG) {
            int mEditorID = getResources().getIdentifier("btScheduledTasksDBEditor", "id", getPackageName());
            Button btEditor = findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        getPreferencesFromFile();

        Intent intent = getIntent();
        idIntentScheduledTask = intent.getIntExtra("currentScheduledTaskId", 0);
        updateScheduledTasks();

        TableRow mRow = findViewById(numberOfViews + idIntentScheduledTask);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableScheduledTasks", "id", getPackageName());
            ScrollView mScrollView = findViewById(mScrID);
            if (mScrollView != null) {
                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
        setTitleOfActivity(this);
    }

    private void updateScheduledTasks() {
        pageScheduledTasks();
        showScheduledTasks();
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rowsNumber = 17;
        }
    }

    private void pageScheduledTasks() {
        List<ScheduledTask> tasks = database.getAllScheduledTasks();
        List<ScheduledTask> pageContent = new ArrayList<>();
        pagedScheduledTasks.clear();
        int pageNumber = 1;
        for (int i = 0; i < tasks.size(); i++) {
            if (idIntentScheduledTask != 0) {
                if (tasks.get(i).getId() == idIntentScheduledTask) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(tasks.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedScheduledTasks.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedScheduledTasks.put(pageNumber, pageContent);
        }
        if (pagedScheduledTasks.size() == 0) {
            currentPage = 0;
        }
    }

    private void showScheduledTasks() {

        Button pageNumber = findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(currentPage + "/" + pagedScheduledTasks.size());
        }

        ScrollView sv = findViewById(R.id.svTableScheduledTasks);
        try {
            sv.removeAllViews();
        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / maxVerticalButtonCount;
        mWidth = displaymetrics.widthPixels / maxHorizontalButtonCount;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 3 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);
        layout.setShrinkAllColumns(true);

        List<ScheduledTask> page = pagedScheduledTasks.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            ScheduledTask task = page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(numberOfViews + task.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowScheduledTask_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            mRow.setLayoutParams(params);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(task.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(2));
            mRow.addView(txt);

            txt = new TextView(this);
            String datetimePlan = Common.getDate(task.getDatetimePlan());
            txt.setText(datetimePlan);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(4));
            mRow.addView(txt);

            txt = new TextView(this);
            String datetimeFact = Common.getDate(task.getDatetimeFact());
            txt.setText(datetimeFact);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(4));
            mRow.addView(txt);

            txt = new TextView(this);
            String status = task.getStatus().getName();
            txt.setText(status);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(4));
            mRow.addView(txt);

            txt = new TextView(this);
            String type = task.getType().getName();
            txt.setText(type);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(4));
            mRow.addView(txt);

            txt = new TextView(this);
            String performed = task.isPerformed() ? "true" : "false";
            txt.setText(performed);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(3));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);
    }

    private void rowScheduledTask_onClick(final TableRow view) {

        blink(view, this);

        int id = view.getId() % numberOfViews;

        Intent intent = new Intent(getApplicationContext(), ActivityScheduledTask.class);
        intent.putExtra("currentScheduledTaskId", id);
        intent.putExtra("isNew", false);
        startActivity(intent);

    }

    public void btDeleteAllScheduledTasks_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to delete all scheduled tasks?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dbCurrentUser != null) {
                            List<ScheduledTask> allScheduledTasks = database.getAllScheduledTasks();
                            allScheduledTasks.forEach(handlingTask -> {
                                cancelWork(context, handlingTask.getId());
                            });
                            database.deleteAllScheduledTasks();
                            updateScheduledTasks();
                        }
                    }
                }).setNegativeButton("No", null).show();

    }

    public void bt_Edit_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(intent);
    }

    public void buttonHome_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btNextPage_onClick(View view) {
        blink(view, this);
        if (currentPage != pagedScheduledTasks.size()) {
            currentPage++;
        }
        showScheduledTasks();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showScheduledTasks();
    }
    List<String> handledTasks = new ArrayList<>();

    public void bt_TaskAdd_onClick(View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityScheduledTask.class);
        intent.putExtra("isNew", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btShowWorks_onClick(View view) {
        if (Constants.mOptionBackupScheduleEnabled) {
            Map<String, List<String>> params = new HashMap<>();
            params.put("status", new ArrayList(Arrays.asList(ScheduledTask.Status.ENQUEUED.getName())));
            params.put("type", new ArrayList(Arrays.asList(ScheduledTask.Type.DAILY.getName(),ScheduledTask.Type.MANUAL.getName())));
            params.put("time", new ArrayList(Arrays.asList("after")));
            List<String> backups = Scheduler.getWorks(this,params);
            if (backups.size() > 0) {
                String srt = "SCHEDULED BACKUPS: " + System.getProperty("line.separator") +
                        backups.stream().map(Object::toString)
                                .collect(Collectors.joining(System.getProperty("line.separator")));
                displayMessage(this, srt, true);
            }
        }
    }
}

