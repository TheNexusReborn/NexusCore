package com.thenexusreborn.api.gamearchive;

import com.google.gson.*;
import com.thenexusreborn.api.NexusAPI;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameLogManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private File baseDir;

    public GameLogManager(File baseDir) {
        this.baseDir = baseDir;
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }
    
    public List<GameInfo> importGames() throws FileNotFoundException {
        List<GameInfo> gameInfos = new ArrayList<>();
        
        for (File file : baseDir.listFiles()) {
            JsonObject jsonObject = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
            gameInfos.add(new GameInfo(jsonObject));
        }
        
        return gameInfos;
    }

    public void exportGames() throws SQLException, IOException {
        List<GameInfo> gameInfos = NexusAPI.getApi().getPrimaryDatabase().get(GameInfo.class);

        for (GameInfo gameInfo : gameInfos) {
            exportGameInfo(gameInfo);
        }
    }

    public void exportGameInfo(GameInfo gameInfo) throws IOException {
        JsonObject gameJson = gameInfo.toJson();

        File jsonFile = new File(baseDir, gameInfo.getId() + ".json");
        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
        }

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(GSON.toJson(gameJson));
        }
    }
}
