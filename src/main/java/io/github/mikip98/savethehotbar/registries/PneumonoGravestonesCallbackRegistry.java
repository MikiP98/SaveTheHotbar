package io.github.mikip98.savethehotbar.registries;

import io.github.mikip98.savethehotbar.modSupport.GravestoneItemSkipCallback;
import net.pneumono.gravestones.api.SkipItemCallback;

public class PneumonoGravestonesCallbackRegistry {
    public static void register() {
        SkipItemCallback.EVENT.register(new GravestoneItemSkipCallback());
    }
}
