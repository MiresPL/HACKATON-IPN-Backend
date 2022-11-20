package com.mires.hackatonipn.objects.question;

import com.mires.hackatonipn.HackatonIpnBackendApplication;

import java.util.Map;
import java.util.UUID;

public class QuestionManager {
    private final Map<UUID, Question> questionMap;

    public QuestionManager() {
        this.questionMap = HackatonIpnBackendApplication.getMysqlManager().loadAllQuestions();
    }

    public Question findQuestion(final UUID uuid) {
        return questionMap.get(uuid);
    }

    public void addQuestion(final Question question) {
        questionMap.put(question.getUuid(), question);
    }
}
