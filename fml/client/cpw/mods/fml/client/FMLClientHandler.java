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

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.ClientRegistry;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GameSettings;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.MLProp;
import net.minecraft.src.MinecraftImpl;
import net.minecraft.src.ModTextureStatic;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.Profiler;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderManager;
import net.minecraft.src.RenderPlayer;
import net.minecraft.src.SidedProxy;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TexturePackBase;
import net.minecraft.src.UnexpectedThrowable;
import net.minecraft.src.World;
import net.minecraft.src.WorldType;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModLoaderContainer;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.IKeyHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ProxyInjector;
import cpw.mods.fml.common.ReflectionHelper;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
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

    private TexturePackBase fallbackTexturePack;

    private NetClientHandler networkClient;

    private ModContainer animationCallbackMod;

    // Cached lookups
    private HashMap<String, ArrayList<OverrideInfo>> overrideInfo = new HashMap<String, ArrayList<OverrideInfo>>();
    private HashMap<Integer, BlockRenderInfo> blockModelIds = new HashMap<Integer, BlockRenderInfo>();
    private HashMap<KeyBinding, ModContainer> keyBindings = new HashMap<KeyBinding, ModContainer>();
    private List<IKeyHandler> keyHandlers = new ArrayList<IKeyHandler>();
    private HashSet<OverrideInfo> animationSet = new HashSet<OverrideInfo>();

    private List<TextureFX> addedTextureFX = new ArrayList<TextureFX>();

    private boolean firstTick;
    /**
     * Called to start the whole game off from
     * {@link MinecraftServer#startServer}
     *
     * @param minecraftServer
     */

    private OptifineModContainer optifineContainer;

    private boolean guiLoaded;

    public void onPreLoad(Minecraft minecraft)
    {
        client = minecraft;
        ReflectionHelper.detectObfuscation(World.class);
        FMLCommonHandler.instance().beginLoading(this);
        FMLRegistry.registerRegistry(new ClientRegistry());
        try
        {
            Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            optifineContainer = new OptifineModContainer(optifineConfig);
        }
        catch (Exception e)
        {
            // OPTIFINE not found
            optifineContainer = null;
        }
        if (optifineContainer != null)
        {
            ModMetadata optifineMetadata;
            try
            {
                optifineMetadata = readMetadataFrom(Loader.instance().getModClassLoader().getResourceAsStream("optifinemod.info"), optifineContainer);
                optifineContainer.setMetadata(optifineMetadata);
            }
            catch (Exception e)
            {
                //not available
            }
            FMLCommonHandler.instance().getFMLLogger().info(String.format("Forge Mod Loader has detected optifine %s, enabling compatibility features",optifineContainer.getVersion()));
        }
        try
        {
            Loader.instance().loadMods();
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
    }

    @Override
    public void haltGame(String message, Throwable t)
    {
        client.func_28003_b(new UnexpectedThrowable(message, t));
    }
    /**
     * Called a bit later on during initialization to finish loading mods
     * Also initializes key bindings
     *
     */
    public void onLoadComplete()
    {
        try
        {
            Loader.instance().initializeMods();
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        for (ModContainer mod : Loader.getModList()) {
            mod.gatherRenderers(RenderManager.field_1233_a.getRendererList());
            for (Render r : RenderManager.field_1233_a.getRendererList().values()) {
                r.func_4009_a(RenderManager.field_1233_a);
            }
        }
        // Load the key bindings into the settings table

        GameSettings gs = client.field_6304_y;
        KeyBinding[] modKeyBindings = harvestKeyBindings();
        KeyBinding[] allKeys = new KeyBinding[gs.field_1564_t.length + modKeyBindings.length];
        System.arraycopy(gs.field_1564_t, 0, allKeys, 0, gs.field_1564_t.length);
        System.arraycopy(modKeyBindings, 0, allKeys, gs.field_1564_t.length, modKeyBindings.length);
        gs.field_1564_t = allKeys;
        gs.func_6519_a();

        // Mark this as a "first tick"

        firstTick = true;
    }

    public KeyBinding[] harvestKeyBindings() {
        List<IKeyHandler> allKeys=FMLCommonHandler.instance().gatherKeyBindings();
        KeyBinding[] keys=new KeyBinding[allKeys.size()];
        int i=0;
        for (IKeyHandler key : allKeys) {
            keys[i++]=(KeyBinding)key.getKeyBinding();
            keyBindings.put((KeyBinding) key.getKeyBinding(), key.getOwningContainer());
        }
        keyHandlers = allKeys;
        return keys;
    }
    /**
     * Every tick just before world and other ticks occur
     */
    public void onPreWorldTick()
    {
        if (client.field_6324_e != null) {
            // For the client world ticks and game ticks are the same
            FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.WORLD), client.field_6324_e, client.field_6313_p, client.field_6324_e);
            FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.GAME,TickType.WORLDGUI), 0.0f, client.field_6313_p, client.field_6324_e);
        }
    }

    /**
     * Every tick just after world and other ticks occur
     */
    public void onPostWorldTick()
    {
        if (client.field_6324_e != null) {
            // For the client world ticks and game ticks are the same
            FMLCommonHandler.instance().tickEnd(EnumSet.of(TickType.WORLD), client.field_6324_e, client.field_6313_p, client.field_6324_e);
            FMLCommonHandler.instance().tickEnd(EnumSet.of(TickType.GAME,TickType.WORLDGUI), 0.0f, client.field_6313_p, client.field_6324_e);
        }
        for (IKeyHandler entry : keyHandlers)
        {
            entry.onEndTick();
        }
    }

    public void onWorldLoadTick()
    {
        if (client.field_6324_e != null) {
            if (firstTick)
            {
                loadTextures(fallbackTexturePack);
                firstTick = false;
            }
            FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.WORLDLOAD));
        }
    }

    public void onRenderTickStart(float partialTickTime)
    {
        FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.RENDER,TickType.GUI), partialTickTime, client.field_6313_p);
    }

    public void onRenderTickEnd(float partialTickTime)
    {
        if (!guiLoaded)
        {
            FMLCommonHandler.instance().rescheduleTicks();
            FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.GUILOAD), partialTickTime, client.field_6313_p);
            guiLoaded = true;
        }
        FMLCommonHandler.instance().tickEnd(EnumSet.of(TickType.RENDER,TickType.GUI), partialTickTime, client.field_6313_p);
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
        FMLCommonHandler.instance().handleWorldGeneration(chunkX, chunkZ, world.func_22138_q(), world, generator, chunkProvider);
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
    public boolean handleChatPacket(Packet3Chat chat)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsNetworkPackets() && mod.getNetworkHandler().onChat(chat))
            {
                return true;
            }
        }

        return false;
    }

    public void handleServerLogin(Packet1Login loginPacket, NetClientHandler handler, NetworkManager networkManager)
    {
        this.networkClient=handler;
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.field_44012_a = "REGISTER";
        packet.field_44011_c = FMLCommonHandler.instance().getPacketRegistry();
        packet.field_44010_b = packet.field_44011_c.length;
        if (packet.field_44010_b > 0)
        {
            networkManager.func_972_a(packet);
        }
        for (ModContainer mod : Loader.getModList()) {
            mod.getNetworkHandler().onServerLogin(handler);
        }
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

    public Properties getCurrentLanguageTable() {
        return StringTranslate.func_20162_a().getTranslationTable();
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
        FMLCommonHandler.instance().getFMLLogger().log(Level.FINE, String.format("Overriding %s @ %d with %s. %d slots remaining",textureToOverride, location, overridingTexturePath, SpriteHelper.freeSlotCount(textureToOverride)));
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
        BlockRenderInfo bri=new BlockRenderInfo(renderId, inventoryRenderer, mlmc);
        blockModelIds.put(renderId, bri);
        return renderId;
    }

    /**
     * @param renderEngine
     * @param path
     * @return
     */
    public BufferedImage loadImageFromTexturePack(RenderEngine renderEngine, String path) throws IOException
    {
        InputStream image=client.field_6298_C.field_6534_a.func_6481_a(path);
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
        mlmc.addKeyHandler(new KeyBindingHandler(keyHandler, allowRepeat, mlmc));
    }

    /**
     * @param renderer
     * @param world
     * @param x
     * @param y
     * @param z
     * @param block
     * @param modelId
     * @return
     */
    public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelId)
    {
        if (!blockModelIds.containsKey(modelId)) {
            return false;
        }
        BlockRenderInfo bri = blockModelIds.get(modelId);
        return bri.renderWorldBlock(world, x, y, z, block, modelId, renderer);
    }

    /**
     * @param renderer
     * @param block
     * @param metadata
     * @param modelID
     */
    public void renderInventoryBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {
        if (!blockModelIds.containsKey(modelID)) {
            return;
        }
        BlockRenderInfo bri=blockModelIds.get(modelID);
        bri.renderInventoryBlock(block, metadata, modelID, renderer);
    }

    /**
     * @param p_1219_0_
     * @return
     */
    public boolean renderItemAsFull3DBlock(int modelId)
    {
        BlockRenderInfo bri = blockModelIds.get(modelId);
        if (bri!=null) {
            return bri.shouldRender3DInInventory();
        }
        return false;
    }

    public void registerTextureOverrides(RenderEngine renderer) {
        for (ModContainer mod : Loader.getModList()) {
            registerAnimatedTexturesFor(mod);
        }

        for (OverrideInfo animationOverride : animationSet) {
            renderer.func_1066_a(animationOverride.textureFX);
            addedTextureFX.add(animationOverride.textureFX);
            FMLCommonHandler.instance().getFMLLogger().finer(String.format("Registered texture override %d (%d) on %s (%d)", animationOverride.index, animationOverride.textureFX.field_1126_b, animationOverride.textureFX.getClass().getSimpleName(), animationOverride.textureFX.field_1128_f));
        }

        for (String fileToOverride : overrideInfo.keySet()) {
            for (OverrideInfo override : overrideInfo.get(fileToOverride)) {
                try
                {
                    BufferedImage image=loadImageFromTexturePack(renderer, override.override);
                    ModTextureStatic mts=new ModTextureStatic(override.index, 1, override.texture, image);
                    renderer.func_1066_a(mts);
                    addedTextureFX.add(mts);
                    FMLCommonHandler.instance().getFMLLogger().finer(String.format("Registered texture override %d (%d) on %s (%d)", override.index, mts.field_1126_b, override.texture, mts.field_1128_f));
                }
                catch (IOException e)
                {
                    FMLCommonHandler.instance().getFMLLogger().throwing("FMLClientHandler", "registerTextureOverrides", e);
                }
            }
        }
    }

    /**
     * @param mod
     */
    private void registerAnimatedTexturesFor(ModContainer mod)
    {
        this.animationCallbackMod=mod;
        mod.requestAnimations();
        this.animationCallbackMod=null;
    }

    public String getObjectName(Object instance) {
        String objectName;
        if (instance instanceof Item) {
            objectName=((Item)instance).func_20009_a();
        } else if (instance instanceof Block) {
            objectName=((Block)instance).func_20013_i();
        } else if (instance instanceof ItemStack) {
            objectName=Item.field_233_c[((ItemStack)instance).field_1617_c].func_21011_b((ItemStack)instance);
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
        JsonNode root=new JdomParser().func_27366_a(new InputStreamReader(input));
        List<JsonNode> lst=root.func_27217_b();
        JsonNode modinfo = null;
        for (JsonNode tmodinfo : lst) {
            if (mod.getName().equals(tmodinfo.func_27213_a("modid"))) {
                modinfo = tmodinfo;
                break;
            }
        }
        if (modinfo == null) {
            FMLCommonHandler.instance().getFMLLogger().fine(String.format("Unable to process JSON modinfo file for %s", mod.getName()));
            return null;
        }
        ModMetadata meta=new ModMetadata(mod);
        try {
            meta.name=modinfo.func_27213_a("name");
            meta.description=modinfo.func_27213_a("description").replace("\r", "");
            meta.version=modinfo.func_27213_a("version");
            meta.credits=modinfo.func_27213_a("credits");
            List authors=modinfo.func_27217_b("authors");
            StringBuilder sb=new StringBuilder();
            for (int i=0; i<authors.size(); i++) {
                meta.authorList.add(((JsonNode)authors.get(i)).func_27216_b());
            }
            meta.logoFile=modinfo.func_27213_a("logoFile");
            meta.url=modinfo.func_27213_a("url");
            meta.updateUrl=modinfo.func_27213_a("updateUrl");
            meta.parent=modinfo.func_27213_a("parent");
            List screenshots=modinfo.func_27217_b("screenshots");
            meta.screenshots=new String[screenshots.size()];
            for (int i=0; i<screenshots.size(); i++) {
                meta.screenshots[i]=((JsonNode)screenshots.get(i)).func_27216_b();
            }
        } catch (Exception e) {
            FMLCommonHandler.instance().getFMLLogger().log(Level.FINE, String.format("An error occured reading the info file for %s",mod.getName()), e);
            if (Item.class.getPackage() != null) //Print the error if we are in MCP so mod devs see it
            {
                System.out.println(String.format("An error occured reading the info file for %s",mod.getName()));
                e.printStackTrace();
            }
        }
        return meta;
    }

    public void pruneOldTextureFX(TexturePackBase var1, List<TextureFX> effects)
    {
        ListIterator<TextureFX> li = addedTextureFX.listIterator();
        while (li.hasNext())
        {
            TextureFX tex = li.next();
            if (tex instanceof FMLTextureFX)
            {
                if (((FMLTextureFX)tex).unregister(client.field_6315_n, effects))
                {
                    li.remove();
                }
            }
            else
            {
                effects.remove(tex);
                li.remove();
            }
        }
    }

    /**
     * @param p_6531_1_
     */
    public void loadTextures(TexturePackBase texturePack)
    {
        registerTextureOverrides(client.field_6315_n);
    }

    /**
     * @param field_6539_c
     */
    public void onEarlyTexturePackLoad(TexturePackBase fallback)
    {
        if (client==null) {
            // We're far too early- let's wait
            this.fallbackTexturePack=fallback;
        } else {
            loadTextures(fallback);
        }
    }

    /**
     * @param packet
     */
    public void sendPacket(Packet packet)
    {
        if (this.networkClient!=null) {
            this.networkClient.func_847_a(packet);
        }
    }

    /**
     * @param anim
     */
    public void addAnimation(TextureFX anim)
    {
        if (animationCallbackMod==null) {
            return;
        }
        OverrideInfo info=new OverrideInfo();
        info.index=anim.field_1126_b;
        info.imageIndex=anim.field_1128_f;
        info.textureFX=anim;
        if (animationSet.contains(info)) {
            animationSet.remove(info);
        }
        animationSet.add(info);
    }

    @Override
    public void profileStart(String profileLabel) {
        Profiler.func_40663_a(profileLabel);
    }

    @Override
    public void profileEnd() {
        Profiler.func_40662_b();
    }

    /**
     *
     */
    public void preGameLoad(String user, String sessionToken)
    {
        // Currently this does nothing, but it's possible I could relaunch Minecraft in a new classloader if I wished
        Minecraft.fmlReentry(user, sessionToken);
    }

    public void onTexturePackChange(RenderEngine engine, TexturePackBase texturepack, List<TextureFX> effects)
    {
        FMLClientHandler.instance().pruneOldTextureFX(texturepack, effects);

        for (TextureFX tex : effects)
        {
            if (tex instanceof ITextureFX)
            {
                ((ITextureFX)tex).onTexturePackChanged(engine, texturepack, getTextureDimensions(tex));
            }
        }

        FMLClientHandler.instance().loadTextures(texturepack);
    }

    private HashMap<Integer, Dimension> textureDims = new HashMap<Integer, Dimension>();
    private IdentityHashMap<TextureFX, Integer> effectTextures = new IdentityHashMap<TextureFX, Integer>();
    public void setTextureDimensions(int id, int width, int height, List<TextureFX> effects)
    {
        Dimension dim = new Dimension(width, height);
        textureDims.put(id, dim);

        for (TextureFX tex : effects)
        {
            if (getEffectTexture(tex) == id && tex instanceof ITextureFX)
            {
                ((ITextureFX)tex).onTextureDimensionsUpdate(width, height);
            }
        }
    }

    public Dimension getTextureDimensions(TextureFX effect)
    {
        return getTextureDimensions(getEffectTexture(effect));
    }

    public Dimension getTextureDimensions(int id)
    {
        return textureDims.get(id);
    }

    public int getEffectTexture(TextureFX effect)
    {
        Integer id = effectTextures.get(effect);
        if (id != null)
        {
            return id;
        }

        int old = GL11.glGetInteger(GL_TEXTURE_BINDING_2D);

        effect.func_782_a(client.field_6315_n);

        id = GL11.glGetInteger(GL_TEXTURE_BINDING_2D);

        GL11.glBindTexture(GL_TEXTURE_2D, old);

        effectTextures.put(effect, id);

        return id;
    }

    public boolean onUpdateTextureEffect(TextureFX effect)
    {
        Logger log = FMLCommonHandler.instance().getFMLLogger();
        ITextureFX ifx = (effect instanceof ITextureFX ? ((ITextureFX)effect) : null);

        if (ifx != null && ifx.getErrored())
        {
            return false;
        }

        String name = effect.getClass().getSimpleName();
        Profiler.func_40663_a(name);

        try
        {
            if (optifineContainer == null)
            {
                effect.func_783_a();
            }
        }
        catch (Exception e)
        {
            log.warning(String.format("Texture FX %s has failed to animate. Likely caused by a texture pack change that they did not respond correctly to", name));
            if (ifx != null)
            {
                ifx.setErrored(true);
            }
            Profiler.func_40662_b();
            return false;
        }
        Profiler.func_40662_b();

        if (ifx != null)
        {
            Dimension dim = getTextureDimensions(effect);
            int target = ((dim.width >> 4) * (dim.height >> 4)) << 2;
            if (effect.field_1127_a.length != target)
            {
                log.warning(String.format("Detected a texture FX sizing discrepancy in %s (%d, %d)", name, effect.field_1127_a.length, target));
                ifx.setErrored(true);
                return false;
            }
        }
        return true;
    }

    //Quick and dirty image scaling, no smoothing or fanciness, meant for speed as it will be called every tick.
    public void scaleTextureFXData(byte[] data, ByteBuffer buf, int target, int length)
    {
        int sWidth = (int)Math.sqrt(data.length / 4);
        int factor = target / sWidth;
        byte[] tmp = new byte[4];

        buf.clear();

        if (factor > 1)
        {
            for (int y = 0; y < sWidth; y++)
            {
                int sRowOff = sWidth * y;
                int tRowOff = target * y * factor;
                for (int x = 0; x < sWidth; x++)
                {
                    int sPos = (x + sRowOff) * 4;
                    tmp[0] = data[sPos + 0];
                    tmp[1] = data[sPos + 1];
                    tmp[2] = data[sPos + 2];
                    tmp[3] = data[sPos + 3];

                    int tPosTop = (x * factor) + tRowOff;
                    for (int y2 = 0; y2 < factor; y2++)
                    {
                        buf.position((tPosTop + (y2 * target)) * 4);
                        for (int x2 = 0; x2 < factor; x2++)
                        {
                            buf.put(tmp);
                        }
                    }
                }
            }
        }

        buf.position(0).limit(length);
    }

    public void onPreRegisterEffect(TextureFX effect)
    {
        Dimension dim = getTextureDimensions(effect);
        if (effect instanceof ITextureFX)
        {
            ((ITextureFX)effect).onTextureDimensionsUpdate(dim.width, dim.height);
        }
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

    /**
     * @param mods
     */
    public void addSpecialModEntries(ArrayList<ModContainer> mods)
    {
        mods.add(new FMLModLoaderContainer());
        if (optifineContainer!=null) {
            mods.add(optifineContainer);
        }
    }

    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        if (optifineContainer!=null)
        {
            return Arrays.asList(String.format("Optifine %s",optifineContainer.getVersion()));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }

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

    /**
     * @param biome
     */
    public void removeBiomeFromDefaultWorldGenerator(BiomeGenBase biome)
    {
        WorldType.field_48635_b.removeBiome(biome);
    }
}
