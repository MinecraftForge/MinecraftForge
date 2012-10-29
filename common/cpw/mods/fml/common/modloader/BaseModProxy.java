/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
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

package cpw.mods.fml.common.modloader;

import java.util.Random;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.asm.SideOnly;

/**
 *
 * Marker interface for BaseMod
 *
 * @author cpw
 *
 */
public interface BaseModProxy
{
    void modsLoaded();

    void load();

    String getName();

    String getPriorities();

    String getVersion();

    boolean doTickInGUI(TickType type, boolean end, Object... tickData);
    boolean doTickInGame(TickType type, boolean end, Object... tickData);
    void generateSurface(World w, Random random, int i, int j);
    void generateNether(World w, Random random, int i, int j);
    int addFuel(int itemId, int damage);
    void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix);
    void takenFromFurnace(EntityPlayer player, ItemStack item);

    public abstract void onClientLogout(INetworkManager manager);

    public abstract void onClientLogin(EntityPlayer player);

    public abstract void serverDisconnect();

    public abstract void serverConnect(NetHandler handler);

    public abstract void receiveCustomPacket(Packet250CustomPayload packet);

    public abstract void clientChat(String text);

    public abstract void onItemPickup(EntityPlayer player, ItemStack item);

    public abstract void serverCustomPayload(NetServerHandler handler, Packet250CustomPayload packet);

    public abstract void serverChat(NetServerHandler source, String message);
}
