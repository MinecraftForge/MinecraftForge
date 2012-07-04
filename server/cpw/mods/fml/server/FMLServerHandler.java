/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BaseMod;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MLProp;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.Profiler;
import net.minecraft.src.ServerRegistry;
import net.minecraft.src.SidedProxy;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.World;
import net.minecraft.src.WorldType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ProxyInjector;
import cpw.mods.fml.common.ReflectionHelper;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.modloader.ModProperty;
import cpw.mods.fml.common.registry.FMLRegistry;

/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #onPreLoad(MinecraftServer)} called from
 * {@link MinecraftServer}
 *
 * Obfuscated code should focus on this class and other members of the "server"
 * (or "client") code
 *
 * The actual mod loading is handled at arms length by {@link Loader}
 *
 * It is expected that a similar class will exist for each target environment:
 * Bukkit and Client side.
 *
 * It should not be directly modified.
 *
 * @author cpw
 *
 */
public class FMLServerHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLServerHandler INSTANCE = new FMLServerHandler();

    /**
     * A reference to the server itself
     */
    private MinecraftServer server;

    /**
     * A handy list of the default overworld biomes
     */
    private BiomeGenBase[] defaultOverworldBiomes;

    /**
     * Called to start the whole game off from
     * {@link MinecraftServer#startServer}
     *
     * @param minecraftServer
     */
    public void onPreLoad(MinecraftServer minecraftServer)
    {
        try
        {
            Class.forName("BaseModMp", false, getClass().getClassLoader());
            MinecraftServer.field_6038_a.severe(""
                    + "Forge Mod Loader has detected that this server has an ModLoaderMP installed alongside Forge Mod Loader.\n"
                    + "This will cause a serious problem with compatibility. To protect your worlds, this minecraft server will now shutdown.\n"
                    + "You should follow the installation instructions of either Minecraft Forge of Forge Mod Loader and NOT install ModLoaderMP \n"
                    + "into the minecraft_server.jar file "
                    + "before this server will be allowed to start up.\n\nFailure to do so will simply result in more startup failures.\n\n"
                    + "The authors of Minecraft Forge and Forge Mod Loader strongly suggest you talk to your mod's authors and get them to\nupdate their "
                    + "requirements. ModLoaderMP is not compatible with Minecraft Forge on the server and they will need to update their mod\n"
                    + "for Minecraft Forge and other server compatibility, unless they are Minecraft Forge mods, in which case they already\n"
                    + "don't need ModLoaderMP and the mod author simply has failed to update his requirements and should be informed appropriately.\n\n"
                    + "The authors of Forge Mod Loader would like to be compatible with ModLoaderMP but it is closed source and owned by SDK.\n"
                    + "SDK, the author of ModLoaderMP, has a standing invitation to submit compatibility patches \n"
                    + "to the open source community project that is Forge Mod Loader so that this incompatibility doesn't last. \n"
                    + "Users who wish to enjoy mods of both types are "
                    + "encouraged to request of SDK that he submit a\ncompatibility patch to the Forge Mod Loader project at \n"
                    + "http://github.com/cpw/FML.\nPosting on the minecraft forums at\nhttp://www.minecraftforum.net/topic/86765- (the MLMP thread)\n"
                    + "may encourage him in this effort. However, I ask that your requests be polite.\n"
                    + "Now, the server has to shutdown so you can reinstall your minecraft_server.jar\nproperly, until such time as we can work together.");
            throw new RuntimeException(
                    "This FML based server has detected an installation of ModLoaderMP alongside. This will cause serious compatibility issues, so the server will now shut down.");
        }
        catch (ClassNotFoundException e)
        {
            // We're safe. continue
        }
        server = minecraftServer;
        ReflectionHelper.detectObfuscation(World.class);
        FMLCommonHandler.instance().beginLoading(this);
        FMLRegistry.registerRegistry(new ServerRegistry());
        Loader.instance().loadMods();
    }

    /**
     * Called a bit later on during server initialization to finish loading mods
     */
    public void onLoadComplete()
    {
        Loader.instance().initializeMods();
    }

    @Override
    public void haltGame(String message, Throwable exception)
    {
        throw new RuntimeException(message, exception);
    }

    public void onPreServerTick()
    {
        FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.GAME));
    }

    public void onPostServerTick()
    {
        FMLCommonHandler.instance().tickEnd(EnumSet.of(TickType.GAME));
    }
    /**
     * Every tick just before world and other ticks occur
     */
    public void onPreWorldTick(World world)
    {
        FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.WORLD), world);
    }

    /**
     * Every tick just after world and other ticks occur
     */
    public void onPostWorldTick(World world)
    {
        FMLCommonHandler.instance().tickEnd(EnumSet.of(TickType.WORLD), world);
    }

    public void onWorldLoadTick()
    {
        FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.WORLDLOAD));
    }
    /**
     * Get the server instance
     *
     * @return
     */
    public MinecraftServer getServer()
    {
        return server;
    }

    /**
     * Get a handle to the server's logger instance
     */
    public Logger getMinecraftLogger()
    {
        return MinecraftServer.field_6038_a;
    }

    /**
     * Called from ChunkProviderServer when a chunk needs to be populated
     *
     * To avoid polluting the worldgen seed, we generate a new random from the
     * world seed and generate a seed from that
     *
     * @param chunkProvider
     * @param chunkX
     * @param chunkZ
     * @param world
     * @param generator
     */
    public void onChunkPopulate(IChunkProvider chunkProvider, int chunkX, int chunkZ, World world, IChunkProvider generator)
    {
        FMLCommonHandler.instance().handleWorldGeneration(chunkX, chunkZ, world.func_22079_j(), world, generator, chunkProvider);
    }

    /**
     * Called from the furnace to lookup fuel values
     *
     * @param itemId
     * @param itemDamage
     * @return
     */
    public int fuelLookup(int itemId, int itemDamage)
    {
        int fv = 0;

        for (ModContainer mod : Loader.getModList())
        {
            fv = Math.max(fv, mod.lookupFuelValue(itemId, itemDamage));
        }

        return fv;
    }

    /**
     * Is the offered class and instance of BaseMod and therefore a ModLoader
     * mod?
     */
    public boolean isModLoaderMod(Class<?> clazz)
    {
        return BaseMod.class.isAssignableFrom(clazz);
    }

    /**
     * Load the supplied mod class into a mod container
     */
    public ModContainer loadBaseModMod(Class<?> clazz, File canonicalFile)
    {
        @SuppressWarnings("unchecked")
        Class<? extends BaseMod> bmClazz = (Class<? extends BaseMod>) clazz;
        return new ModLoaderModContainer(bmClazz, canonicalFile);
    }

    /**
     * Called to notify that an item was picked up from the world
     *
     * @param entityItem
     * @param entityPlayer
     */
    public void notifyItemPickup(EntityItem entityItem, EntityPlayer entityPlayer)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsPickupNotification())
            {
                mod.getPickupNotifier().notifyPickup(entityItem, entityPlayer);
            }
        }
    }

    /**
     * Raise an exception
     *
     * @param exception
     * @param message
     * @param stopGame
     */
    public void raiseException(Throwable exception, String message, boolean stopGame)
    {
        FMLCommonHandler.instance().getFMLLogger().throwing("FMLHandler", "raiseException", exception);
        throw new RuntimeException(exception);
    }

    /**
     * Attempt to dispense the item as an entity other than just as a the item
     * itself
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param xVelocity
     * @param zVelocity
     * @param item
     * @return
     */
    public boolean tryDispensingEntity(World world, double x, double y, double z, byte xVelocity, byte zVelocity, ItemStack item)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsToDispense() && mod.getDispenseHandler().dispense(x, y, z, xVelocity, zVelocity, world, item))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the instance
     */
    public static FMLServerHandler instance()
    {
        return INSTANCE;
    }

    /**
     * Build a list of default overworld biomes
     *
     * @return
     */
    public BiomeGenBase[] getDefaultOverworldBiomes()
    {
        if (defaultOverworldBiomes == null)
        {
            ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>(20);

            for (int i = 0; i < 23; i++)
            {
                if ("Sky".equals(BiomeGenBase.field_35521_a[i].field_6163_m) || "Hell".equals(BiomeGenBase.field_35521_a[i].field_6163_m))
                {
                    continue;
                }

                biomes.add(BiomeGenBase.field_35521_a[i]);
            }

            defaultOverworldBiomes = new BiomeGenBase[biomes.size()];
            biomes.toArray(defaultOverworldBiomes);
        }

        return defaultOverworldBiomes;
    }

    /**
     * Called when an item is crafted
     *
     * @param player
     * @param craftedItem
     * @param craftingGrid
     */
    public void onItemCrafted(EntityPlayer player, ItemStack craftedItem, IInventory craftingGrid)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsCraftingNotification())
            {
                mod.getCraftingHandler().onCrafting(player, craftedItem, craftingGrid);
            }
        }
    }

    /**
     * Called when an item is smelted
     *
     * @param player
     * @param smeltedItem
     */
    public void onItemSmelted(EntityPlayer player, ItemStack smeltedItem)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsCraftingNotification())
            {
                mod.getCraftingHandler().onSmelting(player, smeltedItem);
            }
        }
    }

    /**
     * Called when a chat packet is received
     *
     * @param chat
     * @param player
     * @return true if you want the packet to stop processing and not echo to
     *         the rest of the world
     */
    public boolean handleChatPacket(Packet3Chat chat, EntityPlayer player)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsNetworkPackets() && mod.getNetworkHandler().onChat(chat, player))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when a packet 250 packet is received from the player
     *
     * @param packet
     * @param player
     */
    public void handlePacket250(Packet250CustomPayload packet, EntityPlayer player)
    {
        if ("REGISTER".equals(packet.field_44005_a) || "UNREGISTER".equals(packet.field_44005_a))
        {
            handleClientRegistration(packet, player);
            return;
        }

        ModContainer mod = FMLCommonHandler.instance().getModForChannel(packet.field_44005_a);

        if (mod != null)
        {
            mod.getNetworkHandler().onPacket250Packet(packet, player);
        }
    }

    /**
     * Handle register requests for packet 250 channels
     *
     * @param packet
     */
    private void handleClientRegistration(Packet250CustomPayload packet, EntityPlayer player)
    {
        if (packet.field_44004_c == null)
        {
            return;
        }
        try
        {
            for (String channel : new String(packet.field_44004_c, "UTF8").split("\0"))
            {
                // Skip it if we don't know it
                if (FMLCommonHandler.instance().getModForChannel(channel) == null)
                {
                    continue;
                }

                if ("REGISTER".equals(packet.field_44005_a))
                {
                    FMLCommonHandler.instance().activateChannel(player, channel);
                }
                else
                {
                    FMLCommonHandler.instance().deactivateChannel(player, channel);
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            getMinecraftLogger().warning("Received invalid registration packet");
        }
    }

    /**
     * Handle a login
     *
     * @param loginPacket
     * @param networkManager
     */
    public void handleLogin(Packet1Login loginPacket, NetworkManager networkManager)
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.field_44005_a = "REGISTER";
        packet.field_44004_c = FMLCommonHandler.instance().getPacketRegistry();
        packet.field_44003_b = packet.field_44004_c.length;
        if (packet.field_44003_b > 0)
        {
            networkManager.func_745_a(packet);
        }
    }

    public void announceLogin(EntityPlayer player)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsPlayerTracking())
            {
                mod.getPlayerTracker().onPlayerLogin(player);
            }
        }
    }

    @Override
    public File getMinecraftRootDirectory()
    {
        try
        {
            return server.func_6009_a(".").getCanonicalFile();
        }
        catch (IOException ioe)
        {
            return new File(".");
        }
    }

    /**
     * @param var2
     * @return
     */
    public boolean handleServerCommand(String command, String player, ICommandListener listener)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsConsoleCommands() && mod.getConsoleHandler().handleCommand(command, player, listener))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param player
     */
    public void announceLogout(EntityPlayer player)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsPlayerTracking())
            {
                mod.getPlayerTracker().onPlayerLogout(player);
            }
        }
    }

    /**
     * @param p_28168_1_
     */
    public void announceDimensionChange(EntityPlayer player)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsPlayerTracking())
            {
                mod.getPlayerTracker().onPlayerChangedDimension(player);
            }
        }
    }

    /**
     * @param biome
     */
    public void addBiomeToDefaultWorldGenerator(BiomeGenBase biome)
    {
        WorldType.field_48457_b.addNewBiome(biome);
    }

    /**
     * @param biome
     */
    public void removeBiomeFromDefaultWorldGenerator(BiomeGenBase biome)
    {
        WorldType.field_48457_b.removeBiome(biome);
    }

    @Override
    public Object getMinecraftInstance()
    {
        return server;
    }

    @Override
    public String getCurrentLanguage()
    {
        return StringTranslate.func_25079_a().getCurrentLanguage();
    }

    @Override
    public Properties getCurrentLanguageTable()
    {
        return StringTranslate.func_25079_a().getCurrentLanguageTable();
    }

    @Override
    public String getObjectName(Object instance)
    {
        String objectName;
        if (instance instanceof Item) {
            objectName=((Item)instance).func_20106_a();
        } else if (instance instanceof Block) {
            objectName=((Block)instance).func_20036_e();
        } else if (instance instanceof ItemStack) {
            objectName=Item.field_176_c[((ItemStack)instance).field_855_c].func_35407_a((ItemStack)instance);
        } else {
            throw new IllegalArgumentException(String.format("Illegal object for naming %s",instance));
        }
        objectName+=".name";
        return objectName;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#readMetadataFrom(java.io.InputStream, cpw.mods.fml.common.ModContainer)
     */
    @Override
    public ModMetadata readMetadataFrom(InputStream input, ModContainer mod) throws Exception
    {
        return null;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#profileStart(java.lang.String)
     */
    @Override
    public void profileStart(String profileLabel)
    {
        Profiler.func_40518_a(profileLabel);
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#profileEnd()
     */
    @Override
    public void profileEnd()
    {
        Profiler.func_40517_a();
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getModLoaderPropertyFor(java.lang.reflect.Field)
     */
    @Override
    public ModProperty getModLoaderPropertyFor(Field f)
    {
        if (f.isAnnotationPresent(MLProp.class))
        {
            MLProp prop = f.getAnnotation(MLProp.class);
            return new ModProperty(prop.info(), prop.min(), prop.max(), prop.name());
        }
        return null;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getAdditionalBrandingInformation()
     */
    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getSide()
     */
    @Override
    public Side getSide()
    {
        return Side.SERVER;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#findSidedProxyOn(cpw.mods.fml.common.modloader.BaseMod)
     */
    @Override
    public ProxyInjector findSidedProxyOn(cpw.mods.fml.common.modloader.BaseMod mod)
    {
        for (Field f : mod.getClass().getDeclaredFields())
        {
            if (f.isAnnotationPresent(SidedProxy.class))
            {
                SidedProxy sp = f.getAnnotation(SidedProxy.class);
                return new ProxyInjector(sp.clientSide(), sp.serverSide(), sp.bukkitSide(), f);
            }
        }
        return null;
    }
}
