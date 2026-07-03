package io.github.mikip98.savethehotbar;

import net.minecraft.network.chat.Component;

public abstract class Util {
    #if MC_VERSION < 12108
    public static String msg(String msg) {
        return msg;
    }
    #else
    public static Component msg(String msg) {
        return Component.literal(msg);
    }
    #endif
}
