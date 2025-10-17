package io.github.mikip98.savethehotbar.config.io;

import com.google.gson.*;
import io.github.mikip98.savethehotbar.config.ModConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigSaver {

    // Save the configuration to a JSON file in the Minecraft configuration folder
    public static void saveConfigToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        File configFile = new File(configDir, "saveTheHotbar!.json");

        // Create a JSON object to store the configuration
        JsonObject configJson = new JsonObject();

        configJson.addProperty("enabled", true);

        configJson.addProperty("saveHotbar", ModConfig.saveHotbar);
        configJson.addProperty("saveArmor", ModConfig.saveArmor);
        configJson.addProperty("saveSecondHand", ModConfig.saveSecondHand);

        configJson.addProperty("experienceBehaviour", ModConfig.experienceBehaviour.toString());

        configJson.addProperty("experienceCalculationMode", ModConfig.experienceCalculationMode.toString());
        configJson.addProperty("experienceFraction", ModConfig.experienceFraction);

        configJson.addProperty("randomSpread", ModConfig.randomSpread);
        configJson.addProperty("containDrop", ModConfig.containDrop);
        configJson.addProperty("logDeathCoordinatesInChat", ModConfig.logDeathCoordinatesInChat);
        configJson.addProperty("logGraveCoordinatesInChat", ModConfig.logGraveCoordinatesInChat);

        configJson.addProperty("randomDropChance", ModConfig.randomDropChance);
        configJson.addProperty("rarityDropChanceDecrease", ModConfig.rarityDropChanceDecrease);
        configJson.addProperty("luckDropChanceDecrease", ModConfig.luckDropChanceDecrease);

        configJson.addProperty("containDropMode", ModConfig.containDropMode.toString());

        configJson.addProperty("sackMaxSpawnRadius", ModConfig.sackMaxSpawnRadius);
        configJson.addProperty("mobGraveMaxSpawnRadius", ModConfig.mobGraveMaxSpawnRadius);
        configJson.addProperty("allowGravesToSpawnOnSlabs", ModConfig.allowGravesToSpawnOnSlabs);

        // ------------ MOD SUPPORT ------------
        configJson.addProperty("saveArsenal", ModConfig.saveArsenal);

        // Save the JSON object to a file
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
