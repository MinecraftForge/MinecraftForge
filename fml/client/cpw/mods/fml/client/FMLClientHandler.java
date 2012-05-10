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
package cpw.mods.fml.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.ClientRegistry;
import net.minecraft.src.CommonRegistry;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderPlayer;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.World;
import net.minecraft.src.WorldType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.IKeyHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModContainer.TickType;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

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
public class FMLClientHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLClientHandler INSTANCE = new FMLClientHandler();

    /**
     * A reference to the server itself
     */
    private Minecraft client;
    
    /**
     * A handy list of the default overworld biomes
     */
    private BiomeGenBase[] defaultOverworldBiomes;

    private int nextRenderId = 30;

    private static HashMap<String, ArrayList<OverrideInfo>> overrideInfo = new HashMap<String, ArrayList<OverrideInfo>>();
    private static HashMap<Integer, ModLoaderModContainer> blockModelIds = new HashMap<Integer, ModLoaderModContainer>();

    /**
     * Called to start the whole game off from
     * {@link MinecraftServer#startServer}
     * 
     * @param minecraftServer
     */
    public void onPreLoad(Minecraft minecraft)
    {
/*        try
        {
            Class.forName("BaseModMp", false, getClass().getClassLoader());
            Minecraft.field_6301_A.severe(""
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
        }*/
        client = minecraft;
        FMLCommonHandler.instance().registerSidedDelegate(this);
        CommonRegistry.registerRegistry(new ClientRegistry());
        Loader.instance().loadMods();
    }

    /**
     * Called a bit later on during initialization to finish loading mods
     * Also initializes key bindings
     * 
     */
    public void onLoadComplete()
    {
        Loader.instance().initializeMods();
        client.field_6304_y.loadModKeySettings(harvestKeyBindings());
    }

    public KeyBinding[] harvestKeyBindings() {
        List<IKeyHandler> allKeys=FMLCommonHandler.instance().gatherKeyBindings();
        KeyBinding[] keys=new KeyBinding[allKeys.size()];
        int i=0;
        for (IKeyHandler key : allKeys) {
            keys[i++]=(KeyBinding)key.getKeyBinding();
        }
        return keys;
    }
    /**
     * Every tick just before world and other ticks occur
     */
    public void onPreWorldTick()
    {
        FMLCommonHandler.instance().worldTickStart();
    }

    /**
     * Every tick just after world and other ticks occur
     */
    public void onPostWorldTick()
    {
        FMLCommonHandler.instance().worldTickEnd();
    }

    public void onRenderTickStart(float partialTickTime)
    {
        FMLCommonHandler.instance().tickStart(TickType.RENDER, partialTickTime);
    }
    
    public void onRenderTickEnd(float partialTickTime)
    {
        FMLCommonHandler.instance().tickEnd(TickType.RENDER, partialTickTime);
    }
    /**
     * Get the server instance
     * 
     * @return
     */
    public Minecraft getClient()
    {
        return client;
    }

    /**
     * Get a handle to the client's logger instance
     * The client actually doesn't have one- so we return null
     */
    public Logger getMinecraftLogger()
    {
        return null;
    }

    /**
     * Called from ChunkProvider when a chunk needs to be populated
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
        Random fmlRandom = new Random(world.func_22138_q());
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        fmlRandom.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ world.func_22138_q());

        for (ModContainer mod : Loader.getModList())
        {
            if (mod.generatesWorld())
            {
                mod.getWorldGenerator().generate(fmlRandom, chunkX, chunkZ, world, generator, chunkProvider);
            }
        }
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
    public static FMLClientHandler instance()
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
                if ("Sky".equals(BiomeGenBase.field_35486_a[i].field_6504_m) || "Hell".equals(BiomeGenBase.field_35486_a[i].field_6504_m))
                {
                    continue;
                }

                biomes.add(BiomeGenBase.field_35486_a[i]);
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
    public void handlePacket250(Packet250CustomPayload packet)
    {
        if ("REGISTER".equals(packet.field_44012_a) || "UNREGISTER".equals(packet.field_44012_a))
        {
            handleServerRegistration(packet);
            return;
        }

        ModContainer mod = FMLCommonHandler.instance().getModForChannel(packet.field_44012_a);

        if (mod != null)
        {
            mod.getNetworkHandler().onPacket250Packet(packet);
        }
    }

    /**
     * Handle register requests for packet 250 channels
     * 
     * @param packet
     */
    private void handleServerRegistration(Packet250CustomPayload packet)
    {
        if (packet.field_44011_c == null)
        {
            return;
        }
        try
        {
            for (String channel : new String(packet.field_44011_c, "UTF8").split("\0"))
            {
                // Skip it if we don't know it
                if (FMLCommonHandler.instance().getModForChannel(channel) == null)
                {
                    continue;
                }

                if ("REGISTER".equals(packet.field_44012_a))
                {
                    FMLCommonHandler.instance().activateChannel(client.field_6322_g,channel);
                }
                else
                {
                    FMLCommonHandler.instance().deactivateChannel(client.field_6322_g,channel);
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            getMinecraftLogger().warning("Received invalid registration packet");
        }
    }

    /**
     * Are we a server?
     */
    @Override
    public boolean isServer()
    {
        return false;
    }

    /**
     * Are we a client?
     */
    @Override
    public boolean isClient()
    {
        return true;
    }

    @Override
    public File getMinecraftRootDirectory()
    {
        return client.field_6297_D;
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
        WorldType.field_48635_b.addNewBiome(biome);
    }

    /**
     * Return the minecraft instance
     */
    @Override
    public Object getMinecraftInstance()
    {
        return client;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getCurrentLanguage()
     */
    @Override
    public String getCurrentLanguage()
    {
        return StringTranslate.func_20162_a().func_44024_c();
    }

    /**
     * @param armor
     * @return
     */
    public int addNewArmourRendererPrefix(String armor)
    {
        return RenderPlayer.addNewArmourPrefix(armor);
    }

    public void addNewTextureOverride(String textureToOverride, String overridingTexturePath, int location) {
        if (!overrideInfo.containsKey(textureToOverride))
        {
            overrideInfo.put(textureToOverride, new ArrayList<OverrideInfo>());
        }
        ArrayList<OverrideInfo> list = overrideInfo.get(textureToOverride);
        OverrideInfo info = new OverrideInfo();
        info.index = location;
        info.override = overridingTexturePath;
        info.texture = textureToOverride;
        list.add(info);
    }
    /**
     * @param mod
     * @param inventoryRenderer
     * @return
     */
    public int obtainBlockModelIdFor(BaseMod mod, boolean inventoryRenderer)
    {
        ModLoaderModContainer mlmc=ModLoaderHelper.registerRenderHelper(mod);
        int renderId=nextRenderId++;
        mlmc.addRenderHandler(new BlockRenderInfo(renderId, inventoryRenderer));
        blockModelIds.put(renderId, mlmc);
        return renderId;
    }

    /**
     * @param renderEngine
     * @param path
     * @return
     */
    public BufferedImage loadImageFromTexturePack(RenderEngine renderEngine, String path) throws IOException
    {
        InputStream image=renderEngine.getTexturePackList().field_6534_a.func_6481_a(path);
        if (image==null) {
            throw new RuntimeException(String.format("The requested image path %s is not found",path));
        }
        BufferedImage result=ImageIO.read(image);
        if (result==null)
        {
            throw new RuntimeException(String.format("The requested image path %s appears to be corrupted",path));
        }
        return result;
    }

    /**
     * @param player
     * @param gui
     */
    public void displayGuiScreen(EntityPlayer player, GuiScreen gui)
    {
        if (client.field_22009_h==player && gui != null) {
            client.func_6272_a(gui);
        }
    }

    /**
     * @param mod
     * @param keyHandler
     * @param allowRepeat
     */
    public void registerKeyHandler(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat)
    {
        ModLoaderModContainer mlmc=ModLoaderHelper.registerKeyHelper(mod);
        mlmc.addKeyHandler(new KeyBindingHandler(keyHandler, allowRepeat));
    }

    /**
     * @param renderer
     * @param world
     * @param x
     * @param y
     * @param z
     * @param block
     * @param modelID
     * @return
     */
    public boolean onRenderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID)
    {
        ModLoaderModContainer mod = blockModelIds.get(modelID);
        if (mod == null)
        {
            return false;
        }
        return ((BaseMod)mod.getMod()).renderWorldBlock(renderer, world, x, y, z, block, modelID);
    }

    /**
     * @param renderer
     * @param block
     * @param metadata
     * @param modelID
     */
    public void onRenderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {
        ModLoaderModContainer mod = blockModelIds.get(modelID);
        if (mod != null)
        {
            ((BaseMod)mod.getMod()).renderInvBlock(renderer, block, metadata, modelID);
        }
    }
}
