/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common;

import net.minecraftsingularity.fml.Logging;
import net.minecraftsingularity.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import net.minecraftsingularity.common.singularityConfigSpec.BooleanValue;
import net.minecraftsingularity.common.singularityConfigSpec.ConfigValue;
import org.apache.logging.log4j.Logger;

public class singularityConfig {
    static final Logger LOGGER = LogManager.getLogger();

    public static class Server {
        public final BooleanValue removeErroringBlockEntities;

        public final BooleanValue removeErroringEntities;

        public final BooleanValue fullBoundingBoxLadders;

        public final ConfigValue<String> permissionHandler;

        public final BooleanValue advertiseDedicatedServerToLan;

        Server(singularityConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                   .push("server");

            removeErroringBlockEntities = builder
                    .comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation("singularity.configgui.removeErroringBlockEntities")
                    .worldRestart()
                    .define("removeErroringBlockEntities", false);

            removeErroringEntities = builder
                    .comment("Set this to true to remove any Entity (Note: Does not include BlockEntities) that throws an error in its tick method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation("singularity.configgui.removeErroringEntities")
                    .worldRestart()
                    .define("removeErroringEntities", false);

            fullBoundingBoxLadders = builder
                    .comment("Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticeable differences in mechanics so default is vanilla behavior. Default: false.")
                    .translation("singularity.configgui.fullBoundingBoxLadders")
                    .worldRestart()
                    .define("fullBoundingBoxLadders", false);

            permissionHandler = builder
                    .comment("The permission handler used by the server. Defaults to singularity:default_handler if no such handler with that name is registered.")
                    .translation("singularity.configgui.permissionHandler")
                    .define("permissionHandler", "singularity:default_handler");

            advertiseDedicatedServerToLan = builder
                    .comment("Set this to true to enable advertising the dedicated server to local LAN clients so that it shows up in the Multiplayer screen automatically.")
                    .translation("singularity.configgui.advertiseDedicatedServerToLan")
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

        public final singularityConfigSpec.EnumValue<MigrationHelperMode> migrationHelperMode;

        Common(singularityConfigSpec.Builder builder) {
            builder.comment("General configuration settings")
                    .push("general");

            migrationHelperMode = builder
                    .comment("A config option to help developers find known legacy modded tags that have common convention equivalents when running on integrated server. Defaults to OFF.")
                    .translation("singularity.configgui.migrationHelperMode")
                    .defineEnum("logLegacyTagWarnings", MigrationHelperMode.OFF);

            builder.pop();
        }
    }

    /**
     * Client specific configuration - only loaded clientside from singularity-client.toml
     */
    public static class Client {
        public final BooleanValue showLoadWarnings;

        public final BooleanValue allowMipmapLowering;

        Client(singularityConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                   .push("client");

            showLoadWarnings = builder
                .comment("When enabled, singularity will show any warnings that occurred during loading.")
                .translation("singularity.configgui.showLoadWarnings")
                .define("showLoadWarnings", true);

            allowMipmapLowering = builder
                .comment("When enabled, singularity will allow mipmaps to be lowered in real-time. This is the default behavior in vanilla. Use this if you experience issues with resource packs that use textures lower than 8x8.")
                .translation("singularity.configgui.allowMipmapLowering")
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

    static final singularityConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<Client, singularityConfigSpec> specPair = new singularityConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }


    static final singularityConfigSpec commonSpec;
    public static final Common COMMON;
    static {
        final Pair<Common, singularityConfigSpec> specPair = new singularityConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }


    static final singularityConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, singularityConfigSpec> specPair = new singularityConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    static void onLoad(final ModConfigEvent.Loading configEvent) {
        LOGGER.debug(Logging.singularityMOD, "Loaded singularity config file {}", configEvent.getConfig().getFileName());
    }

    static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        LOGGER.debug(Logging.singularityMOD, "singularity config just got changed on the file system!");
    }

    //General
    //public static boolean disableVersionCheck = false;
    //public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    //public static boolean fixVanillaCascading = false;
}
