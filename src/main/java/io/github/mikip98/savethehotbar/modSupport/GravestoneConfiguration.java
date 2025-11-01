package io.github.mikip98.savethehotbar.modSupport;

import net.minecraft.server.MinecraftServer;
import net.pneumono.gravestones.GravestonesConfig;
import net.pneumono.pneumonocore.config_api.configurations.ConfigManager;
import net.pneumono.pneumonocore.config_api.enums.LoadType;

import java.util.function.Consumer;

public class GravestoneConfiguration {
    public static void gravestoneCheck(Consumer<String> printer, MinecraftServer server) {
        if (!GravestonesConfig.SPAWN_GRAVESTONES_WITH_KEEPINV.getValue()) {
            printer.accept(
                    "Gravestones mod configuration prevents grave spawning with 'keepInventory' set to 'True'; Changing Gravestones's config to allow graves to spawn; If you want to disable 'SaveTheHotbar!', disable it in settings"
            );
            ConfigManager.setValue(GravestonesConfig.SPAWN_GRAVESTONES_WITH_KEEPINV, true, LoadType.INSTANT, server);
        }
    }
}
