package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.MainActivity;
import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.commons.Common;
import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.commons.User;
import ru.brainworkout.sandow_gym.database.AndroidDatabaseManager;
import ru.brainworkout.sandow_gym.database.DatabaseManager;

public class UsersListActivity extends AppCompatActivity {


    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private final DatabaseManager DB = new DatabaseManager(this);

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        showUsers();

        if (Common.mCurrentUser!=null) {
            this.setTitle(getTitle() + "(" + Common.mCurrentUser.getName() + ")");
        }

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


    public void btUsersAdd_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void showUsers() {

        List<User> users= DB.getAllUsers();

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

        for (int numUser = 0; numUser < users.size(); numUser++) {
            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + users.get(numUser).getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowUser_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(users.get(numUser).getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String name=String.valueOf(users.get(numUser).getName())+((users.get(numUser).getIsCurrentUser()==1)?" (CURRENT)":"");
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

    private void rowUser_onClick(final TableRow v) {

        Common.blink(v);

        int id = v.getId() % NUMBER_OF_VIEWS;

        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {

        Common.blink(view);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}

