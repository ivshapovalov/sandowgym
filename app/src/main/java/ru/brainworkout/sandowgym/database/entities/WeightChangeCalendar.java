package ru.brainworkout.sandowgym.database.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.interfaces.DeleteFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SaveToDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

/**
 * Created by Ivan on 25.07.2016.
 */
public class WeightChangeCalendar extends AbstractEntityMultiUser implements SaveToDB,DeleteFromDb{
    private Date _day;
    private int _weight;

    private WeightChangeCalendar(WeightChangeCalendarBuilder weightChangeCalendarBuilder) {
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

    public static class WeightChangeCalendarBuilder extends AbstractEntity {

        private Date _day;
        private int _weight;

        public WeightChangeCalendarBuilder(int id) {
            this._id = id;
        }

        public WeightChangeCalendarBuilder addDay(Date day) {
            this._day = day;
            return this;
        }
        public WeightChangeCalendarBuilder addDay(String day) {
            this._day = Common.ConvertStringToDate(day, Common.DATE_FORMAT_STRING);
            return this;
        }

        public WeightChangeCalendarBuilder addWeight(int _weight) {
            this._weight = _weight;
            return this;
        }

        public WeightChangeCalendar build() {
            WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar(this);
            return weightChangeCalendar;
        }

    }
}
