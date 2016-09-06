package ru.brainworkout.sandowgym.database.entities;


public abstract class AbstractEntity {

    protected int id;

    public AbstractEntity() {

    }


    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }



}
