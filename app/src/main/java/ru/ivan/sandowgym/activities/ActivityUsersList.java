package ru.ivan.sandowgym.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import ru.ivan.sandowgym.database.entities.User;
import ru.ivan.sandowgym.database.manager.AndroidDatabaseManager;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.ivan.sandowgym.common.Common.HideEditorButton;
import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.paramsTextViewWithSpanInList;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityUsersList extends AppCompatActivity {

    private final int maxVerticalButtonCount = 17;
    private final int maxHorizontalButtonCount = 2;
    private final int numberOfViews = 40000;

    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    private SharedPreferences mSettings;
    private int rowsNumber;
    private Map<Integer, List<User>> pagedUsers = new HashMap<>();
    private int currentPage = 1;
    private int idIntentUser;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btUsersDBEditor", "id", getPackageName());
            Button btEditor = findViewById(mEditorID);
            HideEditorButton(btEditor);
        }

        getPreferencesFromFile();

        Intent intent = getIntent();
        idIntentUser = intent.getIntExtra("currentUserId", 0);
        updateUsers();

        TableRow mRow = findViewById(numberOfViews + idIntentUser);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableUsers", "id", getPackageName());
            ScrollView mScrollView = findViewById(mScrID);
            if (mScrollView != null) {
                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);
    }

    private void updateUsers() {
        pageUsers();
        showUsers();
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rowsNumber = 17;
        }
    }


    public void btUsersAdd_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityUser.class);
        intent.putExtra("isNew", true);
        startActivity(intent);

    }

    private void pageUsers() {
        List<User> users = new ArrayList<>();
        users = DB.getAllUsers();
        List<User> pageContent = new ArrayList<>();
        pagedUsers.clear();
        int pageNumber = 1;
        for (int i = 0; i < users.size(); i++) {
            if (idIntentUser != 0) {
                if (users.get(i).getId() == idIntentUser) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(users.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedUsers.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedUsers.put(pageNumber, pageContent);
        }
        if (pagedUsers.size() == 0) {
            currentPage = 0;
        }
    }

    private void showUsers() {

        Button pageNumber = findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage) + "/" + pagedUsers.size());
        }

        ScrollView sv = findViewById(R.id.svTableUsers);
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

        List<User> page = pagedUsers.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            User user = page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(numberOfViews + user.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowUser_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            mRow.setLayoutParams(params);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(user.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(1));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = String.valueOf(user.getName()) + ((user.isCurrentUser() == 1) ? " (CURRENT)" : "");
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(10));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);
    }

    private void rowUser_onClick(final TableRow view) {

        blink(view, this);

        int id = view.getId() % numberOfViews;

        Intent intent = new Intent(getApplicationContext(), ActivityUser.class);
        intent.putExtra("currentUserId", id);
        intent.putExtra("isNew", false);
        startActivity(intent);

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
        if (currentPage != pagedUsers.size()) {
            currentPage++;
        }
        showUsers();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showUsers();
    }
}

