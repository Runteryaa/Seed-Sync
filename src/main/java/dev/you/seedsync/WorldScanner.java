package dev.you.seedsync;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WorldScanner {
    private final Path savesDir;

    public WorldScanner(Path savesDir) { this.savesDir = savesDir; }

    public List<WorldInfo> scanAll() throws IOException {
        List<WorldInfo> out = new ArrayList<>();
        if (!Files.isDirectory(savesDir)) return out;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(savesDir)) {
            for (Path worldDir : stream) {
                if (!Files.isDirectory(worldDir)) continue;
                Path levelDat = worldDir.resolve("level.dat");
                if (!Files.exists(levelDat)) continue;

                try {
                    NbtCompound root = NbtIo.readCompressed(levelDat);
                    NbtCompound data = root.getCompound("Data");

                    String levelName = data.contains("LevelName") ? data.getString("LevelName")
                            : worldDir.getFileName().toString();
                    String versionName = data.contains("Version") ? data.getCompound("Version").getString("Name") : "";
                    Long seed = tryGetSeed(data);
                    String lastPlayedIso = data.contains("LastPlayed")
                            ? toIso(Instant.ofEpochMilli(data.getLong("LastPlayed")))
                            : null;

                    BasicFileAttributes attr = Files.readAttributes(worldDir, BasicFileAttributes.class);
                    String createdIso = toIso(attr.creationTime().toInstant());

                    out.add(new WorldInfo(levelName, worldDir.getFileName().toString(), versionName, seed, createdIso, lastPlayedIso));
                } catch (Exception e) {
                    out.add(new WorldInfo(worldDir.getFileName().toString(), worldDir.getFileName().toString(), "", null, null, null));
                }
            }
        }
        return out;
    }

    private static Long tryGetSeed(NbtCompound data) {
        // Modern: Data.WorldGenSettings.seed
        if (data.contains("WorldGenSettings")) {
            NbtCompound wgs = data.getCompound("WorldGenSettings");
            if (wgs.contains("seed")) {
                try { return wgs.getLong("seed"); } catch (Exception ignored) {}
                try { return Long.parseLong(wgs.getString("seed")); } catch (Exception ignored) {}
            }
        }
        // Older fallback: Data.RandomSeed
        if (data.contains("RandomSeed")) {
            try { return data.getLong("RandomSeed"); } catch (Exception ignored) {}
        }
        return null;
    }

    private static String toIso(Instant i) {
        if (i == null) return null;
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(i);
    }
}
