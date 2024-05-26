package com.thenexusreborn.nexuscore.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stardevllc.starlib.helper.StringHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.nexuscore.NexusCore;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        responseBuilder.append("<img src=\"https://gyazo.com/d8b173ff48af39725507b10f73e362a4.png\" style='display: block; margin-left: auto; margin-right: auto;' width=\"300\" height=\"264.39\"</img>");
        //responseBuilder.append("<h1>Nexus Reborn Survival Games</h1>");
        responseBuilder.append("<h1>Classic Game #").append(gameId).append("</h2>");

        List<String> gameTxt = NexusAPI.getApi().getGameLogExporter().getGameTxt(gameId);
        boolean gameInfo = false, actions = false;
        for (int i = 0; i < gameTxt.size(); i++) {
            String line = gameTxt.get(i);
            if (line.equals(" ")) {
                continue;
            }
            if (line.startsWith("----")) {
                responseBuilder.append("<h2>").append(line.replace("----", "")).append("</h3>");
                if (line.contains("Basic Game Info")) {
                    gameInfo = true;
                    actions = false;
                } else if (line.contains("Actions")) {
                    actions = true;
                    gameInfo = false;
                }
            } else {
                if (gameInfo) {
                    String[] lineSplit = line.split(":");
                    StringBuilder lineBuilder = new StringBuilder();
                    lineBuilder.append("<div class=\"infoDiv\">").append("<b>").append(lineSplit[0]).append(":</b>");
                    for (int s = 1; s < lineSplit.length; s++) {
                        lineBuilder.append(lineSplit[s]);
                        if (s < lineSplit.length - 1) {
                            lineBuilder.append(":");
                        }
                    }
                    line = lineBuilder.append("</div>").toString();
                } else if (actions) {
                    String color = "purple";
                    String[] lineSplit = line.split(" ");
                    StringBuilder lineBuilder = new StringBuilder();
                    lineBuilder.append("<div class='actionDiv'>").append("<span style='color: white; background: {timecolor}; padding: 3px; border-radius: 5px'>").append(lineSplit[0]).append(" ").append(lineSplit[1]).append("</span>"); //This is the time stamp section
                    if (lineSplit[2].equals("statechange")) {
                        lineBuilder.append(" ").append("Game state was changed to ").append(StringHelper.titlize(lineSplit[3]));
                        color = "darkslategray";
                    } else if (lineSplit[2].equals("chat") || lineSplit[2].equals("deadchat")) {
                        String[] firstArgSplit = lineSplit[3].split(":");
                        String player = firstArgSplit[0];
                        StringBuilder msgBuilder = new StringBuilder();
                        msgBuilder.append(firstArgSplit[1]).append(" ");
                        for (int s = 4; s < lineSplit.length; s++) {
                            msgBuilder.append(lineSplit[s]).append(" ");
                        }
                        if (lineSplit[2].equals("deadchat")) {
                            lineBuilder.append(" [Spectators]");
                            color = "dodgerblue";
                        } else {
                            color = "green";
                        }
                        lineBuilder.append(" <b>").append(player).append(": </b>").append(msgBuilder);
                    }  else {
                        if (lineSplit[2].equals("death")) {
                            color = "red";
                        } else if (lineSplit[2].equals("assist")) {
                            color = "orangered";
                        } else if (lineSplit[2].equals("mutation")) {
                            color = "MediumOrchid";
                        }
                        
                        for (int s = 3; s < lineSplit.length; s++) {
                            lineBuilder.append(" ").append(lineSplit[s]);
                        }
                    }
                    
                    line = lineBuilder.append("</div>").toString().replace("{timecolor}", color);
                }
                
                responseBuilder.append("    ").append(line).append("\n");
                if (i < gameTxt.size() - 2) {
                    if (!gameTxt.get(i + 1).startsWith("----")) {
                        //responseBuilder.append("<br>");
                    }
                }
            }

            if (i == gameTxt.size() - 1) {
                responseBuilder.append("</pre>");
            }
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
}