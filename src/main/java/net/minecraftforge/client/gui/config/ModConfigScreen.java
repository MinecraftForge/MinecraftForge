package net.minecraftforge.client.gui.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;

public class ModConfigScreen extends ConfigScreen {

    private final ModConfig modConfig;

    protected ModConfigScreen(ModConfig modConfig, Screen parentScreen) {
        // TODO: Localisation
        super(parentScreen, new StringTextComponent(getModName(modConfig)));
        this.modConfig = modConfig;
        // TODO: Register listener
//        ModList.get().getModContainerById(modConfig.getModId()).orElseThrow(() -> new RuntimeException("Mod config is for a non existent mod")).dispatchConfigEvent();
    }

    private static String getModName(ModConfig modConfig) {
        return ModList.get().getModContainerById(modConfig.getModId()).orElseThrow(() -> new RuntimeException("Mod config is for a non existent mod")).getModInfo().getDisplayName();
    }

    @SubscribeEvent
    public void onConfigChange(ModConfig.Loading event) {
        if (event.getConfig() == modConfig) {
            // TODO: We need to display a screen about the config changing
        }
    }

    @Override
    // onClose
    public void func_231164_f_() {
        super.func_231164_f_();
        // remove event subscriber
    }

}
