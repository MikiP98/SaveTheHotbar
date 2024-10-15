package io.github.mikip98.savethehotbar.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Return the screen here with the one you created from Cloth Config Builder
        return ModConfigScreen::createScreen;
    }
}
