package io.github.mikip98.savethehotbar.config.io;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceCalculation;
import io.github.mikip98.savethehotbar.config.enums.OverlapResolution;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
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
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "enable");

                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveHotbar");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveArmor");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveSecondHand");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveMainInventory");

                    needsUpdating |= tryLoadEnum(configJson, "overlapResolution", OverlapResolution::valueOf);
                    for (VanillaItemTypes type : VanillaItemTypes.values()) {
                        try {
                            ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(type, configJson.get(type.name().toLowerCase()).getAsBoolean());
                        } catch (Exception e) {
                            needsUpdating = true;
                            printLoadError("vanillaItemTypesKeepingMap", configJson, e);
                        }
                    }

                    needsUpdating |= tryLoadEnum(configJson, "experienceBehaviour", ExperienceMode::valueOf);

                    needsUpdating |= tryLoadEnum(configJson, "experienceCalculationMode", ExperienceCalculation::valueOf);
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsFloat, "experienceFraction");

                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "randomSpread");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "containDrop");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "logDeathCoordinatesInChat");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "logGraveCoordinatesInChat");

                    needsUpdating |= tryLoad(configJson, JsonElement::getAsFloat, "randomDropChance");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsFloat, "rarityDropChanceDecrease");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsFloat, "luckDropChanceDecrease");

                    needsUpdating |= tryLoad(configJson, JsonElement::getAsInt, "sackMaxSpawnRadius");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsInt, "mobGraveMaxSpawnRadius");
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "allowGravesToSpawnOnSlabs");

                    needsUpdating |= tryLoadEnum(configJson, "containDropMode", ContainDropMode::valueOf);

                    // ------------ MOD SUPPORT ------------
                    needsUpdating |= tryLoad(configJson, JsonElement::getAsBoolean, "saveArsenal");
                }

                if (needsUpdating) {
                    LOGGER.info("Updating config file to include new values and fix the broken ones");
                    ConfigSaver.saveConfigToFile();  // Update the config file to include new values
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            ConfigSaver.saveConfigToFile();  // Create the config file
        }
    }

    private static <T> boolean tryLoad(JsonObject configJson, Function<JsonElement, T> getter, String fieldName) {
        try {
            T value = getter.apply(configJson.get(fieldName));
            ModConfig.class.getField(fieldName).set(ModConfig.INSTANCE, value);
        } catch (Exception e) {
            printLoadError(fieldName, configJson, e);
            return true;
        }
        return false;
    }
    private static <V> boolean tryLoadEnum(JsonObject configJson, String fieldName, Function<String, V> setter) {
        try {
            String value = configJson.get(fieldName).getAsString();
            ModConfig.class.getField(fieldName).set(ModConfig.INSTANCE, setter.apply(value));
        } catch (Exception e) {
            printLoadError(fieldName, configJson, e);
            return true;
        }
        return false;
    }
    private static <T> boolean tryLoadViaSetter(JsonObject configJson, Function<JsonElement, T> getter, String fieldName, Class<T> clazz) {
        final String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            T value = getter.apply(configJson.get(fieldName));
            ModConfig.class.getMethod(setterName, clazz).invoke(ModConfig.INSTANCE, value);
        } catch (Exception e) {
            printLoadError(fieldName, configJson, e);
            return true;
        }
        return false;
    }

    private static void printLoadError(String fieldName, JsonObject configJson, Exception e) {
        LOGGER.error(
                "Failed to load '{}' from config file: {}\n\tError: {}\n\tStacktrace: {}",
                fieldName, configJson, e.getMessage(), e.getStackTrace()
        );
    }
}
