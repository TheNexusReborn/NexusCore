package com.thenexusreborn.nexuscore.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thenexusreborn.nexuscore.NexusCore;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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

//        StringBuilder responseBuilder = new StringBuilder();
//        responseBuilder.append("""
//                <head>
//                <title>Nexus Reborn Game Info</title>
//                <style>
//                .infoDiv {
//                  font-family: Arial, Helvetica, sans-serif;
//                  padding-left: 30px;
//                  padding-top: 2px;
//                  padding-bottom: 2px;
//                }
//                .actionDiv {
//                  font-family: Arial, Helvetica, sans-serif;
//                  padding-left: 30px;
//                  padding-top: 2px;
//                  padding-bottom: 2px;
//                }
//                h1 {font-family: Arial, Helvetica, sans-serif'; font-size: 30px}
//                h2 {font-family: Arial, Helvetica, sans-serif; font-size: 20px}
//                </style>
//                </head>
//                """);
//        responseBuilder.append("<img src=\"https://gyazo.com/d8b173ff48af39725507b10f73e362a4.png\" style='display: block; margin-left: auto; margin-right: auto;' width=\"300\" height=\"264.39\"</img>");
//        //responseBuilder.append("<h1>Nexus Reborn Survival Games</h1>");
//        responseBuilder.append("<h1>Classic Game #").append(gameId).append("</h2>");
//
//        JsonObject gameJson = new JsonParser().parse(new FileReader(new File(plugin.getDataFolder(), "export" + File.separator + "games" + File.separator + gameId + ".json"))).getAsJsonObject();
//
//        GameInfo gameInfo = new GameInfo(gameJson);
//
//        responseBuilder.append("<h2>Basic Game Info</h2>");
//        //Game Info
//        responseBuilder.append("<b>ID: </b>").append(gameInfo.getId());
//        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a z");
//
//
//        responseBuilder.append(getInfoLine("Server", gameInfo.getServerName()));
//        responseBuilder.append(getInfoLine("Map", gameInfo.getMapName()));
//        responseBuilder.append(getInfoLine("Start Date", dateFormat.format(gameInfo.getGameStart())));
//        responseBuilder.append(getInfoLine("End Date", dateFormat.format(gameInfo.getGameEnd())));
////        TODO responseBuilder.append("<b>Total Duration: </b>").append();
//
//        responseBuilder.append(getInfoLine("Winner", gameInfo.getWinner()));
//        responseBuilder.append(getInfoLine("First Blood", gameInfo.getFirstBlood()));
//        responseBuilder.append(getInfoLine("Players", StringHelper.join(List.of(gameInfo.getPlayers()), ", ")));
//
//        responseBuilder.append("<h2>Actions</h2>");
        //Actions

//        for (GameAction action : gameInfo.getActions()) {
//            String color = "purple";
//            StringBuilder lineBuilder = new StringBuilder();
//            lineBuilder.append("<div class='actionDiv'><span style='color: white; background: {timecolor}; padding: 3px; border-radius: 5px'>").append(lineSplit[0]).append(" ").append(lineSplit[1]).append("</span>"); //This is the time stamp section
//            if (action.getType().equals("statechange")) {
//                lineBuilder.append(" ").append("Game state was changed to ").append(StringHelper.titlize(action.getValueData().get("nicevalue")));
//                color = "darkslategray";
//            } else if (action.getType().equals("chat") || action.getType().equals("deadchat")) {
//                String player = action.getValueData().get("sender");
//                String message = action.getValueData().get("message");
//                if (action.getType().equals("deadchat")) {
//                    lineBuilder.append(" [Spectators]");
//                    color = "dodgerblue";
//                } else {
//                    color = "green";
//                }
//
//                lineBuilder.append(" <b>").append(player).append(": </b>").append(message);
//            } else if (action.getType().equals("death")) {
//                color = "red";
//            } else if (action.getType().equals("assist")) {
//                color = "orangered";
//            } else if (action.getType().equals("mutation")) {
//                color = "MediumOrchid";
//            }
//        }
//
//        for (int i = 0; i < gameTxt.size(); i++) {
//            String color = "purple";
//            StringBuilder lineBuilder = new StringBuilder();
//            lineBuilder.append("<div class='actionDiv'><span style='color: white; background: {timecolor}; padding: 3px; border-radius: 5px'>").append(lineSplit[0]).append(" ").append(lineSplit[1]).append("</span>"); //This is the time stamp section
//
//            if (lineSplit[2].equals("death")) {
//                color = "red";
//            } else if (lineSplit[2].equals("assist")) {
//                color = "orangered";
//            } else if (lineSplit[2].equals("mutation")) {
//                color = "MediumOrchid";
//            }
//
//            for (int s = 3; s < lineSplit.length; s++) {
//                lineBuilder.append(" ").append(lineSplit[s]);
//            }
//
//            line = lineBuilder.append("</div>").toString().replace("{timecolor}", color);
//
//            responseBuilder.append("    ").append(line).append("\n");
//            if (i < gameTxt.size() - 2) {
//                if (!gameTxt.get(i + 1).startsWith("----")) {
//                    //responseBuilder.append("<br>");
//                }
//            }
//
//            if (i == gameTxt.size() - 1) {
//                responseBuilder.append("</pre>");
//            }
//        }

//        String response = responseBuilder.toString();

        JsonObject gameJson = new JsonParser().parse(new FileReader(new File(plugin.getDataFolder(), "export" + File.separator + "games" + File.separator + gameId + ".json"))).getAsJsonObject();
        
        String response = gameJson.toString();
        
//        httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
//        httpExchange.getResponseHeaders().add("Content-Length", Integer.toString(response.length()));
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
    }

    private String getInfoLine(String prefix, Object value) {
        return "<div class=\"infoDiv\"><b>" + prefix + ":</b>" + value + "</div>";
    }

    private void appendInfoLine(StringBuilder lineBuilder, String prefix, Object value) {
        lineBuilder.append("<div class=\"infoDiv\">").append("<b>").append(prefix).append(":</b>").append(value).append("</div>");
    }

    private void appendTimeToActionLine(StringBuilder lineBuilder, String timeColor, String dateTime) {
        lineBuilder.append("<div class='actionDiv'>").append("<span style='color: white; background: ").append("; padding: 3px; border-radius: 5px'>").append(dateTime).append("</span>");
    }

    private void appendChatValue(String team, String sender, String message) {
        //TODO
    }
}