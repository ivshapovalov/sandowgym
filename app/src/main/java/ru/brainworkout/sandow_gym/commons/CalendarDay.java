package ru.brainworkout.sandow_gym.commons;


public class CalendarDay {
    int _id;
    int _id_ex;
    int _count;
    String _day;

    public CalendarDay(String _day) {
        this._day = _day;
    }

    public CalendarDay(int _id_ex) {
        this._id_ex = _id_ex;
    }

    public int get_id_ex() {
        return _id_ex;
    }

    public void set_id_ex(int _id_ex) {
        this._id_ex = _id_ex;
    }

    public int get_count() {
        return _count;
    }

    public void set_count(int _count) {
        this._count = _count;
    }

    public String get_day() {
        return _day;
    }

    public void set_day(String _day) {
        this._day = _day;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }




}
