package ru.brainworkout.sandow_gym.commons;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Training  extends AbstractDatabaseEntityOfUser {
    //private int _id;
    private Date _day;
    private int _weight; //кг

    public Training() {

    }

    public Training(int _id, Date _day, int weight) {
        this();
        this._id = _id;
        this._day = _day;
        this._weight = weight;
    }

    public Training(int _id, int _weight) {
        this();
        this._id = _id;
        this._weight = _weight;
    }

    public Training(int _id, Date _day) {
        this();
        this._id = _id;
        this._day = _day;
    }

    public Training(int _id, String _day) {
        this();
        this._id = _id;
        setDayString(_day);
    }
    public Training(int _id, String _day, int _weight) {
        this();
        this._id = _id;
        setDayString(_day);
        this._weight = _weight;
    }

    public Training(int _id) {
        this();
        this._id = _id;
    }

    public Date getDay() {

        return _day;
    }


    public int getWeight() {
        return _weight;
    }

    public void setWeight(int _weight) {
        this._weight = _weight;
    }

    public String getDayString() {

        String sDate;
        if (_day==null) {
            sDate="";
        }
        else {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            sDate = dateformat.format(_day);
        }
        return sDate;
    }


    public void setDay(Date _day) throws ParseException {
        this._day = _day;
    }

    public void setDayString(String _day) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(_day));
        } catch (ParseException e) {
            e.printStackTrace();
            d=null;
        }

        this._day = d;

    }

//    public void dbUpdate(DatabaseManager DB) {
//        DB.updateTraining(this);
//    }
//    public void dbAdd(DatabaseManager DB) {
//        DB.addTraining(this);
//    }

}

