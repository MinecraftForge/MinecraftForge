package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Makes a ConfigScreen with entries for all of a mod's registered configs.
 */
public class ModConfigScreen extends ConfigScreen {

    private final IModInfo mod;

    public ModConfigScreen(Screen screen, IModInfo mod) {
        super(screen, new StringTextComponent(mod.getDisplayName()));
        this.mod = mod;
    }

    /**
     * Makes a Runnable that will register a config gui factory for the ModContainer
     * BUT ONLY IF a config gui factory does not already exist for the ModContainer.
     *
     * @param modContainer The ModContainer to possibly register a config gui factory for
     * @return The runnable
     * @see ModContainer#addConfig(ModConfig)
     */
    public static DistExecutor.SafeRunnable makeConfigGuiExtensionPoint(final ModContainer modContainer) {
        if (modContainer.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY).isPresent())
            return () -> {
                // No-op
            };
        return () -> modContainer.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
                () -> (minecraft, screen) -> new ModConfigScreen(screen, modContainer.getModInfo()));
    }

    @Override
    protected ConfigElementList makeConfigElementList() {
        Collection<ModConfig> allConfigs = ConfigTracker.INSTANCE.getConfigsForMod(mod.getModId()).values();
        Collection<ModConfig> configsToDisplay = new ArrayList<>();
        for (ModConfig modConfig : allConfigs) {
            if (modConfig.getType() == ModConfig.Type.SERVER)
                if (!Minecraft.getInstance().isSingleplayer() || Minecraft.getInstance().getIntegratedServer().getPublic())
                    continue;
            configsToDisplay.add(modConfig);
        }
        return new ConfigElementList(this, field_230706_i_) {
            {
                for (ModConfig modConfig : configsToDisplay) {
                    String name = StringUtils.capitalize(modConfig.getType().name().toLowerCase());
                    ConfigElement element = new PopupConfigElement(name, "", () -> new ModConfigConfigScreen(ModConfigScreen.this, new StringTextComponent(name), modConfig));
                    this.func_230513_b_(element);
                }
            }
        };
    }

    /**
     * ConfigScreen for a ModConfig.
     */
    private static class ModConfigConfigScreen extends ConfigScreen {

        private final ModConfig modConfig;

        public ModConfigConfigScreen(Screen parentScreen, ITextComponent titleIn, ModConfig modConfig) {
            super(parentScreen, titleIn);
            this.modConfig = modConfig;
        }

        @Override
        protected ConfigElementList makeConfigElementList() {
            CategoryConfigScreen.ConfigCategoryInfo info = CategoryConfigScreen.ConfigCategoryInfo.of(
                    () -> modConfig.getSpec().getValues().valueMap().keySet(),
                    key -> modConfig.getSpec().getValues().get(key),
                    key -> modConfig.getSpec().getSpec().get(key),
                    key -> modConfig.getSpec().getSpec().getComment(key)
            );
            return new CategoryConfigScreen.CategoryConfigElementList(this, field_230706_i_, info);
        }

        @Override
        public void onChange() {
            saveAndReloadConfig();
            super.onChange();
        }

        private void saveAndReloadConfig() {
            // Need to save and reload now rather than waiting for the changes to get noticed by the
            // file watcher because this can take a while (upwards of 10 seconds) to happen and the
            // user is expecting to see their config changes take effect NOW.
            // E.g. A mod customises how buttons look, users changing the config shouldn't need to wait
            // 10 seconds before their changes take effect.
            modConfig.save();
            ((CommentedFileConfig) modConfig.getConfigData()).load();
            modConfig.fireEvent(new ModConfig.Reloading(modConfig));
        }
    }

}
