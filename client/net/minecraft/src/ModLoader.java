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
package net.minecraft.src;

import static cpw.mods.fml.relauncher.Side.CLIENT;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texturefx.TextureFX;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.*;
import net.minecraft.stats.Achievement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.SpriteHelper;
import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.client.modloader.ModLoaderKeyBindingHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;

public class ModLoader
{
    public static final String fmlMarker = "This is an FML marker";
    // TODO dirty workaround for millinaire
    @Deprecated
    public static final Map<String,Map<String,String>> localizedStrings=Collections.emptyMap();

    /**
     * Adds localization info for an achievement, Not used on the server.
     *
     * @param achievement The achievement to name
     * @param name The name
     * @param description The description
     */
    public static void addAchievementDesc(Achievement achievement, String name, String description)
    {
        String achName=achievement.func_75970_i();
        addLocalization(achName, name);
        addLocalization(achName+".desc", description);
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * {@link GameRegistry#getFuelValue(ItemStack)}
     *
     * @param id The Item ID
     * @param metadata The Item Metadata
     * @return The fuel strength, in ticks, 0 if unhandled
     */
    @Deprecated
    public static int addAllFuel(int id, int metadata)
    {
        return 0;
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void addAllRenderers(Map<Class<? extends Entity>, Render> renderers)
    {
    }

    @SideOnly(CLIENT)
    public static void addAnimation(TextureFX anim)
    {
        TextureFXManager.instance().addAnimation(anim);
    }

    /**
     * Adds a new prefix to the armor texture list
     *
     * {@link RenderingRegistry#addNewArmourRendererPrefix(String)}
     *
     * @param armor The new armor prefix
     * @return The new armor index
     */
    @SideOnly(CLIENT)
    public static int addArmor(String armor)
    {
        return RenderingRegistry.addNewArmourRendererPrefix(armor);
    }

    /**
     * This method adds the supplied biome to the set of candidate biomes for the default world generator type.
     *
     * @param biome The biome to add
     */
    public static void addBiome(BiomeGenBase biome)
    {
        GameRegistry.addBiome(biome);
    }

    public static void addEntityTracker(BaseMod mod, Class<? extends Entity> entityClass, int entityTypeId, int updateRange, int updateInterval, boolean sendVelocityInfo)
    {
        ModLoaderHelper.buildEntityTracker(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
    }

    public static void addCommand(ICommand command)
    {
        ModLoaderHelper.addCommand(command);
    }

    /**
     * Add a behaviour to the dispenser
     *
     * @param item
     * @param behavior
     */
    public static void addDispenserBehavior(Item item, IBehaviorDispenseItem behavior)
    {
    	BlockDispenser.field_82527_a.func_82595_a(item, behavior);
    }
    /**
     * Add localization for the specified string
     *
     * @param key Key
     * @param value Value
     */
    public static void addLocalization(String key, String value)
    {
        addLocalization(key, "en_US", value);
    }

    /**
     * Add localization for the specified string
     *
     * @param key Key
     * @param lang Language identifier
     * @param value Value
     */
    public static void addLocalization(String key, String lang, String value)
    {
        LanguageRegistry.instance().addStringLocalization(key, lang, value);
    }

    /**
     * Name the specified minecraft object with the supplied name
     *
     * @param instance Item to name
     * @param name The name to give it
     */
    public static void addName(Object instance, String name)
    {
        addName(instance,"en_US",name);
    }

    /**
     * Unimplemented on the server as it does not generate names
     *
     * @param instance Item to name
     * @param lang Languge identifier
     * @param name Name to give it
     */
    public static void addName(Object instance, String lang, String name)
    {
        LanguageRegistry.instance().addNameForObject(instance, lang, name);
    }

    /**
     * Attempts to register a small image to be applied to a larger texture image,
     * typically how old ModLoader mods add custom Item/Block textures.
     *
     * Forge mods should use setTextureFile in Item/Block
     *
     * Will return the icon index it was applied to.
     *
     * Unimplemented on the server as it does not render textures
     *
     * @param fileToOverride The texture to apply the new image to
     * @param fileToAdd The new image
     * @return The 'icon index' in the main image that the new image will be applied to
     */
    @SideOnly(CLIENT)
    public static int addOverride(String fileToOverride, String fileToAdd)
    {
        return RenderingRegistry.addTextureOverride(fileToOverride, fileToAdd);
    }

    /**
     * Attempts to register a small image to be applied to a larger texture image,
     * typically how old ModLoader mods add custom Item/Block textures.
     *
     * Forge mods should use setTextureFile in Item/Block
     *
     * Unimplemented on the server as it does not render textures
     *
     * @param path The texture to apply the new image to
     * @param overlayPath The new image
     * @param index Where on the texture to apply it
     */
    @SideOnly(CLIENT)
    public static void addOverride(String path, String overlayPath, int index)
    {
        RenderingRegistry.addTextureOverride(path, overlayPath, index);
    }

    /**
     * Add a Shaped Recipe
     *
     * @param output The result
     * @param params The input
     */
    public static void addRecipe(ItemStack output, Object... params)
    {
        GameRegistry.addRecipe(output, params);
    }

    /**
     * Add a shapeless recipe
     *
     * @param output The result
     * @param params The input
     */
    public static void addShapelessRecipe(ItemStack output, Object... params)
    {
        GameRegistry.addShapelessRecipe(output, params);
    }

    /**
     * Add a new product to be smelted
     *
     * @param input
     * @param output
     */
    public static void addSmelting(int input, ItemStack output)
    {
        GameRegistry.addSmelting(input, output, 1.0f);
    }

    /**
     * Add a new product to be smelted
     *
     * @param input
     * @param output
     */
    public static void addSmelting(int input, ItemStack output, float experience)
    {
        GameRegistry.addSmelting(input, output, experience);
    }
    /**
     * Add a mob to the spawn list
     *
     * @param entityClass
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     */
    public static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList)
    {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, WorldType.base12Biomes);
    }

    /**
     * Add a mob to the spawn list
     *
     * @param entityClass
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     * @param biomes
     */
    public static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, biomes);
    }

