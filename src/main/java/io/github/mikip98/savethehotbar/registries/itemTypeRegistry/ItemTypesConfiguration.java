package io.github.mikip98.savethehotbar.registries.itemTypeRegistry;

import io.github.mikip98.savethehotbar.config.enums.ItemTypes;
import io.github.mikip98.savethehotbar.content.tags.ModItemTags;
#if MC_VERSION <= 12004
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
#else
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.component.DataComponents;
#endif
import net.minecraft.world.item.*;
import net.minecraft.world.item.BlockItem;
#if MC_VERSION == 12001 import net.minecraft.world.item.Item; #endif
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.ItemTags;

import java.util.*;

public class ItemTypesConfiguration {
    public static Map<ItemTypes, ItemTypeConfig> vanillaItemTypes = new EnumMap<>(ItemTypes.class);
    static {
        for (ItemTypes type : ItemTypes.values()) {
            vanillaItemTypes.put(type, new ItemTypeConfig(ModItemTags.vanillaItemTypesTagOverridesMap.get(type)));
        }
    }

    public static void registerConfiguration() {
        registerVanillaConfiguration();
        registerModdedConfiguration();
    }

    protected static void registerVanillaConfiguration() {
        vanillaItemTypes.get(ItemTypes.TOOL)
                #if MC_VERSION < 12104
                .addClasses(TieredItem.class)
                #elif MC_VERSION < 12106
                .addClasses(DiggerItem.class)
                // TODO: Consider adding predicates using 'ItemAttributeModifiers'/'DataComponents'
                #endif
                #if MC_VERSION <= 12004
                .addTags(ItemTags.TOOLS);
                #else
                .addTags(ConventionalItemTags.TOOLS);
                #endif

        vanillaItemTypes.get(ItemTypes.WEAPON)
                .addClasses(#if MC_VERSION < 12106 SwordItem.class, #endif AxeItem.class, ProjectileWeaponItem.class, TridentItem.class)
                // TODO: Consider adding predicates using 'ItemAttributeModifiers'/'DataComponents'
                #if MC_VERSION <= 12004
                .addTags(ItemTags.SWORDS, ItemTags.AXES);
                #elif MC_VERSION < 12104
                .addTags(ConventionalItemTags.MELEE_WEAPONS_TOOLS, ConventionalItemTags.RANGED_WEAPONS_TOOLS);
                #else
                .addTags(ConventionalItemTags.MELEE_WEAPON_TOOLS, ConventionalItemTags.RANGED_WEAPON_TOOLS);
                #endif

        vanillaItemTypes.get(ItemTypes.AMMUNITION)
                .addClasses(ArrowItem.class)
                .addTags(ItemTags.ARROWS);

        vanillaItemTypes.get(ItemTypes.ARMOUR)
                #if MC_VERSION < 12106 .addClasses(ArmorItem.class #if MC_VERSION < 12006, DyeableArmorItem.class #endif) #endif  // TODO: Consider adding predicates using 'ItemAttributeModifiers'/'DataComponents'
                #if MC_VERSION >= 12006 .addTags(ConventionalItemTags.ARMORS) #endif;

        #if MC_VERSION < 12104
        vanillaItemTypes.get(ItemTypes.EQUIPMENT).addClasses(Equipable.class);
        #endif

        vanillaItemTypes.get(ItemTypes.FOOD)
                .addTags(ConventionalItemTags.FOODS)
                #if MC_VERSION <= 12004
                .addPredicates(Item::isEdible)
                #else
                .addPredicates((item) -> item.components().has(DataComponents.FOOD))
                #endif;

        vanillaItemTypes.get(ItemTypes.POTION)
                .addTags(ConventionalItemTags.POTIONS)
                .addClasses(PotionItem.class);

        vanillaItemTypes.get(ItemTypes.LIGHT_SOURCE_ON)
                .addPredicates(item -> {
                    if (item instanceof BlockItem blockItem)
                        return blockItem.getBlock().defaultBlockState().getLightEmission() > 0;
                    return false;
                });
        vanillaItemTypes.get(ItemTypes.POSSIBLE_LIGHT_SOURCE)
                .addPredicates(item -> {
                    if (item instanceof BlockItem blockItem)
                        return hasLuminantState(blockItem.getBlock());
                    return false;
                });

        // Validate that all Item Types have valid configurations (a.k.a. I haven't forgotten anything)
        for (ItemTypes type : ItemTypes.values()) {
            ItemTypeConfig config = vanillaItemTypes.get(type);
            if (type != ItemTypes.OTHER && !config.isConfigured()) {
                throw new IllegalStateException("Not all Item Types have a valid configuration");
            }
        }
    }

    // TODO: Maybe instead of caching this here, I should just cache 'isItemStackOfType' inside 'ItemTypeConfig'
    private static final Map<Block, Boolean> luminanceCache = new IdentityHashMap<>();
    protected static boolean hasLuminantState(Block block) {
        return luminanceCache.computeIfAbsent(block, b -> {
            if (b.defaultBlockState().getLightEmission() > 0) {
                return true;
            }
            for (BlockState state : b.getStateDefinition().getPossibleStates()) {
                if (state.getLightEmission() > 0) {
                    return true;
                }
            }
            return false;
        });
    }

    protected static void registerModdedConfiguration() {}
}
