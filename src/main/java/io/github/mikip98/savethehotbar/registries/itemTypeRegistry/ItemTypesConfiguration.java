package io.github.mikip98.savethehotbar.registries.itemTypeRegistry;

import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import io.github.mikip98.savethehotbar.content.tags.ModItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.state.properties.Property;

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
                .addClasses(TieredItem.class)
                .addTags(ItemTags.TOOLS);

        vanillaItemTypes.get(VanillaItemTypes.WEAPON)
                .addClasses(SwordItem.class, AxeItem.class, ProjectileWeaponItem.class, TridentItem.class)
                .addTags(ItemTags.SWORDS, ItemTags.AXES);

        vanillaItemTypes.get(VanillaItemTypes.AMMUNITION)
                .addClasses(ArrowItem.class)
                .addTags(ItemTags.ARROWS);

        vanillaItemTypes.get(VanillaItemTypes.ARMOUR).addClasses(ArmorItem.class);
        vanillaItemTypes.get(VanillaItemTypes.EQUIPMENT).addClasses(Equipable.class);

        vanillaItemTypes.get(VanillaItemTypes.FOOD).addPredicate(Item::isEdible);
        vanillaItemTypes.get(VanillaItemTypes.POTION).addClasses(PotionItem.class);

        vanillaItemTypes.get(VanillaItemTypes.LIGHT_SOURCE_ON)
                .addPredicate(item -> {
                    if (item instanceof BlockItem blockItem)
                        return blockItem.getBlock().defaultBlockState().getLightEmission() > 0;
                    return false;
                });
        vanillaItemTypes.get(VanillaItemTypes.POSSIBLE_LIGHT_SOURCE)
                .addPredicate(item -> {
                    if (item instanceof BlockItem blockItem) {
                        final BlockState blockState = blockItem.getBlock().defaultBlockState();
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
            return base.getLightEmission() > 0;
        }

        Property property = properties.get(index);
        for (Object value : property.getPossibleValues()) {
            BlockState modified = base.setValue(property, (Comparable) value);
            if (hasLuminantCombination(modified, properties, index + 1)) {
                return true;
            }
        }
        return false;
    }

    protected static void registerModdedConfiguration() {}
}
