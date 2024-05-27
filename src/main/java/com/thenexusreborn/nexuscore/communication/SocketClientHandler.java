package com.thenexusreborn.nexuscore.communication;

import com.stardevllc.starlib.misc.CodeGenerator;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.discord.DiscordVerifyCode;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientHandler extends BukkitRunnable {
    
    private NexusCore plugin;
    private Socket socket;
    
    private PrintWriter out;
    private BufferedReader in;

    public SocketClientHandler(NexusCore plugin, Socket socket) throws IOException {
        this.plugin = plugin;
        this.socket = socket;
        
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        plugin.getLogger().info("Connected to " + socket.getRemoteSocketAddress().toString());
    }
    
    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("exit")) {
                    break;
                } else if (inputLine.equals("heartbeat")) {
                    //Do nothing, just to keep the connection running.
                } else if (inputLine.startsWith("link")) {
                    String[] inputSplit = inputLine.split(" ");
                    String discordId = inputSplit[1];

                    DiscordVerifyCode discordVerifyCode = new DiscordVerifyCode(discordId, CodeGenerator.generate(16));
                    plugin.getDiscordVerifyCodes().add(discordVerifyCode);
                    out.println("linkcode " + discordId + " " + discordVerifyCode.getCode());
                    out.flush();
                }
            }
        } catch (Exception e) {
            if (!e.getMessage().contains("Connection reset")) {
                e.printStackTrace();
            }
        } finally {
            try {
                plugin.getClientHandlers().remove(this);
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
