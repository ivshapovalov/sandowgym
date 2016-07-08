package ru.brainworkout.sandow_gym.database.entities;


public class User extends AbstractEntity {
    private int _id;
    private String _name;
    private int _isCurrentUser;

    public User() {
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
}
