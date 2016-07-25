package ru.brainworkout.sandowgym.database.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.interfaces.DeleteFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SaveToDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class Training extends AbstractEntityMultiUser implements SaveToDB,DeleteFromDb {

    private Date _day;

    private Training(TrainingBuilder builder) {

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
            SimpleDateFormat dateformat = new SimpleDateFormat(Common.DATE_FORMAT_STRING);
            sDate = dateformat.format(_day);
        }
        return sDate;
    }


    public void setDay(Date _day) throws ParseException {
        this._day = _day;
    }

    public void setDayString(String _day) {

        this._day = Common.ConvertStringToDate(_day, Common.DATE_FORMAT_STRING);

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


    public static class TrainingBuilder extends AbstractEntity {

        private Date _day;

        public TrainingBuilder(int id) {
            this._id = id;
        }

        public TrainingBuilder addDay(Date day) {
            this._day = day;
            return this;
        }
        public TrainingBuilder addDay(String day) {
            this._day = Common.ConvertStringToDate(day, Common.DATE_FORMAT_STRING);
            return this;
        }

        public Training build() {
            Training training = new Training(this);
            return training;
        }

    }


}

