package ru.ivan.sandowgym.database.entities;

import ru.ivan.sandowgym.common.Common;

public abstract class AbstractEntityMultiUser extends AbstractEntity {

    protected User user;

    public AbstractEntityMultiUser() {

        User currentUser=Common.dbCurrentUser;
        if (currentUser!=null) {
            this.user = currentUser;
        } else {
            throw new NullPointerException("Current user is not defined!");
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId(){
        if (user!=null) {
            return user.getId();
        } else {
            return -1;
        }
    }
}
