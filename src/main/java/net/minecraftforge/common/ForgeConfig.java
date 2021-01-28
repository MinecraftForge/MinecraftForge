/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common;

import static net.minecraftforge.fml.loading.LogMarkers.FORGEMOD;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ForgeConfig
{
    public static class Server {
        public final BooleanValue removeErroringEntities;
        public final BooleanValue removeErroringTileEntities;

        public final BooleanValue fullBoundingBoxLadders;

        public final DoubleValue zombieBaseSummonChance;
        public final DoubleValue zombieBabyChance;

        public final BooleanValue logCascadingWorldGeneration;
        public final BooleanValue fixVanillaCascading;

        public final IntValue dimensionUnloadQueueDelay;

        public final BooleanValue treatEmptyTagsAsAir;

        public final BooleanValue fixAdvancementLoading;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                   .push("server");

            removeErroringEntities = builder
                    .comment("Set this to true to remove any Entity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation("forge.configgui.removeErroringEntities")
                    .worldRestart()
                    .define("removeErroringEntities", false);

            removeErroringTileEntities = builder
                    .comment("Set this to true to remove any TileEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
                    .translation("forge.configgui.removeErroringTileEntities")
                    .worldRestart()
                    .define("removeErroringTileEntities", false);

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

            logCascadingWorldGeneration = builder
                    .comment("Log cascading chunk generation issues during terrain population.")
                    .translation("forge.configgui.logCascadingWorldGeneration")
                    .define("logCascadingWorldGeneration", true);

            fixVanillaCascading = builder
                    .comment("Fix vanilla issues that cause worldgen cascading. This DOES change vanilla worldgen so DO NOT report bugs related to world differences if this flag is on.")
                    .translation("forge.configgui.fixVanillaCascading")
                    .define("fixVanillaCascading", false);

            dimensionUnloadQueueDelay = builder
                    .comment("The time in ticks the server will wait when a dimension was queued to unload. This can be useful when rapidly loading and unloading dimensions, like e.g. throwing items through a nether portal a few time per second.")
                    .translation("forge.configgui.dimensionUnloadQueueDelay")
                    .defineInRange("dimensionUnloadQueueDelay", 0, 0, Integer.MAX_VALUE);

            treatEmptyTagsAsAir = builder
                    .comment("Vanilla will treat crafting recipes using empty tags as air, and allow you to craft with nothing in that slot. This changes empty tags to use BARRIER as the item. To prevent crafting with air.")
                    .translation("forge.configgui.treatEmptyTagsAsAir")
                    .define("treatEmptyTagsAsAir", false);

            fixAdvancementLoading = builder
                    .comment("Fix advancement loading to use a proper topological sort. This may have visibility side-effects and can thus be turned off if needed for data-pack compatibility.")
                    .translation("forge.configgui.fixAdvancementLoading")
                    .define("fixAdvancementLoading", true);

            builder.pop();
        }
    }

    /**
     * General configuration that doesn't need to be synchronized but needs to be available before server startup
     */
    public static class Common {
        public final ForgeConfigSpec.ConfigValue<? extends String> defaultWorldType;

        Common(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General configuration settings")
                    .push("general");

            defaultWorldType = builder
                    .comment("Defines a default world type to use. The vanilla default world type is represented by 'default'.",
                             "The modded world types are registry names which should include the registry namespace, such as 'examplemod:example_world_type'.")
                    .translation("forge.configgui.defaultWorldType")
                    .define("defaultWorldType", "default");

            builder.pop();
        }

    }

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {
        public final BooleanValue zoomInMissingModelTextInGui;

        public final BooleanValue forgeCloudsEnabled;

        public final BooleanValue disableStairSlabCulling;

        public final BooleanValue alwaysSetupTerrainOffThread;

        public final BooleanValue forgeLightPipelineEnabled;
        public final BooleanValue experimentalForgeLightPipelineEnabled;

        public final BooleanValue selectiveResourceReloadEnabled;

        public final BooleanValue showLoadWarnings;

        public final BooleanValue useCombinedDepthStencilAttachment;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                   .push("client");

            zoomInMissingModelTextInGui = builder
                .comment("Toggle off to make missing model text in the gui fit inside the slot.")
                .translation("forge.configgui.zoomInMissingModelTextInGui")
                .define("zoomInMissingModelTextInGui", false);

            forgeCloudsEnabled = builder
                .comment("Enable uploading cloud geometry to the GPU for faster rendering.")
                .translation("forge.configgui.forgeCloudsEnabled")
                .define("forgeCloudsEnabled", true);

            disableStairSlabCulling = builder
                .comment("Disable culling of hidden faces next to stairs and slabs. Causes extra rendering, but may fix some resource packs that exploit this vanilla mechanic.")
                .translation("forge.configgui.disableStairSlabCulling")
                .define("disableStairSlabCulling", false);

            alwaysSetupTerrainOffThread = builder
                .comment("Enable Forge to queue all chunk updates to the Chunk Update thread.",
                        "May increase FPS significantly, but may also cause weird rendering lag.",
                        "Not recommended for computers without a significant number of cores available.")
                .translation("forge.configgui.alwaysSetupTerrainOffThread")
                .define("alwaysSetupTerrainOffThread", false);

            forgeLightPipelineEnabled = builder
                .comment("Enable the Forge block rendering pipeline - fixes the lighting of custom models.")
                .translation("forge.configgui.forgeLightPipelineEnabled")
                .define("forgeLightPipelineEnabled", true);
            experimentalForgeLightPipelineEnabled = builder
                .comment("EXPERIMENTAL: Enable the Forge block rendering pipeline - fixes the lighting of custom models.")
                .translation("forge.configgui.forgeLightPipelineEnabled")
                .define("experimentalForgeLightPipelineEnabled", false);

            selectiveResourceReloadEnabled = builder
                .comment("When enabled, makes specific reload tasks such as language changing quicker to run.")
                .translation("forge.configgui.selectiveResourceReloadEnabled")
                .define("selectiveResourceReloadEnabled", true);

            showLoadWarnings = builder
                .comment("When enabled, Forge will show any warnings that occurred during loading.")
                .translation("forge.configgui.showLoadWarnings")
                .define("showLoadWarnings", true);

            useCombinedDepthStencilAttachment = builder
                    .comment("Set to true to use a combined DEPTH_STENCIL attachment instead of two separate ones.")
                    .translation("forge.configgui.useCombinedDepthStencilAttachment")
                    .define("useCombinedDepthStencilAttachment", false);

            builder.pop();
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
    public static void onLoad(final ModConfig.Loading configEvent) {
        LogManager.getLogger().debug(FORGEMOD, "Loaded Forge config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
        LogManager.getLogger().debug(FORGEMOD, "Forge config just got changed on the file system!");
    }

    //General
    //public static boolean disableVersionCheck = false;
    //public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    //public static boolean fixVanillaCascading = false;
}
