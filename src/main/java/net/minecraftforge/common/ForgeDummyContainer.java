package net.minecraftforge.common;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.network.ForgeConnectionHandler;
import net.minecraftforge.common.network.ForgeNetworkHandler;
import net.minecraftforge.common.network.ForgePacketHandler;
import net.minecraftforge.common.network.ForgeTinyPacketHandler;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.server.command.ForgeCommand;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.NetworkMod;
import static net.minecraftforge.common.ForgeVersion.*;
import static net.minecraftforge.common.Configuration.*;

@NetworkMod(
        channels = "FORGE",
        connectionHandler = ForgeConnectionHandler.class,
        packetHandler     = ForgePacketHandler.class,
        tinyPacketHandler = ForgeTinyPacketHandler.class
    )
public class ForgeDummyContainer extends DummyModContainer implements WorldAccessContainer
{
    public static int clumpingThreshold = 64;
    public static boolean removeErroringEntities = false;
    public static boolean removeErroringTileEntities = false;
    public static boolean disableStitchedFileSaving = false;
    public static boolean forceDuplicateFluidBlockCrash = true;
    public static boolean fullBoundingBoxLadders = false;
    public static double zombieSummonBaseChance = 0.1;
    public static int[] blendRanges = { 20, 15, 10, 5 };
    public static float zombieBabyChance = 0.05f;
    public static boolean shouldSortRecipies = false;

    public ForgeDummyContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId       = "Forge";
        meta.name        = "Minecraft Forge";
        meta.version     = String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
        meta.credits     = "Made possible with help from many people";
        meta.authorList  = Arrays.asList("LexManos", "Eloraam", "Spacetoad");
        meta.description = "Minecraft Forge is a common open source API allowing a broad range of mods " +
                           "to work cooperatively together. It allows many mods to be created without " +
                           "them editing the main Minecraft code.";
        meta.url         = "http://MinecraftForge.net";
        meta.updateUrl   = "http://MinecraftForge.net/forum/index.php/topic,5.0.html";
        meta.screenshots = new String[0];
        meta.logoFile    = "/forge_logo.png";

        Configuration config = null;
        File cfgFile = new File(Loader.instance().getConfigDir(), "forge.cfg");
        try
        {
            config = new Configuration(cfgFile);
        }
        catch (Exception e)
        {
            System.out.println("Error loading forge.cfg, deleting file and resetting: ");
            e.printStackTrace();

            if (cfgFile.exists())
                cfgFile.delete();

            config = new Configuration(cfgFile);
        }
        if (!config.isChild)
        {
            config.load();
            Property enableGlobalCfg = config.get(Configuration.CATEGORY_GENERAL, "enableGlobalConfig", false);
            if (enableGlobalCfg.getBoolean(false))
            {
                Configuration.enableGlobalConfig();
            }
        }
        Property prop;
        prop = config.get(Configuration.CATEGORY_GENERAL, "clumpingThreshold", 64);
        prop.comment = "Controls the number threshold at which Packet51 is preferred over Packet52, default and minimum 64, maximum 1024";
        clumpingThreshold = prop.getInt(64);
        if (clumpingThreshold > 1024 || clumpingThreshold < 64)
        {
            clumpingThreshold = 64;
            prop.set(64);
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "removeErroringEntities", false);
        prop.comment = "Set this to just remove any TileEntity that throws a error in there update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.";
        removeErroringEntities = prop.getBoolean(false);

        if (removeErroringEntities)
        {
            FMLLog.warning("Enabling removal of erroring Entities - USE AT YOUR OWN RISK");
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "removeErroringTileEntities", false);
        prop.comment = "Set this to just remove any TileEntity that throws a error in there update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.";
        removeErroringTileEntities = prop.getBoolean(false);

        if (removeErroringTileEntities)
        {
            FMLLog.warning("Enabling removal of erroring Tile Entities - USE AT YOUR OWN RISK");
        }

