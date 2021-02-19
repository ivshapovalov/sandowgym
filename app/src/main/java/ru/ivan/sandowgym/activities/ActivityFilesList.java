package ru.ivan.sandowgym.activities;

import android.app.Activity;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.processingInProgress;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityFilesList extends AppCompatActivity {

    private final int maxVerticalButtonCount = 17;
    private final int maxHorizontalButtonCount = 2;
    private final int numberOfViews = 50000;

    private SharedPreferences mSettings;
    private int rowsNumber;
    private String downloadType;
    List<String> files = new ArrayList<>();
    private Map<Integer, List<String>> pagedFiles = new HashMap<>();
    private int currentPage = 1;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_list);

        getPreferencesFromFile();

        Intent intent = getIntent();

        files = intent.getStringArrayListExtra("downloadFiles");
        downloadType = intent.getStringExtra("downloadType");
        updateFiles();

        setTitleOfActivity(this);
    }

    private void updateFiles() {
        pageFiles();
        showFiles();
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rowsNumber = 17;
        }
    }

    private void pageFiles() {
        List<String> pageContent = new ArrayList<>();
        pagedFiles.clear();
        int pageNumber = 1;
        for (int i = 0; i < files.size(); i++) {
            pageContent.add(files.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedFiles.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedFiles.put(pageNumber, pageContent);
        }
        if (pagedFiles.size() == 0) {
            currentPage = 0;
        }
    }

    private void showFiles() {

        Button pageNumber = findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage) + "/" + pagedFiles.size());
        }

        ScrollView sv = findViewById(R.id.svTableFiles);
        try {
            sv.removeAllViews();
        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / maxVerticalButtonCount;
        mWidth = displaymetrics.widthPixels / maxHorizontalButtonCount;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);
        layout.setShrinkAllColumns(true);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        params.weight = 100;
        params.leftMargin = 10;
        params.rightMargin = 10;

        List<String> page = pagedFiles.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            TableRow mRow = new TableRow(this);
            String fileName = page.get(num);
            mRow.setId(numberOfViews + num);
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowFileName_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            mRow.setLayoutParams(params);

            TextView txt = new TextView(this);
            txt.setText(fileName);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(params);
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);
    }

    private void rowFileName_onClick(final TableRow view) {

        blink(view, this);

        int id = view.getId() % numberOfViews;
        String file = pagedFiles.get(currentPage).get(id);

        Intent intent = new Intent(getApplicationContext(), ActivityFileExportImport.class);
        intent.putExtra("downloadFile", file);
        intent.putExtra("downloadType", downloadType);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    public void onBackPressed() {
        processingInProgress = false;
        Intent intent = new Intent(getApplicationContext(), ActivityFileExportImport.class);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void btClose_onClick(final View view) {
        processingInProgress = false;
        Intent intent = new Intent(getApplicationContext(), ActivityFileExportImport.class);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();

    }

    public void btNextPage_onClick(View view) {
        blink(view, this);
        if (currentPage != pagedFiles.size()) {
            currentPage++;
        }
        showFiles();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showFiles();
    }
}

