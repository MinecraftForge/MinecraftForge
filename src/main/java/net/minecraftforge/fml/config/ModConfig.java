/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.StringUtils;

import java.nio.file.Path;
import java.util.concurrent.Callable;

public class ModConfig
{
    private final Type type;
    private final ForgeConfigSpec spec;
    private final String fileName;
    private final ModContainer container;
    private final ConfigFileTypeHandler configHandler;
    private CommentedConfig configData;
    private Callable<Void> saveHandler;

    public ModConfig(final Type type, final ForgeConfigSpec spec, final ModContainer container, final String fileName) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        this.container = container;
        this.configHandler = ConfigFileTypeHandler.TOML;
        ConfigTracker.INSTANCE.trackConfig(this);
    }

    public ModConfig(final Type type, final ForgeConfigSpec spec, final ModContainer activeContainer) {
        this(type, spec, activeContainer, defaultConfigName(type, activeContainer.getModId()));
    }

    private static String defaultConfigName(Type type, String modId) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format("%s-%s.toml", modId, type.extension());
    }
    public Type getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public ConfigFileTypeHandler getHandler() {
        return configHandler;
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public String getModId() {
        return container.getModId();
    }

    public CommentedConfig getConfigData() {
        return this.configData;
    }

    void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.spec.setConfig(this.configData);
    }

    public void fireEvent(final ModConfigEvent configEvent) {
        this.container.dispatchConfigEvent(configEvent);
    }

    public void save() {
        ((CommentedFileConfig)this.configData).save();
    }

    public Path getFullPath() {
        return ((CommentedFileConfig)this.configData).getNioPath();
    }

    public enum Type {
        /**
         * Common config is for configuration that needs to be loaded on both environments.
         * Loaded on both server and client environments during startup (after registry events and before setup events).
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-common" by default.
         * Examples of config entries that would use this:
         * - Whether to enable an update checker (FML has this)
         * - What paths to load extra mod assets from (plugin/addon files)
         */
        COMMON,
        /**
         * Client config is for configuration affecting the ONLY client state such as graphical options.
         * Loaded on the client environment during startup (after registry events and before setup events).
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-client" by default.
         * Examples of config entries that would use this:
         * - If certain info should be rendered in a GUI/HUD
         * - The color of something that will be rendered in a GUI/HUD
         * - Options that change how you interact with things
         * - Controls/Accessibility options (For changing the effects of keybinds/mouse clicks/mouse movement)
         */
        CLIENT,
//        /**
//         * Player type config is configuration that is associated with a player.
//         * Preferences around machine states, for example.
//         * Not Implemented (yet).
//         */
//        PLAYER,
        /**
         * Server config is for configuration that is associated with a logical server instance.
         * Loaded during server startup (right before the {@link FMLServerAboutToStartEvent} is fired).
         * Stored in a server/save specific "serverconfig" directory.
         * Synced to clients during connection.
         * Suffix is "-server" by default.
         * Examples of config entries that would use this:
         * - The amount of energy a machine uses/produces
         * - World/Ore gen configuration
         * - Entity spawning configuration
         */
        SERVER;

        public String extension() {
            return StringUtils.toLowerCase(name());
        }
    }

    public static class ModConfigEvent extends Event {
        private final ModConfig config;

        ModConfigEvent(final ModConfig config) {
            this.config = config;
        }

        public ModConfig getConfig() {
            return config;
        }
    }

    public static class Loading extends ModConfigEvent {
        Loading(final ModConfig config) {
            super(config);
        }
    }

    public static class Reloading extends ModConfigEvent {
        public Reloading(final ModConfig config) {
            super(config);
        }
    }
}