        //prop = config.get(Configuration.CATEGORY_GENERAL, "disableStitchedFileSaving", true);
        //prop.comment = "Set this to just disable the texture stitcher from writing the 'debug.stitched_{name}.png file to disc. Just a small performance tweak. Default: true";
        //disableStitchedFileSaving = prop.getBoolean(true);

        prop = config.get(Configuration.CATEGORY_GENERAL, "fullBoundingBoxLadders", false);
        prop.comment = "Set this to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticable differences in mechanics so default is vanilla behavior. Default: false";
        fullBoundingBoxLadders = prop.getBoolean(false);

        prop = config.get(Configuration.CATEGORY_GENERAL, "forceDuplicateFluidBlockCrash", true);
        prop.comment = "Set this to force a crash if more than one block attempts to link back to the same Fluid. Enabled by default.";
        forceDuplicateFluidBlockCrash = prop.getBoolean(true);

        if (!forceDuplicateFluidBlockCrash)
        {
            FMLLog.warning("Disabling forced crashes on duplicate Fluid Blocks - USE AT YOUR OWN RISK");
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "biomeSkyBlendRange", new int[] { 20, 15, 10, 5 });
        prop.comment = "Control the range of sky blending for colored skies in biomes.";
        blendRanges = prop.getIntList();
        
        prop = config.get(Configuration.CATEGORY_GENERAL, "zombieBaseSummonChance", 0.1);
        prop.comment = "Base zombie summoning spawn chance. Allows changing the bonus zombie summoning mechanic.";
        zombieSummonBaseChance = prop.getDouble(0.1);
        
        prop = config.get(Configuration.CATEGORY_GENERAL, "zombieBabyChance", 0.05);
        prop.comment = "Chance that a zombie (or subclass) is a baby. Allows changing the zombie spawning mechanic.";
        zombieBabyChance = (float) prop.getDouble(0.05);

        prop = config.get(CATEGORY_GENERAL, "sortRecipies", shouldSortRecipies);
        prop.comment = "Set to true to enable the post initlization sorting of crafting recipes using Froge's sorter. May cause desyncing on conflicting recipies. ToDo: Set to true by default in 1.7";
        shouldSortRecipies = prop.getBoolean(shouldSortRecipies);

        if (config.hasChanged())
        {
            config.save();
        }
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void modConstruction(FMLConstructionEvent evt)
    {
        FMLLog.info("Registering Forge Packet Handler");
        try
        {
            FMLNetworkHandler.instance().registerNetworkMod(new ForgeNetworkHandler(this));
            FMLLog.info("Succeeded registering Forge Packet Handler");
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Failed to register packet handler for Forge");
        }
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt)
    {
        ForgeChunkManager.captureConfig(evt.getModConfigurationDirectory());
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt)
    {
        BiomeDictionary.registerAllBiomesAndGenerateEvents();
        ForgeChunkManager.loadConfiguration();
    }

    @Subscribe
    public void onAvalible(FMLLoadCompleteEvent evt)
    {
        if (shouldSortRecipies)
        {
            RecipeSorter.sortCraftManager();
        }
    }

    @Subscribe
    public void serverStarting(FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new ForgeCommand(evt.getServer()));
    }
    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        NBTTagCompound forgeData = new NBTTagCompound();
        NBTTagCompound dimData = DimensionManager.saveDimensionDataMap();
        forgeData.setCompoundTag("DimensionData", dimData);
        return forgeData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        if (tag.hasKey("DimensionData"))
        {
            DimensionManager.loadDimensionDataMap(tag.hasKey("DimensionData") ? tag.getCompoundTag("DimensionData") : null);
        }
    }

    @Override
    public File getSource()
    {
        return FMLForgePlugin.forgeLocation;
    }
    @Override
    public Class<?> getCustomResourcePackClass()
    {
        if (getSource().isDirectory())
        {
            return FMLFolderResourcePack.class;
        }
        else
        {
            return FMLFileResourcePack.class;
        }
    }
}
