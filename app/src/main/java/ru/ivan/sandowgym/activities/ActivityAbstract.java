package ru.ivan.sandowgym.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ru.ivan.sandowgym.database.entities.User;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;

public abstract class ActivityAbstract extends AppCompatActivity {

    protected final SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineCurrentUser();
    }

    public void defineCurrentUser() {

        if (dbCurrentUser == null) {
            List<User> userList = database.getAllUsers();
            if (userList.size() == 1) {
                User currentUser = userList.get(0);
                dbCurrentUser = currentUser;
                currentUser.setIsCurrentUser(1);
                currentUser.save(database);
            } else {
                //ищем активного
                for (User user : userList) {
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