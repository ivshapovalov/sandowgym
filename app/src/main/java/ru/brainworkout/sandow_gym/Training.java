package ru.brainworkout.sandow_gym;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ivan on 14.05.2016.
 */
public class Training {
    private int _id;
    private Date _day;

    public Training() {
    }

    public Training(int _id, Date _day) {
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

    public Date getDay() {

        return _day;
    }


    public void setDay(Date _day) throws ParseException {
        this._day = _day;
    }

}

