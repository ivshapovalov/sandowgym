package ru.brainworkout.sandow_gym.database.entities;


import ru.brainworkout.sandow_gym.database.interfaces.DeleteFromDb;
import ru.brainworkout.sandow_gym.database.interfaces.SaveToDB;
import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class User extends AbstractEntity implements SaveToDB,DeleteFromDb {
    private int _id;
    private String _name;
    private int _isCurrentUser;

    private User(UserBuilder builder) {

        this._id = builder._id;
        this._name = builder._name;
        this._isCurrentUser = builder._isCurrentUser;

    }

    public int getID() {
        return _id;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public int getIsCurrentUser() {
        return _isCurrentUser;
    }

    public void setIsCurrentUser(int _isCurrentUser) {
        this._isCurrentUser = _isCurrentUser;
    }

    @Override
    public void dbSave(DatabaseManager db) {
        User user = (User) this;
        try {
            db.getUser(this.getID());
            db.updateUser(user);

        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addUser(user);

        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {
        try {
            db.getUser(this.getID());
            db.deleteUser((User) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
        }
    }


    public static class UserBuilder extends AbstractEntity {

        private String _name;
        private int _isCurrentUser;

        public UserBuilder(int id) {
            this._id = id;
        }

        public UserBuilder addName(String name) {
            this._name = name;
            return this;
        }

        public UserBuilder addIsCurrentUser(int isCurrentUser) {
            this._isCurrentUser = isCurrentUser;
            return this;
        }

        public User build() {
            User user = new User(this);
            validateUserObject(user);
            return user;
        }

        private void validateUserObject(User user) {
            //Do some basic validations to check
            //if user object does not break any assumption of system
        }


    }
}
