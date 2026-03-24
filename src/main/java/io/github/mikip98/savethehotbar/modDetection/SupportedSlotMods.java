package io.github.mikip98.savethehotbar.modDetection;

import net.fabricmc.loader.api.FabricLoader;

public enum SupportedSlotMods {
    #if MC_VERSION == 12001
    ARSENAL("Arsenal", "arsenal", 1)
    #endif
    ;

    public final String modName;
    public final String modId;
    public final byte slotAmount;

    SupportedSlotMods(String modName, String modId, int slotAmount) {
        this.modId = modId;
        this.modName = modName;
        this.slotAmount = (byte) slotAmount;
    }

    public boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
