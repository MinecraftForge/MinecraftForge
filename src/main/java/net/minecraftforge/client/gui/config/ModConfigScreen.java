/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.client.gui.screen.MessageDialogScreen;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link ConfigScreen} with entries for all of a mod's registered configs.
 *
 * @author Cadiboo
 */
public class ModConfigScreen extends ConfigScreen {

    protected final IModInfo mod;
    protected boolean displayRequiresWorldRestartScreen;

    public ModConfigScreen(Screen screen, IModInfo mod) {
        super(screen, new TranslationTextComponent("forge.configgui.title", mod.getDisplayName()));
        this.mod = mod;
    }

    /**
     * Makes a ConfigScreen with entries for all of a mod's registered configs.
     * <p>
     * Makes a Runnable that will register a config gui factory for the ModContainer
     * BUT ONLY IF a config gui factory does not already exist for the ModContainer.
     *
     * @param modContainer The ModContainer to possibly register a config gui factory for
     * @return The runnable
     * @see ModContainer#addConfig(ModConfig)
     */
    public static DistExecutor.SafeRunnable makeConfigGuiExtensionPoint(final ModContainer modContainer) {
        if (modContainer.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY).isPresent())
            return () -> {};
        return () -> modContainer.registerExtensionPoint(
            ExtensionPoint.CONFIGGUIFACTORY,
            () -> (minecraft, screen) -> new ModConfigScreen(screen, modContainer.getModInfo())
        );
    }

    /**
     * @return True if in singleplayer and not open to LAN.
     */
    protected static boolean canPlayerEditServerConfig() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getIntegratedServer() == null)
            return false;
        if (!minecraft.isSingleplayer())
            return false;
        return !minecraft.getIntegratedServer().getPublic();
    }

    @Nullable
    protected static String getDisplayFilePath(ModConfig modConfig) {
        // For Server Configs:
        // ConfigData is null unless we are in world
        // and ConfigData is not an instanceof FileConfig when connected to a multiplayer server.
        final CommentedConfig configData = modConfig.getConfigData();
        if (configData instanceof FileConfig)
            return modConfig.getFullPath().toAbsolutePath().toString();
        else
            return null;
    }

    @Override
    protected ConfigElementList makeConfigElementList() {
        Collection<ModConfig> allConfigs = ConfigTracker.INSTANCE.getConfigsForMod(mod.getModId()).values();
        Collection<ModConfig> configsToDisplay = new ArrayList<>();
        for (ModConfig modConfig : allConfigs) {
            if (modConfig.getType() == ModConfig.Type.SERVER && !canPlayerEditServerConfig())
                continue;
            configsToDisplay.add(modConfig);
        }
        return new ConfigElementList(this, field_230706_i_) {
            {
                for (ModConfig modConfig : configsToDisplay) {
                    String translationKey = "forge.configgui.modConfigType." + modConfig.getType().name().toLowerCase();
                    ITextComponent title = CategoryConfigScreen.translateWithFallback(translationKey, StringUtils.capitalize(modConfig.getType().name().toLowerCase()));
                    List<ITextProperties> tooltip = CategoryConfigScreen.createTooltip(title, translationKey + ".tooltip", null);
                    String filePath = getDisplayFilePath(modConfig);
                    if (filePath != null)
                        tooltip.add(new StringTextComponent(filePath).func_240701_a_(TextFormatting.GRAY));
                    ConfigElement element = new ConfigElement(null, title, tooltip);
                    element.setMainWidget(configScreen.getControlCreator().createPopupButton(title, () -> new ModConfigConfigScreen(ModConfigScreen.this, title /* TODO */, modConfig)));
                    this.func_230513_b_(element); // addEntry
                }
            }
        };
    }

    @Override
    public void onChange(boolean requiresWorldRestart) {
        displayRequiresWorldRestartScreen |= requiresWorldRestart;
        super.onChange(requiresWorldRestart);
    }

    @Override
    // closeScreen
    public void func_231175_as__() {
        if (!displayRequiresWorldRestartScreen || Minecraft.getInstance().world == null) {
            super.func_231175_as__();
            return;
        }
        field_230706_i_.displayGuiScreen(
            new MessageDialogScreen(
                new TranslationTextComponent("forge.configgui.worldRestartRequired"),
                new TranslationTextComponent("forge.configgui.worldRestartRequiredBecause"),
                new TranslationTextComponent("forge.configgui.worldRestartRequiredConfirm"),
                super::func_231175_as__
            )
        );
    }

    /**
     * ConfigScreen for a ModConfig.
     */
    public static class ModConfigConfigScreen extends ConfigScreen {

        protected final ModConfig modConfig;

        public ModConfigConfigScreen(Screen parentScreen, ITextComponent title, ModConfig modConfig) {
            super(parentScreen, title, ConfigScreen.makeSubtitle(parentScreen, title));
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
        public void onChange(boolean requiresWorldRestart) {
            saveAndReloadConfig();
            super.onChange(requiresWorldRestart);
        }

        /**
         * Need to save and reload now rather than waiting for the changes to get noticed by the
         * file watcher because this can take a while (upwards of 10 seconds) to happen and the
         * user is expecting to see their config changes take effect NOW.
         * E.g. A mod customises how buttons look, users changing the config shouldn't need to wait
         * 10 seconds before their changes take effect.
         */
        protected void saveAndReloadConfig() {
//            if (modConfig.getType() == ModConfig.Type.SERVER) {
//                // TODO: Syncing to Server
//                final ByteArrayOutputStream output = new ByteArrayOutputStream();
//                final CommentedConfig configData = modConfig.getConfigData();
//                configData.configFormat().createWriter().write(configData, output);
//                new C2SRequestUpdateConfigData(modConfig.getFileName(), output.toByteArray()).sendToServer();
//                return;
//            }
            modConfig.saveAndReload();
        }
    }

}