    /**
     * Add a mob to the spawn list
     *
     * @param entityName
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     */
    public static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList)
    {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, WorldType.base12Biomes);
    }

    /**
     * Add a mob to the spawn list
     *
     * @param entityName
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     * @param biomes
     */
    public static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, biomes);
    }

    public static void addTrade(int profession, TradeEntry entry)
    {
        ModLoaderHelper.registerTrade(profession, entry);
    }
    /**
     * Send a packet from the client
     * @param packet
     */
    public static void clientSendPacket(Packet packet)
    {
        PacketDispatcher.sendPacketToServer(packet);
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param xVel
     * @param zVel
     * @param item
     * @return Always false, not implemented here
     */
    @Deprecated
    public static boolean dispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item)
    {
        return false;
    }

    /**
     * Remove a container and drop all the items in it on the ground around
     *
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public static void genericContainerRemoval(World world, int x, int y, int z)
    {
/*        TileEntity te = world.func_603_b(x, y, z);

        if (!(te instanceof IInventory))
        {
            return;
        }

        IInventory inv = (IInventory)te;

        for (int l = 0; l < inv.func_469_c(); l++)
        {
            ItemStack itemstack = inv.func_468_c(l);

            if (itemstack == null)
            {
                continue;
            }

            float f = world.field_1037_n.nextFloat() * 0.8F + 0.1F;
            float f1 = world.field_1037_n.nextFloat() * 0.8F + 0.1F;
            float f2 = world.field_1037_n.nextFloat() * 0.8F + 0.1F;

            while (itemstack.field_1615_a > 0)
            {
                int i1 = world.field_1037_n.nextInt(21) + 10;

                if (i1 > itemstack.field_1615_a)
                {
                    i1 = itemstack.field_1615_a;
                }

                itemstack.field_1615_a -= i1;
                EntityItem entityitem = new EntityItem(world, (float)te.field_823_f + f, (float)te.field_822_g + f1, (float)te.field_821_h + f2, new ItemStack(itemstack.field_1617_c, i1, itemstack.func_21181_i()));
                float f3 = 0.05F;
                entityitem.field_608_an = (float) world.field_1037_n.nextGaussian() * f3;
                entityitem.field_607_ao = (float) world.field_1037_n.nextGaussian() * f3 + 0.2F;
                entityitem.field_606_ap = (float) world.field_1037_n.nextGaussian() * f3;

                if (itemstack.func_40710_n())
                {
                    entityitem.field_801_a.func_40706_d((NBTTagCompound) itemstack.func_40709_o().func_40195_b());
                }

                world.func_674_a(entityitem);
            }
        }
*/    }

    /**
     * Get a list of all BaseMod loaded into the system
     * {@link ModLoaderModContainer#findAll}
     *
     * @return A list containing all loaded ModLoader mods
     */
    public static List<BaseMod> getLoadedMods()
    {
        return ModLoaderModContainer.findAll(BaseMod.class);
    }

    /**
     * Get a logger instance {@link FMLCommonHandler#getFMLLogger()}
     *
     * @return The current logger
     */
    public static Logger getLogger()
    {
        return FMLLog.getLogger();
    }

    @SideOnly(CLIENT)
    public static Minecraft getMinecraftInstance()
    {
        return FMLClientHandler.instance().getClient();
    }

    public static MinecraftServer getMinecraftServerInstance()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    /**
     * Get a value from a field using reflection
     * {@link ObfuscationReflectionHelper#getPrivateValue(Class, Object, int)}
     *
     * @param instanceclass
     * @param instance
     * @param fieldindex
     * @return The value in the specified field.
     */
    public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, int fieldindex)
    {
        return ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, fieldindex);
    }

    /**
     * Get a value from a field using reflection
     * {@link ObfuscationReflectionHelper#getPrivateValue(Class, Object, String[])}
     *
     * @param instanceclass
     * @param instance
     * @param field
     * @return The value in the specified field.
     */
    public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field)
    {
        return ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, field);
    }

    /**
     * Stubbed method on the server to return a unique model id
     *
     */
    @SideOnly(CLIENT)
    public static int getUniqueBlockModelID(BaseMod mod, boolean inventoryRenderer)
    {
        return ModLoaderClientHelper.obtainBlockModelIdFor(mod, inventoryRenderer);
    }

    /**
     * Get a new unique entity id
     * {@link EntityRegistry#findGlobalUniqueEntityId()}
     *
     * @return A unique entity ID
     */
    public static int getUniqueEntityId()
    {
        return EntityRegistry.findGlobalUniqueEntityId();
    }

    @SideOnly(CLIENT)
    public static int getUniqueSpriteIndex(String path)
    {
        return SpriteHelper.getUniqueSpriteIndex(path);
    }

    /**
     * To properly implement packet 250 protocol you should always check your
     * channel is active prior to sending the packet
     *
     * @param player
     * @param channel
     * @return If the channel is registered to the current connection.
     */
    public static boolean isChannelActive(EntityPlayer player, String channel)
    {
        return NetworkRegistry.instance().isChannelActive(channel, (Player)player);
    }

    @SideOnly(CLIENT)
    public static boolean isGUIOpen(Class<? extends GuiScreen> gui)
    {
        return FMLClientHandler.instance().getClient().field_71462_r != null && FMLClientHandler.instance().getClient().field_71462_r.equals(gui);
    }

    /**
     * Is the named mod loaded?
     * {@link Loader#isModLoaded(String)}
     *
     * @param modname
     * @return If the specified mod is loaded
     */
    public static boolean isModLoaded(String modname)
    {
        return Loader.isModLoaded(modname);
    }

    /**
     * Implemented elsewhere
     */
    @Deprecated
    public static void loadConfig()
    {
    }

    @SideOnly(CLIENT)
    public static BufferedImage loadImage(RenderEngine renderEngine, String path) throws Exception
    {
        return TextureFXManager.instance().loadImageFromTexturePack(renderEngine, path);
    }

    /**
     * Call in from elsewhere. Unimplemented here.
     * @param player
     * @param item
     */
    @Deprecated
    public static void onItemPickup(EntityPlayer player, ItemStack item)
    {
    }
    /**
     * Call in from elsewhere. Unimplemented here.
     */
    @Deprecated
    @SideOnly(CLIENT)
    public static void onTick(float tick, Minecraft game)
    {
    }

    @SideOnly(CLIENT)
    public static void openGUI(EntityPlayer player, GuiScreen gui)
    {
        FMLClientHandler.instance().displayGuiScreen(player, gui);
    }

    @Deprecated
    public static void populateChunk(IChunkProvider generator, int chunkX, int chunkZ, World world)
    {
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param packet
     */
    @Deprecated
    public static void receivePacket(Packet250CustomPayload packet)
    {
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static KeyBinding[] registerAllKeys(KeyBinding[] keys)
    {
        return keys;
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void registerAllTextureOverrides(RenderEngine cache)
    {
    }

    /**
     * Register a new block
     *
     * @param block
     */
    public static void registerBlock(Block block)
    {
        GameRegistry.registerBlock(block);
    }

    /**
     * Register a new block
     *
     * @param block
     * @param itemclass
     */
    public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass)
    {
        GameRegistry.registerBlock(block, itemclass);
    }

    public static void registerContainerID(BaseMod mod, int id)
    {
        ModLoaderHelper.buildGuiHelper(mod, id);
    }
    /**
     * Register a new entity ID
     *
     * @param entityClass
     * @param entityName
     * @param id
     */
    public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id)
    {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
    }

    /**
     * Register a new entity ID
     *
     * @param entityClass
     * @param entityName
     * @param id
     * @param background
     * @param foreground
     */
    public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id, int background, int foreground)
    {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id, background, foreground);
    }

    @SideOnly(CLIENT)
    public static void registerKey(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat)
    {
        ModLoaderClientHelper.registerKeyBinding(mod, keyHandler, allowRepeat);
    }

    /**
     * Register the mod for packets on this channel.
     * {@link NetworkRegistry#registerChannel(IPacketHandler, String)}
     *
     * @param mod
     * @param channel
     */
    public static void registerPacketChannel(BaseMod mod, String channel)
    {
        NetworkRegistry.instance().registerChannel(ModLoaderHelper.buildPacketHandlerFor(mod), channel);
    }

    /**
     * Register a new tile entity class
     *
     * @param tileEntityClass
     * @param id
     */
    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id)
    {
        GameRegistry.registerTileEntity(tileEntityClass, id);
    }

    @SideOnly(CLIENT)
    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id, TileEntitySpecialRenderer renderer)
    {
        ClientRegistry.registerTileEntity(tileEntityClass, id, renderer);
    }

    /**
     * Remove a biome from the list of generated biomes
     *
     * @param biome
     */
    public static void removeBiome(BiomeGenBase biome)
    {
        GameRegistry.removeBiome(biome);
    }

    /**
     * Remove a spawn
     *
     * @param entityClass
     * @param spawnList
     */
    public static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList)
    {
        EntityRegistry.removeSpawn(entityClass, spawnList, WorldType.base12Biomes);
    }

    /**
     * Remove a spawn
     *
     * @param entityClass
     * @param spawnList
     * @param biomes
     */
    public static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        EntityRegistry.removeSpawn(entityClass, spawnList, biomes);
    }

    /**
     * Remove a spawn
     *
     * @param entityName
     * @param spawnList
     */
    public static void removeSpawn(String entityName, EnumCreatureType spawnList)
    {
        EntityRegistry.removeSpawn(entityName, spawnList, WorldType.base12Biomes);
    }

    /**
     * Remove a spawn
     *
     * @param entityName
     * @param spawnList
     * @param biomes
     */
    public static void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        EntityRegistry.removeSpawn(entityName, spawnList, biomes);
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static boolean renderBlockIsItemFull3D(int modelID)
    {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelID);
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID)
    {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelID);
    }

    /**
     * Configuration is handled elsewhere
     * {@link ModLoaderModContainer}
     */
    @Deprecated
    public static void saveConfig()
    {
    }

    /**
     * Send a packet from client to server
     *
     * @param packet
     */
    public static void sendPacket(Packet packet) {
        PacketDispatcher.sendPacketToServer(packet);
    }
    /**
     * Send a chat message to the server
     *
     * @param text
     */
    @Deprecated
    public static void serverChat(String text)
    {
        //TODO
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void serverLogin(NetClientHandler handler, Packet1Login loginPacket)
    {
        //TODO
    }

    public static void serverSendPacket(NetServerHandler handler, Packet packet)
    {
        if (handler != null)
        {
            PacketDispatcher.sendPacketToPlayer(packet, (Player)handler.getPlayer());
        }
    }
    public static void serverOpenWindow(EntityPlayerMP player, Container container, int ID, int x, int y, int z)
    {
        ModLoaderHelper.openGui(ID, player, container, x, y, z);
    }

    /**
     * Indicate that you want to receive ticks
     *
     * @param mod receiving the events
     * @param enable indicates whether you want to recieve them or not
     * @param useClock don't receive render subticks, just world ticks
     */
    public static void setInGameHook(BaseMod mod, boolean enable, boolean useClock)
    {
        ModLoaderHelper.updateStandardTicks(mod, enable, useClock);
    }


    public static void setInGUIHook(BaseMod mod, boolean enable, boolean useClock)
    {
        ModLoaderHelper.updateGUITicks(mod, enable, useClock);
    }

    /**
     * Set a private field to a value using reflection
     * {@link ObfuscationReflectionHelper#setPrivateValue(Class, Object, int, Object)}
     *
     * @param instanceclass
     * @param instance
     * @param fieldindex
     * @param value
     */
    public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value)
    {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, fieldindex);
    }

    /**
     * Set a private field to a value using reflection
     * {@link ObfuscationReflectionHelper#setPrivateValue(Class, Object, String, Object)}
     *
     * @param instanceclass
     * @param instance
     * @param field
     * @param value
     */
    public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value)
    {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, field);
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param player
     * @param item
     * @param matrix
     */
    @Deprecated
    public static void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory matrix)
    {
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param player
     * @param item
     */
    @Deprecated
    public static void takenFromFurnace(EntityPlayer player, ItemStack item)
    {
    }

    /**
     * Throw the offered exception. Likely will stop the game.
     *
     * @param message
     * @param e
     */
    public static void throwException(String message, Throwable e)
    {
        FMLCommonHandler.instance().raiseException(e, message, true);
    }

    public static void throwException(Throwable e)
    {
        throwException("Exception in ModLoader", e);
    }
}