package io.github.mikip98.savethehotbar.config.enums;

import io.github.mikip98.savethehotbar.config.ModConfig;

public enum FoodLevelCalculationMode {
    VANILLA(oldValue -> 20),
    KEEP(oldValue -> oldValue),
    CLAMP(oldValue -> Math.clamp(oldValue, ModConfig.INSTANCE.hungerControl.minFoodLevel, ModConfig.INSTANCE.hungerControl.maxFoodLevel)),
    FRACTION(oldValue -> Math.round(oldValue * ModConfig.INSTANCE.hungerControl.foodLevelFraction)),
    FRACTION_AND_CLAMP(oldValue -> CLAMP.calculate(FRACTION.calculate(oldValue))),
    SET(oldValue -> ModConfig.INSTANCE.hungerControl.maxFoodLevel);

    private final Calculation calculation;

    FoodLevelCalculationMode(FoodLevelCalculationMode.Calculation calculation) {
        this.calculation = calculation;
    }

    public int calculate(int oldValue) {
        return this.calculation.calculate(oldValue);
    }

    public interface Calculation {
        int calculate(int oldValue);
    }
}
