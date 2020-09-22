package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Makes a ConfigScreen with entries for all of a mod's registered configs.
 */
public class ModConfigScreen extends ConfigScreen {

    private static final Map<ModConfig.Type, String> COMMENTS = makeTypeComments();

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

    // TODO: Move these to translation keys?
    @Deprecated
    private static Map<ModConfig.Type, String> makeTypeComments() {
        Map<ModConfig.Type, String> map = new HashMap<>();

        String commonComment = StringUtils.join(new String[]{
                "Common config is for configuration that needs to be loaded on both environments.",
                "Loaded on both server and client environments during startup (after registry events and before setup events).",
                "Stored in the global config directory.",
                "Not synced.",
        }, "\n");
        String clientComment = StringUtils.join(new String[]{
                "Client config is for configuration affecting the ONLY client state such as graphical options.",
                "Loaded on the client environment during startup (after registry events and before setup events).",
                "Stored in the global config directory.",
                "Not synced.",
        }, "\n");
//        String playerComment = StringUtils.join(new String[]{
//                "Player config is for configuration that is associated with a player.",
//                "Preferences around machine states, for example.",
//                "Not Implemented (yet).",
//        }, "\n");
        String serverComment = StringUtils.join(new String[]{
                "Server config is for configuration that is associated with a logical server instance.",
                "Loaded during server startup (right before the FMLServerAboutToStartEvent is fired.)",
                "Stored in a server/save specific \"serverconfig\" directory",
                "Synced to clients during connection.",
        }, "\n");

        serverComment += "\nRequires you to be in your singleplayer world to change its values from the config gui";

        map.put(ModConfig.Type.COMMON, commonComment);
        map.put(ModConfig.Type.CLIENT, clientComment);
//		map.put(ModConfig.Type.PLAYER, playerComment);
        map.put(ModConfig.Type.SERVER, serverComment);
        return map;
    }

    /**
     * @return True if in singleplayer and not open to LAN
     */
    private static boolean canPlayerEditServerConfig() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getIntegratedServer() == null)
            return false;
        if (!minecraft.isSingleplayer())
            return false;
        return !minecraft.getIntegratedServer().getPublic();
    }

    @Nullable
    private static String getFilePath(ModConfig modConfig) {
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
                    String translatedName = "forge.configgui.modConfigType." + modConfig.getType().name().toLowerCase();
                    String description = COMMENTS.get(modConfig.getType());
                    String filePath = getFilePath(modConfig);
                    if (filePath != null)
                        description += "\n" + TextFormatting.GRAY + filePath;
                    ConfigElement element = new PopupConfigElement(translatedName, description, () -> new ModConfigConfigScreen(ModConfigScreen.this, new StringTextComponent(translatedName), modConfig));
                    this.func_230513_b_(element); // addEntry
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
//            if (modConfig.getType() == ModConfig.Type.SERVER) {
//                // TODO: Syncing to Server
//                final ByteArrayOutputStream output = new ByteArrayOutputStream();
//                final CommentedConfig configData = modConfig.getConfigData();
//                configData.configFormat().createWriter().write(configData, output);
//                new C2SRequestUpdateConfigData(modConfig.getFileName(), output.toByteArray()).sendToServer();
//                return;
//            }
            modConfig.save();
            ((CommentedFileConfig) modConfig.getConfigData()).load();
            modConfig.fireEvent(new ModConfig.Reloading(modConfig));
        }
    }

}
