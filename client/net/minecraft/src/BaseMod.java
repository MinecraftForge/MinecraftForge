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

import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.IConsoleHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.INetworkHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.TickType;

public abstract class BaseMod implements cpw.mods.fml.common.modloader.BaseMod
{
    // CALLBACK MECHANISMS

    public void keyBindingEvent(Object keybinding)
    {
        this.keyboardEvent((KeyBinding)keybinding);
    }

    /**
     * @param minecraftInstance
     * @return
     */
    public final boolean doTickInGame(TickType tick, boolean tickEnd, Object minecraftInstance, Object... data)
    {
        Minecraft mc = (Minecraft) minecraftInstance;
        boolean hasWorld = mc.field_6324_e != null;
        // World and render ticks
        if (tickEnd && ( tick==TickType.RENDER || tick==TickType.GAME ) && hasWorld) {
            return onTickInGame((Float) data[0], mc);
        }
        return true;
    }

    public final boolean doTickInGUI(TickType tick, boolean tickEnd, Object minecraftInstance, Object... data)
    {
        Minecraft mc = (Minecraft) minecraftInstance;
        if (tickEnd && ( tick==TickType.RENDER || tick==TickType.GAME  ) || tick ==  TickType.GUILOAD) {
            return onTickInGUI((Float) data[0], mc, mc.field_6313_p);
        }
        return true;
    }

    /**
     * @param renderers
     */
    public final void onRenderHarvest(Map renderers)
    {
        addRenderer((Map<Class<? extends Entity>,Render>)renderers);

    }

    /**
     *
     */
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
    public final boolean dispense(double x, double y, double z, byte xVelocity, byte zVelocity, Object... data)
    {
        return dispenseEntity((World)data[0], x, y, z, xVelocity, zVelocity, (ItemStack)data[1]);
    }

    @Override
    public final boolean onChat(Object... data)
    {
        receiveChatPacket(((Packet3Chat)data[0]).field_517_a);
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
        onItemPickup(player, item.field_801_a);
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

    /**
     * NO-OP on client side
     */
    @Override
    public final boolean handleCommand(String command, Object... data)
    {
        return false;
    }
    // BASEMOD API
    /**
     * Override if you wish to provide a fuel item for the furnace and return the fuel value of the item
     *
     * @param id
     * @param metadata
     * @return
     */
    public int addFuel(int id, int metadata)
    {
        return 0;
    }

    public void addRenderer(Map<Class<? extends Entity>, Render> renderers)
    {

    }

    /**
     * Override if you wish to perform some action other than just dispensing the item from the dispenser
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param xVel
     * @param zVel
     * @param item
     * @return
     */
    public boolean dispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item)
    {
        return false;
    }

    /**
     * Override if you wish to generate Nether (Hell biome) blocks
     *
     * @param world
     * @param random
     * @param chunkX
     * @param chunkZ
     */
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
    public void generateSurface(World world, Random random, int chunkX, int chunkZ)
    {
    }

    /**
     * Return the name of your mod. Defaults to the class name
     *
     * @return
     */
    public String getName()
    {
        return getClass().getSimpleName();
    }

    /**
     * Get your mod priorities
     *
     * @return
     */
    public String getPriorities()
    {
        return "";
    }

    /**
     * Return the version of your mod
     *
     * @return
     */
    public abstract String getVersion();

    public void keyboardEvent(KeyBinding event)
    {

    }

    /**
     * Load your mod
     */
    public abstract void load();

    /**
     * Finish loading your mod
     */
    public void modsLoaded()
    {
    }

    /**
     * Handle item pickup
     *
     * @param player
     * @param item
     */
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
    public boolean onTickInGame(float time, Minecraft minecraftInstance)
    {
        return false;
    }

    public boolean onTickInGUI(float tick, Minecraft game, GuiScreen gui)
    {
        return false;
    }

    /**
     * Only implemented on the client side
     * {@link #onChatMessageReceived(EntityPlayer, Packet3Chat)}
     *
     * @param text
     */
    public void receiveChatPacket(String text)
    {
        // TODO
    }

    /**
     * Only called on the client side
     * {@link #onPacket250Received(EntityPlayer, Packet250CustomPayload)}
     *
     * @param packet
     */
    public void receiveCustomPacket(Packet250CustomPayload packet)
    {
        // TODO
    }

    public void registerAnimation(Minecraft game)
    {

    }

    public void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {

    }

    public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID)
    {
        return false;

    }

    public void serverConnect(NetClientHandler handler) {

    }

    public void serverDisconnect() {

    }
    /**
     * Called when someone crafts an item from a crafting table
     *
     * @param player
     * @param item
     * @param matrix
     */
    public void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory matrix)
    {
    }

    /**
     * Called when someone takes a smelted item from a furnace
     *
     * @param player
     * @param item
     */
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
     * Called when a 250 packet is received on a channel registered to this mod
     *
     * @param source
     * @param payload
     */
    public void onPacket250Received(EntityPlayer source, Packet250CustomPayload payload)
    {
    }

    /**
     * Called when a chat message is received. Return true to stop further processing
     *
     * @param source
     * @param chat
     * @return true if you want to consume the message so it is not available for further processing
     */
    public boolean onChatMessageReceived(EntityPlayer source, Packet3Chat chat)
    {
        return false;
    }
    /**
     * Called when a server command is received
     * @param command
     * @return true if you want to consume the message so it is not available for further processing
     */
    public boolean onServerCommand(String command, String sender, Object listener)
    {
        return false;
    }

    /**
     * Called when a new client logs in.
     *
     * @param player
     */
    public void onClientLogin(EntityPlayer player)
    {
    }

    /**
     * Called when a client logs out of the server.
     *
     * @param player
     */
    public void onClientLogout(EntityPlayer player)
    {

    }

    /**
     *
     * Called when a client changes dimensions on the server.
     *
     * @param player
     */
    public void onClientDimensionChanged(EntityPlayer player)
    {

    }

}
