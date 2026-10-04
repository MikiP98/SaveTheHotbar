package io.github.mikip98.savethehotbar.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.mikip98.savethehotbar.config.annotations.ConfigExtension;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class AutoCommentedTomlSerializer<T extends ConfigData> extends Toml4jConfigSerializer<T> {
    protected final Config definition;
    protected final Class<T> configClass;

    public AutoCommentedTomlSerializer(Config definition, Class<T> configClass) {
        super(definition, configClass);
        this.definition = definition;
        this.configClass = configClass;
    }

    // Copy of private getConfigPath() in Toml4jConfigSerializer
    protected Path getConfigPath() {
        return Utils.getConfigFolder().resolve(this.definition.name() + ".toml");
    }

    protected JsonObject getLangData() {
        String modId = this.definition.name().toLowerCase(); // TODO: Check if this correctly extracts the modId
        String langPath = "/assets/" + modId + "/lang/en_us.json";

        InputStream stream = this.configClass.getResourceAsStream(langPath);
        if (stream != null) {
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } else throw new RuntimeException("Failed to load lang data from " + langPath);
    }

    @Override
    public void serialize(T config) throws SerializationException {
        super.serialize(config);

        // Inject comments
        final Path configPath = this.getConfigPath();
        final JsonObject lang = getLangData();
        try {
            CommentedFileConfig tomlConfig = CommentedFileConfig.builder(configPath)
                    .sync()
                    .preserveInsertionOrder()
                    .build();

            tomlConfig.load();
            applyComments(this.configClass, tomlConfig, new ArrayList<>(), lang);
            tomlConfig.save();
            tomlConfig.close();

        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    protected void applyComments(Class<?> clazz, CommentedConfig toml, List<String> path, JsonObject lang) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;

            List<String> currentPath = new ArrayList<>(path);
            currentPath.add(field.getName());

            Class<?> fieldType = field.getType();
            boolean isConfigObject = !fieldType.isEnum() &&
                    (fieldType.getDeclaringClass() != null || fieldType.getName().startsWith("io.github.mikip98"));  // TODO: Remove the .startsWith() check, maybe replace with something else

            if (isConfigObject) {
                applyComments(field.getType(), toml, currentPath, lang);
            }

            if (hasTooltip(field)) {
                final String resolvedComment = resolveCommentFromLangFile(currentPath, lang);

                if (resolvedComment != null && !resolvedComment.isEmpty()) {
                    toml.setComment(currentPath, resolvedComment);
                } else {
                    LOGGER.warn("FAILED to found the comment for '{}' field", field.getName());
                }
            } else {
                LOGGER.warn("Field '{}' won't have any comments as it doesn't have a tooltip", field.getName());
            }
        }
    }

    protected static boolean hasTooltip(Field field) {
        return field.isAnnotationPresent(ConfigEntry.Gui.Tooltip.class) || hasGlobalTooltip(field);
    }

    /**
     * Checks if any of the parent classes have the {@link ConfigExtension.Gui.GlobalTooltip} annotation
     * @return {@link ConfigExtension.Gui.GlobalTooltip} annotation instance if found, else null
     */
    public static boolean hasGlobalTooltip(Field field) {
        Class<?> currentClass = field.getDeclaringClass();
        while (currentClass != null) {
            if (currentClass.isAnnotationPresent(ConfigExtension.Gui.GlobalTooltip.class)) return true;
            currentClass = currentClass.getEnclosingClass();
        }
        return false;
    }
    // Copied (then simplified) from client sided ClothConfigGUIRegistry

    protected @Nullable String resolveCommentFromLangFile(List<String> currentPath, JsonObject lang) {
        // TODO: Fix the Maps looking for the tooltip in the wrong place

        // AutoConfig format: text.autoconfig.configName.option.path.to.field
        final String configName = this.definition.name();
        final String fieldPath = String.join(".", currentPath);
        final String baseKey = "text.autoconfig." + configName + ".option." + fieldPath;

        // TODO: Instead of blindly checking both tooltip types just pass how many there should be from the tooltip annotation itself

        // Single-line tooltip (@Tooltip)
        final String singleTooltipKey = baseKey + ".@Tooltip";
        if (lang.has(singleTooltipKey)) {
            return " " + lang.get(singleTooltipKey).getAsString().replace("\n", "\n ");
        }

        // TODO: Consider changing the lang files to have logical true/false values in lower case, or make them lower case here

        // Multi-line tooltip array (@Tooltip[0], @Tooltip[1], etc.)
        final List<String> lines = new ArrayList<>();
        int i = 0;
        while (lang.has(baseKey + ".@Tooltip[" + i + "]")) {
            lines.add(lang.get(baseKey + ".@Tooltip[" + i + "]").getAsString());
            i++;
        }
        if (!lines.isEmpty()) {
            return String.join("\n", lines);
        }

        return null;
    }
}
