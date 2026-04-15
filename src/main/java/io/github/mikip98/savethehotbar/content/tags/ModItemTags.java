package io.github.mikip98.savethehotbar.content.tags;

import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;

import java.util.EnumMap;
import java.util.Map;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.getId;

public class ModItemTags {
    public static Map<VanillaItemTypes, TagKey<Item>> vanillaItemTypesTagOverridesMap = new EnumMap<>(VanillaItemTypes.class);
    static {
        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            if (type == VanillaItemTypes.OTHER) continue;
            vanillaItemTypesTagOverridesMap.put(type,
                    TagKey.create(Registries.ITEM, getId("material_type_" + type.name().toLowerCase() + "_override"))
            );
        }
    }
}
