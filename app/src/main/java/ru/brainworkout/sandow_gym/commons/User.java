package ru.brainworkout.sandow_gym.commons;


public class User {
    private int _id;
    private String _name;

    public User() {
    }

    public User(int _id) {
        this._id = _id;
    }

    public User(int _id, String _name) {

        this._id = _id;
        this._name = _name;
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
}
