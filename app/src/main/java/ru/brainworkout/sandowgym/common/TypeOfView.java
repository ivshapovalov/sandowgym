package ru.brainworkout.sandowgym.common;

/**
 * Created by Ivan on 03.09.2016.
 */
public enum TypeOfView {

    FULL ("trainings_full"),
    SHORT ("trainings"),
    SHORT_WITH_WEIGHTS ("trainings_with_weights");

    private final String name;

    TypeOfView(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
