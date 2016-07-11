package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.common.Common;
import ru.brainworkout.sandow_gym.database.entities.User;
import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class ActivityUser extends AppCompatActivity {

    private User mCurrentUser;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        boolean mUserIsNew = intent.getBooleanExtra("IsNew", false);

        if (mUserIsNew) {
            mCurrentUser = new User.UserBuilder(DB.getUserMaxNumber() + 1).build();
        } else {
            int id = intent.getIntExtra("id", 0);
            try {
                mCurrentUser = DB.getUser(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showUserOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (Common.mCurrentUser!=null) {
            this.setTitle(getTitle() + "(" + Common.mCurrentUser.getName() + ")");
        }
    }

    private void showUserOnScreen() {

        int isCurrentID = getResources().getIdentifier("cb_IsCurrent", "id", getPackageName());
        CheckBox cbIsCurrent = (CheckBox) findViewById(isCurrentID);
        if (cbIsCurrent != null) {
            if (mCurrentUser.getIsCurrentUser() != 0) {
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


        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentUser.getID()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("et_Name", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(mCurrentUser.getName());
        }


    }

    public void btClose_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
        intent.putExtra("id", mCurrentUser.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            mCurrentUser.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }

        //Имя
        int mNameID = getResources().getIdentifier("et_Name", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {

            mCurrentUser.setName(String.valueOf(etName.getText()));

        }

    }

    public void btSave_onClick(final View view) {

        Common.blink(view);
        getPropertiesFromScreen();

        mCurrentUser.dbSave(DB);

        if (mCurrentUser.getIsCurrentUser() == 1) {
            Common.mCurrentUser=mCurrentUser;
            List<User> userList = DB.getAllUsers();

            for (User user : userList) {

                if (user.getID()!=mCurrentUser.getID()) {
                    user.setIsCurrentUser(0);
                    user.dbSave(DB);
                }

            }
        }

        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
        intent.putExtra("id", mCurrentUser.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDelete_onClick(final View view) {

        Common.blink(view);
        //DB.deleteExercise(mCurrentExercise);
        mCurrentUser.dbDelete(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}