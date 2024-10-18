package io.github.mikip98.savethehotbar.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.mikip98.savethehotbar.enums.ContainDropMode;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class ConfigReader {
    // Load the configuration from the JSON file in the Minecraft configuration folder
    public static void loadConfigFromFile() {
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        File configFile = new File(configDir, "saveTheHotbar!.json");

        if (configFile.exists()) {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject configJson = gson.fromJson(reader, JsonObject.class);

                boolean needsUpdating = false;
                if (configJson != null) {
                    // Load the static fields from the JSON object
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveHotbar");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveArmor");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveSecondHand");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "randomSpread");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "containDrop");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "logDeathCoordinatesInChat");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "logGraveCoordinatesInChat");

                    needsUpdating |= tryLoad(configJson, JsonElement::getAsFloat, "randomDropChance");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsFloat, "rarityDropChanceDecrease");

//                    needsUpdating |= tryLoad(configJson, JsonElement::getAsString, "containDropMode");
                    String mode = configJson.get("containDropMode").getAsString();
                    try {
                        ModConfig.containDropMode = ContainDropMode.valueOf(mode);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Invalid containDropMode value: {}, setting to default: {}", mode, DefaultConfig.dContainDropMode);
                        needsUpdating = true;
                    }
                }

                if (needsUpdating) {
                    LOGGER.info("Updating config file to include new values and fix the broken ones");
                    ConfigSaver.saveConfigToFile();  // Update the config file to include new values
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        else {
//            saveConfigToFile();  // Create the config file
//        }
    }
    private static <T> boolean tryLoad(JsonObject configJson, Function<JsonElement, T> getter, String fieldName) {
        try {
            T value = getter.apply(configJson.get(fieldName));
            ModConfig.class.getField(fieldName).set(ModConfig.class, value);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }
}
