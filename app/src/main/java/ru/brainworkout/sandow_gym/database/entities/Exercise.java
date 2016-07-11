package ru.brainworkout.sandow_gym.database.entities;

import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class Exercise extends AbstractEntityMultiUser {

    private int _is_active=1;
    private String _name="";
    private String _explanation="";
    private String _volume_default="";
    private String _picture="--";

    public Exercise() {

    }

    public Exercise(int _id) {
        super();
        this._id = _id;
    }


    public Exercise(int _id, String _name, String _explanation, String _volume_default, String _picture) {
        super();
        this._id = _id;
        this._name = _name;
        this._volume_default = _volume_default;
        this._explanation = _explanation;
        this._picture = _picture;
    }

    public Exercise(int _id, int _is_active, String _name, String _explanation, String _volume_default, String _picture) {
        super();
        this._id = _id;
        this._is_active = _is_active;
        this._name = _name;
        this._volume_default = _volume_default;
        this._explanation = _explanation;
        this._picture = _picture;
    }

    public Exercise(int _id, String _name, String explanation, String _volume_default) {
        super();
        this._id = _id;
        this._name = _name;
        this._volume_default = _volume_default;
        this._explanation = explanation;
    }

    public Exercise(String _name, String explanation, String _volume_default) {
        super();
        this._name = _name;
        this._volume_default = _volume_default;
        this._explanation = explanation;

    }

    public String getPicture() {
        return _picture;
    }

    public void setPicture(String _picture) {
        this._picture = _picture;
    }


    public int getIsActive() {
        return _is_active;
    }

    public void setIsActive(int _is_active) {
        this._is_active = _is_active;
    }


    // getting ID

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    // getting name
    public String getExplanation() {
        return this._explanation;
    }

    // setting name
    public void setExplanation(String explanation) {
        this._explanation = explanation;
    }

    public String getVolumeDefault() {
        return _volume_default;
    }

    public void setVolumeDefault(String _volume_default) {
        this._volume_default = _volume_default;
    }

    @Override
    public void dbSave(DatabaseManager db) {

        try {
            db.getExercise(this.getID());
            db.updateExercise((Exercise) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addExercise((Exercise) this);
        }

    }

    @Override
    public void dbDelete(DatabaseManager db) {


        try {
            db.getExercise(this.getID());
            db.deleteExercise((Exercise) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
        }

    }



}

