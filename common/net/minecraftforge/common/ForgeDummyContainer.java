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
import net.minecraftforge.common.network.ForgeConnectionHandler;
import net.minecraftforge.common.network.ForgeNetworkHandler;
import net.minecraftforge.common.network.ForgePacketHandler;
import net.minecraftforge.common.network.ForgeTinyPacketHandler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.NetworkMod;

import static net.minecraftforge.common.ForgeVersion.*;

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

        prop = config.get(Configuration.CATEGORY_GENERAL, "disableStitchedFileSaving", true);
        prop.comment = "Set this to just disable the texture stitcher from writing the 'debug.stitched_{name}.png file to disc. Just a small performance tweak. Default: true";
        disableStitchedFileSaving = prop.getBoolean(true);

        prop = config.get(Configuration.CATEGORY_GENERAL, "forceDuplicateFluidBlockCrash", true);
        prop.comment = "Set this to force a crash if more than one block attempts to link back to the same Fluid. Enabled by default.";
        forceDuplicateFluidBlockCrash = prop.getBoolean(true);

        if (!forceDuplicateFluidBlockCrash)
        {
            FMLLog.warning("Disabling forced crashes on duplicate Fluid Blocks - USE AT YOUR OWN RISK");
        }

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
    	ForgeChunkManager.loadConfiguration();
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
}
