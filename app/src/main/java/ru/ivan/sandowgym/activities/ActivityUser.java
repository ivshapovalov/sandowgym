package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.database.entities.User;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.dbCurrentUser;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityUser extends AppCompatActivity {

    private final SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(this);

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        boolean mUserIsNew = intent.getBooleanExtra("isNew", false);

        if (mUserIsNew) {
            mCurrentUser = new User.Builder(database.getUserMaxNumber() + 1).build();
        } else {
            int id = intent.getIntExtra("currentUserId", 0);
            try {
                mCurrentUser = database.getUser(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showUserOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showUserOnScreen() {

        int isCurrentID = getResources().getIdentifier("cbIsCurrent", "id", getPackageName());
        CheckBox cbIsCurrent = findViewById(isCurrentID);
        if (cbIsCurrent != null) {
            if (mCurrentUser.isCurrentUser() != 0) {
                cbIsCurrent.setChecked(true);
            } else {
                cbIsCurrent.setChecked(false);
            }
        }

        cbIsCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (mCurrentUser != null) {
                    if (isChecked) {
                        mCurrentUser.setIsCurrentUser(1);
                    } else {
                        mCurrentUser.setIsCurrentUser(0);
                    }

                }
            }
        });

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {
            tvID.setText(String.valueOf(mCurrentUser.getId()));
        }

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = findViewById(mNameID);
        if (etName != null) {
            etName.setText(mCurrentUser.getName());
        }
    }

    private void getPropertiesFromScreen() {

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = findViewById(mNameID);
        if (etName != null) {
            mCurrentUser.setName(String.valueOf(etName.getText()));
        }
    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        getPropertiesFromScreen();
        mCurrentUser.save(database);
        setDBCurrentUser();
        closeActivity();

    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        closeActivity();

    }

    private void closeActivity() {
        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
        intent.putExtra("currentUserId", mCurrentUser.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setDBCurrentUser() {

        if (mCurrentUser.isCurrentUser() == 1) {
            dbCurrentUser = mCurrentUser;
            List<User> userList = database.getAllUsers();

            for (User user : userList) {

                if (user.getId() != mCurrentUser.getId()) {
                    user.setIsCurrentUser(0);
                    user.save(database);
                }

            }
        } else {
            if (dbCurrentUser != null && dbCurrentUser.equals(mCurrentUser)) {
                dbCurrentUser = null;
            }
        }
    }

    public void btDelete_onClick(final View view) {

        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current user, his trainings and other?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCurrentUser.delete(database);
                        if (mCurrentUser.equals(dbCurrentUser)) {
                            List<User> userList = database.getAllUsers();
                            if (userList.size() == 1) {
                                User currentUser = userList.get(0);
                                dbCurrentUser = currentUser;
                                currentUser.setIsCurrentUser(1);
                                currentUser.save(database);
                            } else {
                                dbCurrentUser = null;
                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }
}