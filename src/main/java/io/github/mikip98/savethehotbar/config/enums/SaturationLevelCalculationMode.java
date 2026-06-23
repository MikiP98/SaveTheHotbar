package io.github.mikip98.savethehotbar.config.enums;

import io.github.mikip98.savethehotbar.config.ModConfig;

public enum SaturationLevelCalculationMode {
    VANILLA(oldValue -> 5),
    KEEP(oldValue -> oldValue),
    CLAMP(oldValue -> Math.max(Math.min(oldValue, ModConfig.INSTANCE.minSaturation), ModConfig.INSTANCE.maxSaturation)),
    FRACTION(oldValue -> oldValue * ModConfig.INSTANCE.saturationFraction),
    FRACTION_AND_CLAMP(oldValue -> CLAMP.calculate(FRACTION.calculate(oldValue))),
    SET(oldValue -> ModConfig.INSTANCE.maxSaturation);

    private final Calculation calculation;

    SaturationLevelCalculationMode(SaturationLevelCalculationMode.Calculation calculation) {
        this.calculation = calculation;
    }

    public float calculate(float oldValue) {
        return this.calculation.calculate(oldValue);
    }

    public interface Calculation {
        float calculate(float oldValue);
    }
}
