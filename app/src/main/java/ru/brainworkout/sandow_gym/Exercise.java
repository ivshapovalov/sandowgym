package ru.brainworkout.sandow_gym;

/**
 * Created by Ivan on 13.05.2016.
 */
public class Exercise {

    //private variables
    int _id;
    int _is_active;
    String _name;
    String _explanation;
    String _volume_default;
    String _picture;


    // Empty constructor
    public Exercise() {

    }

    public String getPicture() {
        return _picture;
    }

    public void setPicture(String _picture) {
        this._picture = _picture;
    }

    // constructor
    public Exercise(int _id, String _name,    String _explanation, String _volume_default, String _picture) {
        this._id=_id;
        this._name = _name;
        this._volume_default=_volume_default;
        this._explanation = _explanation;
        this._picture=_picture;
    }

    public Exercise(int _id, int _is_active, String _name,    String _explanation, String _volume_default, String _picture) {
        this._id=_id;
        this._is_active=_is_active;
        this._name = _name;
        this._volume_default=_volume_default;
        this._explanation = _explanation;
        this._picture=_picture;
    }

    public int getIsActive() {
        return _is_active;
    }

    public void setIsActive(int _is_active) {
        this._is_active = _is_active;
    }

    public Exercise(int _id, String _name, String explanation, String _volume_default) {
        this._id=_id;
        this._name = _name;
        this._volume_default=_volume_default;
        this._explanation = explanation;
    }
    public Exercise(String _name,  String explanation,String _volume_default) {

        this._name = _name;
        this._volume_default=_volume_default;
        this._explanation = explanation;

    }

    // getting ID

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
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
}

