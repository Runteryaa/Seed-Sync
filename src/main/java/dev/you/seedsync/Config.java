package dev.you.seedsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;

public class Config {
    public String serverUrl = "https://YOUR_SERVER_HOST"; // no trailing slash; mod will POST to serverUrl + "/sync"
    public String apiKey = "";   // optional, sent as Authorization: Bearer <apiKey>
    public String userId = "";   // optional identifier (uuid/email hash/etc.)

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Config load(Path path) throws IOException {
        if (!Files.exists(path)) {
            Config def = new Config();
            Files.createDirectories(path.getParent());
            try (Writer w = Files.newBufferedWriter(path)) { GSON.toJson(def, w); }
            return def;
        }
        try (Reader r = Files.newBufferedReader(path)) {
            return GSON.fromJson(r, Config.class);
        }
    }
}
