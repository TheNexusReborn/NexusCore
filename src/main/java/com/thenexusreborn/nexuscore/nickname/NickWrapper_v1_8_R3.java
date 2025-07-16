package com.thenexusreborn.nexuscore.nickname;

import com.mojang.authlib.properties.Property;
import com.stardevllc.starmclib.skin.Skin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NickWrapper_v1_8_R3 {
    private final MinecraftServer minecraftServer;
    private final EnumDifficulty difficulty;

    private final PacketPlayOutPlayerInfo.EnumPlayerInfoAction action_remove = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER;
    private final PacketPlayOutPlayerInfo.EnumPlayerInfoAction action_add = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER;

    private final WorldType worldType = WorldType.types[0];

    public NickWrapper_v1_8_R3() {
        this.minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        this.difficulty = minecraftServer.getDifficulty();
    }

    public void refreshOthers(JavaPlugin plugin, Player player) {
        List<Player> canSee = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.canSee(player)) {
                canSee.add(p);
                p.hidePlayer(player);
            }
        }

        for (Player p : canSee) {
            p.showPlayer(player);
        }
    }

    public void setProfileName(Player player, String name) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        try {
            Field nameField = craftPlayer.getProfile().getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(craftPlayer.getProfile(), name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setSkinProperties(Player player, Skin skin) {
        if (skin != null) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getProfile().getProperties().clear();
            craftPlayer.getProfile().getProperties().put("0", new Property("textures", skin.getValue(), skin.getSignature()));
        }
    }

    public void refreshSelf(JavaPlugin plugin, Player nicked) {
        CraftPlayer craftPlayer = (CraftPlayer) nicked;
        PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(action_remove, craftPlayer.getHandle());
        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(action_add, craftPlayer.getHandle());
        PacketPlayOutRespawn respawnPlayer = new PacketPlayOutRespawn(0, difficulty, worldType, craftPlayer.getHandle().playerInteractManager.getGameMode());

        PlayerConnection connection = craftPlayer.getHandle().playerConnection;
        connection.sendPacket(removePlayer);

        new BukkitRunnable() {
            public void run() {
                boolean flying = nicked.isFlying();
                boolean allowFlight = nicked.getAllowFlight();
                Location location = nicked.getLocation();
                int level = nicked.getLevel();
                float xp = nicked.getExp();
                double health = nicked.getHealth();

                connection.sendPacket(respawnPlayer);

                nicked.setFlying(flying);
                nicked.setAllowFlight(allowFlight);
                nicked.teleport(location);
                nicked.updateInventory();
                nicked.setLevel(level);
                nicked.setExp(xp);
                nicked.setHealth(health);

                connection.sendPacket(addPlayer);
            }
        }.runTaskLater(plugin, 2L);
    }

    public void setNick(JavaPlugin plugin, Player player, String name, Skin skin) {
        setProfileName(player.getPlayer(), name);
        setSkinProperties(player.getPlayer(), skin);
        refreshOthers(plugin, player.getPlayer());
        refreshSelf(plugin, player.getPlayer());
    }
}
