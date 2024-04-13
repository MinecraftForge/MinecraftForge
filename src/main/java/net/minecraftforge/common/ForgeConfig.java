/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfig {
    public static class Server {
        public final BooleanValue removeErroringBlockEntities;

        public final BooleanValue removeErroringEntities;

        public final BooleanValue fullBoundingBoxLadders;

        public final DoubleValue zombieBaseSummonChance;
        public final DoubleValue zombieBabyChance;

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

            zombieBaseSummonChance = builder
                    .comment("Base zombie summoning spawn chance. Allows changing the bonus zombie summoning mechanic.")
                    .translation("forge.configgui.zombieBaseSummonChance")
                    .worldRestart()
                    .defineInRange("zombieBaseSummonChance", 0.1D, 0.0D, 1.0D);

            zombieBabyChance = builder
                    .comment("Chance that a zombie (or subclass) is a baby. Allows changing the zombie spawning mechanic.")
                    .translation("forge.configgui.zombieBabyChance")
                    .worldRestart()
                    .defineInRange("zombieBabyChance", 0.05D, 0.0D, 1.0D);

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

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("[DEPRECATED / NO EFFECT]: General configuration settings")
                    .push("general");

            builder.pop();
        }
    }

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {
        public final BooleanValue alwaysSetupTerrainOffThread;

        public final BooleanValue experimentalForgeLightPipelineEnabled;

        public final BooleanValue showLoadWarnings;

        public final BooleanValue useCombinedDepthStencilAttachment;

        @Deprecated(since = "1.20.1", forRemoval = true) // Config option ignored.
        public final BooleanValue compressLanIPv6Addresses;

        public final BooleanValue calculateAllNormals;

        public final BooleanValue stabilizeDirectionGetNearest;


        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                   .push("client");

            alwaysSetupTerrainOffThread = builder
                .comment("Enable Forge to queue all chunk updates to the Chunk Update thread.",
                        "May increase FPS significantly, but may also cause weird rendering lag.",
                        "Not recommended for computers without a significant number of cores available.")
                .translation("forge.configgui.alwaysSetupTerrainOffThread")
                .define("alwaysSetupTerrainOffThread", false);

            experimentalForgeLightPipelineEnabled = builder
                .comment("EXPERIMENTAL: Enable the Forge block rendering pipeline - fixes the lighting of custom models.")
                .translation("forge.configgui.forgeLightPipelineEnabled")
                .define("experimentalForgeLightPipelineEnabled", false);

            showLoadWarnings = builder
                .comment("When enabled, Forge will show any warnings that occurred during loading.")
                .translation("forge.configgui.showLoadWarnings")
                .define("showLoadWarnings", true);

            useCombinedDepthStencilAttachment = builder
                    .comment("Set to true to use a combined DEPTH_STENCIL attachment instead of two separate ones.")
                    .translation("forge.configgui.useCombinedDepthStencilAttachment")
                    .define("useCombinedDepthStencilAttachment", false);

            compressLanIPv6Addresses = builder
                    .comment("[DEPRECATED] Does nothing anymore, IPv6 addresses will be compressed always")
                    .translation("forge.configgui.compressLanIPv6Addresses")
                    .define("compressLanIPv6Addresses", true);

            calculateAllNormals = builder
                    .comment("During block model baking, manually calculates the normal for all faces.",
                            "This was the default behavior of forge between versions 31.0 and 47.1.",
                            "May result in differences between vanilla rendering and forge rendering.",
                            "Will only produce differences for blocks that contain non-axis aligned faces.",
                            "You will need to reload your resources to see results.")
                    .translation("forge.configgui.calculateAllNormals")
                    .define("calculateAllNormals", false);

            stabilizeDirectionGetNearest = builder
                    .comment("When enabled, a slightly biased Direction#getNearest calculation will be used to prevent normal fighting on 45 degree angle faces.")
                    .translation("forge.configgui.stabilizeDirectionGetNearest")
                    .define("stabilizeDirectionGetNearest", true);

            builder.pop();
        }

        // Allow these to be called before the config is loaded because its used before loading the error screens.
        // Prevents a ton of spam when an error screen is displayed.
        public final boolean calculateAllNormals() {
            return clientSpec.isLoaded() ? calculateAllNormals.get() : calculateAllNormals.getDefault();
        }

        public final boolean showLoadWarnings() {
            return clientSpec.isLoaded() ? showLoadWarnings.get() : showLoadWarnings.getDefault();
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

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        LogManager.getLogger().debug(Logging.FORGEMOD, "Loaded forge config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        LogManager.getLogger().debug(Logging.FORGEMOD, "Forge config just got changed on the file system!");
    }

    //General
    //public static boolean disableVersionCheck = false;
    //public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    //public static boolean fixVanillaCascading = false;
}
