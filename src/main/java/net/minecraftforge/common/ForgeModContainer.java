package net.minecraftforge.common;

import static net.minecraftforge.common.ForgeVersion.buildVersion;
import static net.minecraftforge.common.ForgeVersion.majorVersion;
import static net.minecraftforge.common.ForgeVersion.minorVersion;
import static net.minecraftforge.common.ForgeVersion.revisionVersion;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.network.ForgeNetworkHandler;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.server.command.ForgeCommand;

import com.google.common.collect.ImmutableList;
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
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ForgeModContainer extends DummyModContainer implements WorldAccessContainer
{
    public static int clumpingThreshold = 64;
    public static boolean removeErroringEntities = false;
    public static boolean removeErroringTileEntities = false;
    public static boolean disableStitchedFileSaving = false;
    public static boolean forceDuplicateFluidBlockCrash = true;
    public static boolean fullBoundingBoxLadders = false;
    public static double zombieSummonBaseChance = 0.1;
    public static int[] blendRanges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };
    public static float zombieBabyChance = 0.05f;
    public static boolean shouldSortRecipies = true;
    public static boolean disableVersionCheck = false;

    public ForgeModContainer()
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

        prop = config.get(Configuration.CATEGORY_GENERAL, "biomeSkyBlendRange", new int[] { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 });
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

        prop = config.get(CATEGORY_GENERAL, "disableVersionCheck", disableVersionCheck);
        prop.comment = "Set to true to disable Forge's version check mechanics, Forge queries a small json file on our server for version information. For more details see the ForgeVersion class in our github.";
        disableVersionCheck = prop.getBoolean(disableVersionCheck);

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
        NetworkRegistry.INSTANCE.register(this, this.getClass(), null, evt.getASMHarvestedData());
        ForgeNetworkHandler.registerChannel(this, evt.getSide());
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt)
    {
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
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
        forgeData.setTag("DimensionData", dimData);
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

    @Subscribe
    public void mappingChanged(FMLModIdMappingEvent evt)
    {
        Blocks.fire.rebuildFireInfo();
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

    @Override
    public List<String> getOwnedPackages()
    {
        // All the packages which are part of forge. Only needs updating if new logic is added
        // that requires event handlers
        return ImmutableList.of(
                "net.minecraftforge.classloading",
                "net.minecraftforge.client",
                "net.minecraftforge.client.event",
                "net.minecraftforge.client.event.sound",
                "net.minecraftforge.client.model",
                "net.minecraftforge.client.model.obj",
                "net.minecraftforge.client.model.techne",
                "net.minecraftforge.common",
                "net.minecraftforge.common.config",
                "net.minecraftforge.common.network",
                "net.minecraftforge.common.util",
                "net.minecraftforge.event",
                "net.minecraftforge.event.brewing",
                "net.minecraftforge.event.entity",
                "net.minecraftforge.event.entity.item",
                "net.minecraftforge.event.entity.living",
                "net.minecraftforge.event.entity.minecart",
                "net.minecraftforge.event.entity.player",
                "net.minecraftforge.event.terraingen",
                "net.minecraftforge.event.world",
                "net.minecraftforge.fluids",
                "net.minecraftforge.oredict",
                "net.minecraftforge.server",
                "net.minecraftforge.server.command",
                "net.minecraftforge.transformers"
                );
    }
}
