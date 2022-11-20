package com.mires.hackatonipn.enums;

public enum QuestionType {
    SINGLE_CHOICE(1),
    MULTI_CHOICE(2),
    TRUE_FALSE(3);

    private final int id;

    QuestionType(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static QuestionType getQuestionType(final int id) {
        for (final QuestionType questionType : values()) {
            if (questionType.getId() == id) {
                return questionType;
            }
        }
        return null;
    }
}
