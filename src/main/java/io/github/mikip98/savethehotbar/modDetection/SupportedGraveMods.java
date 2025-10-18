package io.github.mikip98.savethehotbar.modDetection;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public enum SupportedGraveMods {
    PNEUMONO_CORE("Pneumono Core", "pneumonocore"),
    PNEUMONO_GRAVESTONES("Gravestones", "gravestones", PNEUMONO_CORE);

    public final String modName;
    public final String modId;
    private final @Nullable SupportedGraveMods dependency;

    SupportedGraveMods(String modName, String modId) {
        this(modName, modId, null);
    }
    SupportedGraveMods(String modName, String modId, @Nullable SupportedGraveMods dependency) {
        this.modId = modId;
        this.modName = modName;
        this.dependency = dependency;
    }

    public boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded(this.modId) && (this.dependency == null || this.dependency.isLoaded());
    }
}
