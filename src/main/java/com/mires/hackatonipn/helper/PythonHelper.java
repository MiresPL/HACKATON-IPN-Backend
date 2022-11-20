package com.mires.hackatonipn.helper;


import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class PythonHelper {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    public static CompletableFuture<String> getPythonQuestions(final String amount, final String difficulty, final String topic, final String type) {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Request request = new Request.Builder().url("http://localhost:8000/question?tag=" + getTopicAsInt(topic) + "&size=" + amount).build();
        HTTP_CLIENT.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                future.complete("");
            }

            public void onResponse(Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null && response.code() == 200) {
                    future.complete(body.string());
                }
                response.body().close();
            }
        });
        return future;
    }

    private static int getTopicAsInt(final String topic) {
        switch (topic) {
            case "Armia Krajowa":
                return 1;
            case "Powstanie Warszawskie":
                return 2;
            case "Lech Wałęsa":
                return 3;
            case "Akcja Burza":
                return 4;
            case "Cichociemni":
                return 5;
            case "Radio Wolna Europa":
                return 6;
            case "Roman Dmowski":
                return 7;
            case "Sanacja":
                return 8;
            default:
                return 0;
        }
    }
}
