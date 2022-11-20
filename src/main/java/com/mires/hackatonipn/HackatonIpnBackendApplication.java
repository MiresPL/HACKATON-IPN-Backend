package com.mires.hackatonipn;

import com.mires.hackatonipn.database.MysqlManager;
import com.mires.hackatonipn.objects.question.QuestionManager;
import com.mires.hackatonipn.objects.tag.TagManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class HackatonIpnBackendApplication {

    private static MysqlManager mysqlManager;
    private static TagManager tagManager;
    private static QuestionManager questionManager;

    public static void main(String[] args) {
        try {
            mysqlManager = new MysqlManager();
            tagManager = new TagManager();
            questionManager = new QuestionManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SpringApplication.run(HackatonIpnBackendApplication.class, args);
    }

    public static MysqlManager getMysqlManager() {
        return mysqlManager;
    }

    public static TagManager getTagManager() {
        return tagManager;
    }

    public static QuestionManager getQuestionManager() {
        return questionManager;
    }

}
