package io.github.mikip98.savethehotbar.modDetection;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class ModDetector {

    public static void detectMods() {
        DetectedMods.PNEUMONO_CORE = isModPresent("pneumonocore", "pneumonocore-");
        DetectedMods.PNEUMONO_GRAVESTONES = isModPresent("gravestones", "gravestones-", new String[]{"gravestones-v"}) && DetectedMods.PNEUMONO_CORE;
    }

    public static boolean isModPresent(String modId, String jarNamePart) { return isModPresent(modId, jarNamePart, new String[0]); }
    public static boolean isModPresent(String modId, String jarNamePart, String[] jarNameBlacklist) {
        boolean modFound = FabricLoader.getInstance().isModLoaded(modId);

        if (!modFound) {
            File[] files = new File(FabricLoader.getInstance().getGameDir().toFile(), "mods").listFiles();

            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (fileName.contains(jarNamePart)) {
                        if (fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
                            boolean blacklisted = false;
                            for (String blacklistedPhrase : jarNameBlacklist) {
                                if (fileName.contains(blacklistedPhrase)) {
                                    blacklisted = true;
                                    break;
                                }
                            }
                            if (!blacklisted) {
                                modFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return modFound;
    }
}
