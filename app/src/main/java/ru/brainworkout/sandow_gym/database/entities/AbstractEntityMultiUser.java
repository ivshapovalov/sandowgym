package ru.brainworkout.sandow_gym.database.entities;

import ru.brainworkout.sandow_gym.common.Common;

public class AbstractEntityMultiUser extends AbstractEntity {

    private int _id_user;

    public AbstractEntityMultiUser() {

        this._id_user= Common.mCurrentUser.getID();
    }

    public int getIdUser() {
        return _id_user;
    }

    public void setIdUser(int _id_user) {
        this._id_user = _id_user;
    }

}
