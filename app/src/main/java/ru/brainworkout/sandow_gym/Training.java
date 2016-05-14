package ru.brainworkout.sandow_gym;

/**
 * Created by Ivan on 14.05.2016.
 */
public class Training {
    private int _id;
    private String _day;

    public Training() {
    }

    public Training(int _id, String _day) {
        this._id = _id;
        this._day = _day;
    }

    public Training(int _id) {

        this._id = _id;
    }

    public int getID() {
        return _id;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public String getDay() {
        return _day;
    }

    public void setDay(String _day) {
        this._day = _day;
    }
}

