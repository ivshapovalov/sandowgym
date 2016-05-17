package ru.brainworkout.sandow_gym;

/**
 * Created by Ivan on 16.05.2016.
 */
public class TrainingContent {

    private int _id;
    private String _volume;
    private int _id_exercise;
    private int _id_training;

    public TrainingContent() {
    }

    public TrainingContent(int _id) {
        this._id = _id;
    }

    public TrainingContent(String _volume) {
        this._volume = _volume;
    }

    public TrainingContent(int _id, String _volume) {

        this._id = _id;
        this._volume = _volume;
    }

    public TrainingContent(int _id, String _volume, int _id_exercise, int _id_training) {
        this._id = _id;
        this._volume = _volume;
        this._id_exercise = _id_exercise;
        this._id_training = _id_training;
    }

    public int getID() {
        return _id;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public String getVolume() {
        return _volume;
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
}
