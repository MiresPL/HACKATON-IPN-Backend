package com.mires.hackatonipn.controlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mires.hackatonipn.HackatonIpnBackendApplication;
import com.mires.hackatonipn.enums.Difficulty;
import com.mires.hackatonipn.helper.PythonHelper;
import com.mires.hackatonipn.objects.exam.Exam;
import com.mires.hackatonipn.objects.question.Question;
import com.mires.hackatonipn.objects.tag.Tag;
import com.mires.hackatonipn.objects.user.User;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/user", method = RequestMethod.POST, produces = "application/json")
public class UserControler {

    @CrossOrigin
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json")
    public String isLoginValid(@RequestBody Map<String, Object> postData) {
        return HackatonIpnBackendApplication.getMysqlManager().getUserData(String.valueOf(postData.get("email")), String.valueOf(postData.get("password")));
    }

    @CrossOrigin
    @RequestMapping(path = "/getExams", method = RequestMethod.POST, produces = "application/json")
    public String getExams(@RequestBody Map<String, Object> postData) {
        try {
            Object json = new ObjectMapper().readValue(HackatonIpnBackendApplication.getMysqlManager().getUserExams(UUID.fromString(String.valueOf(postData.get("id")))), Object.class);
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @CrossOrigin
    @RequestMapping(path = "/register", method = RequestMethod.POST, produces = "application/json")
    public String register(@RequestBody Map<String, Object> postData) {
        final User user = new User(
                UUID.randomUUID(),
                String.valueOf(postData.get("name")),
                String.valueOf(postData.get("surname")),
                String.valueOf(postData.get("email")),
                String.valueOf(postData.get("password")),
                new ArrayList<>());
        return HackatonIpnBackendApplication.getMysqlManager().registerUser(user);
    }


    @CrossOrigin
    @RequestMapping(path = "/generateQuizz", method = RequestMethod.POST, produces = "application/json")
    public String generateQuizz(@RequestBody Map<String, Object> postData) throws ExecutionException, InterruptedException {

        final String difficulty = String.valueOf(postData.get("difficulty"));
        final String topic = String.valueOf(postData.get("topic"));
        final String questionType = String.valueOf(postData.get("questionType"));
        final int amount = Integer.parseInt(String.valueOf(postData.get("amount"))); //


        final List<UUID> questionList = new ArrayList<>();

        final String[] questions = PythonHelper.getPythonQuestions(String.valueOf(amount), difficulty, topic, questionType).get().split("/");
        for (String question : questions) {
            final String[] splitQA = question.split(Pattern.quote("|"));

            Question question1 = new Question(
                    UUID.randomUUID(),
                    splitQA[0].replaceAll("\"", "").replaceAll("____", "blank"),
                    Difficulty.getDifficultyByID(Integer.parseInt(difficulty)),
                    splitQA[1].replaceAll("\"", "").replaceAll(";", " "),
                    splitQA[1].replaceAll("\"", "").replaceAll(";", " "),
                    questionType,
                    new ArrayList<>());
            HackatonIpnBackendApplication.getQuestionManager().addQuestion(question1);
            HackatonIpnBackendApplication.getMysqlManager().addQuestion(question1);
            question1.addTag(HackatonIpnBackendApplication.getTagManager().getTag(topic).getUuid());
            String object = question1.toString();
            System.out.println(object);
            questionList.add(question1.getUuid());
        }

        final Exam exam = new Exam(UUID.randomUUID(), topic, String.valueOf(postData.get("examDescription")), amount, questionList, Difficulty.getDifficultyByID(Integer.parseInt(difficulty)));
        HackatonIpnBackendApplication.getMysqlManager().addExam(UUID.fromString(String.valueOf(postData.get("id"))), exam);
        return exam.toJson();
    }

    @CrossOrigin
    @RequestMapping(path = "/getTopics", method = RequestMethod.POST, produces = "application/json")
    public String getTopics() {
        final JSONObject jsonObject = new JSONObject();
        int i = 1;
        for (Tag tag : HackatonIpnBackendApplication.getTagManager().getTags()) {
            jsonObject.put("Topic_" + i, tag.getName());
            i++;
        }
        return jsonObject.toString();
    }
}
