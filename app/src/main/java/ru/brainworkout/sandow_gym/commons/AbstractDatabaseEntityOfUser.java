package ru.brainworkout.sandow_gym.commons;

/**
 * Created by Ivan on 07.07.2016.
 */
public class AbstractDatabaseEntityOfUser extends AbstractDatabaseEntity {

    private int _id_user;

    public AbstractDatabaseEntityOfUser() {
        this._id_user = 1;
        //TODO получение юзера
    }

    public int getIdUser() {
        return _id_user;
    }

    public void setIdUser(int _id_user) {
        this._id_user = _id_user;
    }

}
