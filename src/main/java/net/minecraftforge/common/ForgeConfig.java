/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.config.ForgeConfigSpec;

public class ForgeConfig
{
    private static ForgeConfig INSTANCE = new ForgeConfig();
    private static ForgeConfigSpec spec = new ForgeConfigSpec.Builder()
         //General
        .comment("General settings that effect both the client and server")
        .push("general")
            .comment("Set to true to disable Forge's version check mechanics. Forge queries a small json file on our server for version information. For more details see the ForgeVersion class in our github.")
            .translation("forge.configgui.disableVersionCheck")
            .define("disableVersionCheck", false)

            .comment("Set this to true to remove any Entity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
            .translation("forge.configgui.removeErroringEntities")
            .worldRestart()
            .define("removeErroringEntities", false)

            .comment("Set this to true to remove any TileEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.")
            .translation("forge.configgui.removeErroringTileEntities")
            .worldRestart()
            .define("removeErroringTileEntities", false)

            .comment("Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticeable differences in mechanics so default is vanilla behavior. Default: false")
            .translation("forge.configgui.fullBoundingBoxLadders")
            .worldRestart()
            .define("fullBoundingBoxLadders", false)

            .comment("Base zombie summoning spawn chance. Allows changing the bonus zombie summoning mechanic.")
            .translation("forge.configgui.zombieBaseSummonChance")
            .worldRestart()
            .defineInRange("zombieBaseSummonChance", 0.1D, 0.0D, 1.0D)

            .comment("Chance that a zombie (or subclass) is a baby. Allows changing the zombie spawning mechanic.")
            .translation("forge.configgui.zombieBabyChance")
            .worldRestart()
            .defineInRange("zombieBabyChance", 0.05D, 0.0D, 1.0D)

            .comment("Log cascading chunk generation issues during terrain population.")
            .translation("forge.configgui.logCascadingWorldGeneration")
            .define("logCascadingWorldGeneration", true)

            .comment("Fix vanilla issues that cause worldgen cascading. This DOES change vanilla worldgen so DO NOT report bugs related to world differences if this flag is on.")
            .translation("forge.configgui.fixVanillaCascading")
            .define("fixVanillaCascading", false)


            .comment("The time in ticks the server will wait when a dimension was queued to unload. This can be useful when rapidly loading and unloading dimensions, like e.g. throwing items through a nether portal a few time per second.")
            .translation("forge.configgui.dimensionUnloadQueueDelay")
            .defineInRange("dimensionUnloadQueueDelay", 0, 0, Integer.MAX_VALUE)
        .pop()

        //Client
        .comment("Client only settings, mostly things related to rendering")
        .push("client")
            .comment("Controls the number threshold at which Packet51 is preferred over Packet52, default and minimum 64, maximum 1024")
            .translation("forge.configgui.clumpingThreshold")
            .worldRestart()
            .defineInRange("clumpingThreshold", 64, 64, 1024)

            .comment("Toggle off to make missing model text in the gui fit inside the slot.")
            .translation("forge.configgui.zoomInMissingModelTextInGui")
            .define("zoomInMissingModelTextInGui", false)

            .comment("Enable uploading cloud geometry to the GPU for faster rendering.")
            .translation("forge.configgui.forgeCloudsEnabled")
            .define("forgeCloudsEnabled", true)

            .comment("Disable culling of hidden faces next to stairs and slabs. Causes extra rendering, but may fix some resource packs that exploit this vanilla mechanic.")
            .translation("forge.configgui.disableStairSlabCulling")
            .define("disableStairSlabCulling", false)

            .comment("Enable forge to queue all chunk updates to the Chunk Update thread.",
                    "May increase FPS significantly, but may also cause weird rendering lag.",
                    "Not recommended for computers without a significant number of cores available.")
            .translation("forge.configgui.alwaysSetupTerrainOffThread")
            .define("alwaysSetupTerrainOffThread", false)

            .comment("Enable the forge block rendering pipeline - fixes the lighting of custom models.")
            .translation("forge.configgui.forgeLightPipelineEnabled")
            .define("forgeLightPipelineEnabled", true)

            .comment("When enabled, makes specific reload tasks such as language changing quicker to run.")
            .translation("forge.configgui.selectiveResourceReloadEnabled")
            .define("selectiveResourceReloadEnabled", true)
        .pop()


        .build();

    private CommentedFileConfig configData;
    private void loadFrom(final Path configFile)
    {
        configData = CommentedFileConfig.builder(configFile).sync()
                .autosave()
                //.autoreload()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        if (!spec.isCorrect(configData)) {
            LogManager.getLogger().warn(CORE, "Configuration file {} is not correct. Correcting", configFile);
            spec.correct(configData, (action, path, incorrectValue, correctedValue) ->
                    LogManager.getLogger().warn(CORE, "Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue));
            configData.save();
        }
    }

    public static void load()
    {
        Path configFile = Paths.get("config").resolve("forge.toml");
        INSTANCE.loadFrom(configFile);
        LogManager.getLogger().debug(CORE, "Loaded FML config from {}", configFile);
    }

    //TODO: Make this less duplciate? Maybe static CfgEntry<T> zombieBaseSummonChance = create((spec, name) -> spec.comment().translation().define(name), "zombieBaseSummonChance")
    public static class GENERAL
    {
        public static double zombieBaseSummonChance() {
            return ForgeConfig.INSTANCE.configData.<Double>getOrElse("general.zombieBaseSummonChance", (double)0.01F);
        }
        public static float zombieBabyChance() {
            return ForgeConfig.INSTANCE.configData.<Float>getOrElse("general.zombieBabyChance", 0.05F);
        }
    }

    //General
    //public static boolean disableVersionCheck = false;
    //public static boolean removeErroringEntities = false;
    //public static boolean removeErroringTileEntities = false;
    //public static boolean fullBoundingBoxLadders = false;
    //public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    //public static boolean fixVanillaCascading = false;
    //public static int dimensionUnloadQueueDelay = 0;

    //Client
    //public static int clumpingThreshold = 64;
    //public static boolean zoomInMissingModelTextInGui = false;
    //public static boolean forgeCloudsEnabled = true;
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
    public void postInit(FMLPostInitializationEvent evt)
    {
        ForgeChunkManager.loadConfiguration();
    }
    */

}
