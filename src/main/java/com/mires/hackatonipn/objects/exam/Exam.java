package com.mires.hackatonipn.objects.exam;

import com.mires.hackatonipn.HackatonIpnBackendApplication;
import com.mires.hackatonipn.enums.Difficulty;
import com.mires.hackatonipn.objects.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Exam {
    public UUID uuid;
    public String title;
    public String description;
    public int size;
    public List<UUID> questions;
    public Difficulty difficulty;

    public void addQuestion(UUID question) {
        questions.add(question);
    }

    public void removeQuestion(UUID question) {
        questions.remove(question);
    }


    public String toJson() {
        final JSONObject exams = new JSONObject();
        final String examID = uuid.toString();
        final JSONObject examJ = new JSONObject();
        examJ.put("title", title);
        examJ.put("description", description);
        examJ.put("size", size);
        examJ.put("difficulty", difficulty);

        final JSONObject questions = new JSONObject();
        System.out.println(this.questions.size());
        int i = 0;
        for (UUID question : this.questions) {
            System.out.println(1);
            final JSONObject questionJ = new JSONObject();
            final Question questionO = HackatonIpnBackendApplication.getQuestionManager().findQuestion(question);
            questionJ.put("content", questionO.getContent());
            questionJ.put("difficulty", questionO.getDifficulty().getID());
            questionJ.put("answers", questionO.getAnswers());
            questionJ.put("correctAnwser", questionO.getCorrectAnswer());
            questionJ.put("questionType", questionO.getQuestionType());
            final JSONObject tags = new JSONObject();
            int j = 1;
            for (UUID tag : questionO.getTags()) {
                final JSONObject tagJ = new JSONObject();
                tagJ.put("name", HackatonIpnBackendApplication.getTagManager().findTag(tag).getName());

                tags.put("Tag_" + j, tagJ);
                j++;
            }
            questionJ.put("tags", tags);

            questions.put("Pytanie_" + i, questionJ);
            i++;
        }
        examJ.put("questions", questions);

        exams.put(examID, examJ);

        return exams.toString();
    }

}
