package ru.brainworkout.sandow_gym.database.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class Training extends AbstractEntityMultiUser {
    private Date _day;

    public Training() {

    }

    public Training(int _id, Date _day, int weight) {
        this();
        this._id = _id;
        this._day = _day;
    }

    public Training(int _id, int _weight) {
        this();
        this._id = _id;
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
    }

    public Training(int _id) {
        this();
        this._id = _id;
    }

    public Date getDay() {

        return _day;
    }

    public String getDayString() {

        String sDate;
        if (_day == null) {
            sDate = "";
        } else {
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
            d = null;
        }

        this._day = d;

    }

    @Override
    public void dbSave(DatabaseManager db) {
        try {
            db.getTraining(this.getID());
            db.updateTraining((Training) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addTraining((Training) this);
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {

            try {
                db.getTraining(this.getID());
                db.deleteTraining((Training) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого

            }

    }
}

