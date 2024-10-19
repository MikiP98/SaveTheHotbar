package io.github.mikip98.savethehotbar.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

        configJson.addProperty("saveHotbar", ModConfig.saveHotbar);
        configJson.addProperty("saveArmor", ModConfig.saveArmor);
        configJson.addProperty("saveSecondHand", ModConfig.saveSecondHand);
        configJson.addProperty("randomSpread", ModConfig.randomSpread);
        configJson.addProperty("containDrop", ModConfig.containDrop);
        configJson.addProperty("logDeathCoordinatesInChat", ModConfig.logDeathCoordinatesInChat);
        configJson.addProperty("logGraveCoordinatesInChat", ModConfig.logGraveCoordinatesInChat);

        configJson.addProperty("randomDropChance", ModConfig.randomDropChance);
        configJson.addProperty("rarityDropChanceDecrease", ModConfig.rarityDropChanceDecrease);

        configJson.addProperty("containDropMode", ModConfig.containDropMode.toString());

        configJson.addProperty("mobGraveMaxSpawnRadius", ModConfig.mobGraveMaxSpawnRadius);

        // Save the JSON object to a file
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
