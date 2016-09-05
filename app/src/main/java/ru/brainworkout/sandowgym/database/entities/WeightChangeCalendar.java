package ru.brainworkout.sandowgym.database.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static ru.brainworkout.sandowgym.common.Common.*;
import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

/**
 * Created by Ivan on 25.07.2016.
 */
public class WeightChangeCalendar extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {
    private Date _day;
    private int _weight;

    private WeightChangeCalendar(Builder weightChangeCalendarBuilder) {
        this._id=weightChangeCalendarBuilder.getID();
        this._day = weightChangeCalendarBuilder._day;
        this._weight = weightChangeCalendarBuilder._weight;
    }

    public int getWeight() {
        return _weight;
    }

    public void setWeight(int _weight) {
        this._weight = _weight;
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

    public long getDayInMillis() {

        return _day.getTime();
    }

    public void setDay(Date _day) throws ParseException {
        this._day = _day;
    }

    public void setDayString(String _day) {

        this._day = ConvertStringToDate(_day, DATE_FORMAT_STRING);

    }

    @Override
    public void dbSave(DatabaseManager db) {

        try {
            db.getWeightChangeCalendar(this.getID());
            db.updateWeightChangeCalendar((WeightChangeCalendar) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addWeightCalendarChange((WeightChangeCalendar) this);
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {

        try {
            db.getWeightChangeCalendar(this.getID());
            db.deleteWeightChangeCalendar((WeightChangeCalendar) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
        }

    }

    public static class Builder extends AbstractEntity {

        private Date _day;
        private int _weight;

        public Builder(int id) {
            this._id = id;
        }

        public Builder addDay(Date day) {
            this._day = day;
            return this;
        }
        public Builder addDay(long day) {
            this._day = ConvertMillisToDate(day);
            return this;
        }

        public Builder addWeight(int _weight) {
            this._weight = _weight;
            return this;
        }

        public WeightChangeCalendar build() {
            WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar(this);
            return weightChangeCalendar;
        }

    }
}
