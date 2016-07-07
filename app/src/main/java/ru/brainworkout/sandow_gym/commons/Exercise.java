package ru.brainworkout.sandow_gym.commons;

public class Exercise extends AbstractDatabaseEntityOfUser {

    //private int _id;
    private int _is_active;
    private String _name;
    private String _explanation;
    private String _volume_default;
    private String _picture;



    public Exercise() {

        this._explanation="";
        this._is_active=1;
        this._name="";
        this._picture="--";
        this._volume_default="";

    }
    public Exercise(int _id) {
        this();
        this._id=_id;
    }


    public Exercise(int _id, String _name,    String _explanation, String _volume_default, String _picture) {
        this();
        this._id=_id;
        this._name = _name;
        this._volume_default=_volume_default;
        this._explanation = _explanation;
        this._picture=_picture;
    }

    public Exercise(int _id, int _is_active, String _name,    String _explanation, String _volume_default, String _picture) {
        this();
        this._id=_id;
        this._is_active=_is_active;
        this._name = _name;
        this._volume_default=_volume_default;
        this._explanation = _explanation;
        this._picture=_picture;
    }

    public Exercise(int _id, String _name, String explanation, String _volume_default) {
        this();
        this._id=_id;
        this._name = _name;
        this._volume_default=_volume_default;
        this._explanation = explanation;
    }
    public Exercise(String _name,  String explanation,String _volume_default) {
        this();
        this._name = _name;
        this._volume_default=_volume_default;
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



    //    public void dbUpdate(DatabaseManager DB) {
//        DB.updateExercise(this);
//    }
//    public void dbAdd(DatabaseManager DB) {
//        DB.addExercise(this);
//    }

//    public void dbDelete(DatabaseManager DB) {
//        DB.deleteExercise(this);
//    }


}

