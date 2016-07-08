package ru.brainworkout.sandow_gym.database.entities;

import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;


public abstract class AbstractEntity {

    protected int _id;

    public AbstractEntity() {

    }


    public void dbSave(DatabaseManager db) {

        if (this instanceof User) {


            User user=(User) this;
            try {
                db.getUser(this.getID());
                db.updateUser(user);

            } catch (TableDoesNotContainElementException e) {
                //нет такого
                db.addUser(user);

            }

        }
        else if (this instanceof Exercise) {

            try {
                db.getExercise(this.getID());
                db.updateExercise((Exercise) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
                db.addExercise((Exercise) this);
            }
        } else  if (this instanceof Training) {

            try {
                db.getTraining(this.getID());
                db.updateTraining((Training) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
                db.addTraining((Training) this);
            }
        } else  if (this instanceof TrainingContent) {

            try {
                db.getTrainingContent(this.getID());
                db.updateTrainingContent((TrainingContent) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
                db.addTrainingContent((TrainingContent) this);
            }
        }

    }

    public void dbDelete(DatabaseManager db) {

        if (this instanceof User) {

            try {
                db.getUser(this.getID());
                db.deleteUser((User) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
            }
        }
        else if (this instanceof Exercise) {

            try {
                db.getExercise(this.getID());
                db.deleteExercise((Exercise) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
            }
        } else  if (this instanceof Training) {

            try {
                db.getTraining(this.getID());
                db.deleteTraining((Training) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого

            }
        } else  if (this instanceof TrainingContent) {

            try {
                db.getTrainingContent(this.getID());
                db.deleteTrainingContent((TrainingContent) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
            }
        }

    }



    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

}
