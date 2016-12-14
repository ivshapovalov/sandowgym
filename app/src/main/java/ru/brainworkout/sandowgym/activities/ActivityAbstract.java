package ru.brainworkout.sandowgym.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import ru.brainworkout.sandowgym.database.entities.User;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.brainworkout.sandowgym.common.Common.dbCurrentUser;

public abstract class ActivityAbstract extends AppCompatActivity {

    protected final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineCurrentUser();
    }

    public void defineCurrentUser() {

        if (dbCurrentUser == null) {
            List<User> userList = DB.getAllUsers();
            if (userList.size() == 1) {
                User currentUser = userList.get(0);
                dbCurrentUser = currentUser;
                currentUser.setIsCurrentUser(1);
                currentUser.dbSave(DB);
            } else {
                //ищем активного
                for (User user : userList
                        ) {
                    if (user.isCurrentUser() == 1) {
                        dbCurrentUser = user;
                        break;
                    }
                }
                isUserDefined();
            }
        }
    }

    public boolean isUserDefined() {
        if (dbCurrentUser == null) {
            Toast toast = Toast.makeText(this,
                    "No active user. Create user and make it active!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }


}