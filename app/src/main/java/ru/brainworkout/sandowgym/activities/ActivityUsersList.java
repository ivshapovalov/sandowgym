package ru.brainworkout.sandowgym.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
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

import ru.brainworkout.sandowgym.R;
import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.entities.Exercise;
import ru.brainworkout.sandowgym.database.entities.User;
import ru.brainworkout.sandowgym.database.manager.AndroidDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;

public class ActivityUsersList extends AppCompatActivity {


    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    private SharedPreferences mSettings;
    private int rows_number = 17;
    Map<Integer, List<User>> pagingUsers = new HashMap<>();
    private int currentPage = 1;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btUsersDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }

        getPreferencesFromFile();
        pageUsers();
        showUsers();

        setTitleOfActivity(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        showUsers();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableExercises", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rows_number = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rows_number = 17;
        }
    }


    public void btUsersAdd_onClick(final View view) {

        blink(view,this);
        Intent intent = new Intent(getApplicationContext(), ActivityUser.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void pageUsers() {
        List<User> users = new ArrayList<User>();
        users = DB.getAllUsers();
        List<User> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < users.size(); i++) {
            pageContent.add(users.get(i));
            if (pageContent.size() == rows_number) {
                pagingUsers.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size()!=0) {
            pagingUsers.put(pageNumber, pageContent);
        }
    }

    private void showUsers() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage));
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableExercises);
        try {

            sv.removeAllViews();

        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        mWidth = displaymetrics.widthPixels/ MAX_HORIZONTAL_BUTTON_COUNT;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);

        List<User> page = pagingUsers.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            TableRow mRow = new TableRow(this);
            User user=page.get(num);
            mRow.setId(NUMBER_OF_VIEWS + user.getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowUser_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(user.getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String name=String.valueOf(user.getName())+((user.isCurrentUser()==1)?" (CURRENT)":"");
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);
    }

    private void rowUser_onClick(final TableRow view) {

        blink(view,this);

        int id = view.getId() % NUMBER_OF_VIEWS;

        Intent intent = new Intent(getApplicationContext(), ActivityUser.class);
        intent.putExtra("id", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {
        blink(view,this);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {

        blink(view,this);
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
        if (currentPage != pagingUsers.size()) {
            currentPage++;
        }
        showUsers();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage != 1) {
            currentPage--;
        }
        showUsers();
    }
}

