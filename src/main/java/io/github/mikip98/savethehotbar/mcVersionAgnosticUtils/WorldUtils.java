package io.github.mikip98.savethehotbar.mcVersionAgnosticUtils;

import net.minecraft.world.level.Level;

public class WorldUtils {
    public static int getTopBuildLimit(Level world) {
        #if MC_VERSION < 12104
        return world.getMaxBuildHeight() - 1;
        #else
        return world.getMaxY();
        #endif
    }
    public static int getBottomBuildLimit(Level world) {
        #if MC_VERSION < 12104
        return world.getMinBuildHeight();
        #else
        return world.getMinY();
        #endif
    }
    public static int getWorldHeight(Level world) {
        #if MC_VERSION < 12104
        return world.getMaxBuildHeight();
        #else
        return world.getMaxY() + 1;
        #endif
    }
}
