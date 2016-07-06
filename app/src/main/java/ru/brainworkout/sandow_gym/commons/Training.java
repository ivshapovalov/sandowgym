package ru.brainworkout.sandow_gym.commons;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Training  implements Parcelable{
    private int _id;
    private Date _day;
    private int _weight; //кг

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
//        String sDate = dateformat.format(_day);
        dest.writeStringArray(new String[] { String.valueOf(_id), getDayString() });
    }

    public static final Parcelable.Creator<Training> CREATOR = new Parcelable.Creator<Training>() {

        @Override
        public Training createFromParcel(Parcel source) {
            return new Training(source);
        }

        @Override
        public Training[] newArray(int size) {
            return new Training[size];
        }
    };
    public Training(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        _id = Integer.parseInt(data[0]);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date d = null;
//        try {
//            d = dateFormat.parse(String.valueOf(data[1]));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        _day = d;

        setDayString(data[1]);

    }
    public Training() {
    }

    public Training(int _id, Date _day, int weight) {
        this._id = _id;
        this._day = _day;
        this._weight = weight;
    }

    public Training(int _id, int _weight) {
        this._id = _id;
        this._weight = _weight;
    }

    public Training(int _id, Date _day) {
        this._id = _id;
        this._day = _day;
    }

    public Training(int _id, String _day) {
        this._id = _id;
        setDayString(_day);
    }
    public Training(int _id, String _day, int _weight) {
        this._id = _id;
        setDayString(_day);
        this._weight = _weight;
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

}

