package io.github.mikip98.savethehotbar.registries;

import io.github.mikip98.savethehotbar.config.ModConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.food.FoodData;

public final class EventRegistry {
    public static void register() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                // TODO: Consider skipping this code when on VANILLA mode to allow other mods to apply their food modifications collision free
                
                // Manage HUNGER & SATURATION
                FoodData oldFoodData = oldPlayer.getFoodData();
                FoodData newFoodData = newPlayer.getFoodData();
                newFoodData.setFoodLevel(ModConfig.INSTANCE.hungerControl.foodLevelCalculationMode.calculate(oldFoodData.getFoodLevel()));
                newFoodData.setSaturation(ModConfig.INSTANCE.hungerControl.saturationLevelCalculationMode.calculate(oldFoodData.getSaturationLevel()));
            }
        });
    }
}
