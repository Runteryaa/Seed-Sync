package dev.you.seedsync;

public class WorldInfo {
    public String worldName;
    public String folderName;
    public String versionName;
    public Long seed;        // may be null if not found
    public String createdAt; // ISO-8601
    public String lastPlayed;// ISO-8601

    public WorldInfo() {}
    public WorldInfo(String worldName, String folderName, String versionName, Long seed, String createdAt, String lastPlayed) {
        this.worldName = worldName;
        this.folderName = folderName;
        this.versionName = versionName;
        this.seed = seed;
        this.createdAt = createdAt;
        this.lastPlayed = lastPlayed;
    }
}
