package ru.brainworkout.sandow_gym.commons;

/**
 * Created by Ivan on 16.05.2016.
 */
public class TrainingContent extends AbstractDatabaseEntity {

    //private int _id;
    private String _volume;
    private int _id_exercise;
    private int _id_training;
    private String _comment;

    public TrainingContent() {
    }

    public TrainingContent(int _id) {
        this._id = _id;
    }

    public TrainingContent(String _volume) {
        this._volume = _volume;
    }

    public TrainingContent(String _volume, String _comment) {
        this._volume = _volume;
        this._comment = _comment;
    }

    public TrainingContent(int _id, String _volume) {

        this._id = _id;
        this._volume = _volume;
    }

    public TrainingContent(int _id, String _volume, String _comment) {
        this._id = _id;
        this._volume = _volume;
        this._comment = _comment;
    }

    public TrainingContent(int _id, String _volume, int _id_exercise, int _id_training) {
        this._id = _id;
        this._volume = _volume;
        this._id_exercise = _id_exercise;
        this._id_training = _id_training;
    }

    public TrainingContent(int _id, String _volume, int _id_exercise, int _id_training, String _comment) {
        this._id = _id;
        this._volume = _volume;
        this._id_exercise = _id_exercise;
        this._id_training = _id_training;
        this._comment = _comment;
    }


    public String getVolume() {
        return _volume;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(String _comment) {
        this._comment = _comment;
    }

    public void setVolume(String _volume) {
        this._volume = _volume;
    }

    public int getIdExercise() {
        return _id_exercise;
    }

    public void setIdExercise(int _id_exercise) {
        this._id_exercise = _id_exercise;
    }

    public int getIdTraining() {
        return _id_training;
    }

    public void setIdTraining(int _id_training) {
        this._id_training = _id_training;
    }

//    public void dbUpdate(DatabaseManager DB) {
//        DB.updateTrainingContent(this);
//    }
//    public void dbAdd(DatabaseManager DB) {
//        DB.addTrainingContent(this);
//    }
}
