/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.logging.log4j.Logger;

public class ForgeConfig {
    static final Logger LOGGER = LogManager.getLogger();

    public static class Server {
        public final BooleanValue removeErroringBlockEntities;

        public final BooleanValue removeErroringEntities;

        public final BooleanValue fullBoundingBoxLadders;

        public final ConfigValue<String> permissionHandler;

        public final BooleanValue advertiseDedicatedServerToLan;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                   .push("server");

            removeErroringBlockEntities = builder
                    .comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation("forge.configgui.removeErroringBlockEntities")
                    .worldRestart()
                    .define("removeErroringBlockEntities", false);

            removeErroringEntities = builder
                    .comment("Set this to true to remove any Entity (Note: Does not include BlockEntities) that throws an error in its tick method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation("forge.configgui.removeErroringEntities")
                    .worldRestart()
                    .define("removeErroringEntities", false);

            fullBoundingBoxLadders = builder
                    .comment("Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticeable differences in mechanics so default is vanilla behavior. Default: false.")
                    .translation("forge.configgui.fullBoundingBoxLadders")
                    .worldRestart()
                    .define("fullBoundingBoxLadders", false);

            permissionHandler = builder
                    .comment("The permission handler used by the server. Defaults to forge:default_handler if no such handler with that name is registered.")
                    .translation("forge.configgui.permissionHandler")
                    .define("permissionHandler", "forge:default_handler");

            advertiseDedicatedServerToLan = builder
                    .comment("Set this to true to enable advertising the dedicated server to local LAN clients so that it shows up in the Multiplayer screen automatically.")
                    .translation("forge.configgui.advertiseDedicatedServerToLan")
                    .define("advertiseDedicatedServerToLan", true);

            builder.pop();
        }
    }

    /**
     * General configuration that doesn't need to be synchronized but needs to be available before server startup
     */
    public static class Common {
        public enum MigrationHelperMode {
            OFF,
            ONLY_IN_DEV_ENV,
            ALWAYS
        }

        public final ForgeConfigSpec.EnumValue<MigrationHelperMode> migrationHelperMode;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General configuration settings")
                    .push("general");

            migrationHelperMode = builder
                    .comment("A config option to help developers find known legacy modded tags that have common convention equivalents when running on integrated server. Defaults to OFF.")
                    .translation("forge.configgui.migrationHelperMode")
                    .defineEnum("logLegacyTagWarnings", MigrationHelperMode.OFF);

            builder.pop();
        }
    }

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {
        public final BooleanValue showLoadWarnings;

        public final BooleanValue allowMipmapLowering;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                   .push("client");

            showLoadWarnings = builder
                .comment("When enabled, Forge will show any warnings that occurred during loading.")
                .translation("forge.configgui.showLoadWarnings")
                .define("showLoadWarnings", true);

            allowMipmapLowering = builder
                .comment("When enabled, Forge will allow mipmaps to be lowered in real-time. This is the default behavior in vanilla. Use this if you experience issues with resource packs that use textures lower than 8x8.")
                .translation("forge.configgui.allowMipmapLowering")
                .define("allowMipmapLowering", false);

            builder.pop();
        }

        // Allow these to be called before the config is loaded because its used before loading the error screens.
        // Prevents a ton of spam when an error screen is displayed.
        public final boolean showLoadWarnings() {
            return clientSpec.isLoaded() ? showLoadWarnings.get() : showLoadWarnings.getDefault();
        }

        public final boolean allowMipmapLowering() {
            return clientSpec.isLoaded() ? allowMipmapLowering.get() : allowMipmapLowering.getDefault();
        }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }


    static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }


    static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    static void onLoad(final ModConfigEvent.Loading configEvent) {
        LOGGER.debug(Logging.FORGEMOD, "Loaded forge config file {}", configEvent.getConfig().getFileName());
    }

    static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        LOGGER.debug(Logging.FORGEMOD, "Forge config just got changed on the file system!");
    }

    //General
    //public static boolean disableVersionCheck = false;
    //public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    //public static boolean fixVanillaCascading = false;
}
