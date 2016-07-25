package ru.brainworkout.sandowgym.database.entities;


public abstract class AbstractEntity {

    protected int _id;

    public AbstractEntity() {

    }


    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }



}
