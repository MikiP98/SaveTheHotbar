package io.github.mikip98.savethehotbar.config.io;

import com.google.gson.*;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
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

        configJson.addProperty("enable", ModConfig.INSTANCE.enable);

        configJson.addProperty("itemKeepingLogicOperator", ModConfig.INSTANCE.itemKeepingLogicOperator.toString());

        configJson.addProperty("saveHotbar", ModConfig.INSTANCE.saveHotbar);
        configJson.addProperty("saveArmor", ModConfig.INSTANCE.saveArmor);
        configJson.addProperty("saveSecondHand", ModConfig.INSTANCE.saveSecondHand);
        configJson.addProperty("saveMainInventory", ModConfig.INSTANCE.saveMainInventory);

        configJson.addProperty("overlapResolution", ModConfig.INSTANCE.overlapResolution.name());
        for (VanillaItemTypes itemType : VanillaItemTypes.values()) {
            configJson.addProperty(itemType.name().toLowerCase(), ModConfig.INSTANCE.vanillaItemTypesKeepingMap.get(itemType));
        }

        configJson.addProperty("experienceBehaviour", ModConfig.INSTANCE.experienceBehaviour.toString());

        configJson.addProperty("experienceCalculationMode", ModConfig.INSTANCE.experienceCalculationMode.toString());
        configJson.addProperty("experienceFraction", ModConfig.INSTANCE.experienceFraction);

        configJson.addProperty("randomSpread", ModConfig.INSTANCE.randomSpread);
        configJson.addProperty("containDrop", ModConfig.INSTANCE.containDrop);
        configJson.addProperty("logDeathCoordinatesInChat", ModConfig.INSTANCE.logDeathCoordinatesInChat);
        configJson.addProperty("logGraveCoordinatesInChat", ModConfig.INSTANCE.logGraveCoordinatesInChat);

        configJson.addProperty("randomDropChance", ModConfig.INSTANCE.randomDropChance);
        configJson.addProperty("rarityDropChanceDecrease", ModConfig.INSTANCE.rarityDropChanceDecrease);
        configJson.addProperty("luckDropChanceDecrease", ModConfig.INSTANCE.luckDropChanceDecrease);

        configJson.addProperty("containDropMode", ModConfig.INSTANCE.containDropMode.toString());

        configJson.addProperty("sackMaxSpawnRadius", ModConfig.INSTANCE.sackMaxSpawnRadius);
        configJson.addProperty("mobGraveMaxSpawnRadius", ModConfig.INSTANCE.mobGraveMaxSpawnRadius);
        configJson.addProperty("allowGravesToSpawnOnSlabs", ModConfig.INSTANCE.allowGravesToSpawnOnSlabs);

        // ------------ MOD SUPPORT ------------
        configJson.addProperty("saveArsenal", ModConfig.INSTANCE.saveArsenal);

        // Save the JSON object to a file
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
