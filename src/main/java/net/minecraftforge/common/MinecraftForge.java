package net.minecraftforge.common;

import java.util.concurrent.Callable;

import com.google.common.collect.ObjectArrays;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks.SeedEntry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class MinecraftForge
{
    /**
     * The core Forge EventBusses, all events for Forge will be fired on these,
     * you should use this to register all your listeners.
     * This replaces every register*Handler() function in the old version of Forge.
     * TERRAIN_GEN_BUS for terrain gen events
     * ORE_GEN_BUS for ore gen events
     * EVENT_BUS for everything else
     */
    public static final EventBus EVENT_BUS = new EventBus();
    public static final EventBus TERRAIN_GEN_BUS = new EventBus();
    public static final EventBus ORE_GEN_BUS = new EventBus();
    public static final String MC_VERSION = Loader.MC_VERSION;

    static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();

    /**
     * Register a new seed to be dropped when breaking tall grass.
     *
     * @param seed The item to drop as a seed.
     * @param weight The relative probability of the seeds,
     *               where wheat seeds are 10.
     */
    public static void addGrassSeed(ItemStack seed, int weight)
    {
        ForgeHooks.seedList.add(new SeedEntry(seed, weight));
    }

   /**
    * Method invoked by FML before any other mods are loaded.
    */
   public static void initialize()
   {
       FMLLog.info("MinecraftForge v%s Initialized", ForgeVersion.getVersion());

       OreDictionary.getOreName(0);

       //Force these classes to be defined, Should prevent derp error hiding.
       @SuppressWarnings("unused")
       CrashReport fake = new CrashReport("ThisIsFake", new Exception("Not real"));
       //Lets init World's crash report inner classes to prevent them from hiding errors.
       String[] handlers = {
           "net.minecraft.world.World$1",
           "net.minecraft.world.World$2",
           "net.minecraft.world.World$3",
           "net.minecraft.world.World$4",
           "net.minecraft.world.chunk.Chunk$1",
           "net.minecraft.world.chunk.Chunk$2",
           "net.minecraft.world.chunk.Chunk$3",
           "net.minecraft.command.server.CommandBlockLogic$1",
           "net.minecraft.command.server.CommandBlockLogic$2",
           "net.minecraft.crash.CrashReportCategory$1",
           "net.minecraft.crash.CrashReportCategory$2",
           "net.minecraft.crash.CrashReportCategory$3",
           "net.minecraft.crash.CrashReportCategory$4",
           "net.minecraft.crash.CrashReportCategory$5",
           "net.minecraft.crash.CrashReportCategory$Entry",
           "net.minecraft.entity.Entity$1",
           "net.minecraft.entity.Entity$2",
           "net.minecraft.entity.Entity$3",
           "net.minecraft.entity.Entity$4",
           "net.minecraft.entity.EntityTracker$1",
           "net.minecraft.world.gen.layer.GenLayer$1",
           "net.minecraft.world.gen.layer.GenLayer$2",
           "net.minecraft.entity.player.InventoryPlayer$1",
           "net.minecraft.world.gen.structure.MapGenStructure$1",
           "net.minecraft.world.gen.structure.MapGenStructure$2",
           "net.minecraft.world.gen.structure.MapGenStructure$3",
           "net.minecraft.server.MinecraftServer$3",
           "net.minecraft.server.MinecraftServer$4",
           "net.minecraft.server.MinecraftServer$5",
           "net.minecraft.nbt.NBTTagCompound$1",
           "net.minecraft.nbt.NBTTagCompound$2",
           "net.minecraft.network.NetHandlerPlayServer$2",
           "net.minecraft.network.NetworkSystem$3",
           "net.minecraft.tileentity.TileEntity$1",
           "net.minecraft.tileentity.TileEntity$2",
           "net.minecraft.tileentity.TileEntity$3",
           "net.minecraft.world.storage.WorldInfo$1",
           "net.minecraft.world.storage.WorldInfo$2",
           "net.minecraft.world.storage.WorldInfo$3",
           "net.minecraft.world.storage.WorldInfo$4",
           "net.minecraft.world.storage.WorldInfo$5",
           "net.minecraft.world.storage.WorldInfo$6",
           "net.minecraft.world.storage.WorldInfo$7",
           "net.minecraft.world.storage.WorldInfo$8",
           "net.minecraft.world.storage.WorldInfo$9"
       };
       String[] client = {
           "net.minecraft.client.Minecraft$3",
           "net.minecraft.client.Minecraft$4",
           "net.minecraft.client.Minecraft$5",
           "net.minecraft.client.Minecraft$6",
           "net.minecraft.client.Minecraft$7",
           "net.minecraft.client.Minecraft$8",
           "net.minecraft.client.Minecraft$9",
           "net.minecraft.client.Minecraft$10",
           "net.minecraft.client.Minecraft$11",
           "net.minecraft.client.Minecraft$12",
           "net.minecraft.client.Minecraft$13",
           "net.minecraft.client.Minecraft$14",
           "net.minecraft.client.Minecraft$15",
           "net.minecraft.client.multiplayer.WorldClient$1",
           "net.minecraft.client.multiplayer.WorldClient$2",
           "net.minecraft.client.multiplayer.WorldClient$3",
           "net.minecraft.client.multiplayer.WorldClient$4",
           "net.minecraft.client.particle,EffectRenderer$1",
           "net.minecraft.client.particle,EffectRenderer$2",
           "net.minecraft.client.particle,EffectRenderer$3",
           "net.minecraft.client.particle,EffectRenderer$4",
           "net.minecraft.client.renderer.EntityRenderer$1",
           "net.minecraft.client.renderer.EntityRenderer$2",
           "net.minecraft.client.renderer.EntityRenderer$3",
           "net.minecraft.server.integrated.IntegratedServer$1",
           "net.minecraft.server.integrated.IntegratedServer$2",
           "net.minecraft.client.renderer.RenderGlobal$1",
           "net.minecraft.client.renderer.entity.RenderItem$1",
           "net.minecraft.client.renderer.entity.RenderItem$2",
           "net.minecraft.client.renderer.entity.RenderItem$3",
           "net.minecraft.client.renderer.entity.RenderItem$4",
           "net.minecraft.client.renderer.texture.TextureAtlasSprite$1",
           "net.minecraft.client.renderer.texture.TextureManager$1",
           "net.minecraft.client.renderer.texture.TextureMap$1",
           "net.minecraft.client.renderer.texture.TextureMap$2",
           "net.minecraft.client.renderer.texture.TextureMap$3"
       };
       String[] server = {
           "net.minecraft.server.dedicated.DedicatedServer$3",
           "net.minecraft.server.dedicated.DedicatedServer$4"
       };
       if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
           handlers = ObjectArrays.concat(handlers, client, String.class);
       else
           handlers = ObjectArrays.concat(handlers, server, String.class);

       //FMLLog.info("Preloading CrashReport classes", ForgeVersion.getVersion());
       for (String s : handlers)
       {
           //FMLLog.info("\t" + s);
           try
           {
               Class<?> cls = Class.forName(s, false, MinecraftForge.class.getClassLoader());
               if (cls != null && !Callable.class.isAssignableFrom(cls))
               {
                   //FMLLog.info("\t% s is not a instance of callable!", s);
               }
           }
           catch (Exception e){}
       }

       UsernameCache.load();
       // Load before all the mods, so MC owns the MC fluids
       FluidRegistry.validateFluidRegistry();
   }
}
