package com.mires.hackatonipn.objects.question;

import com.mires.hackatonipn.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Question {
    public UUID uuid;
    public String content;
    public Difficulty difficulty;
    public String answers;
    public String correctAnswer;
    public String questionType;
    public List<UUID> tags;

    public Question(final UUID uuid, final String content, final Difficulty difficulty, final String answers, final String correctAnswer, final String questionType, final List<UUID> tags) {
        this.uuid = uuid;
        this.content = content;
        this.difficulty = difficulty;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.questionType = questionType;
        this.tags = tags;
    }


    public void addTag(UUID tag) {
        tags.add(tag);
    }

    public void removeTag(UUID tag) {
        tags.remove(tag);
    }

    @Override
    public String toString() {
        return "Question{" +
                "uuid=" + uuid +
                ", content='" + content + '\'' +
                ", difficulty=" + difficulty +
                ", answers='" + answers + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", questionType='" + questionType + '\'' +
                ", tags=" + tags +
                '}';
    }

}
