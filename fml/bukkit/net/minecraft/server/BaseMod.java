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
package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.generator.NetherChunkGenerator;
import org.bukkit.craftbukkit.generator.NormalChunkGenerator;

import cpw.mods.fml.common.IConsoleHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.INetworkHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IWorldGenerator;

public abstract class BaseMod implements cpw.mods.fml.common.modloader.BaseMod
{
    // CALLBACK MECHANISMS
    @Override
    public final void onCrafting(Object... craftingParameters)
    {
        takenFromCrafting((EntityHuman)craftingParameters[0], (ItemStack)craftingParameters[1], (IInventory)craftingParameters[2]);
    }

    @Override
    public final void onSmelting(Object... smeltingParameters)
    {
        takenFromFurnace((EntityHuman)smeltingParameters[0], (ItemStack)smeltingParameters[1]);
    }
    @Override
    public final boolean dispense(double x, double y, double z, byte xVelocity, byte zVelocity, Object... data)
    {
        return dispenseEntity((World)data[0], x, y, z, xVelocity, zVelocity, (ItemStack)data[1]);
    }

    @Override
    public final boolean onChat(Object... data)
    {
        return onChatMessageReceived((EntityHuman)data[1], (Packet3Chat)data[0]);
    }
    @Override
    public final void onPlayerLogin(Object player)
    {
        onClientLogin((EntityHuman) player);
    }

    @Override
    public void onPlayerLogout(Object player)
    {
        onClientLogout((EntityHuman)player);
    }
    
    @Override
    public void onPlayerChangedDimension(Object player)
    {
        onClientDimensionChanged((EntityHuman)player);
    }

    @Override
    public final void onPacket250Packet(Object... data)
    {
        onPacket250Received((EntityHuman)data[1], (Packet250CustomPayload)data[0]);
    }

    @Override
    public final void notifyPickup(Object... pickupData)
    {
        EntityItem item = (EntityItem) pickupData[0];
        EntityHuman player = (EntityHuman) pickupData[1];
        onItemPickup(player, item.itemStack);
    }

    @Override
    public final void generate(Random random, int chunkX, int chunkZ, Object... additionalData)
    {
        World w = (World) additionalData[0];
        IChunkProvider cp = (IChunkProvider) additionalData[1];

        if (cp instanceof ChunkProviderHell || cp instanceof NetherChunkGenerator)
        {
            generateNether(w, random, chunkX << 4, chunkZ << 4);
        }
        else if (cp instanceof ChunkProviderGenerate || cp instanceof NormalChunkGenerator)
        {
            generateSurface(w, random, chunkX << 4, chunkZ << 4);
        }
    }

    @Override
    public final boolean handleCommand(String command, Object... data)
    {
        return onServerCommand(command, (String)data[0], (ICommandListener)data[1]);
    }
    // BASEMOD API
    /**
     * Override if you wish to provide a fuel item for the furnace and return the fuel value of the item
     * @param id
     * @param metadata
     * @return
     */
    public int addFuel(int id, int metadata)
    {
        return 0;
    }

    /**
     * Override if you wish to perform some action other than just dispensing the item from the dispenser
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
     * @return
     */
    public String getName()
    {
        return getClass().getSimpleName();
    }

    /**
     * Get your mod priorities
     * @return
     */
    public String getPriorities()
    {
        return null;
    }

    /**
     * Return the version of your mod
     * @return
     */
    public abstract String getVersion();

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
     * @param player
     * @param item
     */
    public void onItemPickup(EntityHuman player, ItemStack item)
    {
    }

    /**
     * Ticked every game tick if you have subscribed to tick events through {@link ModLoader#setInGameHook(BaseMod, boolean, boolean)}
     * @param minecraftServer the server
     * @return true to continue receiving ticks
     */
    public boolean onTickInGame(MinecraftServer minecraftServer)
    {
        return false;
    }

    /**
     * Not implemented because on the server you don't know who it's from
     * {@link #onChatMessageReceived(EntityHuman, Packet3Chat)}
     * @param text
     */
    @Deprecated
    public void receiveChatPacket(String text)
    {
    }

    /**
     * Not implemented because on the server you don't know who it's from
     * {@link #onPacket250Received(EntityHuman, Packet250CustomPayload)}
     * @param packet
     */
    @Deprecated
    public void receiveCustomPacket(Packet250CustomPayload packet)
    {
    }

    /**
     * Called when someone crafts an item from a crafting table
     * @param player
     * @param item
     * @param matrix
     */
    public void takenFromCrafting(EntityHuman player, ItemStack item, IInventory matrix)
    {
    }

    /**
     * Called when someone takes a smelted item from a furnace
     *
     * @param player
     * @param item
     */
    public void takenFromFurnace(EntityHuman player, ItemStack item)
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
    public void onPacket250Received(EntityHuman source, Packet250CustomPayload payload)
    {
    }

    /**
     * Called when a chat message is received. Return true to stop further processing
     * @param source
     * @param chat
     * @return true if you want to consume the message so it is not available for further processing
     */
    public boolean onChatMessageReceived(EntityHuman source, Packet3Chat chat)
    {
        return false;
    }
    /**
     * Called when a server command is received
     * @param command
     * @return true if you want to consume the message so it is not available for further processing
     */
    public boolean onServerCommand(String command, String sender, ICommandListener listener)
    {
        return false;
    }

    /**
     * Called when a new client logs in.
     * 
     * @param player 
     */
    public void onClientLogin(EntityHuman player)
    {
    }

    /**
     * Called when a client logs out of the server.
     * 
     * @param player
     */
    public void onClientLogout(EntityHuman player)
    {
        
    }

    /**
     * 
     * Called when a client changes dimensions on the server.
     * 
     * @param player
     */
    public void onClientDimensionChanged(EntityHuman player)
    {
        
    }

    // Spare client junk
    // -------
    // void addRenderer(Map<Class<? extends Entity>, Render> renderers);
    // void registerAnimation(Minecraft game);
    // void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID);
    // boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID);
    // boolean onTickInGUI(float tick, Minecraft game, GuiScreen gui);
    // void keyboardEvent(KeyBinding event);
}
