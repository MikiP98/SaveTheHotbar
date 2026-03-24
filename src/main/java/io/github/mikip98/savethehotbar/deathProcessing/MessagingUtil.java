package io.github.mikip98.savethehotbar.deathProcessing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class MessagingUtil {
    public static void sendMessage(PlayerEntity player, String message) {
        sendMessage(player, Text.literal(message));
    }
    public static void sendMessage(PlayerEntity player, Text message) {
        #if MC_VERSION < 12104
        player.sendMessage(message);
        #else
        player.sendMessage(message, false);
        #endif
    }
}
