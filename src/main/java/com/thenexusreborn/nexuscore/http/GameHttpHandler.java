package com.thenexusreborn.nexuscore.http;

import com.google.gson.*;
import com.stardevllc.helper.StringHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thenexusreborn.api.gamearchive.*;
import com.thenexusreborn.nexuscore.NexusCore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameHttpHandler implements HttpHandler {

    private NexusCore plugin;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public GameHttpHandler(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String url = httpExchange.getRequestURI().toString();
        String[] mainURLSplit = url.split("\\?");
        String rawParameters = mainURLSplit[1];

        String[] parameters = rawParameters.split(",");

        int gameId = -1;

        for (String parameter : parameters) {
            String[] parameterSplit = parameter.split("=");
            if (parameterSplit[0].equals("id")) {
                gameId = Integer.parseInt(parameterSplit[1]);
            }
        }

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("""
                <head>
                <title>Nexus Reborn Game Info</title>
                <style>
                .infoDiv {
                  font-family: Arial, Helvetica, sans-serif;
                  padding-left: 30px;
                  padding-top: 2px;
                  padding-bottom: 2px;
                }
                .actionDiv {
                  font-family: Arial, Helvetica, sans-serif;
                  padding-left: 30px;
                  padding-top: 2px;
                  padding-bottom: 2px;
                }
                h1 {font-family: Arial, Helvetica, sans-serif'; font-size: 30px}
                h2 {font-family: Arial, Helvetica, sans-serif; font-size: 20px}
                </style>
                </head>
                """);
        responseBuilder.append("<img src=\"https://gyazo.com/a8b53a677cab6f42486d585d05f69d80.png\" style='display: block; margin-left: auto; margin-right: auto;' width=\"700\" height=\"257\"</img>");
        //responseBuilder.append("<h1>Nexus Reborn Survival Games</h1>");
        responseBuilder.append("<h1>Survival Games Classic #").append(gameId).append("</h2>");

        JsonObject gameJson = new JsonParser().parse(new FileReader(new File(plugin.getDataFolder(), "export" + File.separator + "games" + File.separator + gameId + ".json"))).getAsJsonObject();

        GameInfo gameInfo = new GameInfo(gameJson);

        responseBuilder.append("<h2>Basic Game Info</h2>");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a z");

        responseBuilder.append(getInfoLine("Server", gameInfo.getServerName()));
        responseBuilder.append(getInfoLine("Map", gameInfo.getMapName()));
        responseBuilder.append(getInfoLine("Start Date", dateFormat.format(gameInfo.getGameStart())));
        responseBuilder.append(getInfoLine("End Date", dateFormat.format(gameInfo.getGameEnd())));
//        TODO responseBuilder.append("<b>Total Duration: </b>").append();
        
        Set<PlayerInfo> players = gameInfo.getPlayers();
        
        String winner = "";
        String firstBlood = "";
        List<String> playerNames = new ArrayList<>();
        
        for (PlayerInfo player : players) {
            if (gameInfo.getWinner().equalsIgnoreCase(player.getName()) || gameInfo.getWinner().equalsIgnoreCase(player.getUniqueId().toString())) {
                winner = player.getName();
            } 
            
            if (gameInfo.getFirstBlood().equalsIgnoreCase(player.getName()) || gameInfo.getFirstBlood().equalsIgnoreCase(player.getUniqueId().toString())) {
                firstBlood = player.getName();
            }
            
            playerNames.add(player.getName());
        }
        
        responseBuilder.append(getInfoLine("Winner", winner));
        responseBuilder.append(getInfoLine("First Blood", firstBlood));
        responseBuilder.append(getInfoLine("Players", StringHelper.join(playerNames, ", ")));

        responseBuilder.append("<h2>Game Log</h2>");

        for (GameAction action : gameInfo.getActions()) {
            String color = "purple";
            StringBuilder lineBuilder = new StringBuilder();
            lineBuilder.append("<div class='actionDiv'><span style='color: white; background: {timecolor}; padding: 3px; border-radius: 5px'>").append(dateFormat.format(action.getTimestamp())).append("</span>"); //This is the time stamp section
            Map<String, String> valueData = action.getValueData();
            if (action.getType().equals("statechange")) {
                lineBuilder.append(" ").append("Game state was changed to ").append(StringHelper.titlize(valueData.get("newvalue")));
                color = "darkslategray";
            } else if (action.getType().equals("chat")) {
                String player = valueData.get("sender");
                String message = valueData.get("message");
                if (valueData.get("chatroom").contains("spectators")) {
                    lineBuilder.append(" [Spectators]");
                    color = "dodgerblue";
                } else {
                    color = "green";
                }

                lineBuilder.append(" <b>").append(player).append(": </b>").append(message);
            } else if (action.getType().equals("death")) {
                color = "red";
                lineBuilder.append(" ").append(valueData.get("message"));
            } else if (action.getType().equals("mutate")) {
                color = "MediumOrchid";
                lineBuilder.append(" ").append(valueData.get("mutator")).append(" mutated on ").append(valueData.get("target")).append(" as a(n) ").append(StringHelper.titlize(valueData.get("type")));
            }
            
            String line = lineBuilder.append("</div>").toString().replace("{timecolor}", color);
            responseBuilder.append("    ").append(line).append("\n");
        }

        String response = responseBuilder.toString();
        
        httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        httpExchange.getResponseHeaders().add("Content-Length", Integer.toString(response.length()));
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
    }

    private String getInfoLine(String prefix, Object value) {
        return "<div class=\"infoDiv\"><b>" + prefix + ":</b> " + value + "</div>";
    }

    private void appendInfoLine(StringBuilder lineBuilder, String prefix, Object value) {
        lineBuilder.append("<div class=\"infoDiv\">").append("<b>").append(prefix).append(": </b>").append(value).append("</div>");
    }

    private void appendTimeToActionLine(StringBuilder lineBuilder, String timeColor, String dateTime) {
        lineBuilder.append("<div class='actionDiv'>").append("<span style='color: white; background: ").append("; padding: 3px; border-radius: 5px'>").append(dateTime).append("</span>");
    }

    private void appendChatValue(String team, String sender, String message) {
        //TODO
    }
}