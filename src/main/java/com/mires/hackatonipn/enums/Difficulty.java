package com.mires.hackatonipn.enums;

public enum Difficulty {


    PRIMARY(1),
    SECONDARY(2),
    ADVANCED(3);

    private final int id;

    Difficulty(final int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public static Difficulty getDifficultyByID(final int id) {
        for (final Difficulty difficulty : values()) {
            if (difficulty.getID() == id) {
                return difficulty;
            }
        }
        return null;
    }
}
