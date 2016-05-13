package ru.brainworkout.sandow_gym;

/**
 * Created by Ivan on 13.05.2016.
 */
public class Exercise {

    //private variables
    int _id;
    int _num;
    String _name;

    // Empty constructor
    public Exercise() {

    }

    // constructor
    public Exercise(int _id, int _num, String name) {
        this._id=_id;
        this._num = _num;
        this._name = name;

    }
    public Exercise(int _num, String name) {

        this._num = _num;
        this._name = name;

    }

    // constructor
    public Exercise(String name) {
        this._name = name;
    }

    // getting ID

    public int getNumber() {
        return _num;
    }

    public void setNumber(int _num) {
        this._num = _num;
    }

    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        this._name = name;
    }


}

