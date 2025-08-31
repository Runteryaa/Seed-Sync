package dev.you.seedsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncClient {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void push(Config cfg, List<WorldInfo> worlds) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", cfg.userId);
        body.put("client", "fabric-1.21");
        body.put("worlds", worlds);

        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();

        String json = GSON.toJson(body);
        String url = cfg.serverUrl.endsWith("/") ? cfg.serverUrl + "sync" : cfg.serverUrl + "/sync";

        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (cfg.apiKey != null && !cfg.apiKey.isBlank()) {
            b.header("Authorization", "Bearer " + cfg.apiKey);
        }

        HttpResponse<String> res = client.send(b.build(), HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("Sync failed: " + res.statusCode() + " " + res.body());
        }
    }
}
