package com.mires.hackatonipn.database;

import com.mires.hackatonipn.enums.Difficulty;
import com.mires.hackatonipn.helper.PythonHelper;
import com.mires.hackatonipn.objects.exam.Exam;
import com.mires.hackatonipn.objects.question.Question;
import com.mires.hackatonipn.objects.tag.Tag;
import com.mires.hackatonipn.objects.user.User;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class MysqlManager {

    private ConnectionPoolManager pool;
    private PreparedStatement ps;
    private ResultSet rs;
    private Connection con;

    public MysqlManager() throws SQLException {
        this.pool = new ConnectionPoolManager();
        this.createTables();
    }

    public void test() {
        final User user = new User(UUID.randomUUID(), "Jan", "Kowalski", "jan.kowalski@gmail.com", "jankow123", new ArrayList<>());
        final User user2 = new User(UUID.randomUUID(), "Kuba", "Kowalski", "kuba.kowalski@gmail.com", "kubakow123", new ArrayList<>());
        this.registerUser(user);
        this.registerUser(user2);
        final Tag tag1 = new Tag(UUID.randomUUID(), "Powstanie Warszawskie");
        final Tag tag2 = new Tag(UUID.randomUUID(), "Powstanie Listopadowe");
        final Tag tag3 = new Tag(UUID.randomUUID(), "Powstanie Styczniowe");

        this.addTag(tag1);
        this.addTag(tag2);
        this.addTag(tag3);

        final Question question1 = new Question(UUID.randomUUID(), "Czy takie jest pytanie1?", Difficulty.PRIMARY, "Tak;Nie", "Tak", "blank", new ArrayList<>());
        final Question question2 = new Question(UUID.randomUUID(), "Czy takie jest pytanie2?", Difficulty.PRIMARY, "tak;Nie;Nie Wiem;Moze", "Nie Wiem", "multi", new ArrayList<>());
        final Question question3 = new Question(UUID.randomUUID(), "Czy takie jest pytanie3?", Difficulty.PRIMARY, "Prawda;Falsz", "Prawda", "tf", new ArrayList<>());
        question1.addTag(tag1.getUuid());
        question1.addTag(tag2.getUuid());

        question2.addTag(tag1.getUuid());

        question3.addTag(tag3.getUuid());
        question3.addTag(tag2.getUuid());

        this.addQuestion(question1);
        this.addQuestion(question2);
        this.addQuestion(question3);

        final Exam exam1 = new Exam(UUID.randomUUID(), "Test1", "Testowy Egzamin", 3, new ArrayList<>(3), Difficulty.PRIMARY);

        exam1.addQuestion(question1.getUuid());
        exam1.addQuestion(question2.getUuid());
        exam1.addQuestion(question3.getUuid());

        this.addExam(user.getUuid(), exam1);
    }

    public void createTables() {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS user(ID VARCHAR(255) PRIMARY KEY, email VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), password VARCHAR(255));");
            ps.executeUpdate();

            ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS userExams(ID INT PRIMARY KEY AUTO_INCREMENT, userID VARCHAR(255), examID VARCHAR(255));");
            ps.executeUpdate();

            ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS exams(ID VARCHAR(255) PRIMARY KEY, title VARCHAR(255), description TEXT, difficulty INT(1), questions TEXT);");
            ps.executeUpdate();

            ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS questions(ID VARCHAR(255) PRIMARY KEY, content TEXT, difficulty INT(1), answers TEXT, correctAnwser TEXT, questionType VARCHAR(5), tags TEXT);");
            ps.executeUpdate();

            ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS tag(ID VARCHAR(255) PRIMARY KEY, name VARCHAR(255));");
            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
        } finally {
            pool.close(con, ps, rs);
        }
    }

    public String registerUser(final User user) {
        try {
            con = pool.getConnection();

            ps = con.prepareStatement("SELECT * FROM user WHERE email = ?;");
            ps.setString(1, user.getEmail());
            rs = ps.executeQuery();

            if (rs.next()) {
                return new JSONObject().put("status", "Error-User-Alredy-Exists").toString();
            }

            ps = con.prepareStatement("INSERT INTO user (ID, email, name, surname, password) VALUES (?, ?, ?, ?, ?);");
            ps.setString(1, user.getUuid().toString());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getName());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getPassword());
            ps.executeUpdate();
            return new JSONObject().put("status", "ok").toString();
        } catch (final SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return new JSONObject().put("status", "error").toString();
        } finally {
            pool.close(con, ps, rs);
        }
    }

    public String getUserData(final String email, final String password) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM user;");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("email").equals(email) && rs.getString("password").equals(password)) {
                    return new JSONObject()
                            .put("status", "success")
                            .put("id", rs.getString("ID"))
                            .put("name", rs.getString("name"))
                            .put("surname", rs.getString("surname"))
                            .put("email", rs.getString("email")).toString();
                }
            }
            return new JSONObject()
                    .put("status", "Error-No-Matching-User-Found")
                    .put("id", "")
                    .put("email", "")
                    .put("name", "")
                    .put("surname", "").toString();
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return new JSONObject()
                    .put("status", "Error-SQL")
                    .put("id", "")
                    .put("email", "")
                    .put("name", "")
                    .put("surname", "").toString();
        } finally {
            pool.close(con, ps, rs);
        }
    }

    public String addExam(final UUID userId, final Exam exam) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("INSERT INTO exams (ID, title, description, difficulty, questions) VALUES (?, ?, ?, ?, ?);");
            ps.setString(1, exam.getUuid().toString());
            ps.setString(2, exam.getTitle());
            ps.setString(3, exam.getDescription());
            ps.setInt(4, exam.getDifficulty().getID());
            ps.setString(5, exam.getQuestions().toString());
            ps.executeUpdate();

            ps = con.prepareStatement("INSERT INTO userExams (userID, examID) VALUES (?, ?);");
            ps.setString(1, userId.toString());
            ps.setString(2, exam.getUuid().toString());
            ps.executeUpdate();
            return new JSONObject().put("status", "ok").toString();
        } catch (final SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return new JSONObject().put("status", "error").toString();
        } finally {
            pool.close(con, ps, rs);
        }
    }

    public String getUserExams(final UUID id) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM userExams WHERE userID = ?;");
            ps.setString(1, id.toString());
            rs = ps.executeQuery();

            final JSONObject exams = new JSONObject();
            int i = 1;
            while (rs.next()) {
                final String examID = rs.getString("examID");
                final JSONObject examJ = new JSONObject();
                ps = con.prepareStatement("SELECT * FROM exams WHERE ID = ?;");
                ps.setString(1, examID);
                final ResultSet exam = ps.executeQuery();
                while (exam.next()) {
                    examJ.put("id", examID);
                    examJ.put("title", exam.getString("title"));
                    examJ.put("description", exam.getString("description"));
                    examJ.put("difficulty", exam.getInt("difficulty"));

                    final JSONObject questions = new JSONObject();
                    final String[] questionIDs = exam.getString("questions").split(",");
                    int k = 1;
                    for (String s : questionIDs) {
                        final JSONObject question = new JSONObject();
                        ps = con.prepareStatement("SELECT * FROM questions WHERE ID = ?;");
                        ps.setString(1, s.replace("[", "").replace("]", "").replace(" ", ""));

                        final ResultSet questionR = ps.executeQuery();
                        while (questionR.next()) {
                            question.put("content", questionR.getString("content"));
                            question.put("difficulty", questionR.getInt("difficulty"));
                            question.put("answers", questionR.getString("answers"));
                            question.put("correctAnwser", questionR.getString("correctAnwser"));
                            question.put("questionType", questionR.getString("questionType"));

                            final String[] tags = questionR.getString("tags").split(",");
                            final JSONObject tagsJ = new JSONObject();
                            int j = 1;
                            for (String tag : tags) {
                                ps = con.prepareStatement("SELECT * FROM tag WHERE ID = ?;");
                                ps.setString(1, tag.replace("[", "").replace("]", "").replace(" ", ""));

                                final ResultSet tagR = ps.executeQuery();
                                while (tagR.next()) {
                                    tagsJ.put("Tag_" + j, tagR.getString("name"));
                                }
                                j++;
                            }
                            question.put("tags", tagsJ);

                            questions.put("Pytanie_" + k, question);
                        }
                        k++;
                    }
                    examJ.put("questions", questions);
                    exams.put("" + i, examJ);
                    i++;
                }
            }
            return exams.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return new JSONObject().put("status", "error").toString();
        } finally {
            pool.close(con, ps, rs);
        }
    }

    public String addQuestion(final Question question) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("INSERT INTO questions (ID, content, difficulty, answers, correctAnwser, questionType, tags) VALUES (?, ?, ?, ?, ?, ?, ?);");
            ps.setString(1, String.valueOf(question.getUuid()));
            ps.setString(2, question.getContent());
            ps.setInt(3, question.getDifficulty().getID());
            ps.setString(4, question.getAnswers());
            ps.setString(5, question.getCorrectAnswer());
            ps.setString(6, question.getQuestionType());
            ps.setString(7, String.valueOf(question.getTags()));
            ps.executeUpdate();
            return new JSONObject().put("status", "ok").toString();
        } catch (final SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return new JSONObject().put("status", "error").toString();
        } finally {
            pool.close(con, ps, rs);
        }
    }

    public String addTag(final Tag tag) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("INSERT INTO tag (ID, name) VALUES (?, ?);");
            ps.setString(1, tag.getUuid().toString());
            ps.setString(2, tag.getName());
            ps.executeUpdate();
            return new JSONObject().put("status", "ok").toString();
        } catch (final SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return new JSONObject().put("status", "error").toString();
        } finally {
            pool.close(con, ps, rs);
        }
    }


    public Map<UUID, Tag> loadAllTags() {
        final Map<UUID, Tag> tags = new HashMap<>();
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM tag;");
            rs = ps.executeQuery();
            while (rs.next()) {
                final Tag tag = new Tag(UUID.fromString(rs.getString("ID")), rs.getString("name"));
                tags.put(tag.getUuid(), tag);
            }
            return tags;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
        } finally {
            pool.close(con, ps, rs);
        }
        return tags;
    }

    public Map<UUID, Question> loadAllQuestions() {
        final Map<UUID, Question> questions = new HashMap<>();
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM questions;");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(UUID.fromString(rs.getString("ID").trim().replaceAll(" ", "")));
                System.out.println(UUID.fromString(rs.getString("ID")));
                final Question question = new Question(
                        UUID.fromString(rs.getString("ID").trim().replaceAll(" ", "").replaceAll(Pattern.quote("["), "").replaceAll("]", "")),
                        rs.getString("content"),
                        Difficulty.getDifficultyByID(rs.getInt("difficulty")),
                        rs.getString("answers"),
                        rs.getString("correctAnwser"),
                        rs.getString("questionType"),
                        new ArrayList<>());
                final String[] tags = rs.getString("tags").replaceAll(Pattern.quote("["), "").replace("]", "").split(",");
                for (String s : tags) {
                    question.addTag(UUID.fromString(s.replaceAll(" ", "").trim()));
                }
                questions.put(question.getUuid(), question);
            }
            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
        } finally {
            pool.close(con, ps, rs);
        }
        return questions;
    }

}
