package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.nexuscore.reflection.impl.ActionBar;
import com.thenexusreborn.nexuscore.reflection.impl.PlayerSkull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SpigotUtils {

    private static final ActionBar ACTION_BAR = new ActionBar();
    private static final PlayerSkull PLAYER_SKULL = new PlayerSkull();

    public static void sendActionBar(Player player, String text) {
        ACTION_BAR.send(player, text);
    }

    public static ItemStack getPlayerSkull(Player player) {
        return PLAYER_SKULL.getSkull(player);
    }
}
