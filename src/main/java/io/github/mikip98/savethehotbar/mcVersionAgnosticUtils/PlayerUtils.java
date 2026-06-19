package io.github.mikip98.savethehotbar.mcVersionAgnosticUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class PlayerUtils {
    public static void sendMessage(Player player, String message) {
        sendMessage(player, Component.literal(message));
    }
    public static void sendMessage(Player player, Component message) {
        #if MC_VERSION < 260000
        player.displayClientMessage(message, false);
        #else
        player.sendSystemMessage(message);
        #endif
    }
}
