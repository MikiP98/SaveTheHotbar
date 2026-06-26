package io.github.mikip98.savethehotbar.config.enums;

public enum ItemTypes {
    TOOL,
    WEAPON,
    AMMUNITION,

    ARMOUR,
    #if MC_VERSION < 12104 EQUIPMENT, #endif

    FOOD,
    POTION,

    LIGHT_SOURCE_ON,
    POSSIBLE_LIGHT_SOURCE,

    OTHER
}
