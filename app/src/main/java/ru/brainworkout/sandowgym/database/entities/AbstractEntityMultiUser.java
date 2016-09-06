package ru.brainworkout.sandowgym.database.entities;

import ru.brainworkout.sandowgym.common.Common;

public abstract class AbstractEntityMultiUser extends AbstractEntity {

    protected int id_user;

    public AbstractEntityMultiUser() {

        User currentUser=Common.dbCurrentUser;
        if (currentUser!=null) {
            this.id_user = currentUser.getID();
        } else {
            throw new NullPointerException("Current user is not defined!");
        }
    }

    public int getIdUser() {
        return id_user;
    }

    public void setIdUser(int id_user) {
        this.id_user = id_user;
    }

}
