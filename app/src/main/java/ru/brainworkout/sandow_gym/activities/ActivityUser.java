package ru.brainworkout.sandow_gym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import ru.brainworkout.sandow_gym.database.entities.Exercise;
import ru.brainworkout.sandow_gym.database.entities.Training;
import ru.brainworkout.sandow_gym.database.entities.TrainingContent;
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

        if (Common.dbCurrentUser !=null) {
            this.setTitle(getTitle() + "(" + Common.dbCurrentUser.getName() + ")");
        }
    }

    private void showUserOnScreen() {

        int isCurrentID = getResources().getIdentifier("cb_IsCurrent", "id", getPackageName());
        CheckBox cbIsCurrent = (CheckBox) findViewById(isCurrentID);
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

        setDBCurrentUser();

        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
        intent.putExtra("id", mCurrentUser.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void setDBCurrentUser() {

        if (mCurrentUser.isCurrentUser() == 1) {
            Common.dbCurrentUser =mCurrentUser;
            List<User> userList = DB.getAllUsers();

            for (User user : userList) {

                if (user.getID()!=mCurrentUser.getID()) {
                    user.setIsCurrentUser(0);
                    user.dbSave(DB);
                }

            }
        } else {
            if (Common.dbCurrentUser.equals(mCurrentUser)) {
                Common.dbCurrentUser=null;
            }

        }

    }

    public void btDelete_onClick(final View view) {

        Common.blink(view);


        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущего пользователя, его тренировки и упражнения?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<Exercise> exercisesOfUser=DB.getAllExercisesOfUser(mCurrentUser.getID());
                        List<Training> trainingsOfUser=DB.getAllTrainingsOfUser(mCurrentUser.getID());
                        for (Training currentTraining:trainingsOfUser
                             ) {
                            DB.deleteTrainingContentOfTraining(currentTraining.getID());
                            currentTraining.dbDelete(DB);

                        }
                        for (Exercise currentExercise:exercisesOfUser
                                ) {
                            currentExercise.dbDelete(DB);

                        }

                        mCurrentUser.dbDelete(DB);

                        if (mCurrentUser.equals(Common.dbCurrentUser)) {
                            List<User> userList = DB.getAllUsers();
                            if (userList.size() == 1) {
                                User currentUser=userList.get(0);
                                Common.dbCurrentUser = currentUser;
                                currentUser.setIsCurrentUser(1);
                                currentUser.dbSave(DB);
                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }
}