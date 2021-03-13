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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.database.entities.Log;
import ru.ivan.sandowgym.database.manager.AndroidDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.hideEditorButton;
import static ru.ivan.sandowgym.common.Common.paramsTextViewWithSpanInList;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;

public class ActivityLogsList extends ActivityAbstract {

    private final int maxVerticalButtonCount = 17;
    private final int maxHorizontalButtonCount = 2;
    private final int numberOfViews = 40000;

    private SharedPreferences mSettings;
    private int rowsNumber;
    private Map<Integer, List<Log>> pagedLogs = new HashMap<>();
    private int currentPage = 1;
    private int idIntentLog;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs_list);

        if (!Constants.IS_DEBUG) {
            int mEditorID = getResources().getIdentifier("btLogsDBEditor", "id", getPackageName());
            Button btEditor = findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        getPreferencesFromFile();

        Intent intent = getIntent();
        idIntentLog = intent.getIntExtra("currentLogId", 0);
        updateLogs();

        TableRow mRow = findViewById(numberOfViews + idIntentLog);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableLogs", "id", getPackageName());
            ScrollView mScrollView = findViewById(mScrID);
            if (mScrollView != null) {
                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);
    }

    private void updateLogs() {
        pageLogs();
        showLogs();
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rowsNumber = 17;
        }
    }

    public void bt_LogsAdd_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityLog.class);
        intent.putExtra("isNew", true);
        startActivity(intent);

    }

    private void pageLogs() {
        List<Log> logs = database.getAllLogs();
        List<Log> pageContent = new ArrayList<>();
        pagedLogs.clear();
        int pageNumber = 1;
        for (int i = 0; i < logs.size(); i++) {
            if (idIntentLog != 0) {
                if (logs.get(i).getId() == idIntentLog) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(logs.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedLogs.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedLogs.put(pageNumber, pageContent);
        }
        if (pagedLogs.size() == 0) {
            currentPage = 0;
        }
    }

    private void showLogs() {

        Button pageNumber = findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage) + "/" + pagedLogs.size());
        }

        ScrollView sv = findViewById(R.id.svTableLogs);
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

        List<Log> page = pagedLogs.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            Log log = page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(numberOfViews + log.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowLog_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            mRow.setLayoutParams(params);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(log.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(2));
            mRow.addView(txt);

            txt = new TextView(this);
            String datetime = Common.getDate(log.getDatetime());
            txt.setText(datetime);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(4));
            mRow.addView(txt);

            txt = new TextView(this);
            String text = log.getText();
            txt.setText(text);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(16));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);
    }

    private void rowLog_onClick(final TableRow view) {

        blink(view, this);

        int id = view.getId() % numberOfViews;

        Intent intent = new Intent(getApplicationContext(), ActivityLog.class);
        intent.putExtra("currentLogId", id);
        intent.putExtra("isNew", false);
        startActivity(intent);

    }

    public void btDeleteAllLogs_onClick(final View view) {

        blink(view, this);

        new AlertDialog.Builder(this)
                .setMessage("Do you wish to delete all logs with content?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dbCurrentUser != null) {
                            database.deleteAllLogs();
                            updateLogs();
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

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btNextPage_onClick(View view) {
        blink(view, this);
        if (currentPage != pagedLogs.size()) {
            currentPage++;
        }
        showLogs();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showLogs();
    }
}

