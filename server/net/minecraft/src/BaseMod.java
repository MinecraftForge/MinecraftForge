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

import java.util.Random;

import fml.IWorldGenerator;
import fml.Mod;
import fml.obf.FMLHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

public abstract class BaseMod implements IWorldGenerator {
  public int addFuel(int id, int metadata) {
    return 0;
  }

  public boolean dispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item) {
    return false;
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, Object... additionalData) {
    World w=(World) additionalData[0];
    IChunkProvider cp=(IChunkProvider) additionalData[1];
    
    if (cp instanceof ChunkProviderGenerate) {
      generateSurface(w, random, chunkX<<4, chunkZ<<4);
    } else if (cp instanceof ChunkProviderHell){
      generateNether(w, random, chunkX<<4, chunkZ<<4);
    }
  }
  
  public void generateNether(World world, Random random, int chunkX, int chunkZ) {
  }

  public void generateSurface(World world, Random random, int chunkX, int chunkZ) {
  }

  public String getName() {
    return getClass().getSimpleName();
  }

  public String getPriorities() {
    return null;
  }

  public abstract String getVersion();

  public abstract void load();

  public void modsLoaded() {
  }

  public void onItemPickup(EntityPlayer player, ItemStack item) {
  }

  /**
   * Ticked every game tick if you have subscribed to tick events through {@link ModLoader#setInGameHook(BaseMod, boolean, boolean)}
   * @param minecraftServer the server
   * @return true to continue receiving ticks
   */
  public boolean onTickInGame(MinecraftServer minecraftServer) {
    return false;
  }

  public void receiveChatPacket(String text) {
  }

  public void receiveCustomPacket(Packet250CustomPayload packet) {
  }

  public void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory matrix) {
  }

  public void takenFromFurnace(EntityPlayer player, ItemStack item) {

  }

  // void addRenderer(Map<Class<? extends Entity>, Render> renderers);
  // void registerAnimation(Minecraft game);
  // void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID);
  // boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID);
  // boolean onTickInGUI(float tick, Minecraft game, GuiScreen gui);
  // void keyboardEvent(KeyBinding event);
  
  @Override
  public String toString() {
    return getName()+" "+getVersion();
  }
}
