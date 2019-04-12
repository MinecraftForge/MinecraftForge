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

package net.minecraftforge.common;

import static net.minecraftforge.fml.Logging.CORE;
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

        public final IntValue clumpingThreshold;

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
                    .comment("Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticeable differences in mechanics so default is vanilla behavior. Default: false")
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

            clumpingThreshold = builder
                    .comment("Controls the number threshold at which Packet51 is preferred over Packet52, default and minimum 64, maximum 1024")
                    .translation("forge.configgui.clumpingThreshold")
                    .worldRestart()
                    .defineInRange("clumpingThreshold", 64, 64, 1024);

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

        public final BooleanValue selectiveResourceReloadEnabled;

        public final BooleanValue showLoadWarnings;
        
        public final BooleanValue allowEmissiveItems;

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
                .comment("Enable forge to queue all chunk updates to the Chunk Update thread.",
                        "May increase FPS significantly, but may also cause weird rendering lag.",
                        "Not recommended for computers without a significant number of cores available.")
                .translation("forge.configgui.alwaysSetupTerrainOffThread")
                .define("alwaysSetupTerrainOffThread", false);

            forgeLightPipelineEnabled = builder
                .comment("Enable the forge block rendering pipeline - fixes the lighting of custom models.")
                .translation("forge.configgui.forgeLightPipelineEnabled")
                .define("forgeLightPipelineEnabled", true);

            selectiveResourceReloadEnabled = builder
                .comment("When enabled, makes specific reload tasks such as language changing quicker to run.")
                .translation("forge.configgui.selectiveResourceReloadEnabled")
                .define("selectiveResourceReloadEnabled", true);

            showLoadWarnings = builder
                .comment("When enabled, forge will show any warnings that occurred during loading")
                .translation("forge.configgui.showloadwarnings")
                .define("showLoadWarnings", true);
            
            allowEmissiveItems = builder
                .comment("Allow item rendering to detect emissive quads and draw them properly. This allows glowing blocks to look the same in item form, but incurs a very slight performance hit.")
                .translation("forge.configgui.allowEmissiveItems")
                .define("allowEmissiveItems", true);

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


    static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        LogManager.getLogger().debug(FORGEMOD, "Loaded forge config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        LogManager.getLogger().fatal(CORE, "Forge config just got changed on the file system!");
    }

    //General
    //public static boolean disableVersionCheck = false;
    //public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    //public static boolean fixVanillaCascading = false;

    //Client
    //public static int clumpingThreshold = 64;
    //public static boolean zoomInMissingModelTextInGui = false;
    //public static boolean disableStairSlabCulling = false;
    //public static boolean alwaysSetupTerrainOffThread = false;
    //public static boolean forgeLightPipelineEnabled = true;
    //public static boolean selectiveResourceReloadEnabled = false;

    //TODO
    //public static int[] blendRanges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };

    /*
    private static void syncConfig(boolean load)
    {
        //prop = config.get(Configuration.CATEGORY_CLIENT, "biomeSkyBlendRange", new int[] { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 });
        //prop.setComment("Control the range of sky blending for colored skies in biomes.");
        //prop.setLanguageKey("forge.configgui.biomeSkyBlendRange");
        //blendRanges = prop.getIntList();
        //propOrder.add(prop.getName());
    }

    /**
     * By subscribing to the OnConfigChangedEvent we are able to execute code when our config screens are closed.
     * This implementation uses the optional configID string to handle multiple Configurations using one event handler.
     * /
    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event)
    {
        if (getMetadata().modId.equals(event.getModID()))
        {
            if ("chunkLoader".equals(event.getConfigID()))
            {
                ForgeChunkManager.syncConfigDefaults();
                ForgeChunkManager.loadConfiguration();
            }
            else
            {
                boolean tmpStairs = disableStairSlabCulling;

                syncConfig(false);

                if (event.isWorldRunning() && tmpStairs != disableStairSlabCulling)
                {
                    FMLCommonHandler.instance().reloadRenderers();
                }

            }
        }
    }
    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        UsernameCache.setUsername(event.player.getPersistentID(), event.player.getGameProfile().getName());
    }

    @Subscribe
    public void postInit(InterModProcessEvent evt)
    {
        ForgeChunkManager.loadConfiguration();
    }
    */

}
