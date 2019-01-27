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
import static net.minecraftforge.fml.loading.LogMarkers.FORGEMOD;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgeConfig
{    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static final General GENERAL = new General(BUILDER);
    public static final Client CLIENT = new Client(BUILDER);
    
    public static class General {
        
        public final BooleanValue disableVersionCheck;
        
        public final BooleanValue removeErroringEntities; 
        public final BooleanValue removeErroringTileEntities;
    
        public final BooleanValue fullBoundingBoxLadders;
    
        public final DoubleValue zombieBaseSummonChance;
        public final DoubleValue zombieBabyChance ;
    
        public final BooleanValue logCascadingWorldGeneration;
        public final BooleanValue fixVanillaCascading;
    
        public final IntValue dimensionUnloadQueueDelay;
    
        public final IntValue clumpingThreshold;
        
        General(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings that effect both the client and server")
                   .push("general");
            
            disableVersionCheck = builder
                    .comment("Set to true to disable Forge's version check mechanics. Forge queries a small json file on our server for version information. For more details see the ForgeVersion class in our github.")
                    .translation("forge.configgui.disableVersionCheck")
                    .define("disableVersionCheck", false);
            
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
    
    public static class Client {
        
        public final BooleanValue zoomInMissingModelTextInGui;

        public final BooleanValue forgeCloudsEnabled;
        
        public final BooleanValue disableStairSlabCulling;

        public final BooleanValue alwaysSetupTerrainOffThread;

        public final BooleanValue forgeLightPipelineEnabled;
        
        public final BooleanValue selectiveResourceReloadEnabled;
        
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
            
            builder.pop();
        }
    }
    
    private static final ForgeConfigSpec spec = BUILDER.build();
    
    private static final ForgeConfigSpec.Builder CHUNK_BUILDER = new ForgeConfigSpec.Builder();
    
    public static final Chunk CHUNK = new Chunk(CHUNK_BUILDER);
    
    public static class Chunk {
        
        public final BooleanValue enable;

        public final IntValue chunksPerTicket;

        public final IntValue maxTickets;

        public final IntValue playerTicketCount;

        public final IntValue dormantChunkCacheSize;

        public final BooleanValue asyncChunkLoading;

        private final ForgeConfigSpec chunkSpec = new ForgeConfigSpec.Builder()
                .define("modid", "forge").next()
                .defineInRange("maxTickets", 200, 0, Integer.MAX_VALUE).next()
                .defineInRange("chunksPerTicket", 25, 0, Integer.MAX_VALUE).next()
                .build();
        
        private final CommentedConfig modCfgDefault = CommentedConfig.inMemory();

        
        Chunk(ForgeConfigSpec.Builder builder) {
            builder.comment("Default configuration for Forge chunk loading control")
                   .push("defaults");
            
            enable = builder
                .comment("Allow mod overrides, false will use default for everything.")
                .translation("forge.configgui.enableModOverrides")
                .define("enable", true);
    
            chunksPerTicket = builder
                .comment("The default maximum number of chunks a mod can force, per ticket,",
                         "for a mod without an override. This is the maximum number of chunks a single ticket can force.")
                .translation("forge.configgui.maximumChunksPerTicket")
                .defineInRange("chunksPerTicket", 25, 0, Integer.MAX_VALUE);
    
            maxTickets = builder
                .comment("The default maximum ticket count for a mod which does not have an override",
                         "in this file. This is the number of chunk loading requests a mod is allowed to make.")
                .translation("forge.configgui.maximumTicketCount")
                .defineInRange("maxTickets", 200, 0, Integer.MAX_VALUE);
    
            playerTicketCount = builder
                .comment("The number of tickets a player can be assigned instead of a mod. This is shared across all mods and it is up to the mods to use it.")
                .translation("forge.configgui.playerTicketCount")
                .defineInRange("playerTicketCount", 500, 0, Integer.MAX_VALUE);
    
            dormantChunkCacheSize = builder
                .comment("Unloaded chunks can first be kept in a dormant cache for quicker loading times. Specify the size (in chunks) of that cache here")
                .translation("forge.configgui.dormantChunkCacheSize")
                .defineInRange("dormantChunkCacheSize", 0, 0, Integer.MAX_VALUE);
    
            asyncChunkLoading = builder
                .comment("Load chunks asynchronously for players, reducing load on the server thread.",
                         "Can be disabled to help troubleshoot chunk loading issues.")
                .translation("forge.configgui.asyncChunkLoading")
                .define("asyncChunkLoading", true);
            
            chunkSpec.correct(modCfgDefault);
            builder.pop();
        }
        
        private final ConfigValue<List<? extends CommentedConfig>> mods = CHUNK_BUILDER
                .defineList("mods", Lists.newArrayList(modCfgDefault), o -> {
                    if (!(o instanceof CommentedConfig)) return false;
                    return chunkSpec.isCorrect((CommentedConfig) o);
                });
        
        private int getByMod(ConfigValue<Integer> def, String name, String modid) {
            if (!enable.get() || modid == null)
                return def.get();
            
            return mods.get().stream().filter(c -> modid.equals(c.get("modid"))).findFirst()
                    .map(c -> c.<Integer>get(name))
                    .orElseGet(def::get);
        }

        public int maxTickets(@Nullable String modid) {
            return getByMod(maxTickets, "maxTickets", modid);
        }
        
        public int chunksPerTicket(@Nullable String modid) {
            return getByMod(chunksPerTicket, "chunksPerTicket", modid);
        }
    }
    
    public static final ForgeConfigSpec chunk_spec = CHUNK_BUILDER.build();

    private static void loadFrom(final Path configRoot) {
        Path configFile = configRoot.resolve("forge.toml");
        spec.setConfigFile(configFile);
        LogManager.getLogger().debug(FORGEMOD, "Loaded Forge config from {}", configFile);

        configFile = configRoot.resolve("forge_chunks.toml");
        chunk_spec.setConfigFile(configFile);
        LogManager.getLogger().debug(FORGEMOD, "Loaded Forge Chunk config from {}", configFile);
    }

    public static void load() {
        loadFrom(FMLPaths.CONFIGDIR.get());
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
