package io.github.mikip98.savethehotbar.registries.itemTypeRegistry;

import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import io.github.mikip98.savethehotbar.content.tags.ModItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.Property;

import java.util.*;

public class ItemTypesConfiguration {
    public static Map<VanillaItemTypes, ItemTypeConfig> vanillaItemTypes = new EnumMap<>(VanillaItemTypes.class);
    static {
        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            vanillaItemTypes.put(type, new ItemTypeConfig(ModItemTags.vanillaItemTypesTagOverridesMap.get(type)));
        }
    }

    public static void registerConfiguration() {
        registerVanillaConfiguration();
        registerModdedConfiguration();
    }

    protected static void registerVanillaConfiguration() {
        vanillaItemTypes.get(VanillaItemTypes.TOOL)
                .addClasses(ToolItem.class)
                .addTags(ItemTags.TOOLS);

        vanillaItemTypes.get(VanillaItemTypes.WEAPON)
                .addClasses(SwordItem.class, AxeItem.class, RangedWeaponItem.class, TridentItem.class)
                .addTags(ItemTags.SWORDS, ItemTags.AXES);

        vanillaItemTypes.get(VanillaItemTypes.AMMUNITION)
                .addClasses(ArrowItem.class)
                .addTags(ItemTags.ARROWS);

        vanillaItemTypes.get(VanillaItemTypes.ARMOUR).addClasses(ArmorItem.class);
        vanillaItemTypes.get(VanillaItemTypes.EQUIPMENT).addClasses(Equipment.class);

        vanillaItemTypes.get(VanillaItemTypes.FOOD).addPredicate(Item::isFood);
        vanillaItemTypes.get(VanillaItemTypes.POTION).addClasses(PotionItem.class);

        vanillaItemTypes.get(VanillaItemTypes.LIGHT_SOURCE_ON)
                .addPredicate(item -> {
                    if (item instanceof BlockItem blockItem)
                        return blockItem.getBlock().getDefaultState().getLuminance() > 0;
                    return false;
                });
        vanillaItemTypes.get(VanillaItemTypes.POSSIBLE_LIGHT_SOURCE)
                .addPredicate(item -> {
                    if (item instanceof BlockItem blockItem) {
                        final BlockState blockState = blockItem.getBlock().getDefaultState();
                        return hasLuminantCombination(blockState, new ArrayList<>(blockState.getProperties()), 0);
                    }
                    return false;
                });

        // Validate that all Item Types have valid configurations (a.k.a. I haven't forgotten anything)
        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            ItemTypeConfig config = vanillaItemTypes.get(type);
            if (type != VanillaItemTypes.OTHER && config.getClasses().isEmpty() && config.getTags().isEmpty() && config.getPredicate() == null) {
                throw new IllegalStateException("Not all Item Types have a valid configuration");
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static boolean hasLuminantCombination(BlockState base, List<Property<?>> properties, int index) {
        if (index >= properties.size()) {
            return base.getLuminance() > 0;
        }

        Property property = properties.get(index);
        for (Object value : property.getValues()) {
            BlockState modified = base.with(property, (Comparable) value);
            if (hasLuminantCombination(modified, properties, index + 1)) {
                return true;
            }
        }
        return false;
    }

    protected static void registerModdedConfiguration() {}
}
