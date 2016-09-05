package ru.brainworkout.sandowgym.database.entities;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.brainworkout.sandowgym.common.Common.*;
import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class Training extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private Date _day;

    private Training(Builder builder) {

        this._id = builder._id;
        this._day = builder._day;
    }

    public Date getDay() {

        return _day;
    }

    public String getDayString() {

        String sDate;
        if (_day == null) {
            sDate = "";
        } else {
            SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMAT_STRING);
            sDate = dateformat.format(_day);
        }
        return sDate;
    }


    public void setDay(Date _day) throws ParseException {
        this._day = _day;
    }

    public void setDayInMillis(long millis) {

        this._day = new Date(millis);
    }

    public void setDayString(String _day) {

        this._day = ConvertStringToDate(_day, DATE_FORMAT_STRING);

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

    public static Training getTrainingFromDB(DatabaseManager DB, int id) {
        return DB.getTraining(id);
    }

    public long getDayInMillis() {
        return _day.getTime();
    }

    public static class Builder extends AbstractEntity {

        private Date _day;

        public Builder(DatabaseManager DB) {
            this._id = DB.getTrainingMaxNumber() + 1;
        }
        public Builder(int _id) {
            this._id = _id;
        }

        public Builder addDay(Date day) {
            this._day = day;
            return this;
        }
        public Builder addDay(long day) {
            this._day = ConvertMillisToDate(day);
            return this;
        }

        public Training build() {
            Training training = new Training(this);
            return training;
        }

    }


}

