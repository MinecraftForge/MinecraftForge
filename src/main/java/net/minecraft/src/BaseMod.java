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

import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.*;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.SideOnly;


/**
 * Bye bye ModLoader. Deprecated without direct replacement
 * @author cpw
 *
 */
@Deprecated
public abstract class BaseMod implements cpw.mods.fml.common.modloader.BaseModProxy
{
    // CALLBACK MECHANISMS

    @Deprecated
    public final boolean doTickInGame(TickType tick, boolean tickEnd, Object... data)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();
        boolean hasWorld = mc.field_71441_e != null;
        // World and render ticks
        if (tickEnd && ( tick==TickType.RENDER || tick==TickType.CLIENT ) && hasWorld) {
            return onTickInGame((Float) data[0], mc);
        }
        return true;
    }

    @Deprecated
    public final boolean doTickInGUI(TickType tick, boolean tickEnd, Object... data)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();

        boolean hasWorld = mc.field_71441_e != null;

        if (tickEnd && ( tick==TickType.RENDER || ( tick==TickType.CLIENT && hasWorld))) {
            return onTickInGUI((Float) data[0], mc, mc.field_71462_r);
        }
        return true;
    }

   /*
    public final void onRenderHarvest(Map renderers)
    {
        addRenderer((Map<Class<? extends Entity>,Render>)renderers);
    }

    public final void onRegisterAnimations()
    {
        registerAnimation(FMLClientHandler.instance().getClient());
    }

    @Override
    public final void onCrafting(Object... craftingParameters)
    {
        takenFromCrafting((EntityPlayer)craftingParameters[0], (ItemStack)craftingParameters[1], (IInventory)craftingParameters[2]);
    }

    @Override
    public final void onSmelting(Object... smeltingParameters)
    {
        takenFromFurnace((EntityPlayer)smeltingParameters[0], (ItemStack)smeltingParameters[1]);
    }

    @Override
    public final boolean dispense(double x, double y, double z, int xVelocity, int zVelocity, Object... data)
    {
        return dispenseEntity((World)data[0], x, y, z, xVelocity, zVelocity, (ItemStack)data[1]);
    }

    @Override
    public final boolean onChat(Object... data)
    {
        receiveChatPacket(((Packet3Chat)data[0]).field_73476_b);
        return true;
    }


    @Override
    public final void onServerLogin(Object handler) {
        serverConnect((NetClientHandler) handler);
    }

    public final void onServerLogout() {
        serverDisconnect();
    }

    @Override
    public final void onPlayerLogin(Object player)
    {
        onClientLogin((EntityPlayer) player);
    }

    @Override
    public final void onPlayerLogout(Object player)
    {
        onClientLogout((EntityPlayer)player);
    }

    @Override
    public final void onPlayerChangedDimension(Object player)
    {
        onClientDimensionChanged((EntityPlayer)player);
    }

    @Override
    public final void onPacket250Packet(Object... data)
    {
        receiveCustomPacket((Packet250CustomPayload)data[0]);
    }

    @Override
    public final void notifyPickup(Object... pickupData)
    {
        EntityItem item = (EntityItem) pickupData[0];
        EntityPlayer player = (EntityPlayer) pickupData[1];
        onItemPickup(player, item.field_70294_a);
    }

    @Override
    public final void generate(Random random, int chunkX, int chunkZ, Object... additionalData)
    {
        World w = (World) additionalData[0];
        IChunkProvider cp = (IChunkProvider) additionalData[1];

        if (cp instanceof ChunkProviderGenerate)
        {
            generateSurface(w, random, chunkX << 4, chunkZ << 4);
        }
        else if (cp instanceof ChunkProviderHell)
        {
            generateNether(w, random, chunkX << 4, chunkZ << 4);
        }
    }

    @Override
    public final boolean handleCommand(String command, Object... data)
    {
        return false;
    }

    */
    // BASEMOD API
    /**
     * Override if you wish to provide a fuel item for the furnace and return the fuel value of the item
     *
     * @param id
     * @param metadata
     */
    @Deprecated
    public int addFuel(int id, int metadata)
    {
        return 0;
    }

    @SideOnly(CLIENT)
    @Deprecated
    public void addRenderer(Map<Class<? extends Entity>, Render> renderers)
    {
    }

    /**
     * Override if you wish to generate Nether (Hell biome) blocks
     *
     * @param world
     * @param random
     * @param chunkX
     * @param chunkZ
     */
    @Deprecated
    public void generateNether(World world, Random random, int chunkX, int chunkZ)
    {
    }

    /**
     * Override if you wish to generate Overworld (not hell or the end) blocks
     *
     * @param world
     * @param random
     * @param chunkX
     * @param chunkZ
     */
    @Deprecated
    public void generateSurface(World world, Random random, int chunkX, int chunkZ)
    {
    }

    /**
     * Callback to return a gui screen to display
     * @param player
     * @param containerID
     * @param x
     * @param y
     * @param z
     */
    @Deprecated
    @SideOnly(CLIENT)
    public GuiContainer getContainerGUI(EntityClientPlayerMP player, int containerID, int x, int y, int z)
    {
        return null;
    }

    /**
     * Return the name of your mod. Defaults to the class name
     */
    @Deprecated
    public String getName()
    {
        return getClass().getSimpleName();
    }

    /**
     * Get your mod priorities
     */
    @Deprecated
    public String getPriorities()
    {
        return "";
    }

    /**
     * Return the version of your mod
     */
    @Deprecated
    public abstract String getVersion();

    @SideOnly(CLIENT)
    @Deprecated
    public void keyboardEvent(KeyBinding event)
    {

    }

    /**
     * Load your mod
     */
    @Deprecated
    public abstract void load();

    /**
     * Finish loading your mod
     */
    @Deprecated
    public void modsLoaded()
    {
    }

    /**
     * Handle item pickup
     *
     * @param player
     * @param item
     */
    @Deprecated
    public void onItemPickup(EntityPlayer player, ItemStack item)
    {
    }

    /**
     * Ticked every game tick if you have subscribed to tick events through {@link ModLoader#setInGameHook(BaseMod, boolean, boolean)}
     *
     * @param time the rendering subtick time (0.0-1.0)
     * @param minecraftInstance the client
     * @return true to continue receiving ticks
     */
    @Deprecated
    @SideOnly(CLIENT)
    public boolean onTickInGame(float time, Minecraft minecraftInstance)
    {
        return false;
    }

    @Deprecated
    public boolean onTickInGame(MinecraftServer minecraftServer)
    {
        return false;
    }

    @Deprecated
    @SideOnly(CLIENT)
    public boolean onTickInGUI(float tick, Minecraft game, GuiScreen gui)
    {
        return false;
    }

    /**
     * Only implemented on the client side
     * {@link #serverChat(NetServerHandler, String)}
     *
     * @param text
     */
    @Override
    @Deprecated
    public void clientChat(String text)
    {
    }

    /**
     * Called when a client connects
     * @param handler
     */
    @SideOnly(CLIENT)
    @Deprecated
    public void clientConnect(NetClientHandler handler)
    {

    }

    /**
     * Called when the client disconnects
     * @param handler
     */
    @SideOnly(CLIENT)
    @Deprecated
    public void clientDisconnect(NetClientHandler handler)
    {

    }
    /**
     * Called client side to receive a custom payload for this mod
     *
     * NOTE: this method is not provided in Risugami's implementation of BaseMod!
     *
     * @param packet
     */
    @Override
    @Deprecated
    public void receiveCustomPacket(Packet250CustomPayload packet)
    {
    }

    @SideOnly(CLIENT)
    @Deprecated
    public void registerAnimation(Minecraft game)
    {

    }

    @SideOnly(CLIENT)
    @Deprecated
    public void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {

    }

    @SideOnly(CLIENT)
    @Deprecated
    public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID)
    {
        return false;

    }
    /*
     * NOTE: this method is not provided in Risugami's implementation of BaseMod!
     */
    @Override
    @Deprecated
    public void serverConnect(NetHandler handler) {

    }

    @Override
    @Deprecated
    public void serverCustomPayload(NetServerHandler handler, Packet250CustomPayload packet)
    {

    }

    /*
     * NOTE: this method is not provided in Risugami's implementation of BaseMod!
     */
    @Override
    @Deprecated
    public void serverDisconnect() {

    }
    /**
     * Called when someone crafts an item from a crafting table
     *
     * @param player
     * @param item
     * @param matrix
     */
    @Deprecated
    public void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory matrix)
    {
    }

    /**
     * Called when someone takes a smelted item from a furnace
     *
     * @param player
     * @param item
     */
    @Deprecated
    public void takenFromFurnace(EntityPlayer player, ItemStack item)
    {
    }

    /**
     * The identifier string for the mod- used in client<->server negotiation
     */
    @Override
    public String toString()
    {
        return getName() + " " + getVersion();
    }

    /**
     * Called when a chat message is received. Return true to stop further processing
     */
    @Override
    @Deprecated
    public void serverChat(NetServerHandler source, String message)
    {
    }
    /**
     * Called when a new client logs in.
     *
     * NOTE: this method is not provided in Risugami's implementation of BaseMod!
     *
     * @param player
     */
    @Override
    @Deprecated
    public void onClientLogin(EntityPlayer player)
    {
    }

    /**
     * Called when a client logs out of the server.
     *
     * NOTE: this method is not provided in Risugami's implementation of BaseMod!
     */
    @Override
    @Deprecated
    public void onClientLogout(INetworkManager mgr)
    {

    }

    /**
     * Spawn the entity of the supplied type, if it is your mod's
     */
    @SideOnly(CLIENT)
    @Deprecated
    public Entity spawnEntity(int entityId, World world, double scaledX, double scaledY, double scaledZ)
    {
        return null;
    }

    @SideOnly(CLIENT)
    @Deprecated
    public void clientCustomPayload(NetClientHandler handler, Packet250CustomPayload packet)
    {

    }

}
