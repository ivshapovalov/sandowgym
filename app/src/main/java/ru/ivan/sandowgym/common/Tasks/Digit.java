package ru.ivan.sandowgym.common.Tasks;

public enum Digit {
    AMOUNT("Amount"),
    WEIGHT("Weight"),
    INTEGER("Integer");

    private final String text;

    Digit(final String text) {
        this.text = text;
    }

    public boolean equalValue(String passedValue) {
        return this.text.equalsIgnoreCase(passedValue);
    }

}