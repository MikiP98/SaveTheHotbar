package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.annotations.FloatRange;
import io.github.mikip98.savethehotbar.config.annotations.GlobalTooltip;
import io.github.mikip98.savethehotbar.config.annotations.IntRange;
import io.github.mikip98.savethehotbar.config.annotations.SkipGlobalTooltip;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class ClothConfigGUIRegistry {
    public static void register() {
        GuiRegistry registry = AutoConfig.getGuiRegistry(ModConfig.class);
        registerCustomProviders(registry);
        registerCustomTransformers(registry);
    }

    protected static void registerCustomProviders(GuiRegistry registry) {
        registerEnumBooleanMapProvider(registry);
        registerFloatRangeStringFieldProvider(registry);
        registerIntRangeStringFieldProvider(registry);
    }

    protected static void registerCustomTransformers(GuiRegistry registry) {
        registerGlobalTooltipTransformer(registry);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static void registerEnumBooleanMapProvider(GuiRegistry registry) {
        registry.registerPredicateProvider(
                (i13n, field, config, defaults, guiProvider) -> {
                    ConfigEntryBuilder builder = ConfigEntryBuilder.create();
                    List<AbstractConfigListEntry<?>> entries = new ArrayList<>();

                    try {
                        // Extract the specific Enum class using reflection
                        ParameterizedType pType = (ParameterizedType) field.getGenericType();

                        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) pType.getActualTypeArguments()[0];

                        // Grab the actual map and default state map
                        Map<Enum<?>, Boolean> map = (Map<Enum<?>, Boolean>) field.get(config);
                        Map<Enum<?>, Boolean> defaultMap = (Map<Enum<?>, Boolean>) field.get(defaults);

                        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
                            String translationKey = i13n + "." + enumValue.name().toLowerCase();

                            boolean defaultValue = (defaultMap != null && defaultMap.containsKey(enumValue))
                                    ? defaultMap.get(enumValue)
                                    : true;

                            var toggleBuilder = builder.startBooleanToggle(
                                            Component.translatable(translationKey),
                                            map.getOrDefault(enumValue, true)
                                    )
                                    .setDefaultValue(defaultValue)
                                    .setSaveConsumer(newValue -> map.put(enumValue, newValue));

                            boolean hasTooltip = field.isAnnotationPresent(ConfigEntry.Gui.Tooltip.class) || getGlobalTooltip(field) != null;
                            if (hasTooltip) {
                                toggleBuilder.setTooltip(Component.translatable(translationKey + ".@Tooltip"));
                            }

                            entries.add(toggleBuilder.build());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error: {};\nStacktrace: {}", e, e.getStackTrace());
                    }

                    return (List<AbstractConfigListEntry>) (Object) entries;
                },
                field -> {
                    if (Map.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType pType) {
                        Type[] typeArgs = pType.getActualTypeArguments();
                        if (typeArgs.length == 2) {
                            boolean isEnumKey = typeArgs[0] instanceof Class<?> clazz && clazz.isEnum();
                            boolean isBoolValue = typeArgs[1] == Boolean.class;
                            return isEnumKey && isBoolValue;
                        }
                    }
                    return false;
                }
        );
    }

    protected static void registerFloatRangeStringFieldProvider(GuiRegistry registry) {
        registry.registerAnnotationProvider(
                (i13n, field, config, defaults, guiProvider) -> {
                    FloatRange bounds = field.getAnnotation(FloatRange.class);
                    float min = bounds.min();
                    float max = bounds.max();

                    try {
                        float currentValue = field.getFloat(config);
                        float defaultValue = field.getFloat(defaults);

                        var floatBuilder = ConfigEntryBuilder.create()
                                .startFloatField(Component.translatable(i13n), currentValue)
                                .setDefaultValue(defaultValue)
                                .setMin(min)
                                .setMax(max)
                                .setSaveConsumer(newValue -> {
                                    try {
                                        field.setFloat(config, newValue);
                                    } catch (IllegalAccessException e) {
                                        LOGGER.error("Error, could not set the value of the FloatRange field: {};\nStacktrace: {}", e, e.getStackTrace());
                                    }
                                });

                        return Collections.singletonList(floatBuilder.build());

                    } catch (Exception e) {
                        LOGGER.error("Error (returning empty FloatRange): {};\nStacktrace: {}", e, e.getStackTrace());
                        return Collections.emptyList();
                    }
                },
                field -> field.getType() == float.class,
                FloatRange.class
        );
    }

    protected static void registerIntRangeStringFieldProvider(GuiRegistry registry) {
        registry.registerAnnotationProvider(
                (i13n, field, config, defaults, guiProvider) -> {
                    IntRange bounds = field.getAnnotation(IntRange.class);
                    int min = bounds.min();
                    int max = bounds.max();

                    try {
                        int currentValue = field.getInt(config);
                        int defaultValue = field.getInt(defaults);

                        var intBuilder = ConfigEntryBuilder.create()
                                .startIntField(Component.translatable(i13n), currentValue)
                                .setDefaultValue(defaultValue)
                                .setMin(min)
                                .setMax(max)
                                .setSaveConsumer(newValue -> {
                                    try {
                                        field.setInt(config, newValue);
                                    } catch (IllegalAccessException e) {
                                        LOGGER.error("Error, could not set the value of the IntRange field: {};\nStacktrace: {}", e, e.getStackTrace());
                                    }
                                });

                        return Collections.singletonList(intBuilder.build());

                    } catch (Exception e) {
                        LOGGER.error("Error (returning empty IntRange): {};\nStacktrace: {}", e, e.getStackTrace());
                        return Collections.emptyList();
                    }
                },
                field -> field.getType() == int.class,
                IntRange.class
        );
    }

    // TODO: REMOVE THIS IF THIS GETS MERGED INTO CLOTH-CONFIG DIRECTLY
    protected static void registerGlobalTooltipTransformer(GuiRegistry registry) {
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    if (guis.size() > 1) return guis; // The size heuristic we wrote

                    return guis.stream()
                            .peek(gui -> {
                                if (!(gui instanceof TextListEntry)) {
                                    GlobalTooltip globalTooltip = getGlobalTooltip(field);

                                    assert globalTooltip != null;
                                    final int count = globalTooltip.count();

                                    if (count == 0) {
                                        tryRemoveTooltip(gui);
                                    } else if (count == 1) {
                                        tryApplyTooltip(
                                                gui,
                                                new Component[]{
                                                        Component.translatable(String.format("%s.%s", i18n, "@Tooltip"))
                                                }
                                        );
                                    } else {
                                        tryApplyTooltip(
                                                gui, IntStream.range(0, count).boxed()
                                                        .map(i -> String.format("%s.%s[%d]", i18n, "@Tooltip", i))
                                                        .map(Component::translatable)
                                                        .toArray(Component[]::new)
                                        );
                                    }
                                }
                            })
                            .collect(Collectors.toList());
                },
                field -> getGlobalTooltip(field) != null && !field.isAnnotationPresent(SkipGlobalTooltip.class) && !field.isAnnotationPresent(ConfigEntry.Gui.Tooltip.class)
        );
    }

    // TODO: REMOVE THIS IF THIS GETS MERGED INTO CLOTH-CONFIG DIRECTLY
    /**
     * Checks if any of the parent classes have the {@link GlobalTooltip} annotation
     * @return {@link GlobalTooltip} annotation instance if found, else null
     */
    public static GlobalTooltip getGlobalTooltip(Field field) {
        Class<?> currentClass = field.getDeclaringClass();
        while (currentClass != null) {
            if (currentClass.isAnnotationPresent(GlobalTooltip.class)) {
                return currentClass.getAnnotation(GlobalTooltip.class);
            }
            currentClass = currentClass.getEnclosingClass();
        }
        return null;
    }

    // TODO: REMOVE THIS IF THIS GETS MERGED INTO CLOTH-CONFIG DIRECTLY
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void tryApplyTooltip(AbstractConfigListEntry gui, Component[] text) {
        if (gui instanceof TooltipListEntry tooltipGui) {
            tooltipGui.setTooltipSupplier(() -> Optional.of(text));
        }
    }

    // TODO: REMOVE THIS IF THIS GETS MERGED INTO CLOTH-CONFIG DIRECTLY
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void tryRemoveTooltip(AbstractConfigListEntry gui) {
        if (gui instanceof TooltipListEntry tooltipGui) {
            tooltipGui.setTooltipSupplier(Optional::empty);
        }
    }
}
