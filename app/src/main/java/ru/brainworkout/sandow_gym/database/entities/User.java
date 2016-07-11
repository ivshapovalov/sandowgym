package ru.brainworkout.sandow_gym.database.entities;


import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class User extends AbstractEntity {
    private int _id;
    private String _name;
    private int _isCurrentUser;

    private User() {
    }

    private User(UserBuilder userBuilder) {

            this.firstName = builder.firstName;
            this.lastName = builder.lastName;
            this.age = builder.age;
            this.phone = builder.phone;
            this.address = builder.address;

    }


    public User(int _id) {
        this._id = _id;
    }

    public User(int _id, String _name) {

        this._id = _id;
        this._name = _name;
    }

    public User(int _id, String _name, int _isCurrentUser) {
        this._id = _id;
        this._name = _name;
        this._isCurrentUser = _isCurrentUser;
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
        User user=(User) this;
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


    public static class UserBuilder {

        private final int _id;
        private String _name;
        private int _isCurrentUser;

        public UserBuilder(int id) {
            this._id=id;
        }

        public UserBuilder AddName(String name) {
            this._name=name;
            return this;
        }

        public UserBuilder AddIsCurrentUser(int isCurrentUser) {
            this._isCurrentUser=isCurrentUser;
            return this;
        }
        //Return the finally consrcuted User object
        public User build() {
            User user =  new User(this);
            validateUserObject(user);
            return user;
        }
        private void validateUserObject(User user) {
            //Do some basic validations to check
            //if user object does not break any assumption of system
        }


    }
}
