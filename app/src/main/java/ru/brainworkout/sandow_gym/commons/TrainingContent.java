package ru.brainworkout.sandow_gym.commons;

/**
 * Created by Ivan on 16.05.2016.
 */
public class TrainingContent extends AbstractDatabaseEntityOfUser {


    private int _id_exercise;
    private int _id_training;
    private String _comment;
    private String _volume;

    public TrainingContent() {

    }

    public TrainingContent(int _id) {
        this();
        this._id = _id;
    }

    public TrainingContent(String _volume) {
        this();
        this._volume = _volume;
    }

    public TrainingContent(String _comment,String _volume) {
        this();
        this._volume = _volume;
        this._comment = _comment;
    }

    public TrainingContent(int _id, String _volume) {

        this();
        this._id = _id;
        this._volume = _volume;
    }

    public TrainingContent(int _id, String _comment, String _volume) {
        this();
        this._id = _id;
        this._volume = _volume;
        this._comment = _comment;
    }

    public TrainingContent(int _id,  int _id_exercise, int _id_training,String _volume) {
        this();
        this._id = _id;
        this._volume = _volume;
        this._id_exercise = _id_exercise;
        this._id_training = _id_training;
    }

    public TrainingContent(int _id, int _id_exercise, int _id_training, String _comment,String _volume) {
        this();
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
